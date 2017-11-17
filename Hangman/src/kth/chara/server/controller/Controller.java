package kth.chara.server.controller;

import kth.chara.server.model.Hangman;

public class Controller {
    private Hangman hangman = new Hangman();

    //begin new game instance for calling the constructor of the Hangman class
    public void newHangman(){
        hangman.newInstance();
    }

    //start a new game
    public void startNewGame(){
        hangman.newGame();
    }

    //make a new guess
    public void newGuess(String guess){
        hangman.setGuess(guess);
    }

    //get the response from the server
    public String getResponse(){
        return hangman.getResponse();
    }
}
