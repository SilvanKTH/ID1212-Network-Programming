package kth.chara.server.model;

/**
 * Handles the case of client exception.
 */

public class ClientException extends Exception{
    public ClientException(String message){
        super(message);
    }
}
