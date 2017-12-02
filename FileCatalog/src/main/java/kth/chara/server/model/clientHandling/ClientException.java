package kth.chara.server.model.clientHandling;

/**
 * Handles the case of client exception.
 */

public class ClientException extends Exception{
    ClientException(String message){
        super(message);
    }
}
