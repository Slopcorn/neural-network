package main.java.network;

import java.io.Serializable;

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
            biases[i]  = new Matrix(sizes[i], 1);
        }
    }
}
