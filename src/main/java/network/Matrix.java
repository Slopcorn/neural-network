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

    public Matrix mult(Matrix other) {
        // Dimensions have to match - n1, m1 x n2, m2 = n1, m2; m1 and n2 have to be the same
        if (cols != other.getRows()) {
            throw new RuntimeException("Matrix multiplication operands do not have appropriate sizes");
        }
        // Do the multiplication here.
        // Every element inside the matrix is the dot product
        // of a row from the left operand and a column
        // from the right operand.
        // Since we can only really access rows easily, transpose the right.
        // Then we can access a column vector by pulling a row from the transposed matrix.
        Matrix right = other.transposed();

        // The new matrix is of size: n (of this one) x m (of the other one)
        double[][] result = new double[rows][other.getCols()];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.getCols(); j++) {
                result[i][j] = dotProduct(this.getRow(i), right.getRow(j));
            }
        }
        return new Matrix(rows, other.getCols(), result);
    }

    private double dotProduct(double[] row1, double[] row2) {
        // These should be the same length anyway now.
        double result = 0;
        for (int i = 0; i < row1.length; i++) {
            result += row1[i] * row2[i];
        }
        return result;
    }

    private static Matrix convertArrayToMatrix(double[] data) {
        // The array is turned into a column vector.
        Matrix newMatrix = new Matrix(data.length, 1);
        for (int i = 0; i < data.length; i++) {
            newMatrix.set(i, 1, data[i]);
        }
        return newMatrix;
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
        Matrix mult = left.mult(right);
        System.out.println(mult.toString());
        System.out.println(mult.mult(left));
    }
}
