package kth.chara.client.net;


import kth.chara.common.MsgProcess;
import kth.chara.common.MsgType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Manages all communication with the server.
 * All operations are non-blocking.
 * Now the class implements a Runnable method.
 */
public class ServerConnection implements Runnable {
    private InetSocketAddress inetSocketAddress;
    private Selector selector;
    private SocketChannel socketChannel;
    private final Queue<ByteBuffer> msgQueue = new ArrayDeque<>();
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(300);
    private final MsgProcess msgProcess = new MsgProcess();
    private volatile boolean readyToSend = false;
    private volatile boolean connected;
    private final List<CommunicationListener> communicationListeners = new ArrayList<>();

    /**
     * New listeners are added to the listener's list
     *
     * @param communicationListener The specific listener
     */
    public void addListener(CommunicationListener communicationListener){
        communicationListeners.add(communicationListener);
    }

    /**
     * Connect to the appropriate host and port
     *
     * @param host The name of the host
     * @param port The port of the server
     */
    public void connect(String host, int port){
        inetSocketAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try{
            socketChannel = SocketChannel.open(); //open the socket channel
            socketChannel.configureBlocking(false); //set it non blocking
            socketChannel.connect(inetSocketAddress); //connect it to the specific server
            connected = true;

            selector = Selector.open(); //open a selector
            socketChannel.register(selector, SelectionKey.OP_CONNECT); //register the client socket to the selector

            while (connected) {
                if (readyToSend){
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE); //set interest in write operations
                    readyToSend = false;
                }

                selector.select(); // it blocks until a channel is selected
                // loop through all selected keys to check for actions
                for (SelectionKey key : selector.selectedKeys()){
                    selector.selectedKeys().remove(key); // remove the key from the selected keys
                    // continue to next iteration if key is not valid
                    if (!key.isValid()){
                        continue; //if invalid key then continue
                    }
                    if (key.isConnectable()){
                        finishConnection(key);
                    } else if (key.isReadable()) {
                        responseFromServer(key);
                    } else if (key.isWritable()){
                        sendToServer(key);
                    }
                }
            }
        } catch (Exception e){
            System.err.println("The connection is lost!");
        }
    }

    private void finishConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_WRITE); //after establishing connection the client will write to the server
        notifyListeners("\nConnected to: " + inetSocketAddress + "\nCommand => ");
    }

    private void responseFromServer(SelectionKey key) throws IOException {
        // Clears the buffer. The position is set to zero,
        // the limit is set to the capacity, and the mark is discarded
        byteBuffer.clear();
        int numBytes = socketChannel.read(byteBuffer);
        if (numBytes == -1) {
            throw new IOException("Reading from server failure!");
        }
        String receivedMessage = msgFromBuffer(); //read the message from the buffer
        msgProcess.appendMsg(receivedMessage); //add the new received string to previously received ones
        while (msgProcess.hasNextMsg()){
            String msg = msgProcess.nextMsg()[1];
            notifyListeners(msg); //notify the listeners for the server's response
        }
    }

    private String msgFromBuffer(){
        // Flips this buffer. The limit is set to the current position and then
        // the position is set to zero. If the mark is defined then it is
        // discarded.
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new String(bytes);
    }

    private void sendToServer(SelectionKey key) throws IOException{
        ByteBuffer msg;
        while ((msg = msgQueue.peek()) != null){
            socketChannel.write(msg);
            if (msg.hasRemaining()) return;
            msgQueue.remove();
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    public void startHangman() throws IOException{
        sendMsg(MsgType.START, "");
    }

    public void makeGuess(String guess) throws IOException{
        sendMsg(MsgType.GUESS, guess);
    }

    public void disconnect() throws IOException{
        connected = false;
        sendMsg(MsgType.QUIT, "");
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
    }

    private void sendMsg(MsgType msgType, String msgBody){
        ByteBuffer message = MsgProcess.messageJoiner(msgType, msgBody);
        msgQueue.add(message);
        readyToSend = true;
        selector.wakeup();
    }

    public boolean isConnected(){
        return connected;
    }

    /**
     * This method is handled by a thread pool and not the actual thread
     * that is bind with the selector
     */
    private void notifyListeners(String message){
        Executor threadPool = ForkJoinPool.commonPool();
        for (CommunicationListener listener : communicationListeners){
            threadPool.execute(() -> listener.ServerMsg(message));
        }
    }
}