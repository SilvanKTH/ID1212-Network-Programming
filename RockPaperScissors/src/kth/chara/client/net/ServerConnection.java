package kth.chara.client.net;

import com.rabbitmq.client.*;
import kth.chara.common.MsgType;

import java.io.IOException;

/**
 * The implementation of connection between client and server using RabbitMQ.
 */
public class ServerConnection {
    private OutputHandler serverResponse;
    private volatile boolean connected;
    private final static String QUEUE_NAME = "clientRequest";
    private static final String EXCHANGE_NAME = "serverMsg";
    private int numStarts = 0;


    /**
     * RabbitMQ: Sending messages through a specific channel, while establishing a new connection.
     * @param msg The specific message to send to server's queue.
     * @throws Exception In case of message queue error.
     */
    private void sendMsg(String msg) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes("UTF-8"));

        channel.close();
        connection.close();
    }

    /**
     * RabbitMQ: Receiving server messages to a queue by declaring a specific exchange name.
     * @throws Exception In case of message queue error.
     */
    private void receiveMsg() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                serverResponse.ServerMsg(message);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    public void startRPS(int id, OutputHandler serverResponse) throws Exception {
        numStarts++;
        this.serverResponse = serverResponse;
        connected = true;
        if (numStarts <= 1){
            receiveMsg();
        }
        sendMsg(String.valueOf(id) + " " + MsgType.START.toString());
    }

    public void playRPS(int id, String move) throws Exception{
        sendMsg(String.valueOf(id) + " " + MsgType.PLAY.toString() + " " + move);
    }

    public void disconnect(int id) throws Exception{
        sendMsg(String.valueOf(id) + " " + MsgType.QUIT.toString());
        connected = false;
    }

    public boolean isConnected(){
        return connected;
    }

}