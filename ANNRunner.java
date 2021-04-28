import java.util.*;
import java.io.*;

public class ANNRunner {
    static double[][] weight_matrix_one = { {0.1, 0.3, 0.4}, {0.2, 0.2, 0.3}, {0.3, 0.7, 0.9} };
    static double[][] weight_matrix_two = { {0.2, 0.3, 0.6}, {0.3, 0.5, 0.4}, {0.5, 0.7, 0.8} };
    static double[][] weight_matrix_three = { {0.1, 0.3, 0.5}, {0.4, 0.7, 0.2}, {0.8, 0.2, 0.9} };
    public static void testData(ArrayList<Layer> n){
        Layer one = new Layer(new ArrayList<Neuron>());
        Layer two = new Layer(new ArrayList<Neuron>());
        one.n.add(new Neuron(0.1,0,0));
        one.n.add(new Neuron(0.2,0,0));
        one.n.add(new Neuron(0.7,0,0));
        two.n.add(new Neuron(1.9,0,0));
        two.n.add(new Neuron(0.6,0,0));
        two.n.add(new Neuron(0.3,0,0));
        n.add(one);
        n.add(two);
    }

    public static void TRAIN(){
        DataReader d = new DataReader();
        d.readFromFile();
        ArrayList<Layer> PREMADE_DATA = d.parsed_input;
        System.out.println("DATA READ");
        int[] layer_sizes = {785,11};
        ANNMain a = new ANNMain();
        a.INITIALIZE_NEURAL_NET(layer_sizes);
        a.RANDOMIZE_NEURAL_NET(layer_sizes);
        for(Layer l : PREMADE_DATA){
            Layer end = a.FEEDFORWARD(l);
            ArrayList<Double> SOFTMAX = Neuron.softmax(end.n);
            for(Neuron n : end.n){System.out.print(n.RAW_VALUE + " ");}System.out.println();
            System.out.print("SOFTMAX: ");
            for(Double db : SOFTMAX){System.out.print(db + " ");}System.out.println();
        }
    }

    public static void main(String[] args) {
        int[] my_layers = {3,3,3};
        ANNMain a = new ANNMain();
        a.SET_WEIGHTS_AND_BIASES();
        ArrayList<Layer> l = new ArrayList<Layer>();
        testData(l);

        //this is code for the mini-batch testing stuff (with set weights)
        int ONE = 0;
        for(Layer lp : l) {
            ArrayList<Double> NOT_SOFTMAX = new ArrayList<Double>();
            Layer lay = a.MODIFIED_FEEDFORWARD(lp, 4);
            for(int i = 0; i < lay.n.size(); i++){
                if(i==ONE){
                    NOT_SOFTMAX.add(1.0);
                } else {
                    NOT_SOFTMAX.add(0.0);
                }
            }
            ++ONE;
            //for(Neuron nl : lay.n){System.out.print(nl.ACTIVATION_VALUE + " ");}System.out.println();
            System.out.println("SOFTMAX: ");
            ArrayList<Double> SOFTMAX = Neuron.softmax(lay.n);
            for (Double d : SOFTMAX) {
                System.out.print(d + " ");
            }
            for(Double d : NOT_SOFTMAX) {
                System.out.print(d + " ");
            }
            System.out.println();
            System.out.println("CROSS ENTROPY: " + Neuron.CROSS_ENTROPY(NOT_SOFTMAX, SOFTMAX));
            System.out.println("------------------------------------");
            //System.out.println();
        }
        /*
        a.INITIALIZE_NEURAL_NET(my_layers);
        a.RANDOMIZE_NEURAL_NET(my_layers);
        ArrayList<Neuron> input = new ArrayList<Neuron>();
        input.add(new Neuron(1,0));
        input.add(new Neuron(0,0));
        input.add(new Neuron(1,0));
        Layer end = a.FEEDFORWARD(new Layer(input));
        ArrayList<Double> SOFTMAX = Neuron.softmax(end.n);
        for(Double db : SOFTMAX){System.out.print(db + " ");}System.out.println();
         */


        //TRAIN();

        /*
        System.out.println("READ DATA");
        ArrayList<Neuron> n = new ArrayList<Neuron>();
        testData(n);
        Layer l = new Layer(n);
        Layer end = a.FEEDFORWARD(l);
        ArrayList<Double> finlayer = Neuron.softmax(end.n);
        for(Double db : finlayer){System.out.print(db + " ");}System.out.println();
         */
    }
}

