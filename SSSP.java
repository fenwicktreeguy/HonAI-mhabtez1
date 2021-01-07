import java.util.*;

class Edge{
    int a, b, w;
    public Edge(int a, int b){
        this.a=a;
        this.b=b;
        this.w=1;
    }
}
class SSSP_Runner{
    public static LinkedList<Integer> adj[];
    public static HashMap<Integer,Boolean> vis;
    public static HashMap<Edge,Integer> weights;
    public static int[] predecessor;
    public SSSP_Runner(int n_nodes){
        adj = new LinkedList[n_nodes];
        weights = new HashMap<Edge,Integer>();
        vis = new HashMap<Integer,Boolean>();
        for(int i = 0; i < n_nodes; i++){
            adj[i] = new LinkedList();
        }
        predecessor = new int[n_nodes];
        for(int i = 0; i < n_nodes; i++){
            predecessor[i] = -1;
        }
    }
    public void addEdge(int u, int v, int w){
        adj[u].add(v);
        adj[v].add(u);
        weights.put(new Edge(u, v), w);
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
        return ans;
    }

    public static ArrayList<Integer> bfs(int start_node){
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(start_node);
        ArrayList<Integer> ret = new ArrayList<Integer>();
        while(q.size() > 0){
            int t = q.poll();
            ret.add(t);
            vis.put(t,true);
            LinkedList<Integer> ar = adj[t];
            for(Integer j : ar){
                if(!vis.containsKey(j)){
                    predecessor[j]=t;
                    q.add(j);
                    vis.put(j,true);
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
            System.out.println(q.size());
            int t = q.pop();
            ret.add(t);
            vis.put(t,true);
            LinkedList<Integer> ar = adj[t];
            for(Integer j : ar){
                if(!vis.containsKey(j)){
                    q.push(j);
                    vis.put(j,true);
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
        g.addEdge(0, 3, 118);
        g.addEdge(0, 4, 140);
        g.addEdge(1, 0, 75);
        g.addEdge(1, 2, 71);

        g.addEdge(2, 1, 71);
        g.addEdge(2, 4, 151);

        g.addEdge(3, 0, 118);
        g.addEdge(3, 5, 111);

        g.addEdge(4, 2, 151);
        g.addEdge(4, 0, 140);
        g.addEdge(4, 8, 80);
        g.addEdge(4, 9, 99);

        g.addEdge(5, 3, 111);
        g.addEdge(5, 6, 70);

        g.addEdge(6, 5, 70);
        g.addEdge(6, 7, 75);

        g.addEdge(7, 6, 75);
        g.addEdge(7, 11, 120);

        g.addEdge(8, 4, 80);
        g.addEdge(8, 11, 146);
        g.addEdge(8, 10, 97);


        g.addEdge(9, 4, 99);
        g.addEdge(9, 12, 211);


        g.addEdge(10, 8, 97);
        g.addEdge(10, 11, 138);
        g.addEdge(10, 12, 101);

        g.addEdge(11, 10, 138);
        g.addEdge(11, 8, 146);
        g.addEdge(11, 7, 120);
        g.addEdge(12, 10, 101);

        g.addEdge(12, 9, 211);
        g.addEdge(13, 12, 85);
        g.addEdge(12, 13, 85);

        g.addEdge(14, 12, 90);
        g.addEdge(12, 14, 90);

        ArrayList<Integer> one = g.bfs(0);
        ArrayList<Integer> two = g.dfs(0);
        ArrayList<Integer> three = g.optimal_path(14);
        for(Integer i  :one) {
            System.out.print(i + " ");
        }
        System.out.println();
        for(Integer i : two){
            System.out.print(i + " ");
        }
        System.out.println();
        for(Integer i : three){
            System.out.print(i + " ");
        }
        System.out.println();
    }

}
