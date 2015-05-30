package com.adward;

import libsvm.svm_model;
import libsvm.svm_node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

class svm_nodes implements Serializable{
    public int index;
    public double value;

    svm_nodes(int index, double value) {
        this.index = index;
        this.value = value;
    }
}

public class Main {

    public static void main(String[] args) {
        File test_file = new File("test.txt");
        File model_file = new File("train.model");
        BufferedReader test_reader = null;
        BufferedReader model_reader = null;
        try {

            String model = "";
            model_reader = new BufferedReader(new FileReader(model_file));
            //String tmpStr = null;
            //while ((tmpStr = model_reader.readLine())!=null){
            //    model += tmpStr + '\n';
            //}
            //System.out.println(model);
            //String[] testArgs = {"test.txt", "train.model", "result.txt"};
            svm_model svmModel = libsvm.svm.svm_load_model(model_reader);

            test_reader = new BufferedReader(new FileReader(test_file));
            String[] attrs = test_reader.readLine().split(" ");
            //svm_node[] svm_nodes = {};

            //List<svm_node> list = new ArrayList<svm_node>();
            svm_node[] svm_nodes = new svm_node[270];
            for (int i=0;i<270;i++){
                //svm_node node = new svm_node();
                try {
                    svm_nodes[i] = new svm_node();
                    svm_nodes[i].index = Integer.parseInt(attrs[i+1].split(":")[0]);
                    svm_nodes[i].value = Double.parseDouble(attrs[i+1].split(":")[1]);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                //list.add(node); //shallow copy?
            }
            //svm_node[] svm_nodes = list.toArray(new svm_node[list.size()]);

            double label = libsvm.svm.svm_predict(svmModel, svm_nodes);
            System.out.println(label);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
