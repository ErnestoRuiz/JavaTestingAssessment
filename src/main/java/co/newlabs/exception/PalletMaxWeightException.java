package co.newlabs.exception;

public class PalletMaxWeightException extends RuntimeException {
    public PalletMaxWeightException(double currentWeight, double itemWeight) {
        super(String.format("Current weight: %f Item weight: %f", currentWeight, itemWeight));
    }
}
