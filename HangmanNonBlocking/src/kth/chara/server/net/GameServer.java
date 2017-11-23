package kth.chara.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Handling the game server
 */
public class GameServer {
    private static final int T_LINGER = 5000; //linger time
    private static int port = 8080; //default listening port
    private Selector selector;
    private final Queue<SelectionKey> queueKey = new ArrayDeque<>(); //corresponds to selection keys with msgs to send


    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        System.out.println("Server started at port: " + port);
        server.start();
    }

    /**
     * Starting the non-blocking server
     */
    private void start(){
        try{
            selector = Selector.open(); //one selector for all operations
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false); //make it non-blocking
            serverSocketChannel.bind(new InetSocketAddress(port)); //bind int with the specific port
            //register server socket channel to selector with interest in accepting operations
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true){
                //Retrieves and removes the head of this queue
                //while setting the interest to write operations for these keys
                while (!queueKey.isEmpty()) {
                    queueKey.poll().interestOps(SelectionKey.OP_WRITE);
                }

                selector.select(); //blocks until a channel is selected
                //scan all the keys connected with the selector
                for (SelectionKey key : selector.selectedKeys()){
                    selector.selectedKeys().remove(key); //remove the key from selector
                    if (!key.isValid()) continue; //if it's not valid continue

                    if (key.isAcceptable()){
                        connectPlayer(key);
                    } else if (key.isReadable()){
                        readFromPlayer(key);
                    } else if (key.isWritable()){
                        writeToPlayer(key);
                    }
                }
            }
        } catch (Exception e){
            System.err.println("Server failed!");
        }
    }

    private void connectPlayer(SelectionKey key) throws IOException {
        //return the channel for which this specific key was created
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel playerChannel = serverSocketChannel.accept(); //accept connection
        playerChannel.configureBlocking(false); //make it non-blocking channel
        PlayerHandler playerHandler = new PlayerHandler(this, playerChannel);
        //register the player channel to our selector for the new player,
        //while setting interest to read operations
        playerHandler.setKey(playerChannel.register(selector, SelectionKey.OP_READ, playerHandler));
        playerChannel.setOption(StandardSocketOptions.SO_LINGER, T_LINGER);
        System.out.println("New player connected!");
    }

    private void readFromPlayer(SelectionKey key) throws IOException{
        PlayerHandler playerHandler = (PlayerHandler) key.attachment(); //we retrieve the attachment from the key
        try{
            playerHandler.receiveMessage();
        }catch (IOException clientClosed){
            System.out.println("Player closed connection!");
            deleteClient(key);
        }
    }

    private void writeToPlayer(SelectionKey key) throws IOException{
        PlayerHandler playerHandler = (PlayerHandler) key.attachment();
        playerHandler.sendMsg();
        key.interestOps(SelectionKey.OP_READ); //after responding to player we are interested in read operations
    }

    private void deleteClient(SelectionKey key) throws IOException{
        PlayerHandler playerHandler = (PlayerHandler) key.attachment();
        playerHandler.disconnectPlayer();
        key.cancel(); //cancel the selection key of the specific player
    }

    void selectorOn(){
        selector.wakeup(); //causes the first selection operation that has not yet returned to return immediately.
    }

    void appendQueueKey(SelectionKey key){
        try {
            queueKey.add(key); //add the specific key to the queue to set write operations
        } catch (Exception e){
            System.out.println("Player's channel key error");
        }
    }
}