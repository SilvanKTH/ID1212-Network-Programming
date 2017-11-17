package kth.chara.client.view;

/**
 * The message of the client is analyzed to export the type and the body of it.
 */

public class ProcessCmd {
    private Commands msgType;
    private String msgBody;
    private final String fullMsg;

    ProcessCmd(String fullMsg){
        this.fullMsg = fullMsg;
        processMsg();
    }

    private void processMsg(){
        String[] words = fullMsg.split("\\s");//splits the string based on whitespace
        switch (words[0].toUpperCase()){
            case "CONNECT":
                msgType = Commands.CONNECT;
                if (words.length != 1){
                    msgBody = null;
                } else {
                    msgBody = "";
                }
                break;
            case "START":
                msgType = Commands.START;
                if (words.length != 1){
                    msgBody = null;
                } else {
                    msgBody = "";
                }
                break;
            case "GUESS":
                msgType = Commands.GUESS;
                if (words.length != 2) {
                    msgBody = null;
                } else {
                    msgBody = words[1];
                }
                break;
            case "QUIT":
                msgType = Commands.QUIT;
                if (words.length != 1){
                    msgBody = null;
                } else {
                    msgBody = "";
                }
                break;
            default:
                msgBody = null;
                msgType = Commands.WRONG_COMMAND;
                break;
        }
    }

    Commands getCommand(){
        return msgType;
    }

    String getMsgBody(){
        return msgBody;
    }
}
