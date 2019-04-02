package lab3;

import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SolutionLab3 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/lab3/input.txt"));
        int n = sc.nextInt();
        int m = sc.nextInt();

        double[][] matrix = new double[n][m];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = sc.nextDouble();
            }
        }

        int[] benefit = new int[m - 2];

        for (int i = 0; i < benefit.length; i++) {
            benefit[i] = sc.nextInt();
        }

        //TODO: solve 2 problems by simplex solver and get 2 answers
    }

    // TODO: make simplex solver as in lab1
    public static RealPointValuePair simplexSolve(LinearObjectiveFunction function, ArrayList constaints) {
        RealPointValuePair solution = null;
        return solution;
    }
}
