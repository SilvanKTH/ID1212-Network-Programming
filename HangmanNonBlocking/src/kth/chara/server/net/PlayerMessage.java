package kth.chara.server.net;


import kth.chara.common.MsgType;

/**
 * Defining the message type and body from the client
 */

class PlayerMessage {
    private String[] fullMsg;

    PlayerMessage(String[] fullMsg){
        this.fullMsg = fullMsg;
    }

    MsgType getMsgType() {
        return MsgType.valueOf(fullMsg[0]);
    }

    String getMsgBody() {
        return fullMsg[1];
    }
}
