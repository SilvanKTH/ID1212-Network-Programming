package kth.chara.common;

/**
 * All possible message types for the client and server
 */

public enum MsgType {
    // client msg to start a new game
    START,
    // client msg to make a new guess
    GUESS,
    // client msg to disconnect
    QUIT,
    // server msg when it responds to the client
    SERVER_RESPONSE
}
