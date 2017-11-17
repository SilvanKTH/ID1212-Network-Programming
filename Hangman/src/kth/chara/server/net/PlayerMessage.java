package kth.chara.server.net;


import kth.chara.common.MsgType;

/**
 * Defining the message type and body from the client
 */

class PlayerMessage {
    private String fullMsg;
    private MsgType msgType;
    private String msgBody;

    PlayerMessage(String fullMsg){
        this.fullMsg = fullMsg;
        processMsg();
    }

    private void processMsg(){
        String[] words = fullMsg.split("\\s");
        switch (words[0].toUpperCase()){
            case "START":
                msgType = MsgType.START;
                break;
            case "GUESS":
                msgType = MsgType.GUESS;
                msgBody = words[1].toLowerCase();
                break;
            case "QUIT":
                msgType = MsgType.QUIT;
                break;
            default:
                System.out.println("\nClient message corrupted!\n");
        }
    }

    MsgType getMsgType() {
        return msgType;
    }

    String getMsgBody() {
        return msgBody;
    }
}
