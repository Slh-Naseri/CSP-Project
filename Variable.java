package cspproject;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;

public class Variable {

    String name;
    int i, j, value=0, constraint = 0,sumR=0,sumC=0;
    LinkedList<Integer> domain = new LinkedList<Integer>(Arrays.asList(9, 1, 3, 8, 7, 4, 5, 2, 6));
    LinkedList<String> consC  = new LinkedList<String>();
    LinkedList<String> consR  = new LinkedList<String>();
    public Variable(String name, int i, int j) {
        this.name = name;
        this.i = i;
        this.j = j;
    }
    public Variable copy(){
        Variable v=new Variable(this.name,this.i,this.j);
        v.consC=this.consC;
        v.consR=this.consR;
        v.constraint=this.constraint;
        v.domain=this.domain;
        v.sumC=this.sumC;
        v.sumR=this.sumR;
        v.value=this.value;
        return v;
        
    }
    
}
