package main.java.network;

import java.io.Serializable;
import java.util.Arrays;

public class Matrix implements Serializable {
    private static final long serialVersionUID = 42L;

    private int rows;
    private int cols;
    private double[][] matrix;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        matrix = new double[rows][cols]; // all are automatically set to 0
    }

    private Matrix(int rows, int cols, double[][] matrix) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = matrix.clone();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double get(int x, int y) {
        return matrix[x][y];
    }

    public void set(int x, int y, double val) {
        matrix[x][y] = val;
    }

    public Matrix transposed() {
        double[][] newMatrix = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newMatrix[j][i] = matrix[i][j];
            }
        }
        return new Matrix(cols, rows, newMatrix);
    }

    private double[] getRow(int n) {
        return matrix[n].clone();
    }

    private static double dotProduct(double[] row1, double[] row2) {
        // These should be the same length anyway now.
        double result = 0;
        for (int i = 0; i < row1.length; i++) {
            result += row1[i] * row2[i];
        }
        return result;
    }

    public static Matrix convertArrayToMatrix(double[] data) {
        // The array is turned into a column vector.
        Matrix newMatrix = new Matrix(data.length, 1);
        for (int i = 0; i < data.length; i++) {
            newMatrix.set(i, 1, data[i]);
        }
        return newMatrix;
    }

    public static Matrix add(Matrix l, Matrix r) {
        // Check size.
        if (l.getRows() != r.getRows() || l.getCols() != r.getCols()) {
            throw new IllegalArgumentException("Matrix addition operands are not of the same size");
        }
        // The size of the new matrix, really for convenience.
        int n = l.getRows();
        int m = l.getCols();
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = l.get(i, j) + r.get(i, j);
            }
        }
        return new Matrix(n, m, result);
    }

    public static Matrix mult(Matrix l, Matrix r) {
        // Check size.
        if (l.getCols() != r.getRows()) {
            throw new IllegalArgumentException("Matrix multiplication operands do not have appropriate sizes");
        }
        // The size of the new matrix.
        int n = l.getRows();
        int m = r.getCols();

        r = r.transposed(); // Transpose the right for easy access to its columns
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = dotProduct(l.getRow(i), r.getRow(j));    // Each element as dot product.
            }
        }
        return new Matrix(n, m, result);
    }

    public static Matrix scalarMult(double a, Matrix x) {
        int n = x.getRows();
        int m = x.getCols();

        Matrix out = new Matrix(n, m);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                out.set(i, j, a * x.get(i, j));
            }
        }

        return out;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            sb.append(Arrays.toString(row)).append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Just a test method.
        Matrix left = new Matrix(4, 1);
        left.set(0, 0, 1);
        left.set(1, 0, 2);
        left.set(2, 0, 3);
        left.set(3, 0, 4);
        System.out.println(left.toString());
        Matrix right = new Matrix(1, 4);
        right.set(0, 0, 1);
        right.set(0, 1, 2);
        right.set(0, 2, 3);
        right.set(0, 3, 4);
        System.out.println(right.toString());
        Matrix mult = mult(left, right);
        System.out.println(mult.toString());
        System.out.println(mult(mult, left));
    }
}
