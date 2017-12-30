package kth.chara.server.controller;


import kth.chara.server.model.RPSgame;

public class Controller {
    private RPSgame rpsGame = new RPSgame();

    /**
     * Start a new game.
     * @param id The player's id.
     */
    public void startNewGame(String id){
        rpsGame.newGame(id);
    }

    /**
     * A new move.
     * @param id The player's id.
     * @param move The specific move of the player.
     */
    public void newPlay(String id, String move){
        rpsGame.setPlay(id, move);
    }

    /**
     * Quit the game.
     * @param id The player's id.
     */
    public void quitGame(String id){
        rpsGame.finishGame(id);
    }

    /**
     * Response from the server.
     * @return The response string.
     */
    public String getResponse(){
        return rpsGame.getResponse();
    }
}
