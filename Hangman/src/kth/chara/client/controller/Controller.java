package kth.chara.client.controller;

import kth.chara.client.net.OutputHandler;
import kth.chara.client.net.ServerConnection;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();

    /**
     * Connect with the server
     */

    public void connect(String host, int port, OutputHandler serverResponse){
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, serverResponse);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> serverResponse.ServerMsg("\nConnected to: " + host + ":" + port +"\nCommand => "));
    }

    /**
     * Start a new game
     */

    public void startHangman(){
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.startHangman();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Make a new guess
     */

    public void makeGuess(String guess){
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.makeGuess(guess);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Quit the game, while disconnecting from the server
     */

    public void disconnect() throws IOException{
        serverConnection.disconnect();
    }

    public boolean isConnected(){
        return serverConnection.isConnected();
    }
}
