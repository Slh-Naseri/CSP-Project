package cspproject;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class KakuroSolver {

    boolean complete = false;

    public Hashtable<String, Variable> CSP_Backtracking(int n, Hashtable<String, Variable> var, Hashtable<String, Variable> selected, Hashtable<String, Variable> result) {
        KakuroSolver csp = new KakuroSolver();
        if (selected.size() == (n - 1)) {
            complete = true;
            return selected;
        }
        Ac3 ac3 = new Ac3();
        ac3.FindArcConsistency(var, selected);
        Enumeration enu = var.keys();
        while (enu.hasMoreElements()) {
            String name = enu.nextElement().toString();
            if (var.get(name).domain == null || var.get(name).domain.size() == 0) {
                return selected;
            }
        }
        
        Hashtable<String, Variable> saveVar = new Hashtable<String, Variable>();
        enu = var.keys();
        while (enu.hasMoreElements()) {
            Variable v = var.get(enu.nextElement().toString()).copy();
            if (!selected.containsKey(v.name)) {
                saveVar.put(v.name, v);
            }
        }
        Variable X = csp.SelectVariable(saveVar, selected);
        csp.LCV(X, saveVar);
        for (int i = 0; i < X.domain.size(); i++) {
            if (i != 0) {
                enu = var.keys();
                while (enu.hasMoreElements()) {
                    Variable v = var.get(enu.nextElement().toString()).copy();
                    if (!selected.containsKey(v.name)) {
                        saveVar.put(v.name, v);
                    }
                }
            }
            X.value = X.domain.get(i);
        
            Hashtable<String, Variable> saveSelected = new Hashtable<String, Variable>();
            enu = selected.keys();
            while (enu.hasMoreElements()) {
                Variable v = selected.get(enu.nextElement().toString());
                saveSelected.put(v.name, v);
            }
            saveSelected.put(X.name, X);
            csp.forwardChecking(saveVar, saveSelected, X);

            enu = saveVar.keys();
            int j = 0;
            while (enu.hasMoreElements()) {
                Variable v = saveVar.get(enu.nextElement().toString());
                if (v.domain == null || v.domain.size() == 0) {
                    break;
                }
                j++;
            }
            if (j == saveVar.size()) {
                result = CSP_Backtracking(n, saveVar, saveSelected,result);
                if (complete) {
                    return result;
                }
            }
            saveSelected.remove(X);
        }
        return selected;
    }

    public Variable SelectVariable(Hashtable<String, Variable> var, Hashtable<String, Variable> selected) {
        LinkedList<Variable> mrv = new LinkedList<Variable>();
        int minValue = 100;
        Enumeration enu = var.keys();
        while (enu.hasMoreElements()) {//MRV
            Variable v = var.get(enu.nextElement().toString());
            if (minValue > v.domain.size()) {
                minValue = v.domain.size();
            }
        }
        enu = var.keys();
        while (enu.hasMoreElements()) {
            Variable v = var.get(enu.nextElement().toString());
            if (minValue == v.domain.size()) {
                mrv.add(v);
            }
        }
        Variable maxConstraint = mrv.get(0);
        for (int i = 1; i < mrv.size(); i++) {//Degree heuristics
            if (maxConstraint.constraint < mrv.get(i).constraint) {
                maxConstraint = mrv.get(i);
            }
        }
        mrv.remove();
        return maxConstraint;
    }

    public void LCV(Variable v, Hashtable<String, Variable> vars) {
        int minConglict = 100, index = 0;
        int i = 0;
        if(v.domain.size()>1){
        for (; i < v.domain.size(); i++) {
            int repetition = 0;
            for (int j = 0; j < v.consR.size(); j++) {
                if (vars.containsKey((v.consR.get(j))) && vars.get(v.consR.get(j)).domain.contains(v.domain.get(i))) {
                    repetition++;
                }
            }
            for (int j = 0; j < v.consC.size(); j++) {
                if (vars.containsKey((v.consC.get(j))) && vars.get(v.consC.get(j)).domain.contains(v.domain.get(i))) {
                    repetition++;
                }
            }
            if (minConglict > repetition) {
                minConglict=repetition;
                index = i;
            }
        }
            int value = v.domain.get(index);
            v.domain.remove(index);
            v.domain.add(0, value);
        }
        
    }

    public Hashtable<String, Variable> forwardChecking(Hashtable<String, Variable> vars, Hashtable<String, Variable> selected, Variable X) {
        int sumC = 0, sumR = 0,
                n = 0,//number of neighbor
                index = 0;
        Enumeration enu = selected.keys();
        while (enu.hasMoreElements()) {
            Variable v = selected.get(enu.nextElement().toString());
            if (v.j == X.j) {
                sumC += v.value;
            }
            if (v.i == X.i) {
                sumR += v.value;
            }
        }
        KakuroSolver csp = new KakuroSolver();
        for (int i = 0; i < X.consC.size(); i++) {
            if (!selected.containsKey((X.consC.get(i))) && vars.get(X.consC.get(i)).domain.contains(X.value)) {
                LinkedList<Integer> delete = new LinkedList<Integer>();
                delete.add(X.value);
                for (int j = 0; j < vars.get(X.consC.get(i)).domain.size(); j++) {
                    if (vars.get(X.consC.get(i)).domain.get(j) + sumC > vars.get(X.consC.get(i)).sumC) {
                        delete.add(vars.get(X.consC.get(i)).domain.get(j));
                    }
                }
                vars.get(X.consC.get((i))).domain = csp.Delete(vars.get(X.consC.get(i)), delete);
                n++;
                index = i;
            } else if (!selected.containsKey((X.consC.get(i)))) {
                n++;
                index = i;
            }
        }
        if (n == 1) {
            int i = 0;
            LinkedList<Integer> delete = new LinkedList<Integer>();
            for (; i < vars.get(X.consC.get((index))).domain.size(); i++) {
                if (!(sumC + vars.get(X.consC.get((index))).domain.get(i) == vars.get(X.consC.get((index))).sumC)) {
                    delete.add(vars.get(X.consC.get((index))).domain.get(i));
                }
            }
            if (delete.size() == vars.get(X.consC.get((index))).domain.size()) {
                vars.get(X.consC.get((index))).domain = null;
            } else if (delete.size() > 0) {
                vars.get(X.consC.get((index))).domain = csp.Delete(vars.get(X.consC.get(index)), delete);
            }
        }
        index = 0;
        n = 0;
        for (int i = 0; i < X.consR.size(); i++) {
            if (!selected.containsKey((X.consR.get(i))) && vars.get(X.consR.get((i))).domain.contains(X.value)) {
                LinkedList<Integer> delete = new LinkedList<Integer>();
                delete.add(X.value);
                for (int j = 0; j < vars.get(X.consR.get(i)).domain.size(); j++) {
                    if (vars.get(X.consR.get(i)).domain.get(j) + sumR > vars.get(X.consR.get(i)).sumR) {
                        delete.add(vars.get(X.consR.get(i)).domain.get(j));
                    }
                }
                vars.get(X.consR.get((i))).domain = csp.Delete(vars.get(X.consR.get(i)), delete);
                n++;
                index = i;
            } else if (!selected.containsKey((X.consR.get(i)))) {
                n++;
                index = i;
            }
        }
        if (n == 1) {
            LinkedList<Integer> delete = new LinkedList<Integer>();
            for (int i = 0; i < vars.get(X.consR.get((index))).domain.size(); i++) {
                if (!(sumR + vars.get(X.consR.get((index))).domain.get(i) == vars.get(X.consR.get((index))).sumR)) {
                    delete.add(vars.get(X.consR.get((index))).domain.get(i));
                }
            }
            if (delete.size() == vars.get(X.consR.get((index))).domain.size()) {
                vars.get(X.consR.get((index))).domain = null;
            } else if (delete.size() > 0) {
                vars.get(X.consR.get((index))).domain = csp.Delete(vars.get(X.consR.get(index)), delete);
            }
        }
        return vars;
    }

    public LinkedList<Integer> Delete(Variable v, LinkedList<Integer> value) {
        LinkedList<Integer> domain = new LinkedList<Integer>();
        for (int j = 0; j < v.domain.size(); j++) {
            if (!value.contains(v.domain.get(j))) {
                domain.add(v.domain.get(j));
            }
        }
        v.domain = null;
        return domain;
    }

}
