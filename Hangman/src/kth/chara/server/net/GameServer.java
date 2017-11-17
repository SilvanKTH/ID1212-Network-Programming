package kth.chara.server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class GameServer {
    private static final int T_LINGER = 5000;
    private static final int SOCKET_TIMEOUT = 1800000;
    private static int port = 8080; //default listening port
    private final List<PlayerHandler> players = new ArrayList<>();
    //private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        /*System.out.print("Listening port => ");
        try{
            port = Integer.parseInt(br.readLine());
        } catch(NumberFormatException nfe){
            System.out.println("Invalid port Format! Listening port remains: " + port);
        }*/

        GameServer server = new GameServer();
        server.listenSocket();
    }

    /**
     * The listening socket that blocks until new client connects
     */

    private void listenSocket() {
        try (ServerSocket socket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Listening players...");
                Socket player = socket.accept(); // Blocking
                handlePlayer(player);
            }
        } catch (IOException e) {
            System.err.println("Server failure!");
            e.printStackTrace();
        }
    }

    /**
     * Every client is handled in a separate thread
     */

    private void handlePlayer(Socket player) throws SocketException {
        System.out.println("New player just connected!");

        player.setSoLinger(true, T_LINGER);
        player.setSoTimeout(SOCKET_TIMEOUT);

        PlayerHandler pHandler = new PlayerHandler(this, player);
        players.add(pHandler);

        Thread handlerThread = new Thread(pHandler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }

    void removePlayerHandler(PlayerHandler player) {
        players.remove(player);
    }
}
