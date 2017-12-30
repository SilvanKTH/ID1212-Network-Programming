package kth.chara.client.controller;

import kth.chara.client.net.OutputHandler;
import kth.chara.client.net.ServerConnection;

import java.util.concurrent.CompletableFuture;

public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();

    /**
     * Start a new game.
     * @param id The player's id.
     * @param serverResponse The OutputHandler.
     */
    public void startGame(int id, OutputHandler serverResponse){
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.startRPS(id, serverResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRun(() -> serverResponse.ServerMsg("Connected to server! The game will begin soon!"));
    }

    /**
     * Play a new move.
     * @param id The player's id.
     * @param move The specific play move.
     */
    public void playMove(int id, String move){
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.playRPS(id, move);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Disconnect from the game.
     * @param id The player's id.
     * @throws Exception The specific exception in case of message queue error.
     */
    public void disconnect(int id) throws Exception {
        serverConnection.disconnect(id);
    }

    /**
     * Check if a player is actually connected to the server.
     * @return <code>true</code> if connected or <code>false</code> if not.
     */
    public boolean isConnected(){
        return serverConnection.isConnected();
    }
}

