package kth.chara.server.net;

import kth.chara.common.MsgType;
import kth.chara.server.controller.Controller;

import java.io.*;
import java.net.Socket;

/**
 * Each player is handled with a new thread
 */

public class PlayerHandler implements Runnable {
    private final GameServer server;
    private final Socket playerSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private boolean connected;
    private Controller controller = new Controller();


    PlayerHandler(GameServer server, Socket playerSocket) {
        this.server = server;
        this.playerSocket = playerSocket;
        controller.newHangman();
        connected = true;
    }

    @Override
    public void run() {
        try {
            fromClient = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            toClient = new PrintWriter(playerSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Stream creation failure!");
            e.printStackTrace();
        }

        while (connected) {
            try {
                PlayerMessage msg = new PlayerMessage(fromClient.readLine()); // Blocking TCP connection
                switch (msg.getMsgType()) {
                    case START:
                        System.out.println("New game!");
                        controller.startNewGame();
                        ServerResponse(controller.getResponse());
                        break;
                    case GUESS:
                        System.out.println("New guess from a player!");
                        controller.newGuess(msg.getMsgBody());
                        ServerResponse(controller.getResponse());
                        break;
                    case QUIT:
                        System.out.println("A player just disconnected!");
                        disconnectPlayer();
                        break;
                    default:
                        throw new StreamCorruptedException("Corrupted message: " + msg.getMsgType());
                }
            }  catch (IOException ioe) {
                disconnectPlayer();
                System.err.println("Player disconnected unexpectingly!");
            }
        }
    }

    /**
     * Send a message to the player.
     */

    private void ServerResponse(String reply) throws IOException {
        if (reply == null) return;
        toClient.println(MsgType.SERVER_RESPONSE.toString() + "\n" + reply);
    }

    private void disconnectPlayer() {
        try {
            playerSocket.close();
        } catch (IOException e) {
            System.err.println("Problem with closing client's connection!");
            e.printStackTrace();
        }
        connected = false;
        server.removePlayerHandler(this);
    }
}
