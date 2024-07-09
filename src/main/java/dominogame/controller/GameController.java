package dominogame.controller;

import dominogame.model.Board;
import dominogame.model.Boneyard;
import dominogame.model.Domino;
import dominogame.model.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class GameController {

    private Boneyard boneyard;
    private Player playerHuman;
    private Player playerCPU;
    private Board board;
    private Scanner sc = new Scanner(System.in);
    private int turns;
    private int humanPlayerHandIndex;

    public GameController() {
        boneyard = new Boneyard();
        playerHuman = new Player();
        playerCPU = new Player();
        board = new Board();
    }

    public void init() {
        boneyard.initalizeDominos();
        playerHuman.setMyHand(boneyard.fillPlayerHands());
        playerCPU.setMyHand(boneyard.fillPlayerHands());
    }






    public boolean checkWinner() {
        if (boneyard.isEmpty() && (playerHuman.isEmptyHand() || playerCPU.isEmptyHand())) {
            return true;
        } else {
            return false;
        }
    }



    public String showWinner() {
        int humanScore = playerHuman.countScores();
        int computerScore = playerCPU.countScores();
        if (humanScore == computerScore) {
            if (turns > playerCPU.countTurns()) {
                return "You";
            } else {
                return "Computer";
            }
        }
        else if (humanScore < computerScore){
            return "You";
        } else {
            return "Computer";
        }
    }

    public Board getBoard() {
        return board;
    }

    public Boneyard getBoneyard() {
        return boneyard;
    }

    public ArrayList<Domino> getHumanHand() {
        return playerHuman.getHand();
    }

    public Player getHuman() {
        return playerHuman;
    }

    /**
     * These two functions are for when user clicks on a domino,
     * setters and getters for the arraylist index of the human hand
     * @param index use to set the human hand index
     */
    public void setHumanPlayerHandIndex (int index) {
        humanPlayerHandIndex = index;
    }

    /**
     * These two functions are for when user clicks on a domino,
     * setters and getters for the arraylist index of the human hand
     * This one returns the current human hand index
     */
    public int getHumanPlayerHandIndex () {
        return humanPlayerHandIndex;
    }

    public Player getPlayerCPU() {
        return playerCPU;
    }

}
