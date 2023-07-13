package io.github.navjotsrakhra.filedownloader.fileSys;

public class NotADirectoryException extends Throwable {
    NotADirectoryException() {
        super("The given path is not the path of a directory.");
    }

    public NotADirectoryException(String s) {
        super(s);
    }
}
