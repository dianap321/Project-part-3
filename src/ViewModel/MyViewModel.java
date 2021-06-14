//package ViewModel;
//
//import algorithms.mazeGenerators.MyMazeGenerator;
//
//import java.util.Observable;
//import java.util.Observer;
//
//public class MyViewModel extends Observable implements Observer {
//    @Override
//    public void update(Observable o, Object arg) {
//
//    }
//}
package ViewModel;

import Model.IModel;
//import Model.Solution;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }

    public void exit(){
        model.exit();
    }
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public void generateMaze(int row, int cols){
        model.generateMaze(row,cols);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public void solveMaze(){
       model.solveMaze();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void movePlayer(KeyEvent keyEvent){
        int direction = -1;
        switch (keyEvent.getCode())
        {
            case NUMPAD1:
            direction = 1;
            break;
            case NUMPAD2:
                direction = 2;
                break;
            case NUMPAD3:
                direction = 3;
                break;
            case NUMPAD4:
                direction = 4;
                break;
            case NUMPAD6:
                direction = 6;
                break;
            case NUMPAD7:
                direction = 7;
                break;
            case NUMPAD8:
                direction = 8;
                break;
            case NUMPAD9:
                direction = 9;
                break;
        }
        model.updatePlayerLocation(direction);
    }

    public int getPlayerRow(){
        return model.getPlayerRow();

    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }
}
