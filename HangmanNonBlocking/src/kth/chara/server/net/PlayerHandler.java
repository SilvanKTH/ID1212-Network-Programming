package kth.chara.server.net;

import kth.chara.common.MsgProcess;
import kth.chara.common.MsgType;
import kth.chara.server.controller.Controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

/**
 * Handles all communication with one particular player
 */
public class PlayerHandler implements Runnable {
    private final GameServer server;
    private final SocketChannel socketChannel;
    private SelectionKey playerKey;
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(300);
    private final MsgProcess msgProcess = new MsgProcess();
    private final Queue<ByteBuffer> msgQueue = new ArrayDeque<>();
    private Controller controller = new Controller();


    PlayerHandler(GameServer server, SocketChannel socketChannel) {
        this.server = server;
        this.socketChannel = socketChannel;
        controller.newHangman();
    }

    void setKey (SelectionKey playerKey){
        this.playerKey = playerKey;
    }

    void receiveMessage() throws IOException {
        byteBuffer.clear();
        int numOfBytes = socketChannel.read(byteBuffer);
        if (numOfBytes == -1) {
            throw new IOException("Player has closed connection!");
        }
        String rcvdMsg = getMsgFromBuffer();
        msgProcess.appendMsg(rcvdMsg);
        CompletableFuture.runAsync(this);
    }

    private String getMsgFromBuffer(){
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new String(bytes);
    }

    @Override
    public void run() {
        while (msgProcess.hasNextMsg()) {
            try {
                PlayerMessage msg = new PlayerMessage(msgProcess.nextMsg());
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

    private void ServerResponse(String reply) throws IOException {
        if (reply == null) return;
        //create byte buffer
        ByteBuffer msg = MsgProcess.messageJoiner(MsgType.SERVER_RESPONSE, reply);
        msgQueue.add(msg);
        server.appendQueueKey(playerKey);
        server.selectorOn();
    }

    void sendMsg() throws IOException {
        ByteBuffer msg;
        while ((msg = msgQueue.peek()) != null){
            socketChannel.write(msg);
            if (msg.hasRemaining()) throw new IOException("Message not sent!");
            msgQueue.poll();
        }
    }

    void disconnectPlayer() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            System.err.println("Problem with closing client's connection!");
            e.printStackTrace();
        }
    }
}