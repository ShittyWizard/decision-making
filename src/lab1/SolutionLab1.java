package lab1;

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

public class SolutionLab1 {
    public static void main(String[] args) throws FileNotFoundException, OptimizationException {
        Scanner sc = new Scanner(new File("src/lab1/input.txt"));
        int delta;
        // количество строк
        int n = sc.nextInt();
        // количество столбцов
        int m = sc.nextInt();

        // платёжная матрица
        int[][] matrix = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = sc.nextInt();
            }
        }

        // вектора для отображения ответа в случае седловой точки
        int[] player1 = new int[n];
        int[] player2 = new int[m];

        // delta это какое число мы прибавляем ко всем элементам, чтобы матрица стала положительной
        delta = makeMatrixPositive(matrix);

        // проверка на седловую точку
        if (getMaxMin(matrix)[0] == getMinMax(matrix)[0]) {
            System.out.println("Решение найдено в чистых стратегиях, так как существует седловая точка.");
            System.out.println("Нижняя цена игры (без учёта delta) = " + getMaxMin(matrix)[0]);
            System.out.println("Верхняя цена игры (без учёта delta) = " + getMinMax(matrix)[0]);
            System.out.println("delta = " + delta);
            System.out.println("Цена игры (с учётом delta) = " + (getMaxMin(matrix)[0] - delta));

            Arrays.fill(player1, 0);
            Arrays.fill(player2, 0);
            // Для вывода чистой стратегии нужно поставить 1 в максимине (для первого игрока) и в минимаксе(для второго игрока)
            player1[getMaxMin(matrix)[1]] = 1;
            player2[getMinMax(matrix)[1]] = 1;
            System.out.println("Для первого игрока: " + Arrays.toString(player1));
            System.out.println("Для второго игрока: " + Arrays.toString(player2));
        } else {
            System.out.println(Arrays.deepToString(matrix));
            // переменные для первого и второго игроков, заполняем их единицами
            // тип double так как библиотека работает только с таким
            double[] first = new double[n];
            double[] second = new double[m];
            Arrays.fill(first, 1);
            Arrays.fill(second, 1);

            // переводим матрицу в double
            double[][] doubleMatrix = new double[n][m];

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    doubleMatrix[i][j] = matrix[i][j];
                }
            }

            // целевая функция для первого игрока (x1 + x2 + x3 и тд)
            LinearObjectiveFunction W1 = new LinearObjectiveFunction(first, 0);

            // массив ограничений
            ArrayList constraints1 = new ArrayList();

            // ограничения для первого игрока являются коэффициенты столбцов, умноженные на соотв. переменную
            // для примера 3 * x1 + 4 * x2 >= 1
            // GEQ - больше или равно
            for (int i = 0; i < m; i++) {
                constraints1.add(new LinearConstraint(getColumn(doubleMatrix, i), Relationship.GEQ, 1));
            }

            // заполняем солюшен
            // целевая функция минимизируется
            // последний true - это чтобы переменные все были неотрицательные
            SimplexSolver simplexSolver1 = new SimplexSolver();
            RealPointValuePair solution1 = simplexSolver1.optimize(W1, constraints1,
                    GoalType.MINIMIZE, true);

            // получаем вектор относительных частот для первого игрока
            double[] player1Result = getProbVector(solution1);

            System.out.println(Arrays.toString(player1Result));
            // выводим цену игры
            System.out.println(1 / solution1.getValue());

            // аналогично для второго
            // у ограничений для второго знак LEQ, а функция максимизируется
            LinearObjectiveFunction W2 = new LinearObjectiveFunction(second, 0);
            ArrayList constraints2 = new ArrayList();

            for (int j = 0; j < n; j++) {
                constraints2.add(new LinearConstraint(doubleMatrix[j], Relationship.LEQ, 1));
            }

            SimplexSolver simplexSolver2 = new SimplexSolver();
            RealPointValuePair solution2 = simplexSolver2.optimize(W2, constraints2,
                    GoalType.MAXIMIZE, true);

            double[] player2Result = getProbVector(solution2);

            System.out.println(Arrays.toString(player2Result));
            System.out.println(1 / solution2.getValue());
        }

    }

    /**
     * @param array - платёжная матрица
     * @return - {максимин (нижнее значение игры), индекс стратегии}
     */
    private static int[] getMaxMin(int[][] array) {
        int minimal = Integer.MAX_VALUE;
        int index = 0;
        int[] min = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                minimal = Math.min(array[i][j], minimal);
            }
            min[i] = minimal;
            minimal = Integer.MAX_VALUE;
        }
        int maximum = Integer.MIN_VALUE;
        for (int i = 0; i < min.length; i++) {
            if (min[i] > maximum) {
                index = i;
                maximum = min[i];
            }
        }
        return new int[]{maximum, index};
    }

    /**
     * @param array - платёжная матрица
     * @return - {минимакс (верхнее значение игры), индекс стратегии}
     */
    private static int[] getMinMax(int[][] array) {
        int maximum = Integer.MIN_VALUE;
        int index = 0;
        int[] max = new int[array[0].length];
        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < array.length; j++) {
                maximum = Math.max(array[j][i], maximum);
            }
            max[i] = maximum;
            maximum = Integer.MIN_VALUE;
        }
        int minimum = Integer.MAX_VALUE;
        for (int i = 0; i < max.length; i++) {
            if (max[i] < minimum) {
                index = i;
                minimum = max[i];
            }
        }
        return new int[]{minimum, index};
    }

    /**
     * @param array - платёжная матрица
     * @return - сколько мы прибавили к каждому элементу, чтобы сделать матрицу положительной
     */
    private static int makeMatrixPositive(int[][] array) {
        int minimal = Integer.MAX_VALUE;
        int delta = 0;

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                minimal = Math.min(minimal, array[i][j]);
            }
        }

        if (minimal <= 0) {
            delta = Math.abs(minimal) + 1;
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[0].length; j++) {
                    array[i][j] += delta;
                }
            }
        }

        return delta;
    }

    /**
     * @param array - платёжная матрица
     * @param index - индекс нужного столбца
     * @return - столбец
     */
    private static double[] getColumn(double[][] array, int index) {
        double[] column = new double[array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (j == index) {
                    column[i] = array[i][j];
                    break;
                }
            }
        }
        return column;
    }

    /**
     * @param solution - решение ЗЛП
     * @return - массив относительных частот
     */
    private static double[] getProbVector(RealPointValuePair solution) {
        double[] playerResult = solution.getPoint();
        for (int i = 0; i < playerResult.length; i++) {
            playerResult[i] = playerResult[i] / solution.getValue();
        }
        return playerResult;
    }
}
