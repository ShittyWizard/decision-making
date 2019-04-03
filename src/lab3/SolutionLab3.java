package lab3;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.optimization.linear.SimplexSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SolutionLab3 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/lab3/input.txt"));
        // сколько видов станков
        int n = sc.nextInt();
        // сколько видов товаров
        int m = sc.nextInt();

        double[][] matrix = new double[n][m];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = sc.nextDouble();
            }
        }

        double[] timeFund = new double[n];

        for (int i = 0; i < n; i++) {
            timeFund[i] = sc.nextDouble();
        }

        double[] benefit = new double[m];

        for (int i = 0; i < benefit.length; i++) {
            benefit[i] = sc.nextDouble();
        }

        double[] penalty = new double[n];

        for (int i = 0; i < n; i++) {
            penalty[i] = sc.nextDouble();
        }

        // система ограничений
        ArrayList constraints = new ArrayList();
        for (int i = 0; i < n; i++) {
            constraints.add(new LinearConstraint(matrix[i], Relationship.LEQ, timeFund[i]));
        }

        //решение задачи ДО добавления второго критерия и уступки
        double[] resultBefore;
        double valueBefore;

        LinearObjectiveFunction functionZ1 = new LinearObjectiveFunction(benefit, 0);

        resultBefore = simplexSolve(functionZ1, constraints, GoalType.MAXIMIZE).getPoint();
        valueBefore = simplexSolve(functionZ1, constraints, GoalType.MAXIMIZE).getValue();

        System.out.println("Result before new criterion : " + Arrays.toString(resultBefore));
        System.out.println("Value before new criterion : " + valueBefore);

        //решение задачи ПОСЛЕ добавления второго критерия и уступки
        double[] resultAfter;
        double valueAfter;

        double deltaZ = 8;

        // добавляем новое ограничение
        constraints.add(new LinearConstraint(benefit, Relationship.GEQ,valueBefore - deltaZ));

        // свободный член в целевой функции Z2
        double constantZ2 = 0;

        // коэффициенты при переменных в целевой функции Z2
        double[] temp = new double[m];

        for (int i = 0; i < n; i++) {
            constantZ2 += penalty[i] * timeFund[i];
            for (int j = 0; j < m; j++) {
                temp[j] -= penalty[i] * matrix[i][j];
            }
        }

        //определяем новую целевую функцию
        LinearObjectiveFunction functionZ2 = new LinearObjectiveFunction(temp, constantZ2);

        resultAfter = simplexSolve(functionZ2, constraints, GoalType.MINIMIZE).getPoint();
        valueAfter = simplexSolve(functionZ2, constraints, GoalType.MINIMIZE).getValue();

        System.out.println("Result after new criterion : " + Arrays.toString(resultAfter));
        System.out.println("Value after new criterion : " + valueAfter);
    }

    public static RealPointValuePair simplexSolve(LinearObjectiveFunction function, ArrayList constraints, GoalType goalType) {
        SimplexSolver simplexSolver = new SimplexSolver();
        RealPointValuePair solution = null;
        try {
            solution = simplexSolver.optimize(function, constraints, goalType, true);
        } catch (OptimizationException e) {
            e.printStackTrace();
        }
        return solution;
    }
}
