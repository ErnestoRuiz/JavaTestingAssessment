package co.newlabs.exception;

public class ItemNotOnPalletException extends RuntimeException {
    public ItemNotOnPalletException(int itemId) {
        super(String.format("Item id: %d not on identified pallet", itemId));
    }
}
