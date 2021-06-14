package View;

import ViewModel.MyViewModel;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Observable;
import java.util.Observer;

public class MazeScreen implements Observer {

    public MazeDisplayer mazeDisplayer2;
    private MyViewModel viewModel;

    public void setMyViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }



    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change) {
            case "maze generated":
                mazeGenerated();
                break;
//            case "player moved":
//                playerMoved();
//                break;
//            case "maze solved":
//                mazeSolved();
//                break;
//        }
        }
    }
    public void About(ActionEvent actionEvent) {
    }

    public void Exit(ActionEvent actionEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
    }


    private void mazeGenerated() {
        mazeDisplayer2.drawMaze(viewModel.getMaze());

    }
}
