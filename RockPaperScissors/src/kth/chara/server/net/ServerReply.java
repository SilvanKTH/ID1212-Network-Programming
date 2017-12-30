package kth.chara.server.net;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import kth.chara.server.controller.Controller;

/**
 * Responsible for processing of clients' messages and responding to them.
 */
class ServerReply {
    private static final String EXCHANGE_NAME = "serverMsg";
    private Controller controller = new Controller();

    /**
     * RabbitMQ: Sending messages to all clients queues connected to the server, by declaring a specific exchange name.
     * @param reply The message to send
     * @throws Exception In case of message queue error.
     */
    private void sendReply(String reply) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        channel.basicPublish(EXCHANGE_NAME, "", null, reply.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + reply + "'");

        channel.close();
        connection.close();
    }

    void processMsg(String msg) {
        PlayerMessage playerMessage = new PlayerMessage(msg);
        switch (playerMessage.getMsgType()) {
            case START:
                System.out.println("New game!");
                controller.startNewGame(playerMessage.getMsgId());
                try {
                    sendReply(controller.getResponse());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PLAY:
                System.out.println("New move from a player!");
                controller.newPlay(playerMessage.getMsgId(), playerMessage.getMsgBody());
                try {
                    sendReply(controller.getResponse());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case QUIT:
                System.out.println("A player just disconnected! Game is finished!");
                controller.quitGame(playerMessage.getMsgId());
                try {
                    sendReply(controller.getResponse());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
