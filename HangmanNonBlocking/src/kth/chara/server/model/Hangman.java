package kth.chara.server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Hangman {
    private NewHangman newHangman;

    public void newInstance(){
        newHangman = new NewHangman();
    }

   public void newGame (){
       newHangman.startNew();
    }

    public void setGuess(String playerGuess){
       newHangman.getGuess(playerGuess);
    }

    public String getResponse(){
        return newHangman.getResult();
    }

    /**
     * A private class for different instances of the game
     */

    private class NewHangman {
        private String newWord;
        private List<String> guesses = new ArrayList<>();
        private String underscore;
        private int count;
        private int score;
        private boolean playing = false;
        private boolean win;
        private boolean wrongGuess;
        private boolean startNew = false;

        private NewHangman() {
            this.score = 0;
        }

        void startNew(){
            this.playing = true;
            this.win = false;
            this.wrongGuess = false;
            this.startNew = true;
            this.guesses.clear();
            String filepath = "src/words.txt";
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filepath))) {
                List<String> wordsList = reader.lines().collect(Collectors.toList());
                wordsList.replaceAll(String::toLowerCase);
                int randomNum = ThreadLocalRandom.current().nextInt(0, wordsList.size()+1);
                this.newWord = wordsList.get(randomNum);
                this.underscore = new String(new char[newWord.length()]).replace("\0", "_");
                this.count = newWord.length();
            } catch (IOException e) {
                System.err.printf("Failed to read the file \"%s\"", filepath);
                e.printStackTrace();
            }
        }

        void getGuess(String pGuess){
            if (count != 0 && underscore.contains("_") && playing) {
                guesses.add(pGuess);
                hangAlgorithm(pGuess);
            }
        }

        void hangAlgorithm(String guess){
            StringBuilder newUnderscore = new StringBuilder();
            if (Objects.equals(guess, newWord)) {
                win = true;
                score++;
            }
            else {
                if (guess.length() == 1){
                    for (int i = 0; i < newWord.length(); i++) {
                        if (newWord.charAt(i) == guess.charAt(0)) {
                            newUnderscore.append(guess.charAt(0));
                        } else if (underscore.charAt(i) != '_') {
                            newUnderscore.append(newWord.charAt(i));
                        } else {
                            newUnderscore.append("_");
                        }
                    }

                    if (underscore.equals(newUnderscore.toString())) {
                        count--;
                        wrongGuess = true;
                        if (count == 0) {
                            score--;
                        }
                    } else {
                        underscore = newUnderscore.toString();
                        wrongGuess = false;
                        if (underscore.equals(newWord)) {
                            win = true;
                            score++;
                        }
                    }
                }
                else {
                    count--;
                    if (count == 0){
                        score--;
                    }
                    wrongGuess = true;
                }
            }
        }

        String getResult(){
            String response;
            if (!playing){
                return "\nFirst you need to start a new game to start guessing!";
            }
            if (startNew) {
                response = "\nHidden word: " + newWord +  " (included just to be able to test the program)" +
                        "\nNew Hangman game!" + "\nWord: " + underscore + "\nGuesses: " + guesses + "\nAttempts left: " + count
                        + "\nPlayer's score: " + score + "\nNext guess => ";
                startNew = false;
                return response;
            }
            if (count == 0){
                response = "\nYou lost!\n" + "\nYour score: " + score + "\nFor new game type 'start', or 'quit' to exit => ";
                return response;
            }
            if (!win && !wrongGuess){
                response = "\nWord: " + underscore + "\nGuesses: " + guesses + "\nAttempts left: " + count
                        + "\nPlayer's score: " + score + "\nNext guess => ";
                return response;
            }
            else if (!win && wrongGuess){
                response = "\nWrong guess!\n" + "\nWord: " + underscore + "\nGuesses: " + guesses + "\nAttempts left: " + count
                        + "\nPlayer's score: " + score + "\nNext guess => ";
                return response;
            }
            else {
                response = "\nCorrect! You win! The word was " + newWord + "\nYour score: " + score + "\nFor new game type 'start', or 'quit' to exit => ";
                return response;
            }
        }
    }
}