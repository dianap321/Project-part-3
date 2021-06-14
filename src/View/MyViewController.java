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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyViewController implements IView, Initializable, Observer {

    public Menu solution_menu;
    public CheckMenuItem sol_show;
    public BorderPane pane;
    public MenuItem save;
    private MyViewModel viewModel;

    public void setMyViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label lbl_playerRow;
    public Label lbl_PlayerCol;

    public static Stage stage;
    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int row) {
        this.updatePlayerRow.set("" + row);
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int col) {
        this.updatePlayerCol.set(""+col);
    }

    public void generateMaze(ActionEvent actionEvent) throws IOException {

        //check args
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        viewModel.generateMaze(rows, cols);

        mazeDisplayer.setSolution(null);
    }
    public void generateMaze(String row, String col) throws IOException {

        //check args
        int rows = Integer.valueOf(row);
        int cols = Integer.valueOf(col);

        viewModel.generateMaze(rows, cols);
      //  mazeDisplayer.setSolution(null);
    }

    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze();
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent.getCode());
        keyEvent.consume();
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

    private void goalReached() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        ButtonType newGame = new ButtonType("New Game");
//        ButtonType exit = new ButtonType("Exit Game");
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",newGame,exit);
        alert.setHeight(300);
        alert.setWidth(500);
        Image image = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Emojione_1F62D.svg/64px-Emojione_1F62D.svg.png");
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);
        alert.setTitle("You solved the maze!");
        alert.setResizable(true);
        alert.setHeaderText("YAY! YOU DID IT!");
        alert.setContentText("Congratulations! You helped A reach B!");
        Optional<ButtonType> selcted = alert.showAndWait();
        if(!selcted.isPresent());
        // alert is exited, no button has been pressed.
        else if(selcted.get() == ButtonType.OK)
        {
            try {
                generateMaze(String.valueOf(viewModel.getMaze().getMaze().length), String.valueOf(viewModel.getMaze().getMaze()[0].length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(selcted.get() == ButtonType.CANCEL)
        {
            Exit();
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        lbl_playerRow.textProperty().bind(updatePlayerRow);
//        lbl_PlayerCol.textProperty().bind(updatePlayerCol);
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

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    private void mazeGenerated() {
        mazeDisplayer.drawMaze(viewModel.getMaze());
        solution_menu.setDisable(false);
        save.setDisable(false);
    }

    public void About(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("About our game:\n" +
                "Our names are Ariel Perelman and Diana Prochorov.\n" +
                "We created this Maze game using MyMazeGenerator and we solve it using BFS searching algorithm.\n" +
                "We hope you enjoy!");
        alert.show();
    }

    public void Exit(ActionEvent actionEvent) {
        viewModel.exit();
        Platform.exit();
    }
    public void Exit() {
        viewModel.exit();
        Platform.exit();
    }

    public void MoveScene(ActionEvent actionEvent) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("MazeScreen.fxml").openStream());
            Scene scene = new Scene(root, 500, 500);
            scene.getStylesheets().add(getClass().getResource("MainStyle.css").toExternalForm());
            //BackPlayer.pause(); //pause the back music
            stage.setScene(scene);

            MyViewController myViewController =fxmlLoader.getController();
            myViewController.setStage(stage);
            myViewController.setMyViewModel(viewModel);
            viewModel.addObserver(myViewController);

            myViewController.setResizeEvent(scene);
            stage.show();


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

        } catch (Exception e) {

        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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
        alert.setContentText("In this game you must help characterA reach characterB by completing the maze.\n" +
                "You can walk through the maze using the NumPad on your keyboard:\n" +
                "1: Go diagonally down-left\n" +
                "2: Go down\n" +
                "3: Go diagonally down-right\n" +
                "4: Go left\n" +
                "6: Go right\n" +
                "7: Go diagonally up-left\n" +
                "8: Go up\n" +
                "9: Go diagonally up-right\n" +
                "On the game display you can see walls - character A cannot go through them!\n" +
                "Also, you may walk diagonally only if the up/down or left/right step in the direction of the diagonal is a path.");
        alert.show();
    }

    public void New(ActionEvent actionEvent) {
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

    public void show_hide_sol(ActionEvent actionEvent) {
            if (!sol_show.isSelected())
                mazeDisplayer.setSolution(null);
            else
                viewModel.solveMaze();
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
}

