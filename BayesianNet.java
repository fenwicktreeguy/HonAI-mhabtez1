import java.util.*;
import java.io.*;

//DATASET USED: https://archive.ics.uci.edu/ml/datasets/Breast+Cancer+Wisconsin+%28Diagnostic%29

class Edge{
    int current_factor;
    boolean aff_or_neg;
    public Edge(int current_factor, boolean aff_or_neg){
        this.current_factor = current_factor;
        this.aff_or_neg = aff_or_neg;
    }
}

class EPair{
    int node_id;
    Edge coinciding_edge;
    public EPair(int node_id, Edge coinciding_edge){
        this.node_id = node_id;
        this.coinciding_edge = coinciding_edge;
    }
}
class QueueType{
    public static ArrayList< ArrayList<String> > data;
    int node_id;
    String usedCols;
    double raw_entropy;
    public QueueType(ArrayList< ArrayList<String> > data, String usedCols, double raw_entropy){
        this.data = data;
        this.usedCols = usedCols;
        this.raw_entropy = raw_entropy;
        node_id = 0;
    }
}

public class BayesianNet {
    //public static DataParser d;
    String[][] default_matrix;
    public static ArrayList< ArrayList<String> > data;
    public static ArrayList< ArrayList<EPair> > graph;
    public HashMap<Integer, ArrayList<String> > terminal_states;
    boolean[] vis = new boolean[100001];
    public double GLOBAL_AFFIRMATIVE_STORAGE=0;
    public double GLOBAL_NEGATIVE_STORAGE=0;
    //stores edges which contain information about queried value and whether its affirmative or negative

    public ArrayList<Double> THRESHOLDS_FOR_CLASSIFICATION;

    public BayesianNet(){
        data = new ArrayList< ArrayList<String> >();
        THRESHOLDS_FOR_CLASSIFICATION = new ArrayList<Double>();
        default_matrix = new String[620][620];
        assign_thresholds();
    }
    //these thresholds
    //for data which was discretized by taking the average, the entropy values are pretty reasonable
    //look to better methods for this, or just better datasets
    public void assign_thresholds(){
        double shift_val = 2.0;
        THRESHOLDS_FOR_CLASSIFICATION.add(14.127291739894563 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(19.28964850615117 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(91.96903339191566 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(654.8891036906857 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add( 0.096360281195079  * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.10434098418277686 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add( 0.08879931581722322 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.048919145869947236 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.181161862917399 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.06279760984182778 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.4051720562390161 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(1.2168534270650269 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(2.8660592267135288 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(40.33707908611603 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.007040978910369071 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.025478138840070306 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.031893716344463946 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.011796137082601056 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.020542298769771532 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.0037949038664323383 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(16.269189806678394 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(25.677223198594014 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(107.2612126537786 *  shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(880.5831282952545 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.13236859402460469 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.25426504393673144 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.27218848330404205 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.11460622319859404 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add( 0.29007557117750454 * shift_val);
        THRESHOLDS_FOR_CLASSIFICATION.add(0.08394581722319855 * shift_val);
    }

    public BayesianNet(int nNodes) {
        default_matrix = new String[620][620];
        data = new ArrayList< ArrayList<String> >();
        THRESHOLDS_FOR_CLASSIFICATION = new ArrayList<Double>();
        graph = new ArrayList< ArrayList<EPair> >();
        for(int i = 0; i < 1200; i++){
            graph.add(new ArrayList<EPair>());
        }
        terminal_states = new HashMap<Integer, ArrayList<String> >();
        assign_thresholds();
        //d = new DataParser(data);
    }

    public ArrayList<String> calculate_entropy_precomp(ArrayList< ArrayList<String> > data, int col, boolean target){
        ArrayList<String> ret = new ArrayList<String>();
        int i = col;
        ArrayList<String> dat = data.get(i);
        for (int j = 0; j < dat.size(); j++) {
            //System.out.println( Double.parseDouble(dat.get(j)) + " " + THRESHOLDS_FOR_CLASSIFICATION.get(i-2));
            boolean val = Double.parseDouble(dat.get(j)) >= THRESHOLDS_FOR_CLASSIFICATION.get(i-2);
            if (val == target) {
                ret.add(data.get(1).get(j));
            }
        }
        return ret;
    }

    public double calculate_entropy_postcomp(ArrayList<String> affneg){
        double a = 0;
        double b = 0;
        for(String s : affneg){
            if(s.equals("M")){
                ++a;
            } else {
                ++b;
            }
        }
        if(a==0 || b == 0){
            return 1000000000.0;
        }
        double num1 = (double)(a)/(double)(a+b);
        double num2 = (double)(b)/(double)(a+b);
        return - (  (num1 * (double)(Math.log(num1))/(double)(Math.log(2)) ) + (num2 * (double)(Math.log(num2))/(double)(Math.log(2))) );
    }

    public void compute_global_vals(ArrayList<String> pts){
        for(String s : pts){
            if(s.equals("M")){
                ++GLOBAL_AFFIRMATIVE_STORAGE;
            } else {
                ++GLOBAL_NEGATIVE_STORAGE;
            }
        }
    }

    public ArrayList< ArrayList<String> > updateData(ArrayList< ArrayList<String> > cur_data, int col, boolean target){
        ArrayList< ArrayList<String> > new_data = new ArrayList< ArrayList<String> >();
        double aff = THRESHOLDS_FOR_CLASSIFICATION.get(col-2);
        ArrayList<Integer> desired_indices = new ArrayList<Integer>();
        ArrayList<String> column = cur_data.get(col);
        for(int i = 0; i < column.size(); i++){
            boolean val = Double.parseDouble(column.get(i)) >= aff;
            if( val == target){
                desired_indices.add(i);
            }
        }
        for(int i = 0; i < cur_data.size(); i++){
            ArrayList<String> tmp = new ArrayList<String>();
            for(int j : desired_indices){
                tmp.add(cur_data.get(i).get(j));
            }
            new_data.add(tmp);
        }
        return new_data;

    }

    public double calculate_gini(){
        return 0.0;
    }

    public void parseData(){
        try {
            BufferedReader sc = new BufferedReader(new FileReader("data.csv"));
            int x_size = 0;
            int y_size = 0;
            String line;
            while( (line = sc.readLine()) != null) {
                String[] col = line.split(",");
                default_matrix[x_size++] = col;
                if(y_size==0){
                    y_size = col.length;
                }
            }
            default_matrix = take_transpose(x_size,y_size);

            for(int i = 0; i < y_size; i++){
                data.add( new ArrayList<String>(Arrays.asList(default_matrix[i])));
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public String[][] take_transpose(int x_size, int y_size){
        String[][] cur = new String[y_size][x_size];
        //System.out.println(x_size + " " + y_size);
        for(int i = 0; i < y_size; i++){
            for(int j = 0; j < x_size; j++){
                if(cur[i][j] == null){
                    cur[i][j] = default_matrix[j][i];
                }
            }
        }
        return cur;
    }

    public double calc_average(int idx){
        String[] tmp = default_matrix[idx];
        double sm = 0;
        for(int i = 0; i < tmp.length; i++){
            sm += Double.parseDouble(tmp[i]);
        }
        System.out.println("AVERAGE FOR COL: " + idx + " : " + (double)(sm)/(double)(tmp.length));
        return (double)(sm)/(double)(tmp.length);
    }


    //TODO: look into why there seems to be some sort of compression happening in the chosen features (where some features are not included in the decision tree).
    //TODO: Not sure if this is a bug, a consequence of the data, or a consequence of how the data was made binary(maybe do different stats stuff)

    public void construct_decision_tree_entropy(){
        ArrayList<Integer> current_cols = new ArrayList<Integer>();
        ArrayList<String> pot_strings = new ArrayList<String>();
        String cur = "";
        for(int i = 2; i < data.size(); i++){
            current_cols.add(i);
            cur += '0';
        }
        double raw_entropy = 0;
        double pos = 0, neg = 0;
        ArrayList<String> res = data.get(1);
        for(int j = 0; j < res.size(); j++){
            if(res.get(j).equals("M")){
                ++pos;
            } else {
                ++neg;
            }
        }
        double prob1 = (double)pos/(pos+neg);
        double prob2 = (double)neg/(pos+neg);
        raw_entropy = - (  (prob1 * (double)(Math.log(prob1))/(double)(Math.log(2)) ) + (prob2 * (double)(Math.log(prob2))/(double)(Math.log(2))) );

        QueueType seed = new QueueType( ( (ArrayList< ArrayList<String> >)data.clone()),cur, raw_entropy);
        Queue< QueueType > q = new LinkedList< QueueType >();
        q.add(seed);
        int NODE_PTR = 0;
        while(!q.isEmpty()) {
            ArrayList<ArrayList<String>> newdata_aff = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> newdata_neg = new ArrayList<ArrayList<String>>();
            double optimal_gain = 0;
            int optimal_column = -1;
            double new_raw_ent_one = 0;
            double new_raw_ent_two = 0;
            QueueType top = q.poll();
            int NODE_ID = top.node_id;
            ArrayList<ArrayList<String>> quasidata = top.data;

            String curbit = top.usedCols;
            System.out.println("USED COLS: " + curbit);
            double raw_ent = top.raw_entropy;
            System.out.println("INITIAL RAW ENTROPY: " + raw_ent);
            String curbit_new = "";
            for (int i = 2; i < curbit.length(); i++) {
                if (curbit.charAt(i) != '1') {
                    String tmp = curbit;
                    curbit = curbit.substring(0, i) + '1' + curbit.substring(i + 1);
                    ArrayList<String> parsed = calculate_entropy_precomp(quasidata, i, true);
                    ArrayList<String> parsed2 = calculate_entropy_precomp(quasidata, i, false);
                    double entropy_val_one = calculate_entropy_postcomp(parsed);
                    double entropy_val_two = calculate_entropy_postcomp(parsed2);
                    compute_global_vals(quasidata.get(1));
                    double pb1 = (double) (GLOBAL_AFFIRMATIVE_STORAGE) / (double) (GLOBAL_AFFIRMATIVE_STORAGE + GLOBAL_NEGATIVE_STORAGE);
                    double pb2 = (double) (GLOBAL_NEGATIVE_STORAGE) / (double) (GLOBAL_AFFIRMATIVE_STORAGE + GLOBAL_NEGATIVE_STORAGE);
                    //System.out.println("RAW ENTROPY: " + raw_ent);
                    //System.out.println("AFFIRMATIVE ENTROPY: " + entropy_val_one);
                    //System.out.println("NEGATIVE ENTROPY: " + entropy_val_two);
                    double inf_gain = raw_ent - (pb1 * entropy_val_one + pb2 * entropy_val_two);
                    optimal_gain = Math.max(optimal_gain, inf_gain);
                    if (optimal_gain == inf_gain) {
                        optimal_column = i;
                        new_raw_ent_one = entropy_val_one;
                        new_raw_ent_two = entropy_val_two;
                        newdata_aff = updateData(data, i, true);
                        newdata_neg = updateData(data, i, false);
                        curbit_new = curbit;

                    }
                    curbit = tmp;
                }
            }

            if (optimal_column != -1) {
                QueueType qone = new QueueType(newdata_aff, curbit_new, new_raw_ent_one);
                QueueType qtwo = new QueueType(newdata_neg, curbit_new, new_raw_ent_two);
                qone.node_id = NODE_ID + 1;
                qtwo.node_id = NODE_ID + 2;
                Edge one = new Edge(optimal_column, true);
                Edge two = new Edge(optimal_column, false);
                System.out.println("RUN TWO");

                ArrayList<EPair> c1 = graph.get(NODE_ID);
                c1.add(new EPair(NODE_ID + 1, one));
                c1.add(new EPair(NODE_ID + 2, one));
                graph.set(NODE_ID, c1);
                q.add(qone);
                q.add(qtwo);
            } else {
                terminal_states.put(NODE_ID, quasidata.get(1));
            }

        }

    }

    //this assumes that our decision tree structure is already generated.
    public boolean supervised_decision(int node_id, ArrayList<String> parameters){
        System.out.println("CURRENT NODE: " + node_id);
        if(graph.get(node_id).size() > 0){
            ArrayList<EPair> chs = graph.get(node_id);
            double thresh = THRESHOLDS_FOR_CLASSIFICATION.get(chs.get(0).coinciding_edge.current_factor - 2);
            //System.out.println("FACTOR: " + chs.get(0).coinciding_edge.current_factor );
            for(EPair e : chs) {
                boolean targ_val = e.coinciding_edge.aff_or_neg;
                int col = e.coinciding_edge.current_factor;
                if( (Double.parseDouble(parameters.get(col-2)) <= thresh) == targ_val){
                    return supervised_decision(e.node_id, parameters);
                }
            }
        } else {
            ArrayList<String> terminal = terminal_states.get(node_id);
            double M = 0;
            double B = 0;
            for(String s : terminal){
                if(s.equals("M")){
                    ++M;
                } else {
                    ++B;
                }
            }
            double targ = 0.5;
            return ( ((double)(M)/(double)(M+B))  >= targ);
        }
        return true;

    }

    public void trav(int node_id){
        if(graph.get(node_id).size() > 0) {
            vis[node_id] = true;
            ArrayList<EPair> tmp = graph.get(node_id);
            System.out.println("NODE: " + node_id + " FACTOR: " + tmp.get(0).coinciding_edge.current_factor);
            for (EPair e : tmp) {
                if(!vis[e.node_id]) {
                    trav(e.node_id);
                }
            }
        } else {
            if(!vis[node_id]) {
                System.out.println("NODE: " + node_id);
            }
        }
    }



}
