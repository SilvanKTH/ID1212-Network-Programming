package kth.chara.client.view;

import kth.chara.client.net.CommunicationListener;
import kth.chara.client.net.ServerConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Implements Runnable for responsive UI
 * Now the view package has a direct communication with the net package, while skipping
 * the controller
 */
public class NonBlockingInterpreter implements Runnable{
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private boolean newInput = false;
    private ServerConnection serverConnection;

    public void start(){
        if (newInput) return;
        newInput = true;
        serverConnection = new ServerConnection(); //direct connection with ServerConnection class in net package
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("Welcome to Hangman!\n" +
                "The commands of the game are:\n" +
                "connect                    //to initialize a connection with the server//\n" +
                "start                      //to start a new game//\n" +
                "guess <your letter/word>   //to make a guess//\n" +
                "quit                       //to quit and disconnect from server//\n");
        System.out.println("Command => ");
        while (newInput){
            try {
                ProcessCmd processCmd = new ProcessCmd(br.readLine());
                if (processCmd.getMsgBody() == null || processCmd.getCommand() == Commands.WRONG_COMMAND){
                    System.out.println("\nWrong command!\nThe correct commands are: " +
                            "\nconnect\nstart\nguess <your letter/word>\nquit\n");
                    System.out.println("Command => ");
                }
                else {
                    //check first if user has connected to start any further actions
                    if ((processCmd.getCommand() != Commands.CONNECT && !serverConnection.isConnected()) && processCmd.getCommand() != Commands.QUIT) {
                        System.out.println("\nFirst you need to connect!\nUse the command: connect\n");
                        System.out.println("Command => ");
                        continue;
                    }
                    switch (processCmd.getCommand()){
                        case CONNECT:
                            serverConnection.addListener(new ServerResponse());
                            //Connect to localhost at port 8080
                            serverConnection.connect("127.0.0.1", 8080);
                            break;
                        case START:
                            serverConnection.startHangman();
                            break;
                        case GUESS:
                            serverConnection.makeGuess(processCmd.getMsgBody());
                            break;
                        case QUIT:
                            newInput = false;
                            serverConnection.disconnect();
                            break;
                    }
                }
            } catch (Exception e){
                if (newInput) {
                    System.out.println("Failure with command processing");
                }
            }
        }
    }

    /**
     * ServerResponse is used to get the response from the Server side in Client's console.
     * To respect the top-down architecture of the program, the class implements an interface.
     */

    private class ServerResponse implements CommunicationListener {
        @Override
        public void ServerMsg(String message){
            System.out.println(message);
        }
    }
}
