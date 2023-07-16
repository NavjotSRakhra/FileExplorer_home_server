package io.github.navjotsrakhra.filedownloader.exceptions;

public class IllegalPathException extends Exception {
    public IllegalPathException() {
        super("Illegal path supplied");
    }

    public IllegalPathException(String errorMessage) {
        super(errorMessage);
    }
}
