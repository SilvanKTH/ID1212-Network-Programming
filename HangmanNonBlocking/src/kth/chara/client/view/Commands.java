package kth.chara.client.view;

/**
 * All possible client's commands
 */

public enum Commands {
    //to start a new game
    START,
    //to make a new guess
    GUESS,
    //to connect to the server
    CONNECT,
    //to quit and disconnect from server
    QUIT,
    //anything else typed considered wrong command
    WRONG_COMMAND
}
