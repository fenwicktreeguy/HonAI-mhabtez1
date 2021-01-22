import java.util.*;

class Edge{
    int a, b, w;
    public Edge(int a, int b){
        this.a=a;
        this.b=b;
        this.w=1;
    }
    public Edge(int a, int b, int w){
        this.a=a;
        this.b=b;
        this.w=w;
    }
}

class Location{
    int x;
    int y;
    public Location(int x, int y){
        this.x=x;
        this.y=y;
    }
    public static int euclidean_distance(Location loc1, Location loc2){
        return (int)(Math.sqrt( Math.pow(loc1.x-loc2.x,2) + Math.pow(loc1.y-loc2.y,2) ));
    }
}

class NodeObj{
    int nd;
    double cost;
    double cost_goal;
    public NodeObj(int nd, double cost, double cost_goal){
        this.nd=nd;
        this.cost=cost;
        this.cost_goal=cost_goal;
    }
    public NodeObj(int nd, double cost){
        this.nd=nd;
        this.cost=cost;
    }
}

class NodeVis{
    int nd;
    double cost;
    public NodeVis(int nd, double cost){
        this.nd=nd;
        this.cost=cost;
    }
}

class NodeComparator implements Comparator<NodeObj>{
    public int compare(NodeObj one, NodeObj two){
        if(one.cost < two.cost){
            return 1;
        } else if(one.cost == two.cost){
            return 0;
        }
        return -1;
    }
}

class NodeComparatorTwo implements Comparator<NodeObj>{
    public int compare(NodeObj one, NodeObj two) {
        if (one.cost + one.cost_goal < two.cost + two.cost_goal) {
            return 1;
        } else if (one.cost + one.cost_goal < two.cost + two.cost_goal) {
            return 0;
        }
        return -1;
    }
}

class SSSP_Runner{
    public static LinkedList<NodeObj> adj[];
    public static HashMap<Integer,Boolean> vis;
    public static HashMap<Integer, Double> intermediate_sp;
    public static HashMap<Integer, Double> intermediate_sp_heuristic;
    public static int[] predecessor;
    public static int[] ucs_predecessor;
    public static int[] astar_predecessor;
    public static PriorityQueue<NodeObj> pq;
    public static PriorityQueue<NodeObj> pq_two;
    //specifically for A* algorithm animation
    public static HashMap<Integer, Location> animation_procedure;
    public SSSP_Runner(int n_nodes){
        adj = new LinkedList[n_nodes];
        vis = new HashMap<Integer,Boolean>();
        intermediate_sp = new HashMap<Integer,Double>();
        intermediate_sp_heuristic = new HashMap<Integer, Double>();
        for(int i = 0; i < n_nodes; i++){
            adj[i] = new LinkedList();
        }
        predecessor = new int[n_nodes];
        ucs_predecessor = new int[n_nodes];
        astar_predecessor = new int[n_nodes];
        for(int i = 0; i < n_nodes; i++){
            predecessor[i] = -1;
            ucs_predecessor[i] = -1;
        }
        pq = new PriorityQueue<NodeObj>(new NodeComparator());
        pq_two = new PriorityQueue<NodeObj>(new NodeComparatorTwo() );
        animation_procedure = new HashMap<Integer,Location>();
    }
    public void addEdge(int u, int v, int w){
        adj[u].add(new NodeObj(v,w));
        //adj[v].add(new NodeObj(u,w));
        //System.out.println(u + " " + v);
        //System.out.println(v + " " + u);
    }

    public void addEdge(int u, int v, double w, double heuristic){
        adj[u].add(new NodeObj(v,w,heuristic));
    }
    //returns order of nodes in bfs
    public static ArrayList<Integer> optimal_path(int end_node){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        int pt = predecessor[end_node];
        ans.add(end_node);
        while(pt != -1){
            ans.add(pt);
            pt = predecessor[pt];
        }
        Collections.reverse(ans);
        return ans;
    }

    public static ArrayList<Integer> optimal_path_ucs(int end_node){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        int pt = ucs_predecessor[end_node];
        ans.add(end_node);
        while(pt != -1){
            System.out.println(pt);
            ans.add(pt);
            pt = ucs_predecessor[pt];
        }
        Collections.reverse(ans);
        return ans;
    }
    public static ArrayList<Integer> optimal_path_astar(int end_node){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        int pt = astar_predecessor[end_node];
        ans.add(end_node);
        while(pt != -1){
            ans.add(pt);
            //System.out.println("PATH: " + pt);
            pt = astar_predecessor[pt];
        }
        Collections.reverse(ans);
        return ans;
    }

    public static void UCS(NodeObj startNode){
        pq.add(startNode);
        int amt = 0;
        ArrayList<Integer> seen = new ArrayList<Integer>();
        while(pq.size() > 0){
            ++amt;
            NodeObj tmp = pq.poll();
            LinkedList<NodeObj> adj_list = (LinkedList<NodeObj>)(adj[tmp.nd].clone());
            if(!seen.contains(tmp)) {
                seen.add(tmp.nd);
            } else {
                continue;
            }
            System.out.println(tmp.nd + " " + tmp.cost);
            int prev_nd = 0;
            double relax_v = 100000000;
            while(!adj_list.isEmpty()) {
                NodeObj tp = adj_list.poll();
                double wt = tp.cost;
                NodeObj addend = new NodeObj(tp.nd, tmp.cost + wt);
                relax_v = Math.min(relax_v, tmp.cost + wt);
                double cst = tmp.cost + wt;
                if(!intermediate_sp.containsKey(tp.nd)) {
                    intermediate_sp.put(tp.nd, tmp.cost + wt);
                    ucs_predecessor[tp.nd] = tmp.nd;
                    pq.add(addend);
                } else {
                    intermediate_sp.put(tp.nd, Math.min(intermediate_sp.get(tp.nd), cst));
                    if(Math.min(intermediate_sp.get(tp.nd), cst)==cst){
                        ucs_predecessor[tp.nd] = tmp.nd;
                        pq.add(addend);
                    }
                }
            }
        }
        System.out.println("AMOUNT: " + amt);
    }
    public static boolean is_contained(ArrayList<NodeObj> a, NodeObj comp){
        for(NodeObj n : a){
            if(n.cost == comp.cost && n.cost_goal == comp.cost_goal && n.nd == comp.nd){
                return true;
            }
        }
        return false;
    }

    public static void astar(NodeObj start_node, NodeObj goal_node){
        pq_two.add(start_node);
        HashMap<NodeObj,Boolean> seen = new HashMap<NodeObj,Boolean>();
        ArrayList<NodeObj> seen_two = new ArrayList<NodeObj>();
        int amt = 0;
        while(pq_two.size() > 0){
            ++amt;
            NodeObj tmp = pq_two.poll();

            if(!is_contained(seen_two,tmp)){
                seen_two.add(tmp);
            } else {
                continue;
            }

            if(amt % 900 == 0) {
                //System.out.println(tmp.nd + " " + tmp.cost + " " + tmp.cost_goal);
            }
            LinkedList<NodeObj> adj_list = adj[tmp.nd];
            double relax_v = 100000000;
            for(int i = 0; i < adj_list.size(); i++){
                NodeObj tp = adj_list.get(i);
                double wt = tp.cost;
                NodeObj addend = new NodeObj(tp.nd, tmp.cost + wt, tp.cost_goal);
                relax_v = Math.min(relax_v, tmp.cost + wt);
                double  cst = tmp.cost + wt + tmp.cost_goal;
                if(!intermediate_sp_heuristic.containsKey(tp.nd)) {
                    intermediate_sp_heuristic.put(tp.nd, tmp.cost + wt + tmp.cost_goal);
                    astar_predecessor[tp.nd] = tmp.nd;
                    pq_two.add(addend);
                } else {
                    intermediate_sp_heuristic.put(tp.nd, Math.min(intermediate_sp_heuristic.get(tp.nd), cst));
                    if(Math.min(intermediate_sp_heuristic.get(tp.nd), cst)==cst){
                        astar_predecessor[tp.nd] = tmp.nd;
                        pq_two.add(addend);
                    }
                }
            }
        }
        System.out.println("OPTIMAL PATH LENGTH: " + intermediate_sp_heuristic.get(goal_node.nd));
    }

    public static ArrayList<Integer> bfs(int start_node){
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(start_node);
        ArrayList<Integer> ret = new ArrayList<Integer>();
        while(q.size() > 0){
            int t = q.poll();
            ret.add(t);
            vis.put(t,true);
            LinkedList<NodeObj> ar = adj[t];
            for(NodeObj j : ar){
                if(!vis.containsKey(j.nd)){
                    predecessor[j.nd]=t;
                    q.add(j.nd);
                    vis.put(j.nd,true);
                }
            }
        }
        vis.clear();
        return ret;
    }
    public static ArrayList<Integer> dfs(int start_node){
        Stack<Integer> q = new Stack<Integer>();
        q.add(start_node);
        ArrayList<Integer> ret = new ArrayList<Integer>();
        while(q.size() > 0){
            //System.out.println(q.size());
            int t =q.pop();
            ret.add(t);
            vis.put(t,true);
            LinkedList<NodeObj> ar = adj[t];
            for(NodeObj j : ar){
                if(!vis.containsKey(j.nd)){
                    q.push(j.nd);
                    vis.put(j.nd,true);
                }
            }
        }
        vis.clear();
        return ret;
    }

}

public class SSSP {
    public static int nd = 1;
    public static int goal = 800;
    static int[][] grid = {
            {0, 1, 2, 3,-1,-1, 4, 5},
            {6, 7, 8, 9,-1,10,11,12},
            {-1,-1,13,14,-1,15,16,17},
            {-1,-1,18,19,20,21,22,-1},
            {36,23,24,25,26,27,-1,-1},
            {28,-1,-1,29,30,31,32,-1}
    };
    static int[][] expanded_grid;

    static int dx[] = {1,0,-1,0};
    static int dy[] = {0,1,0,-1};
    static int UNIFORM_WEIGHT= 1;
    static int MAXIMUM_WEIGHT = 1000000000;
    static double[][] global_height_mp;
    static ArrayList<Location> save_optimum = new ArrayList<Location>();
    static ArrayList<Double> height_range = new ArrayList<Double>();

    //make height map out of grid (use some color scale to indicate height in 2D map
    public static double[][] augment_grid(double[] poly_coeffs_x, double[] poly_coeffs_y, double[] neg_x, double[] neg_y, int constant){
        double[][] grid_heightmap = new double[expanded_grid.length][expanded_grid[0].length];
        int SHIFT_X = expanded_grid.length/2;
        int SHIFT_Y = expanded_grid[0].length/2;
        for(int i = 0; i < expanded_grid.length; i++){
            for(int j = 0; j < expanded_grid[0].length; j++){
                for(int k = 0; k < poly_coeffs_x.length; k++) {
                    grid_heightmap[i][j] += (double)(poly_coeffs_x[k] * Math.pow((i-SHIFT_X),k+1));
                }
                for(int k = 0; k < poly_coeffs_y.length; k++){
                    grid_heightmap[i][j] += (double)(poly_coeffs_y[k] * Math.pow((j-SHIFT_Y),k+1));
                }
                /*
                for(int k = 0; k < neg_x.length; k++){
                    grid_heightmap[i][j] += (double)(neg_x[k] * Math.pow((i-SHIFT_X),k-1));
                }
                for(int k = 0; k < neg_y.length; k++){
                    grid_heightmap[i][j] += (double)(neg_y[k] * Math.pow((j-SHIFT_Y),k-1));
                }
                 */
                height_range.add(grid_heightmap[i][j]);
            }
        }
        Collections.sort(height_range);
        return grid_heightmap;
    }

    public static float normalize_color(double value){
        double MAX = height_range.get(height_range.size()-1);
        double MIN = height_range.get(0);
        return (float)(value - MIN)/(float)(MAX - MIN);
    }

    public static int[][] expand_grid(int[][] param, int factor){
        int P_X =param.length;
        int P_Y = param[0].length;
        int[][] retval= new int[( (P_X) * (factor)) ][( (P_Y) * (factor)) ];

        System.out.println(retval.length + " " + retval[0].length);
        int PTR_X = 0;
        int PTR_Y = 0;
        int CURRENT_VALUE = 1;

        for(int i = 0; i < retval.length; i++){
            for(int j = 0; j < retval[0].length; j++){
                retval[i][j]=-1;
            }
        }

        for(int i = 0; i < P_Y; i++){
            for(int j = 0; j < P_X; j++){
                for(int k = PTR_X; k < PTR_X+(factor); k++){
                    for(int l = PTR_Y; l < PTR_Y + (factor); l++){
                        System.out.println(k + " " + l);
                        if(param[j][i] == -1) {
                            retval[k][l] = param[j][i];
                        } else {
                            retval[k][l] = CURRENT_VALUE;
                            ++CURRENT_VALUE;
                        }
                    }
                }
                System.out.println("----------------");
                PTR_X += factor;
            }
            PTR_Y += factor;
            PTR_X=0;

        }

        for(int i = 0; i < retval.length; i++){
            for(int j = 0; j < retval[0].length; j++){
                System.out.print(retval[i][j] + " ");
            }
            System.out.println();
        }
        return retval;
    }

    public static ArrayList<Integer> develop_graph(){
        SSSP_Runner g = new SSSP_Runner(120000);
        double[] poly_coeffs_x = {0,0.5};//coeffs are constants for polys x,x^2,x^3....
        double[] poly_coeffs_y = {0,0.5};//coeffs are constants for y,y^2,y^3,...
        double[] poly_coeffs_neg_x = {0};//coeffs are constants 1/x, 1/(x^2), 1/(x^3)...
        double[] poly_coeffs_neg_y = {0};//coeffs are constants for 1/y, 1/(y^2), 1/(y^3)...
        int constant = 4;
        expanded_grid = expand_grid(grid,5);
        double[][] heightmap = augment_grid(poly_coeffs_x,poly_coeffs_y,poly_coeffs_neg_x,poly_coeffs_neg_y,constant);
        global_height_mp = heightmap;
        for(int i = 0; i < expanded_grid.length; i++){
            for(int j = 0; j < expanded_grid[0].length; j++){
                if(expanded_grid[i][j] == -1){
                    continue;
                }
                for(int k = 0; k < 4; k++) {
                    boolean pos = true;
                    if (i + dx[k] >= expanded_grid.length || j + dy[k] >= expanded_grid[0].length){
                        pos=false;
                    }
                    if(i + dx[k] < 0 || j + dy[k] < 0){
                        pos = false;
                    }
                    if(pos){
                        //System.out.println("ONE: " + i + " " + j);
                        //System.out.println("TWO: " + (i+dx[k]) + " " + (j+dy[k]) );
                        Location one = new Location(i,j);
                        Location two = new Location(i+dx[k],j+dy[k]);
                        int val = Location.euclidean_distance(one,two);
                        //adding 1 so i can add -1 into the graph class (shift down values by one in optimal path)
                        //System.out.println("EDGE: " + grid[i][j] + " " + grid[i+dx[k]][j+dy[k]]);
                        /*
                        if(grid[i+dx[k]][j+dy[k]] != -1) {
                            int wt = Math.max(1,Math.abs(heightmap[i][j] - heightmap[i+dx[k]][j+dy[k]]));
                            g.addEdge(grid[i][j], grid[i + dx[k]][j + dy[k]], UNIFORM_WEIGHT, val);
                            System.out.println("EDGE: " + grid[i][j] + " " + grid[i+dx[k]][j+dy[k]] + " " + UNIFORM_WEIGHT);
                        } else {
                            continue;
                         }
                         */
                        if(expanded_grid[i+dx[k]][j+dy[k]] != -1) {
                           double wt = Math.max(1,Math.abs(heightmap[i][j] - heightmap[i+dx[k]][j+dy[k]]));
                            g.addEdge(expanded_grid[i][j], expanded_grid[i + dx[k]][j + dy[k]], wt, val);
                            System.out.println("EDGE: " + expanded_grid[i][j] + " " + expanded_grid[i+dx[k]][j+dy[k]] + " " + wt);
                        } else {
                            continue;
                        }
                    }
                }
            }
        }

        g.astar(new NodeObj(nd,0), new NodeObj(goal,0));
        g.astar_predecessor[nd] = -1;
        ArrayList<Integer> ar = g.optimal_path_astar(goal);
        for(Integer i : ar){
            System.out.print(i + " " );
        }
        System.out.println();
        return ar;
    }

    public static void main(String[] args) {
        /*
        g.addEdge(0, 3, 118, 329);
        g.addEdge(0, 4, 140, 253);

        g.addEdge(1, 0, 75, 366);
        g.addEdge(1, 2, 71, 380 );

        g.addEdge(2, 1, 71, 374);
        g.addEdge(2, 4, 151, 253);

        g.addEdge(3, 0, 118, 366);
        g.addEdge(3, 5, 111, 244);

        g.addEdge(4, 2, 151, 380);
        g.addEdge(4, 0, 140, 366);
        g.addEdge(4, 8, 80, 193);
        g.addEdge(4, 9, 99, 176);

        g.addEdge(5, 3, 111, 329);
        g.addEdge(5, 6, 70, 241);

        g.addEdge(6, 5, 70, 244);
        g.addEdge(6, 7, 75, 242);

        g.addEdge(7, 6, 75, 241);
        g.addEdge(7, 11, 120, 160);

        g.addEdge(8, 4, 80, 253);
        g.addEdge(8,  11, 146, 160);
        g.addEdge(8, 10, 97,100);


        g.addEdge(9, 4, 99, 253);
        g.addEdge(9, 12, 211, 0);


        g.addEdge(10, 8, 97, 193);
        g.addEdge(10, 11, 138, 160);
        g.addEdge(10, 12, 101, 0);

        g.addEdge(11, 10, 138, 100);
        g.addEdge(11, 8, 146, 193);
        g.addEdge(11, 7, 120, 242);
        g.addEdge(12, 10, 101, 100);

        g.addEdge(12, 9, 211, 176);
        g.addEdge(13, 12, 85, 0);
        g.addEdge(12, 13, 85, 80);

        g.addEdge(14, 12, 90, 0);
        g.addEdge(12, 14, 90, 90);

        /*
        g.addEdge(0, 2, 8);
        g.addEdge(0, 1, 18);
        g.addEdge(0, 3, 15);
        g.addEdge(0, 4, 14);
        g.addEdge(1, 0, 18);
        g.addEdge(1, 4, 13);
        g.addEdge(1, 6, 8);
        g.addEdge(1, 8, 7);
        g.addEdge(1, 7, 10);
        g.addEdge(2, 0, 8);
        g.addEdge(2, 5, 8);
        g.addEdge(2, 6, 6);
        g.addEdge(3, 0, 15);
        g.addEdge(3, 5, 5);
        g.addEdge(3, 10, 12);
        g.addEdge(4, 0, 14);
        g.addEdge(4, 1, 13);
        g.addEdge(4, 7, 10);
        g.addEdge(5, 2, 8);
        g.addEdge(5, 3, 5);
        g.addEdge(5, 6, 6);
        g.addEdge(5, 11, 8);
        g.addEdge(5, 13, 11);
        g.addEdge(6, 1, 8);
        g.addEdge(6, 2, 6);
        g.addEdge(6, 5, 6);
        g.addEdge(6, 8, 5);
        g.addEdge(6, 11, 8);
        g.addEdge(7, 1, 10);
        g.addEdge(7, 4, 10);
        g.addEdge(8, 1, 7);
        g.addEdge(8, 6, 5);
        g.addEdge(8, 9, 9);
        g.addEdge(8, 12, 11);
        g.addEdge(9, 8, 9);
        g.addEdge(9, 11, 8);
        g.addEdge(9, 12, 5);
        g.addEdge(9, 15, 11);
        g.addEdge(9, 18, 12);
        g.addEdge(10, 3, 12);
        g.addEdge(10, 13, 9);
        g.addEdge(11, 5, 8);
        g.addEdge(11, 6, 8);
        g.addEdge(11, 9, 8);
        g.addEdge(11, 14, 7);
        g.addEdge(12, 8, 11);
        g.addEdge(12, 9, 5);
        g.addEdge(12, 18, 12);
        g.addEdge(13, 5, 11);
        g.addEdge(13, 10, 9);
        g.addEdge(13, 14, 5);
        g.addEdge(13, 16, 7);
        g.addEdge(14, 11, 7);
        g.addEdge(14, 13, 5);
        g.addEdge(14, 16, 5);
        g.addEdge(14, 15, 9);
        g.addEdge(15, 9, 11);
        g.addEdge(15, 14, 9);
        g.addEdge(15, 17, 9);
        g.addEdge(16, 13, 7);
        g.addEdge(16, 14, 5);
        g.addEdge(17, 15, 9);
        g.addEdge(17, 18, 8);
        g.addEdge(18, 12, 12);
        g.addEdge(18, 9, 12);
        g.addEdge(18, 17, 8);
         */

        develop_graph();

        /*
        int nd = 0;
        int goal = 14;

        ArrayList<Integer> one = g.bfs(nd);
        ArrayList<Integer> two = g.dfs(nd);
        ArrayList<Integer> three = g.optimal_path(goal);
        System.out.println("BFS: ");
        for(Integer i  :one) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("DFS: ");
        for(Integer i : two){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("BFS OPTIMAL PATH: ");
        for(Integer i : three){
            System.out.print(i + " ");
        }
        System.out.println();



        g.UCS(new NodeObj(nd,0));
        g.astar(new NodeObj(nd,0), new NodeObj(goal, 0));
        System.out.println("UCS WEIGHT: ");
        System.out.println(g.intermediate_sp.get(goal));
        System.out.println("A STAR WEIGHT: ");
        System.out.println(g.intermediate_sp_heuristic.get(goal));
        g.ucs_predecessor[nd] = -1;
        g.astar_predecessor[nd]= -1;
        System.out.println("UCS OPTIMAL WEIGHTED PATH: ");
        ArrayList<Integer> wt_path =  g.optimal_path_ucs(goal);
        ArrayList<Integer> wt_path_two = g.optimal_path_astar(goal);

        for(Integer i : wt_path){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("ASTAR OPTIMAL WEIGHTED PATH");
        for(Integer i : wt_path_two){
            System.out.print(i + " ");
        }
        System.out.println();
        */
    }

    //SHORTEST UNWEIGHTED PATH FROM PHILLY TO POTTSTOWN: 0,1,8,9,18
    //SHORTEST WEIGHTED PATH TO PHILLY TO POTTSTOWN: 40, 0,2,6,8,9,18
    //SHORTEST UNWEIGHTED PATH FROM NEWTOWN SQUARE TO ST. PETERS: 5,11,9,15,17
    //SHORTEST WEIGHTED PATH FROM NEWTOWN SQUARE TO ST. PETERS: 33, 5,11,14,15,17
    //SHORTEST UNWEIGHTED PATH FROM NORTH WALES TO CHADSFORD: 7,1,0,3,10
    //SHORTEST WEIGHED PATH FROM NORTH WALES TO CHADSFORD: 41, 7,1,6,5,3,10

}
