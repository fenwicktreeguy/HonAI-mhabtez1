import java.util.*;

public class BayesianRunner extends BayesianNet{
    public static void main(String[] args){
        BayesianNet b = new BayesianNet(2000);
        b.parseData();
        b.construct_decision_tree_entropy();
        Double[] cur = {10.03,21.28,63.19,307.3,0.08117,0.03912,0.00247,0.005159,0.163,0.06439,0.1851,1.341,1.184,11.6,0.005724,0.005697,0.002074,0.003527,0.01445,0.002411,11.11,28.94,69.92,376.3,0.1126,0.07094,0.01235,0.02579,0.2349,0.08061};
        List<Double> ra = new ArrayList<Double>(Arrays.asList(cur));

        ArrayList<String> tmp = new ArrayList<String>();
        for(int i = 0; i < ra.size(); i++){
            System.out.println(ra.get(i));
            tmp.add((Double.toString(ra.get(i))) );
        }
        b.trav(0);

        boolean HAS_CANCER = b.supervised_decision(0,tmp);
        if(HAS_CANCER){
            System.out.println("PATIENT DOES NOT HAVE CANCER");
        } else {
            System.out.println("PATIENT HAS CANCER");
        }


    }
}
