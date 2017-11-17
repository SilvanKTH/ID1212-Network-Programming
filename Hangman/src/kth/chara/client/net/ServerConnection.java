package kth.chara.client.net;

import kth.chara.common.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection {
    private static final int SOCKET_TIMEOUT = 1800000; //half hour
    private static final int CONNECTION_TIMEOUT = 30000; //half minute
    private OutputHandler serverResponse;
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;

    /**
     * Connect to the appropriate host and port
     */

    public void connect(String host, int port, OutputHandler serverResponse) throws IOException{
        this.serverResponse = serverResponse;
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT);
        socket.setSoTimeout(SOCKET_TIMEOUT);
        connected = true;
        toServer = new PrintWriter(socket.getOutputStream(), true);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(new Listener(serverResponse)).start();
    }

    public void startHangman() throws IOException{
        toServer.println(MsgType.START);
    }

    public void makeGuess(String guess) throws IOException{
        toServer.println(MsgType.GUESS + " " + guess);
    }

    public void disconnect() throws IOException{
        toServer.println(MsgType.QUIT);
        socket.close();
        socket = null;
        connected = false;
    }

    public boolean isConnected(){
        return connected;
    }

    /**
     * Separate thread for listening of new response from the server
     */

    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    outputHandler.ServerMsg(fromServer.readLine());
                }
            } catch (Throwable connectionFailure) {
                if (connected) {
                    outputHandler.ServerMsg("\nConnection lost!\n");
                }
            }
        }
    }
}