//package View;
//
// {
//}

package View;

import Server.Configurations;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyViewController implements IView, Initializable, Observer {

    private MyViewModel viewModel;

    public static Stage stage;
    public BorderPane pane;
    public Menu solution_menu;
    public MenuItem save;
    public CheckMenuItem sol_show;
    public CheckMenuItem mute;
    public MazeDisplayer mazeDisplayer;
    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    //TODO: currently not used
    StringProperty SoundFileNameOpen = new SimpleStringProperty();
    StringProperty SoundFileNameMaze = new SimpleStringProperty();
    StringProperty SoundFileNameWin = new SimpleStringProperty();
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label lbl_playerRow;
    public Label lbl_PlayerCol;
    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }
    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void generateMaze(ActionEvent actionEvent) throws IOException {

        //check args
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        viewModel.generateMaze(rows, cols);
        mazeDisplayer.setSolution(null);
    }
    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze();
    }

    //Music:
    //Opening song:
    Media openingSong = new Media(new File("./resources/music/Field-Trip-open.mp3").toURI().toString());
    MediaPlayer openingSongPlayer = new MediaPlayer(openingSong);
    //During the maze song:
    Media mazeSong = new Media(new File("./resources/music/Filter-Attempt-maze.mp3").toURI().toString());
    MediaPlayer mazeSongPlayer = new MediaPlayer(mazeSong);
    //Winning song:
    Media winSong = new Media(new File("./resources/music/Happy-nemoFound.mp3").toURI().toString());
    MediaPlayer winSongPlayer = new MediaPlayer(winSong);

    public void setMyViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    public void setUpdatePlayerRow(int row) {
        this.updatePlayerRow.set("" + row);
    }
    public void setUpdatePlayerCol(int col) {
        this.updatePlayerCol.set(""+col);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void generateMaze(String row, String col) throws IOException {

        //check args
        try {
            int rows = Integer.valueOf(row);
            int cols = Integer.valueOf(col);

            if (rows < 2 || cols < 2 || rows > 100 || cols > 100){
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setContentText("Number of rows and columns must be between 2 and 100!");
                b.showAndWait();
                setMazeParamWindow();
            }

            viewModel.generateMaze(rows, cols);
        }
        catch (NumberFormatException e){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("You must enter numbers!");
            a.showAndWait();
            setMazeParamWindow();
        }


      //  mazeDisplayer.setSolution(null); TODO: see if needed
    }

    @Override
    public void solveMaze() {
        viewModel.solveMaze();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent.getCode());
        keyEvent.consume();
    }

    public void Exit(ActionEvent actionEvent) {
        viewModel.exit();
        Platform.exit();
    }

    public void zoom(ScrollEvent scrollEvent) {
        mazeDisplayer.zoom(scrollEvent);
    }

    public void drag(MouseEvent mouseEvent) {
        if(viewModel.getMaze() == null)
        {
            return;
        }
        double x = mouseEvent.getX() / (mazeDisplayer.getWidth() / viewModel.getMaze().getMaze()[0].length);
        double y = mouseEvent.getY() / (mazeDisplayer.getHeight() / viewModel.getMaze().getMaze().length);
        if(x > viewModel.getPlayerCol()+1){
            viewModel.movePlayer(KeyCode.NUMPAD6); //RIGHT
        }
        else if(x < viewModel.getPlayerCol()){
            viewModel.movePlayer(KeyCode.NUMPAD4); //LEFT
        }
        else if(y < viewModel.getPlayerRow()){
            viewModel.movePlayer(KeyCode.NUMPAD8); //UP
        }
        else if(y > viewModel.getPlayerRow()+1){
            viewModel.movePlayer(KeyCode.NUMPAD2); //DOWN
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String)arg;
        switch (change){
            case "maze generated":
                mazeGenerated();
                break;
            case "player moved":
                playerMoved();
                break;
            case "maze solved":
                mazeSolved();
                break;
        }
    }

    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setGetPlayerPos(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
        int gr = viewModel.getGoalRow();
        int gc = viewModel.getGoalCol();
        if (row == gr && col == gc)
            goalReached();
    }

    //update functions:
    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    private void mazeGenerated() {
        mazeDisplayer.drawMaze(viewModel.getMaze());
        if (solution_menu.isDisable()) //TODO: check
            solution_menu.setDisable(false);
        if (save.isDisable())
            save.setDisable(false);
    }

    public void MoveScene(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("MazeScreen.fxml").openStream());
            Scene scene = new Scene(root, 500, 500);
            scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
            stage.setScene(scene);

            MyViewController myViewController =fxmlLoader.getController();
            myViewController.setStage(stage);
            myViewController.setMyViewModel(viewModel);
            viewModel.addObserver(myViewController);

            myViewController.setResizeEvent(scene);
            openingSongPlayer.pause();
            stage.show();
            playMazeMusic();
            setMazeParamWindow();

        } catch (Exception e) {

        }
    }

    public void goalReached() {
//        controlMusic("stop", mazeSongPlayer);
//        controlMusic("play", winSongPlayer);
        playWinMusic();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        ButtonType newGame = new ButtonType("New Game");
//        ButtonType exit = new ButtonType("Exit Game");
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",newGame,exit);
        alert.setHeight(300);
        alert.setWidth(500);
        alert.setTitle("You found Nemo!");
        alert.setResizable(true);
        alert.setHeaderText("YAY! YOU DID IT!");
        alert.setContentText("Congratulations! You helped Marlin and Dory find Nemo!\n" +
                "To start a new game click OK.\n" +
                "To exit the program click Cancel.");

        Optional<ButtonType> selcted = alert.showAndWait();
        if(!selcted.isPresent());
            // alert is exited, no button has been pressed.
        else if(selcted.get() == ButtonType.OK)
        {
            try {
                winSongPlayer.pause();
                generateMaze(String.valueOf(viewModel.getMaze().getMaze().length), String.valueOf(viewModel.getMaze().getMaze()[0].length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(selcted.get() == ButtonType.CANCEL)
        {
            winSongPlayer.pause();
            Exit();
        }
    }

    //menu bar:
    public void New(ActionEvent actionEvent) {
        setMazeParamWindow();
    }

    public void show_hide_sol(ActionEvent actionEvent) {
        if (!sol_show.isSelected())
            mazeDisplayer.setSolution(null);
        else
            solveMaze();
    }


    public void About(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("About our game:\n" +
                "Our names are Ariel Perelman and Diana Prochorov.\n" +
                "We created this Maze game using MyMazeGenerator and we solve it using BFS searching algorithm.\n" +
                "We hope you enjoy!");
        alert.show();
    }

    public void Proparties(ActionEvent actionEvent) throws IOException {
        //String generate = Configurations.getInstance().getMazeGeneratingAlgorithm().toString();
        String solveMaze = Configurations.getInstance().getMazeSearchingAlgorithm().getName();
        String numberThreads = Configurations.getInstance().getThreadPoolSize();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Properties:\n" +
                "The generating algorithm is MyMazeGenerator" + "\n" +
                "The solving algorithm is " + solveMaze + "\n" +
                "The number of threads in the pool is "+numberThreads);
        alert.show();
    }

    public void Instructions(ActionEvent actionEvent) { //TODO:change photo and name in instruction if there is time change to stage
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setHeight(100);
        alert.setWidth(300);
        Image image = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Emojione_1F62D.svg/64px-Emojione_1F62D.svg.png");
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);
        alert.setTitle("Instructions");
        alert.setResizable(true);
        alert.setHeaderText("Instructions:");
        alert.setContentText("In this game you must help Marlin and Dory find Nemo by completing the maze.\n" +
                "You can walk through the maze using the NumPad on your keyboard:\n" +
                "1: Go diagonally down-left\n" +
                "2: Go down\n" +
                "3: Go diagonally down-right\n" +
                "4: Go left\n" +
                "6: Go right\n" +
                "7: Go diagonally up-left\n" +
                "8: Go up\n" +
                "9: Go diagonally up-right\n" +
                "Another way you can play is by dragging Marlin and Dory using the mouse.\n" +
                "On the game display you can see walls - character A cannot go through them!\n" +
                "Also, you may walk diagonally only if the up/down or left/right step in the direction of the diagonal is a path.");
        alert.show();
    }

    public void Save(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File File = fileChooser.showSaveDialog(null);

        if (File != null) {
            viewModel.saveMaze(File);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("File was not saved.");
        }
    }

    public void Load(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("maze", "*.maze"));
        File file= fileChooser.showOpenDialog(stage);
        if(file!=null)
        {
            if (file.getPath().endsWith(".maze"))
                viewModel.load(file);
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Can't load a file that is not .maze.");
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("File was not loaded.");
        }
    }

    public void Mute(ActionEvent actionEvent) {
        if (mute.isSelected()){
            openingSongPlayer.setMute(true);
            mazeSongPlayer.setMute(true);
            winSongPlayer.setMute(true);
        }
        else {
            openingSongPlayer.setMute(false);
            mazeSongPlayer.setMute(false);
            winSongPlayer.setMute(false);
        }
    }

    public void Exit() {
        viewModel.exit();
        Platform.exit();
    }



    private void setMazeParamWindow(){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Set parameters");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField rows = new TextField();
        rows.setPromptText("Enter number of rows");
        TextField columns = new TextField();
        columns.setPromptText("Enter number of columns");

        gridPane.add(new Label("Rows:"), 0, 0);
        gridPane.add(rows, 1, 0);
        gridPane.add(new Label("Columns:"), 2, 0);
        gridPane.add(columns, 3, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(() -> rows.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(rows.getText(), columns.getText());
            }
            return null;
        });


        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            try {
                generateMaze(pair.getKey(), pair.getValue());

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setResizeEvent(Scene scene) {
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {

                mazeDisplayer.setHeight((Double) newSceneHeight-35.3333);
                mazeDisplayer.draw();
            }
        });
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {

                mazeDisplayer.setWidth((Double) newSceneWidth-10);
                mazeDisplayer.draw();
            }
        });

    }

    public void playOpeningMusic(){
        if (openingSongPlayer == null)
            return;
        openingSongPlayer.play();
        openingSongPlayer.setAutoPlay(true);
        openingSongPlayer.setOnEndOfMedia(new Runnable() { //non-stoppable song, will repeat the song when finish
            public void run() {
                openingSongPlayer.seek(Duration.ZERO);
            }
        });
    }
    public void playMazeMusic(){
        if (mazeSongPlayer == null)
            return;
        mazeSongPlayer.play();
        System.out.println("maze music");/*
        mazeSongPlayer.setAutoPlay(true);
        mazeSongPlayer.setOnEndOfMedia(new Runnable() { //non-stoppable song, will repeat the song when finish
            public void run() {
                mazeSongPlayer.seek(Duration.ZERO);
            }
        });*/
    }
    public void playWinMusic(){
        mazeSongPlayer.setVolume(0);
        if (winSongPlayer == null)
            return;
        winSongPlayer.play();
        winSongPlayer.setAutoPlay(true);
        winSongPlayer.setOnEndOfMedia(new Runnable() { //non-stoppable song, will repeat the song when finish
            public void run() {
                winSongPlayer.seek(Duration.ZERO);
            }
        });
    }

    public void controlMusic(String operation, MediaPlayer mp){
        if (mp == null)
            return;
        switch (operation){
            case "play":
                mp.play();
                mp.setAutoPlay(true);
                break;
            case "stop":
                mp.stop();
        }
    }


}

