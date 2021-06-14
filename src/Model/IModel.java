package Model;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    void assignObserver(Observer o);
    void updatePlayerLocation(int direction);
    public Maze getMaze();
    public void solveMaze();
    public Solution getSolution();
    public int getPlayerRow();
    public int getPlayerCol();
    public void generateMaze(int rows, int cols);
    public void exit();
}
