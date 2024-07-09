package dominogame.view;

import dominogame.controller.DominoFactory;
import dominogame.controller.GameController;
import dominogame.model.Board;
import dominogame.model.Boneyard;
import dominogame.model.Domino;
import dominogame.model.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;


public class GameView extends Application {

    private BorderPane root;
    public GridPane humanHandDisplay;
    public GameController gameController;
    public ArrayList<Domino> humanPlayerHand;
    private HBox hBox1;
    private HBox hBox2;
    private DominoFactory dd;

    private Boneyard boneyard;
    private Player human;
    private Board board;
    private ArrayList<Domino> top;
    private ArrayList<Domino> bottom;
    private Domino baseCase;
    private int bCCounter =0;
    private int filler = 0;
    private Player computer;

    public SimpleIntegerProperty currentBoneyardDominos;
    public SimpleIntegerProperty currentComputerDominos;

    private boolean computerTurn;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dominos");
        root = new BorderPane();



        dd = new DominoFactory();

        currentBoneyardDominos = new SimpleIntegerProperty(14);
        currentComputerDominos = new SimpleIntegerProperty(7);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        top = new ArrayList<>();
        bottom = new ArrayList<>();
        hBox1 = new HBox();
        hBox2 = new HBox();

        gameController = new GameController();
        gameController.init();
        humanPlayerHand = gameController.getHumanHand();
        boneyard = gameController.getBoneyard();
        human = gameController.getHuman();
        board = gameController.getBoard();
        computer = gameController.getPlayerCPU();

        humanDisplay();
        boardDisplay();
        boneyardAndComputerInfoDisplay();

        Alert gameNotOverDraw = new Alert(Alert.AlertType.WARNING);
        gameNotOverDraw.setHeaderText(null);
        gameNotOverDraw.setContentText("You must draw from the boneyard since you have no valid moves.");
        if ((human.isEmptyHand()) && !(boneyard.isDominoListEmpty())) {
            gameNotOverDraw.showAndWait();
        }

    }

    private void boardDisplay() {
        hBox1.setAlignment(Pos.CENTER);
        hBox2.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(hBox1, hBox2);
        vBox.setAlignment(Pos.CENTER);

        vBox.setPadding(new Insets(0, 25, 0, dd.WIDTH/2));

        Background bg = new Background(new BackgroundFill(Color.web("#fffebf"), CornerRadii.EMPTY, Insets.EMPTY));
        vBox.setBackground(bg);

        root.setCenter(vBox);
    }



    /**
     * Displays information about how many dominos in the boneyard and hand of the computer
     */
    private void boneyardAndComputerInfoDisplay() {
        HBox dominoAmountsDisplay = new HBox();
        HBox boneyardy = new HBox();
        HBox computery = new HBox();
        dominoAmountsDisplay.setSpacing(600);
        dominoAmountsDisplay.setPrefHeight(50);
        dominoAmountsDisplay.setAlignment(Pos.TOP_CENTER);
        boneyardy.setAlignment(Pos.CENTER);
        computery.setAlignment(Pos.CENTER);


        Background bg = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
        dominoAmountsDisplay.setBackground(bg);
        Label boneYardLabel = new Label();
        Label boneYardDominosAmount = new Label("Dominó Boneyard: ");
        boneYardLabel.textProperty().bind(currentBoneyardDominos.asString());
        boneyardy.getChildren().addAll(boneYardDominosAmount,boneYardLabel);

        Label computerLabel = new Label();
        Text computerDominosAmount = new Text("Dominó de computador: " );
        computerLabel.textProperty().bind(currentComputerDominos.asString());
        computery.getChildren().addAll(computerDominosAmount,computerLabel);
        dominoAmountsDisplay.getChildren().addAll(boneyardy,computery);
        root.setBottom(dominoAmountsDisplay);

    }

    /**
     * Draws up the board for user to see
     */
    public void renderBoard(String prevSide) {
        //System.out.println("board list == " + board.getBoard());
        //System.out.println("base case index: " + board.getBoard().indexOf(baseCase) + "base case domino == " + baseCase);
        hBox1.getChildren().clear();
        hBox2.getChildren().clear();
        top.clear();
        bottom.clear();

        //helps determine what place the dominos will be based on the original inserted domino
        if (board.getBoard().indexOf(baseCase) % 2 == 0) {
            renderHelper(top, bottom);
        } else {
            renderHelper(bottom, top);
        }


        // creation of the dominos on the board based on 2 hboxes and 2 arrays
        for (int i = 0; i < top.size(); i++) {
            hBox1.getChildren().add(dd.dominoCreator(top.get(i).getLeft(), top.get(i).getRight(), false));
        }
        for (int i = 0; i < bottom.size(); i++) {
            hBox2.getChildren().add(dd.dominoCreator(bottom.get(i).getLeft(), bottom.get(i).getRight(), false));
        }

        fillerTester(prevSide);

        if (top.size() < bottom.size()) {
            filler = 1;
        }
        else {
            filler = 0;
        }

        if (gameController.checkWinner()) {
            announceWinner();
        }
    }


    public void computerPlay() {
        computer.computerPlays(board,boneyard);
        currentComputerDominos.setValue(computer.getHand().size());
        renderBoard(computer.lastSideComputerPlayers());
    }


    /**
     *  adds filler if even number of top and bottom dominos so the spacing is correct
     */
    private void fillerTester(String prevSide) {
        if (top.size() != 0 && top.size() == bottom.size() && prevSide.equals("r") && filler ==0) {
            hBox1.getChildren().add(fillRectangle());
        } else if (top.size() != 0 && top.size() == bottom.size() && prevSide.equals("l") && filler == 0) {
           // System.out.println("left");
            hBox1.getChildren().add(0,fillRectangle());
        }

        if (top.size() != 0 && top.size() == bottom.size() && prevSide.equals("r") && filler ==1) {
            hBox1.getChildren().add(0,fillRectangle());
        } else if (top.size() != 0 && top.size() == bottom.size() && prevSide.equals("l") && filler == 1) {
           // System.out.println("left");
            hBox1.getChildren().add(fillRectangle());

        }
    }

    /**
     * determines where each domino will be: top or bottom hboxes that display the dominos
     * @param top upper hbox that is within the board display vbox
     * @param bottom lower hbox that is within the board display vbox
     */
    private void renderHelper(ArrayList<Domino> top, ArrayList<Domino> bottom) {
        for (int i = 0; i < board.getBoardSize(); i+=2) {
            top.add(board.getBoard().get(i));
        }
        for (int i = 1; i < board.getBoardSize(); i+=2) {
            bottom.add(board.getBoard().get(i));
        }
    }

    /**
     * Invisible rectangle for spacing on the board
     * @return
     */
    private Rectangle fillRectangle() {
        Rectangle rectangle = new Rectangle(dd.WIDTH,dd.HEIGHT);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.TRANSPARENT);
        rectangle.setArcHeight(20);
        rectangle.setArcWidth(20);
        return rectangle;
    }


    /**
     * Displays the bottom of the scene where the user can see their own dominos and
     * the player options
     */
    private void humanDisplay() {
        humanHandDisplay = new GridPane();


        VBox humanHandControls = new VBox();

        VBox humanDisplayView = new VBox(humanHandDisplay,humanHandControls);



        humanDisplayView.setSpacing(20);
        humanDisplayView.setMinWidth(170);
        Background light = new Background(new BackgroundFill(Color.web("#462f0f"), CornerRadii.EMPTY, Insets.EMPTY));
        humanDisplayView.setBackground(light);
        humanDisplayView.setAlignment(Pos.CENTER);

        root.setRight(humanDisplayView);

        Button draw = new Button("Empate");
        draw.setMinWidth(150);
        draw.setOnAction(event -> {
            drawButton();
        });
        Button rotateBtn = new Button("Rodar Peça");
        rotateBtn.setMinWidth(150);
        rotateBtn.setOnAction(event -> {
            rotateGUIDomino();
        });
        Button placeLeft = new Button("Colocar na esquerda");
        placeLeft.setMinWidth(150);
        placeLeft.setOnAction(event -> {
            if (board.isLegalMove(humanPlayerHand.get(gameController.getHumanPlayerHandIndex()),"left")) {
                placeDominoOnLeft();
                computerPlay();
            } else {
                illegalMoveWarning();
            }
        });
        Button placeRight = new Button("Colocar na direita");
        placeRight.setMinWidth(150);
        placeRight.setOnAction(event -> {
            if (board.isLegalMove(humanPlayerHand.get(gameController.getHumanPlayerHandIndex()),"right")) {
                placeDominoOnRight();
            } else {
           illegalMoveWarning();
        }

        });



        Button rulesBtn = new Button("Regras");
        rulesBtn.setMinWidth(150);
        rulesBtn.setOnAction(event -> {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Regras do Jogo");
            alert.setHeaderText(null);
            alert.setContentText("Regras: \nCada pedra é dividida em 2 espaços iguais onde aparece um número de 0 a 6. "
                    + "As pedras cobrem todas as combinações possíveis com esses números. Pode jogar com 2, 3 ou 4 jogadores "
                    + "ou em duplas. O objetivo do jogo é colocar todas as pedras na mesa antes dos adversários e marcar pontos.");
            alert.showAndWait();

        });

        Button quitBtn = new Button("Sair");
        quitBtn.setMinWidth(150);
        quitBtn.setOnAction(event -> {

            Platform.exit();

        });

        humanHandControls.getChildren().addAll(placeLeft,placeRight,rotateBtn,draw,rulesBtn,quitBtn);
        humanHandControls.setSpacing(10);
        humanHandControls.setAlignment(Pos.CENTER);

        humanHandDisplay.setVgap(10);
        humanHandDisplay.setPadding(new Insets(0,0,0,10));
        humanHandDisplay.setAlignment(Pos.CENTER_LEFT);
        renderHumanHand();

        humanHandDisplay.setOnMouseClicked(event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();
            setClickedIndex(x, y);
        });

    }

    /**
     * Places domino from hand to the board on the right side
     */
    private void placeDominoOnRight() {

        bCCounter++;
        if (bCCounter == 1) {
            baseCase = humanPlayerHand.get(gameController.getHumanPlayerHandIndex());
        }

        board.getBoard().add(humanPlayerHand.get(gameController.getHumanPlayerHandIndex()));
        humanPlayerHand.remove(gameController.getHumanPlayerHandIndex());

        renderHumanHand();
        renderBoard("r");
        computerPlay();

    }

    /**
     * Places domino from the hand to the left side of the board
     */
    private void placeDominoOnLeft() {
        bCCounter++;
        if (bCCounter == 1) {
            baseCase = humanPlayerHand.get(gameController.getHumanPlayerHandIndex());
        }

        board.getBoard().add(0, humanPlayerHand.get(gameController.getHumanPlayerHandIndex()));
        humanPlayerHand.remove(gameController.getHumanPlayerHandIndex());

        renderHumanHand();
        renderBoard("l");
    }

    /**
     * Visualization of the current dominos in the players hand
     */
    private void renderHumanHand() {
        humanHandDisplay.getChildren().clear();
        for (int i = 0; i < humanPlayerHand.size(); i++) {
            Node domino = dd.dominoCreator(humanPlayerHand.get(i).getLeft(), humanPlayerHand.get(i).getRight(),
                    humanPlayerHand.get(i).isSelected());
            humanHandDisplay.add(domino, 1, i); // Add to column 1, row i
        }
    }



    /**
     * Sets the index of the array based on the domino that user clicks.
     * Uses location of the click to determine which index is used based on
     * how big the arraylist size is and if the x range is between 0 and 50
     * y range is between 10-100*count. Will also make the trigger highlightSelected()
     *
     * @param x horizontal coordinate of mouse click
     * @param y vertical coordinate of mouse click
     */
    private void setClickedIndex(int x, int y) {
        boolean validIndex = false;
        if (x >= 0 && x <= dd.WIDTH) { // Change to check x range instead of y

            int count = 0;
            for (int i = 0; i < humanPlayerHand.size(); i++) {
                // System.out.println("array size== " +humanPlayerHand.size());

                if ((y >= 10 + count) && (y <= 10 + count + dd.HEIGHT)) { // Change to check y range instead of x
                    for (int j = 0; j < humanPlayerHand.size(); j++) {
                        humanPlayerHand.get(j).setSelected(false);
                    }

                    gameController.setHumanPlayerHandIndex(i);
                    humanPlayerHand.get(i).setSelected(true);
                    renderHumanHand();
                    validIndex = true;
                    break;
                }
                count = 10 + count + dd.HEIGHT; // Update count based on dd.HEIGHT
            }
        }
        if (!validIndex) {
            gameController.setHumanPlayerHandIndex(7);
        }
        // System.out.println("current index== " + gameController.getHumanPlayerHandIndex());
    }


    /**
     * draws from the boneyard
     */
    private void drawButton() {
        if (!human.playerHasLegitMove(board)) {
            human.drawFromBoneyard(boneyard);
            currentBoneyardDominos.setValue(boneyard.getDominoListSize());
            renderHumanHand();
        } else {
            Alert cannotDraw = new Alert(Alert.AlertType.WARNING);
            cannotDraw.setHeaderText(null);
            cannotDraw.setContentText("You can't draw if you have a playable domino.");
            cannotDraw.showAndWait();
        }

    }

    private void rotateGUIDomino() {
       // System.out.println(humanPlayerHand.get(gameController.getHumanPlayerHandIndex()));
        humanPlayerHand.get(gameController.getHumanPlayerHandIndex()).flipDomino();
        renderHumanHand();
    }

    public void announceWinner() {
        Alert winnerAlert = new Alert(Alert.AlertType.CONFIRMATION);
        winnerAlert.setHeaderText("Winner!");
        winnerAlert.setContentText(gameController.showWinner() + " won!");
        winnerAlert.showAndWait();
        System.exit(1);
    }

    public void illegalMoveWarning() {
        Alert illegalAlert = new Alert(Alert.AlertType.WARNING);
        illegalAlert.setHeaderText("Illegal Move!");
        illegalAlert.setContentText("That is not a legal move.");
        illegalAlert.showAndWait();

    }



}

