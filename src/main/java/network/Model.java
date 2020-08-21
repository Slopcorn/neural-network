package main.java.network;

import java.io.*;
import java.util.Random;

public class Model implements Serializable {
    private static final long serialVersionUID = 42L;

    Matrix[] weights;   // Weights between each pair of layers
    Matrix[] biases;    // Bias column vector for each non-out layer
    double rate;        // Learning rate

    public Model(int... sizes) {
        this(0.5, sizes);   // Given a default learning rate
    }

    public Model(double rate, int... sizes) {
        // With n layers, we have n - 1 sets of weights
        // and layers with biases. Initialise accordingly.
        this.rate = rate;
        weights = new Matrix[sizes.length - 1];
        biases  = new Matrix[sizes.length - 1];
        for (int i = 0; i < sizes.length - 1; i++) {
            weights[i] = new Matrix(sizes[i + 1], sizes[i]);
            biases[i]  = new Matrix(sizes[i + 1], 1);
        }
        // Initial randomisation of weights
        Random random = new Random(42);
        for (int i = 0; i < weights.length; i++) {
            randomiseMatrix(weights[i], random);
            randomiseMatrix(biases[i], random);
        }
    }

    private void randomiseMatrix(Matrix m, Random random) {
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getCols(); j++) {
                m.set(i, j, random.nextGaussian());
            }
        }
    }

    public double[] score(double[] input) {
        Matrix[] scores = feedforward(Matrix.arrayToColumnVector(input));
        return Matrix.columnVectorToArray(scores[scores.length - 1]);
    }

    public void learn(double[][] data, double[][] expected, int epochs) {
        assert data.length == expected.length;
        // Change data to a more suitable form.
        Matrix[] newData = new Matrix[data.length];
        Matrix[] newExpected = new Matrix[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i]     = Matrix.arrayToColumnVector(data[i]);
            newExpected[i] = Matrix.arrayToColumnVector(expected[i]);
        }

        // Learn on the data.
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < newData.length; i++) {
                Matrix[] result = feedforward(newData[i]);    // Activations in all layers
                backpropagate(result, newExpected[i]);
            }
        }
    }

    private Matrix[] feedforward(Matrix data) {
        Matrix[] activations = new Matrix[weights.length + 1];

        Matrix scores = data;   // Activations of the first layer

        for (int i = 0; i < weights.length; i++) {  // Activations of the rest of the layers
            activations[i] = scores;
            scores = Matrix.add(Matrix.mult(weights[i], scores), biases[i]);
            applySigmoid(scores);
        }

        activations[weights.length] = scores;

        return activations;
    }

    private void backpropagate(Matrix[] activations, Matrix expected) {
        for (int i = weights.length - 1; i >= 0; i--) {
            Matrix error = Matrix.sub(expected, activations[i + 1]);
            if (i != 0) expected = findNextExpected(activations[i + 1], i);
            fixWeights(activations[i], error, i);
        }
    }

    private void fixWeights(Matrix activation, Matrix error, int layer) {
        Matrix errorsWeights = Matrix.mult(error, activation.transposed());
        weights[layer] = Matrix.add(weights[layer], Matrix.scalarMult(rate, errorsWeights));
        biases[layer]  = Matrix.add(biases[layer],  Matrix.scalarMult(rate, error));
    }

    private Matrix findNextExpected(Matrix expected, int layer) {
        // TODO: figure this one out
        // This thing will absolutely not work
        // Calculating ideal values for previous layer by
        // calculating the 'feedforward'... backwards
        Matrix w = weights[layer].transposed();
        Matrix idealsPossblyIDontKnow = Matrix.mult(w, expected);
        applySigmoid(idealsPossblyIDontKnow);
        return idealsPossblyIDontKnow;
    }

    private static void applySigmoid(Matrix scores) {
        int n = scores.getRows();
        int m = scores.getCols();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                scores.set(i, j, sigmoid(scores.get(i, j)));
            }
        }
    }

    private static double sigmoid(double x) {
        return 1.0 / (1 + Math.pow(Math.E, -x));
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
