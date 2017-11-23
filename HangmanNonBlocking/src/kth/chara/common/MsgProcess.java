package kth.chara.common;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringJoiner;

/**
 * Class responsible for all the message processing that occurs
 * for both Client and Server
 */
public class MsgProcess {
    private StringBuilder receivedMsg = new StringBuilder();
    private final Queue<String[]> msgQueue = new ArrayDeque<>();

    /**
     * Wrap a string message to a ByteBuffer
     *
     * @param msgType The message type, e.g. START, GUESS etc.
     * @param msgBody The body of the message
     */
    public static ByteBuffer messageJoiner(MsgType msgType, String msgBody) {
        StringJoiner joiner = new StringJoiner("##");
        joiner.add(msgType.toString());
        joiner.add(msgBody);
        String msg = withLengthHeader(joiner.toString()); //include header length
        return ByteBuffer.wrap(msg.getBytes());
    }

    private static String withLengthHeader(String msg) {
        StringJoiner joiner = new StringJoiner("###");
        joiner.add(Integer.toString(msg.length()));
        joiner.add(msg);
        return joiner.toString();
    }

    /**
     * Add the new received string to previously received ones.
     *
     * @param msg The new message to add
     */
    public void appendMsg(String msg) {
        receivedMsg.append(msg);
        while (extractMsg());
    }

    /**
     * @return <code>String[]</code> with the message type and body
     *         <code>null</code> if there isn't any other message in the queue
     */
    public String[] nextMsg() {
        return msgQueue.poll();
    }

    /**
     * @return <code>true</code> if there is still a message in the queue
     *         <code>false</code> if there isn't any other message in the queue
     */
    public boolean hasNextMsg() {
        return !msgQueue.isEmpty();
    }

    private boolean extractMsg() {
        String msgToString = receivedMsg.toString();
        String[] splitAtHeaderLength = msgToString.split("###");

        if (splitAtHeaderLength.length < 2)
        {
            return false; // received only part of the msg
        }

        int msgHeaderLength = Integer.parseInt(splitAtHeaderLength[0]);

        if (completeMsg(msgHeaderLength, splitAtHeaderLength[1])) {
            String msg = splitAtHeaderLength[1].substring(0, msgHeaderLength); //keep the body
            String[] finalMsg = msg.split("##"); //extract the delimiter
            msgQueue.add(finalMsg); // add to message queue
            receivedMsg.delete(0, msgToString.length()); // remove the extracted message
            return true;
        }
        return false;
    }

    private boolean completeMsg(int msgHeaderLength, String msg) {
        return msg.length() >= msgHeaderLength;
    }
}