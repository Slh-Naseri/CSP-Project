package cspproject;

import java.util.*;

public class Initialize {

    public static void main(String[] args) {
        Hashtable<String, Variable> selected = new Hashtable<>();
        Hashtable<String, Variable> result = new Hashtable<>();
        Hashtable<String, Variable> var = new Hashtable<>();
        Initialize getInput = new Initialize();
        Scanner input = new Scanner(System.in);
        int cols = input.nextInt();
        int rows = input.nextInt();
        Variable v;
        int n = 1;
        int arr[][] = new int[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                arr[i][j] = input.nextInt();
                if (arr[i][j] == 0) {
                    v = new Variable("v" + n, i, j);
                    n++;
                    for (int k = 0; k < i; k++) {//Initialize sum column
                        if (arr[k][j] != -1) {
                            v.sumC = arr[k][j];
                            break;
                        }
                    }
                    for (int k = 0; k < j; k++) {//Initialize sum row
                        if (arr[i][k] != -1) {
                            v.sumR = arr[i][k];
                            break;
                        }
                    }
                    var.put(v.name, v);
                }
            }
        }
        getInput.FindConstraints(var);
        KakuroSolver solver = new KakuroSolver();
        
        result = solver.CSP_Backtracking(n, var, selected, result);
        System.out.println("reslt : ");
        for (int i = 1; i <= result.size(); i++) {
            arr[result.get("v" + i).i][result.get("v" + i).j] = result.get("v" + i).value;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void FindConstraints(Hashtable<String, Variable> var) {
        for (int i = 1; i <= var.size(); i++) {
            for (int j = i + 1; j <= var.size(); j++) {
                if (var.get("v" + i).i == var.get("v" + j).i) {
                    var.get("v" + i).consR.add(var.get("v" + j).name);
                    var.get("v" + j).consR.add(var.get("v" + i).name);
                    var.get("v" + i).constraint++;
                    var.get("v" + j).constraint++;
                } else if (var.get("v" + i).j == var.get("v" + j).j) {
                    var.get("v" + i).consC.add(var.get("v" + j).name);
                    var.get("v" + j).consC.add(var.get("v" + i).name);
                    var.get("v" + i).constraint++;
                    var.get("v" + j).constraint++;
                }
            }
        }
    }
}
