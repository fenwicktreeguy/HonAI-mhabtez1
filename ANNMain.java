import java.util.*;
import java.io.*;


//do a matrix based feedforward & backpropagation




class Neuron {
    public static final double LEARNING_RATE = 0.4;
    double RAW_VALUE, ACTIVATION_VALUE, BIAS_VALUE;
    public Neuron(double RAW_VALUE, double BIAS_VALUE, int KEY) {
        this.RAW_VALUE = RAW_VALUE;
        if(KEY==0) {
            this.ACTIVATION_VALUE = this.RAW_VALUE;
        } else if(KEY==1) {
            //this.ACTIVATION_VALUE = this.RAW_VALUE;
            this.ACTIVATION_VALUE = Neuron.ReLU(RAW_VALUE);
        } else if(KEY==2){
            //this.ACTIVATION_VALUE = this.RAW_VALUE;
            this.ACTIVATION_VALUE = Neuron.sigmoid(RAW_VALUE);
        } else if(KEY==3){this.ACTIVATION_VALUE = this.RAW_VALUE;}
        this.BIAS_VALUE = BIAS_VALUE;
    }

    public Neuron(double RAW_VALUE, double BIAS_VALUE) {
        this.RAW_VALUE = RAW_VALUE;
        this.ACTIVATION_VALUE = Neuron.sigmoid(RAW_VALUE);
        this.BIAS_VALUE = BIAS_VALUE;
    }
    /*
    public Neuron(double RAW_VALUE, double ACTIVATION_VALUE, double BIAS_VALUE) {
        this.RAW_VALUE = RAW_VALUE;
        this.ACTIVATION_VALUE = ACTIVATION_VALUE;
        this.BIAS_VALUE = BIAS_VALUE;
    }
     */

    public Neuron(double RAW_VALUE) {
        this.RAW_VALUE = RAW_VALUE;
    }

    public static double sigmoid(double x) {
        double ret = 1 / (double) (1 + Math.pow(Math.E, -x));
        return ret;
    }

    public static double sigmoid_derivative(double x) {
        return (sigmoid(x) * (1 - sigmoid(x)));
    }
    public static double CROSS_ENTROPY(ArrayList<Double> OHE, ArrayList<Double> input){
        double res = 0;
        for(int i = 0; i < OHE.size(); i++){
            res += (OHE.get(i) * (double)(Math.log(input.get(i)))/(double)Math.log(Math.E)) ;
        }
        return (-1 * res);
    }
    public static ArrayList<Double> SIGLAYER(ArrayList<Neuron> finlayer) {
        ArrayList<Double> ans = new ArrayList<Double>();
        for(int i = 0; i < finlayer.size(); i++){
            ans.add(sigmoid_derivative(finlayer.get(i).RAW_VALUE));
        }
        return ans;
    }
    public static double ReLU(double x) {
        return Math.max(0, x);
    }
    public static ArrayList<Double> softmax(ArrayList<Neuron> vals) {
        double sm = 0;
        ArrayList<Double> n = new ArrayList<Double>();
        for (int i = 0; i < vals.size(); i++) {
            sm += Math.exp(vals.get(i).RAW_VALUE);
        }
        for (int i = 0; i < vals.size(); i++) {
            double nw = (double) (Math.exp(vals.get(i).RAW_VALUE)) / (double) (sm);
            n.add(nw);
        }
        return n;
    }
    public static double MSE_ERROR_DERIVATIVE(double VAL, double EXP) {return 2*(VAL-EXP); }
    public static ArrayList<Double> FIN_LAYER_ERROR(ArrayList<Double> layer, ArrayList<Double> expected) {
        ArrayList<Double> RET = new ArrayList<Double>();
        for(int i = 0; i < layer.size(); i++){
            RET.add(MSE_ERROR_DERIVATIVE(layer.get(i),expected.get(i)));
        }
        return RET;
    }
    public static double[] MATMUL(double[][] A, double[] B) {
        double[] ret = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            int ans = 0;
            for (int k = 0; k < B.length; k++) {
                ans += B[k] * A[i][k];
            }
            ret[i] = ans;
        }
        return ret;
    }
    //note that for this application, the hadamard product is only being applied on COLUMN VECTORS,
    //but when being applied recursively backwards, the intuition can be extended to matrices.
    public static double[] HADAMARD_PRODUCT(ArrayList<Double> one, ArrayList<Double> two){
        double[] ret = new double[one.size()];
        for(int i = 0; i < one.size(); i++){
            ret[i] = one.get(i)*two.get(i);
        }
        return ret;
    }
}

class Layer{
    ArrayList<Neuron> n;
    public Layer(ArrayList<Neuron> n){
        this.n = n;
    }
}


public class ANNMain extends DataReader {
    //layers for the entire network
    public ArrayList<Layer> NEURAL_NETWORK = new ArrayList<Layer>();
    //weights for the entire network
    public ArrayList<ArrayList<ArrayList<Double>>> WEIGHTS = new ArrayList<ArrayList<ArrayList<Double>>>();
    //biases for the entire network
    public ArrayList<ArrayList<Double>> BIASES = new ArrayList<ArrayList<Double>>();

    double[][] WEIGHT_GRADIENT = new double[1001][1001];
    double[][] BIAS_GRADIENT = new double[1001][1001];
    ArrayList<double[]> NODE_GRADIENT = new ArrayList<double[]>();

    //for testing stuff
    public static ArrayList<double[]> raw_layers = new ArrayList<double[]>();
    public static ArrayList<double[]> raw_weights = new ArrayList<double[]>();
    public static double[][] weights;

    public ANNMain() {
        this.NEURAL_NETWORK = new ArrayList<Layer>();
        weights = new double[1001][1001];
        raw_weights = new ArrayList<double[]>();
    }

    public static double dp(ArrayList<Neuron> one, ArrayList<Double> two) {
        double r = 0;
        for (int i = 0; i < one.size(); i++) {
            r += (one.get(i).ACTIVATION_VALUE * two.get(i));
        }
        return r;
    }

    public Layer custom_dot_product(Layer l, int idx, int KEY) {
        ArrayList<Neuron> neurons = l.n;;
        ArrayList<Neuron> ret = new ArrayList<Neuron>();
        //System.out.println("INDEX " + idx);
        ArrayList<ArrayList<Double>> weights = this.WEIGHTS.get(idx);

        ArrayList<Double> biases = BIASES.get(idx + 1);
        int bias_idx = 0;
        for (ArrayList<Double> wt : weights) {
            double e = dp(l.n, wt) + biases.get(bias_idx);
            //for(Neuron nm : l.n){System.out.print(nm.RAW_VALUE + " ");}System.out.println();
            //for(Neuron nm : l.n){System.out.print(nm.ACTIVATION_VALUE + " ");}System.out.println();
            //for(Double d : wt){System.out.print(d + " ");}System.out.println();
            //System.out.println("---------------------------------------------------");
            double d = 0;
            ret.add(new Neuron(e, biases.get(bias_idx), KEY));
            bias_idx++;
        }
        return new Layer(ret);
    }

    public Layer custom_dot_product(Layer l, int idx) {
        ArrayList<Neuron> neurons = l.n;
        //System.out.println("CURRENT LAYER: ");
        //for(Neuron inp: neurons){System.out.print(inp.RAW_VALUE + " ");}System.out.println();

        ArrayList<Neuron> ret = new ArrayList<Neuron>();
        ArrayList<ArrayList<Double>> weights = this.WEIGHTS.get(idx);

        ArrayList<Double> biases = BIASES.get(idx + 1);
        int bias_idx = 0;
        for (ArrayList<Double> wt : weights) {
            double e = dp(l.n, wt) + biases.get(bias_idx);
            double d = Neuron.sigmoid(e);
            bias_idx++;
            ret.add(new Neuron(e, d));
        }
        return new Layer(ret);
    }


    public static double[][] dotprodraw(double[][] raw_inputs, ArrayList<double[]> weights, double[] bias) {
        int idx = 0;
        double[][] ret = new double[weights.size()][weights.size()];
        System.out.println("SIZE: " + raw_inputs.length);
        System.out.println("SIZE: " + raw_inputs[0].length);
        for (int i = 0; i < raw_inputs.length; i++) {
            double[] d = raw_inputs[i];
            for (int j = 0; j < weights.size(); j++) {
                double[] e = weights.get(j);
                System.out.println(ret[i][j]);
            }
        }
        System.out.println("exit");
        return ret;
    }

    //create a neural net architecture which has layers of size layer_sizes[i] for i from 0 to layer_sizes.size()
    public void INITIALIZE_NEURAL_NET(int[] layer_sizes) {
        for (int i = 0; i < layer_sizes.length; i++) {
            ArrayList<Neuron> n = new ArrayList<Neuron>(layer_sizes[i]);
            Layer l = new Layer(n);
            this.NEURAL_NETWORK.add(l);
        }
    }

    public void SET_WEIGHTS_AND_BIASES(){
        ArrayList< ArrayList<Double> > one = new ArrayList< ArrayList<Double> >();
        ArrayList< ArrayList<Double> > two = new ArrayList< ArrayList<Double> >();
        ArrayList< ArrayList<Double> > three = new ArrayList< ArrayList<Double> >();
        for(int i = 0; i < ANNRunner.weight_matrix_one.length; i++){
            ArrayList<Double> d = new ArrayList<Double>();
            for(int j = 0; j < ANNRunner.weight_matrix_one.length; j++){
                d.add(ANNRunner.weight_matrix_one[i][j]);
                System.out.print(ANNRunner.weight_matrix_one[i][j] + " ");
            }
            System.out.println();
            one.add(d);
        }
        System.out.println("---------------------------");
        for(int i = 0; i < ANNRunner.weight_matrix_two.length; i++){
            ArrayList<Double> d = new ArrayList<Double>();
            for(int j = 0; j < ANNRunner.weight_matrix_two.length; j++){
                d.add(ANNRunner.weight_matrix_two[i][j]);
                System.out.print(ANNRunner.weight_matrix_two[i][j] + " ");
            }
            System.out.println();
            two.add(d);
        }
        System.out.println("---------------------------------------");
        for(int i = 0; i < ANNRunner.weight_matrix_three.length; i++){
            ArrayList<Double> d = new ArrayList<Double>();
            for(int j = 0; j < ANNRunner.weight_matrix_three.length; j++){
                d.add(ANNRunner.weight_matrix_three[i][j]);
                System.out.print(ANNRunner.weight_matrix_three[i][j] + " ");
            }
            System.out.println();
            three.add(d);
        }
        System.out.println("---------------------------------------");
        this.WEIGHTS.add(one);
        this.WEIGHTS.add(two);
        this.WEIGHTS.add(three);
        for(int i = 0; i < 4; i++) {
            ArrayList<Double> bias = new ArrayList<Double>();
            for(int j = 0; j < 4; j++){
                bias.add(1.0);
            }
            BIASES.add(bias);
        }
    }

    public Layer MODIFIED_FEEDFORWARD(Layer seed, int sz){
        Layer l = seed;
        int amt = 1;
        while(amt < sz){
            l = custom_dot_product(l, amt - 1, amt);
            for(Neuron neu : l.n){System.out.print(neu.ACTIVATION_VALUE + " " );}System.out.println();
            ++amt;
        }
        return l;
    }


    //method for randomizing the weights and biases for our ANN
    public void RANDOMIZE_NEURAL_NET(int[] layer_sizes) {
        System.out.println("SIZE: " + this.NEURAL_NETWORK.size());
        System.out.println("-----------------------------------------------------");
        for (int i = 1; i < this.NEURAL_NETWORK.size(); i++) {
            ArrayList<ArrayList<Double>> LAYER_WEIGHTS = new ArrayList<ArrayList<Double>>();
            for (int k = 0; k < layer_sizes[i]; k++) {
                ArrayList<Double> d = new ArrayList<Double>();
                for (int j = 0; j < layer_sizes[i - 1]; j++) {
                    double a = (double) (Math.random());
                    double sign = (Math.random());
                    if (sign >= 0.5) {
                        a *= -1;
                    }
                    d.add(a);
                }
                LAYER_WEIGHTS.add(d);
            }
            /*
            for(ArrayList<Double> ai : LAYER_WEIGHTS){
                for(double d : ai){
                    //System.out.print(d + " ");
                }
                //System.out.println();
            }

             */
            this.WEIGHTS.add(LAYER_WEIGHTS);
        }
        //System.out.println("-----------------------------------------------------");
        //input layer shouldnt have any biases (only for internal neurons)

        for (int i = 0; i < layer_sizes.length; i++) {
            ArrayList<Double> tmp = new ArrayList<Double>();
            for (int j = 0; j < layer_sizes[i]; j++) {
                double s = Math.random();
                double sign = Math.random();
                if (sign >= 0.5) {
                    s *= -1;
                }
                tmp.add(s);
            }
            BIASES.add(tmp);
        }
    }


    public Layer FEEDFORWARD(Layer seed) {
        System.out.println("NETWORK SIZE: " + this.NEURAL_NETWORK.size());
        for(ArrayList< ArrayList<Double> > d : this.WEIGHTS){
            for(ArrayList<Double> e : d){
                for(double f : e){
                    System.out.print(f + " ");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
        System.out.println();
        for(ArrayList<Double> d : BIASES){
            for(double e : d){
                System.out.print(e + " ");
            }
            System.out.println();
        }
        Layer s_v = seed;
        int i = 1;
        while (true) {
            if (i == this.NEURAL_NETWORK.size()) {
                break;
            }
            s_v = custom_dot_product(s_v, i - 1);
            ++i;
        }
        System.out.println("EXIT");
        return s_v;
    }

    public static ArrayList<Double> ONE_HOT_ENCODING(int idx, int LIM) {
        ArrayList<Double> d = new ArrayList<Double>();
        for (int i = 1; i <= LIM; i++) {
            if (i == idx) {
                d.add(1.0);
            } else {
                d.add(0.0);
            }
        }
        return d;
    }


    //should produce a matrix representing the gradients for our weights and activations
    //THIS BACKPROP IMPL IS INCOMPLETE
    //https://stats.stackexchange.com/questions/5363/backpropagation-algorithm
    public void BACKPROPAGATION(Layer expected_input) {
        //calculate partials with respect to activations and weights
        int ptr = this.NEURAL_NETWORK.size() - 1;
        while (ptr > 0) {
            Layer ed = FEEDFORWARD(expected_input);
            ArrayList<Double> output = Neuron.softmax(ed.n);
            int expected_output = STORE_LABELS.get(expected_input);
            ArrayList<Double> optim = ONE_HOT_ENCODING(expected_output, 10);
            ArrayList<Double> error = Neuron.FIN_LAYER_ERROR(output, optim);
            if (ptr == this.NEURAL_NETWORK.size() - 1) {
                ArrayList<Double> give = Neuron.SIGLAYER(ed.n);
                double[] nxt = Neuron.HADAMARD_PRODUCT(error, give);
                NODE_GRADIENT.add(nxt);
                --ptr;
            } else {
                double[] last_gradient = NODE_GRADIENT.get(NODE_GRADIENT.size() - 1);
                ArrayList< ArrayList<Double> > weight_col = WEIGHTS.get(ptr);
                double[][] P_ONE = new double[weight_col.size()][weight_col.get(0).size()];
                for(int i = 0; i < weight_col.size(); i++){
                    ArrayList<Double> tmp = weight_col.get(i);
                    for(int j = 0; j < weight_col.get(0).size(); i++){
                        P_ONE[i][j] = tmp.get(j);
                    }
                }
                //double[][] trans = Neuron.TRANSPOSE_MATRIX(weight_col);
                //in the way that i encoded the weights in the NN, the transpose is already taken
                ArrayList<Double> f = Neuron.SIGLAYER(this.NEURAL_NETWORK.get(ptr).n);
                double[] prod = Neuron.MATMUL(P_ONE, last_gradient);
                NODE_GRADIENT.add(Neuron.HADAMARD_PRODUCT((ArrayList)Arrays.asList(prod), f));
                --ptr;
            }

        }

    }
}


