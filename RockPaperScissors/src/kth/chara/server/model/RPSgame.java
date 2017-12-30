package kth.chara.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The game algorithm.
 */
public class RPSgame {
    private String response;
    private static final int PLAYERS = 3;
    private List<Player> playerList;
    private boolean readyToPlay = false;

    public RPSgame(){
        this.playerList = new ArrayList<>();
    }

    /**
     * Define a new player object and add it to the ArrayList.
     * @param id The player's id.
     */
    public void newGame(String id) {
        Player player = new Player(id);
        playerList.add(player);
        if (playerList.size() >= PLAYERS){
            readyToPlay = true;
            response = "Game started! Play a move!";
        } else {
            response = "New player connected!";
        }
    }

    /**
     * Define the play move of the corresponding player.
     * @param id The player's id.
     * @param move The specific move play.
     */
    public void setPlay(String id, String move) {
        if (readyToPlay){
            int playedAll = 0;
            for (Player player:playerList){
                if (player.getId().equals(id)){
                    player.setPlay(move);
                }
                if (!player.getPlay().equals("none")) {
                    playedAll++;
                }
            }
            if (playedAll == playerList.size()){
                processGame();
            } else {
                response = "Player #" + id + " has made a move!";
            }
        } else {
            response = "Not ready to play. Still waiting for other players!\n";
        }
    }

    public String getResponse() {
        return response;
    }

    /**
     * Finish the corresponding game.
     * @param id The player's id that quited the game.
     */
    public void finishGame(String id) {
        readyToPlay = false;
        playerList.clear();
        response = "Player #" + id + " quited the game!\nGame finished!\nType start for a new game!\n";
    }

    /**
     * The core of the game.
     * Create the final string that will be send to all the players with the scores.
     */
    private void processGame(){
        StringBuilder sb = new StringBuilder();
        for (Player player:playerList){
            int roundScore = 0;
            int totalScore = player.getTotalScore();
            String playerMove = player.getPlay();

            for (Player playerCheckMove:playerList){
                if (!playerCheckMove.getId().equals(player.getId())){
                    if (playerMove.equals("r") && playerCheckMove.getPlay().equals("s")){
                        roundScore++;
                    } else if (playerMove.equals("p") && playerCheckMove.getPlay().equals("r")){
                        roundScore++;
                    } else if (playerMove.equals("s") && playerCheckMove.getPlay().equals("p")){
                        roundScore++;
                    }
                }
            }

            totalScore += roundScore;
            player.setTotalScore(totalScore);
            sb.append("Player #").
                    append(player.getId()).
                    append("-> Played: ").
                    append(playerMove).
                    append(", RoundScore: ").
                    append(roundScore).
                    append(", TotalScore: ").
                    append(player.getTotalScore()).
                    append("\n");
        }

        for (Player player:playerList){
            player.setPlay("none");
        }

        sb.append("\nFor a new round just play a new move. Otherwise type quit to exit.\n");
        response = "\n\n" + sb.toString();
    }

    /**
     * The Player class for defining player's id, play move, and totalScore.
     */
    public class Player {
        private String id;
        private String play;
        private int totalScore;

        Player(String id){
            this.id = id;
            this.play = "none";
            this.totalScore = 0;
        }

        String getId() {
            return id;
        }

        void setPlay(String play) {
            this.play = play;
        }

        String getPlay() {
            return play;
        }

        void setTotalScore(int totalScore) {
            this.totalScore = totalScore;
        }

        int getTotalScore() {
            return totalScore;
        }
    }
}
