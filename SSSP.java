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

class NodeObj{
    int nd;
    int cost;
    public NodeObj(int nd, int cost){
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

class SSSP_Runner{
    public static LinkedList<NodeObj> adj[];
    public static HashMap<Integer,Boolean> vis;
    public static HashMap<Edge,Integer> weights;
    public static HashMap<Integer, Integer> intermediate_sp;
    public static int[] predecessor;
    public static int[] ucs_predecessor;
    public static PriorityQueue<NodeObj> pq;
    public SSSP_Runner(int n_nodes){
        adj = new LinkedList[n_nodes];
        weights = new HashMap<Edge,Integer>();
        vis = new HashMap<Integer,Boolean>();
        intermediate_sp = new HashMap<Integer,Integer>();
        for(int i = 0; i < n_nodes; i++){
            adj[i] = new LinkedList();
        }
        predecessor = new int[n_nodes];
        ucs_predecessor = new int[n_nodes];
        for(int i = 0; i < n_nodes; i++){
            predecessor[i] = -1;
            ucs_predecessor[i] = -1;
        }
        pq = new PriorityQueue<NodeObj>(new NodeComparator());
    }
    public void addEdge(int u, int v, int w){
        adj[u].add(new NodeObj(v,w));
        //adj[v].add(new NodeObj(u,w));
        //System.out.println(u + " " + v);
        //System.out.println(v + " " + u);
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
            ans.add(pt);
            pt = ucs_predecessor[pt];
        }
        Collections.reverse(ans);
        return ans;
    }

    public static void UCS(NodeObj startNode){
        pq.add(startNode);
        ArrayList<Integer> seen = new ArrayList<Integer>();
        while(pq.size() > 0){
            NodeObj tmp = pq.poll();
            System.out.println(tmp.nd + " " + tmp.cost);
            LinkedList<NodeObj> adj_list = (LinkedList<NodeObj>)(adj[tmp.nd].clone());
            if(!seen.contains(tmp.nd)) {
                seen.add(tmp.nd);
            }
            int prev_nd = 0;
            int relax_v = 100000000;
            while(!adj_list.isEmpty()) {
                NodeObj tp = adj_list.poll();
                int wt = tp.cost;
                NodeObj addend = new NodeObj(tp.nd, tmp.cost + wt);
                relax_v = Math.min(relax_v, tmp.cost + wt);
                int cst = tmp.cost + wt;
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
    public static void main(String[] args) {
        SSSP_Runner g = new SSSP_Runner(25);

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

        int nd = 7;
        int goal = 10;

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
        System.out.println("UCS WEIGHT: ");
        System.out.println(g.intermediate_sp.get(goal));
        g.ucs_predecessor[nd] = -1;
        System.out.println("UCS OPTIMAL WEIGHTED PATH: ");
        ArrayList<Integer> wt_path =  g.optimal_path_ucs(goal);
        for(Integer i : wt_path){
            System.out.print(i + " ");
        }
        System.out.println();

    }

    //SHORTEST UNWEIGHTED PATH FROM PHILLY TO POTTSTOWN: 0,1,8,9,18
    //SHORTEST WEIGHTED PATH TO PHILLY TO POTTSTOWN: 40, 0,2,6,8,9,18
    //SHORTEST UNWEIGHTED PATH FROM NEWTOWN SQUARE TO ST. PETERS: 5,11,9,15,17(technically same length as weighted path)
    //SHORTEST WEIGHTED PATH FROM NEWTOWN SQUARE TO ST. PETERS: 33, 5,11,14,15,17
    //SHORTEST UNWEIGHTED PATH FROM NORTH WALES TO CHADSFORD: 7,1,0,3,10
    //SHORTEST WEIGHED PATH FROM NORTH WALES TO CHADSFORD: 41, 7,1,6,5,3,10

}
