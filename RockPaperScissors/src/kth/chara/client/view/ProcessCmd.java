package kth.chara.client.view;

import kth.chara.common.MsgType;

/**
 * The message of the player is analyzed to export the type and the body of it.
 */

class ProcessCmd {
    private MsgType msgType;
    private String msgBody;
    private final String fullMsg;

    ProcessCmd(String fullMsg){
        this.fullMsg = fullMsg;
        processMsg();
    }

    private void processMsg(){
        String[] words = fullMsg.split("\\s");//splits the string based on whitespace
        switch (words[0].toUpperCase()){
            case "START":
                msgType = MsgType.START;
                if (words.length != 1){
                    msgBody = null;
                } else {
                    msgBody = "";
                }
                break;
            case "PLAY":
                msgType = MsgType.PLAY;
                if (words.length != 2) {
                    msgBody = null;
                } else {
                    if (words[1].equals("r") || words[1].equals("p") || words[1].equals("s")){
                        msgBody = words[1];
                    } else {
                        msgBody = null;
                    }
                }
                break;
            case "QUIT":
                msgType = MsgType.QUIT;
                if (words.length != 1){
                    msgBody = null;
                } else {
                    msgBody = "";
                }
                break;
            default:
                msgBody = null;
                msgType = MsgType.WRONG_COMMAND;
                break;
        }
    }

    MsgType getCommand(){
        return msgType;
    }

    String getMsgBody(){
        return msgBody;
    }
}

