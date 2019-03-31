package ch.heigvd.res.pagedidier.Calculator;

/**
 * Définition des différents type d'opérations
 * @author Nohan Budry, Didier Page
 */
public enum OperationType implements Operation {

    /**
     * Définition d'une addition
     */
    ADDITION {
        @Override public int calculate(int a, int b) {

            return a + b;
        }
    },

    /**
     * Définition d'une soustraction
     */
    SUBTRACTION {
        @Override public int calculate(int a, int b) {

            return a - b;
        }
    },

    /**
     * Définition d'une multiplication
     */
    MULTIPLICATION {
        @Override public int calculate(int a, int b) {

            return a * b;
        }
    }
}