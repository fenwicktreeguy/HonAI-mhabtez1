public class BayesianRunner extends BayesianNet{
    public static void main(String[] args){
        BayesianNet b = new BayesianNet(2000);
        b.parseData();
        b.construct_decision_tree();
    }
}
