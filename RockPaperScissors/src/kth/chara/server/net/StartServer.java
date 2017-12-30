package kth.chara.server.net;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * RabbitMQ: Start server for receiving messages in a message queue with a specific name.
 */
public class StartServer {
    private final static String QUEUE_NAME = "clientRequest";
    private static ServerReply serverReply = new ServerReply();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("Server is up and running!\nWaiting for messages...");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    serverReply.processMsg(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
