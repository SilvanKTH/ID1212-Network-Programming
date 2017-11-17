package kth.chara.client.view;

import kth.chara.client.controller.Controller;
import kth.chara.client.net.OutputHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Implements Runnable for responsive UI
 */

public class NonBlockingInterpreter implements Runnable{
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private boolean newInput = false;
    private Controller controller;

    public void start(){
        if (newInput) return;
        newInput = true;
        controller = new Controller();
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
        System.out.print("Command => ");
        while (newInput){
            try {
                ProcessCmd processCmd = new ProcessCmd(br.readLine());
                if (processCmd.getMsgBody() == null || processCmd.getCommand() == Commands.WRONG_COMMAND){
                    System.out.println("\nWrong command!\nThe correct commands are: " +
                            "\nconnect\nstart\nguess <your letter/word>\nquit\n");
                    System.out.print("Command => ");
                }
                else {
                    //check first if user has connected to start any further actions
                    if ((processCmd.getCommand() != Commands.CONNECT && !controller.isConnected()) && processCmd.getCommand() != Commands.QUIT) {
                        System.out.println("\nFirst you need to connect!\nUse the command: connect\n");
                        System.out.print("Command => ");
                        continue;
                    }
                    switch (processCmd.getCommand()){
                        case CONNECT:
                            //Connect to localhost at port 8080
                            controller.connect("127.0.0.1", 8080, new ServerResponse());
                            break;
                        case START:
                            controller.startHangman();
                            break;
                        case GUESS:
                            controller.makeGuess(processCmd.getMsgBody());
                            break;
                        case QUIT:
                            newInput = false;
                            controller.disconnect();
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

    private class ServerResponse implements OutputHandler{
        @Override
        public void ServerMsg(String message){
            System.out.println(message);
        }
    }
}
