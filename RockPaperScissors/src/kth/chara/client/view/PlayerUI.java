package kth.chara.client.view;

import kth.chara.client.controller.Controller;
import kth.chara.client.net.OutputHandler;
import kth.chara.common.MsgType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Implements Runnable for responsive UI
 */

public class PlayerUI implements Runnable{
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private boolean newInput = false;
    private Controller controller;
    private int id;

    public void start(){
        if (newInput) return;
        newInput = true;
        controller = new Controller();
        Random random = new Random();
        this.id = 1 + random.nextInt(300);
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("Welcome to Rock-Paper-Scissors game - Player# " + id + " !\n" +
                "The commands of the game are:\n" +
                "start                      //to start a new game//\n" +
                "play r/p/s                 //to play a move, where 'r' for rock, 'p' for paper, 's' for scissors//\n" +
                "quit                       //to quit the game//\n");
        System.out.print("id#" + id + "> ");
        while (newInput){
            try {
                ProcessCmd processCmd = new ProcessCmd(br.readLine());
                if (processCmd.getMsgBody() == null || processCmd.getCommand() == MsgType.WRONG_COMMAND){
                    System.out.println("\nWrong command!\nThe correct commands are: " +
                            "\nstart\nplay r/p/s\nquit\n");
                    System.out.print("id#" + id + "> ");
                }
                else {
                    //check first if user has requested to start a new game for any further actions
                    if ((processCmd.getCommand() != MsgType.START && !controller.isConnected()) && processCmd.getCommand() != MsgType.QUIT) {
                        System.out.println("\nFirst you need to start a new game!\nUse the command: start\n");
                        System.out.print("id#" + id + "> ");
                        continue;
                    }
                    switch (processCmd.getCommand()){
                        case START:
                            controller.startGame(id, new ServerResponse());
                            break;
                        case PLAY:
                            controller.playMove(id, processCmd.getMsgBody());
                            break;
                        case QUIT:
                            newInput = false;
                            controller.disconnect(id);
                            System.exit(0);
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
            System.out.print("id#" + id + "> ");
        }
    }
}
