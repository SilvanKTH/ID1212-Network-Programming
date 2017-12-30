package kth.chara.server.net;


import kth.chara.common.MsgType;

/**
 * Defining the message id, type and body from the client
 */
class PlayerMessage {
    private String fullMsg;
    private String msgId;
    private MsgType msgType;
    private String msgBody;

    PlayerMessage(String fullMsg){
        this.fullMsg = fullMsg;
        processMsg();
    }

    private void processMsg(){
        String[] words = fullMsg.split("\\s");
        switch (words[1].toUpperCase()){
            case "START":
                msgId = words[0];
                msgType = MsgType.START;
                break;
            case "PLAY":
                msgId = words[0];
                msgType = MsgType.PLAY;
                msgBody = words[2].toLowerCase();
                break;
            case "QUIT":
                msgId = words[0];
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

    String getMsgId() {
        return msgId;
    }
}
