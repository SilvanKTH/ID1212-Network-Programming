package kth.chara.server.model.fileHandling;

/**
 * Handles the case of file exception.
 */

public class FileException extends Exception {
    public FileException(String message){
        super(message);
    }
}
