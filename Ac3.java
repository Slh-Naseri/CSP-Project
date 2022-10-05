
package cspproject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

public class Ac3 {

    public Hashtable<String, Variable> FindArcConsistency(Hashtable<String, Variable> vars, Hashtable<String, Variable> selected) {
        boolean contradiction = false, notSet=false;
        int sum = 0;
        LinkedList<String> Q = new LinkedList<String>();
        Hashtable<String, Variable> saveVar = new Hashtable<String, Variable>();
        Ac3 ac3 = new Ac3();
        Enumeration enu = vars.keys();
        while (enu.hasMoreElements()) {
            String key = enu.nextElement().toString();
            Q.add(vars.get(key).name);
        }
        enu = vars.keys();
        while (enu.hasMoreElements()) {
            Variable v = vars.get(enu.nextElement().toString()).copy();
            saveVar.put(v.name, v);

        }
        while (Q.size()>0 && !contradiction) {
            Variable v1 = vars.get(Q.get(0));
            if (v1.domain == null) {
                break;
            }
            Q.remove(v1.name);
            LinkedList<Variable> consC = new LinkedList<Variable>();
            LinkedList<Variable> consR = new LinkedList<Variable>();
            for (int j = 0; j < v1.consC.size(); j++) { //In this loop we put all the dependent Q variables in the column in a list
                if (!selected.containsKey(v1.consC.get(j))) {
                    consC.add(saveVar.get(v1.consC.get(j)));
                    notSet=true;
                } else {
                    sum += selected.get(v1.consC.get(j)).sumC;
                }
            }

            for (int i = 0; i < consC.size(); i++) {
                if (ac3.RemoveValues(v1, consC.get(i), sum, 0,notSet)| ac3.RemoveValues(v1, consC.get(i), sum, v1.sumC,notSet)) {
                    if (consC.get(i).domain == null) {
                        contradiction = true;
                    }
                    if (!Q.contains(consC.get(i).name)) {
                        Q.add(consC.get(i).name);
                    }
                }
            }
            notSet=false;
            sum = 0;
            for (int j = 0; j < v1.consR.size(); j++) { //In this loop we put all the dependent Q variables in the row in a list
                if (!selected.containsKey(v1.consR.get(j))) {
                    consR.add(saveVar.get(v1.consR.get(j)));
                    notSet=true;
                } else {
                    sum += selected.get(v1.consR.get(j)).sumR;
                }
            }
            for (int i = 0; i < consR.size(); i++) {
                if (ac3.RemoveValues(v1, consR.get(i), sum, 0,notSet)|ac3.RemoveValues(v1, consR.get(i), sum, v1.sumR,notSet)) {
                    if (consR.get(i).domain == null) {
                        contradiction = true;
                    }
                    if (!Q.contains(consR.get(i).name)) {
                        Q.add(consR.get(i).name);
                    }
                }
            }
        }
        return saveVar;
    }
    public boolean RemoveValues(Variable v1, Variable v2, int sum, int sumCR,boolean notSet) {
        boolean remove = false;
        Ac3 ac3 = new Ac3();
        if (sumCR > 0) {
            LinkedList<Integer> delete = new LinkedList<Integer>();
            for (int i = 0; v2.domain != null &&i < v2.domain.size(); i++) {
                int j = 0;
                for (; j < v1.domain.size(); j++) {
                    if (notSet ){
                        if (v2.domain.get(i) + v1.domain.get(j) + sum < sumCR) {
                        }
                    }
                    else if (v2.domain.get(i) + v1.domain.get(j) + sum == sumCR) {
                        break;
                    }
                }
                if (j == v1.domain.size()) {
                    delete.add(v2.domain.get(i));
                    remove = true;
                }
            }
            if (delete.size() > 0 && v2.domain != null && delete.size() != v2.domain.size()) {
                v2.domain = ac3.Delete(v2, delete);
                remove=true;
            } else if(v2.domain != null && delete.size() == v2.domain.size()) {
                remove=true;
                v2.domain = null;
            }
        } else {
            LinkedList<Integer> delete = new LinkedList<Integer>();
            for (int i = 0; v2.domain != null && i < v2.domain.size(); i++) {
                if (v1.domain.size() > 1) {
                    break;
                } else if (v2.domain.size() > 0 && v1.domain.size() == 1 && v2.domain.contains(v1.domain.get(0))) {
                    delete.add(v1.domain.get(0));
                    break;
                }
            }
            if (delete.size() > 0 && v2.domain != null && delete.size() != v2.domain.size()) {
                v2.domain = ac3.Delete(v2, delete);
                remove=true;
            } else if(v2.domain != null &&delete.size() == v2.domain.size()) {
                remove=true;
                v2.domain = null;
            }

        }
        return remove;
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
