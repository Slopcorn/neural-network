package main.java.network;

import java.io.*;

public class Model implements Serializable {
    private static final long serialVersionUID = 42L;

    Matrix[] weights;   // Weights between each pair of layers
    Matrix[] biases;    // Bias column vector for each non-out layer

    public Model(int... sizes) {
        // With n layers, we have n - 1 sets of weights
        // and layers with biases. Initialise accordingly.
        weights = new Matrix[sizes.length - 1];
        biases  = new Matrix[sizes.length - 1];
        for (int i = 0; i < sizes.length - 1; i++) {
            weights[i] = new Matrix(sizes[i + 1], sizes[i]);
            biases[i]  = new Matrix(sizes[i + 1], 1);
        }
    }

    public void learn(double[][] data, double[][] expected, int epochs) {
        assert data.length == expected.length;
        // Change data to a more suitable form.
        Matrix[] newData = new Matrix[data.length];
        Matrix[] newExpected = new Matrix[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i]     = Matrix.convertArrayToMatrix(data[i]);
            newExpected[i] = Matrix.convertArrayToMatrix(expected[i]);
        }

        // Learn on the data.
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < data.length; i++) {
                learn(newData[i], newExpected[i], 1); // Start at layer 1
            }
        }
    }

    private Object learn(Matrix data, Matrix expected, int layer) {
        // TODO: everything. this is probably going to be recursive at this stage
        return null;
    }

    public void save(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(this);
            System.out.println("Model saved as " + filename + "!");
        } catch (IOException e) {
            System.out.println("Could not save model! " + e);
        }
    }

    public static Model load(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            return (Model) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load model! " + e);
        }
        return null;    // Probably bad.
    }
}
