package kth.chara.common;

/**
 * All possible message types for the client and server
 */

public enum MsgType {
    // client msg to start a new game
    START,
    // client msg to play a new move
    PLAY,
    // client msg to disconnect
    QUIT,
    // anything else typed considered wrong command
    WRONG_COMMAND
}
