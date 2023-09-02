package me.falu.peepopractice.core.exception;

public class InvalidCategorySyntaxException extends Exception {
    public InvalidCategorySyntaxException() {
        super();
    }

    public InvalidCategorySyntaxException(String categoryId) {
        super("Invalid category: " + categoryId);
    }
}
