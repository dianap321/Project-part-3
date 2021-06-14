package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {
    Maze maze;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    private int playerRow;
    private int playerCol;
    private int goalRow;
    private int goalCol;
    private Solution solution;

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public int getGoalRow() {
        return goalRow;
    }

    public void setGoalRow(int goalRow) {
        this.goalRow = goalRow;
    }

    public int getGoalCol() {
        return goalCol;
    }

    public void setGoalCol(int goalCol) {
        this.goalCol = goalCol;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getGetPlayerCol() {
        return playerCol;
    }

    public void setGoalLocation(int row, int col){
        this.goalRow = row;
        this.goalCol = col;
    }
    public void setGetPlayerPos(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }


    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public void drawMaze(Maze maze) {
        this.maze = maze;
        setGetPlayerPos(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());
        setGoalLocation(maze.getGoalPosition().getRowIndex(), maze.getGoalPosition().getColumnIndex());
        draw();
    }

    public void draw() {
        if (maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getMaze().length;
            int cols = maze.getMaze()[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);


            drawMazeWalls(graphicsContext,rows, cols,cellHeight, cellWidth);
            if(solution != null)
                drawSolution(graphicsContext,cellHeight, cellWidth);
            drawMazePlayer(graphicsContext ,cellHeight, cellWidth);
            drawGoalPosition(graphicsContext, cellWidth, cellHeight);
        }
    }

    private void drawGoalPosition(GraphicsContext graphicsContext, double cellWidth, double cellHeight) {
        Image goalImage = null;
        try {
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
        } catch (FileNotFoundException e) {
            System.out.println("No such Image for goal.");
        }
        double x = getGoalCol() *cellWidth;
        double y = getGoalRow() *cellHeight;
        graphicsContext.setFill(Color.BLUE);
        if (goalImage == null)
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        else
            graphicsContext.drawImage(goalImage,x,y,cellWidth,cellHeight);
    }

    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        graphicsContext.setFill(Color.PAPAYAWHIP);
        ArrayList<AState> solPath = solution.getSolutionPath();
        for (int j = 0; j < solPath.size(); j++) {
            //System.out.println(maze.getMaze()[i][j]);
            AState aState = solPath.get(j);
            int row = Integer.parseInt(aState.getState().substring(aState.getState().indexOf("{")+1,aState.getState().indexOf(",")));
            int col = Integer.parseInt(aState.getState().substring(aState.getState().indexOf(",")+1,aState.getState().indexOf("}")));
            double x = row * cellHeight;
            double y = col * cellWidth;
            graphicsContext.fillRect(y, x, cellWidth, cellHeight);
            }
    }

    private void drawMazePlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("No such Image for player.");
        }
        double x = getGetPlayerCol() *cellWidth;
        double y = getPlayerRow() *cellHeight;
        graphicsContext.setFill(Color.GREEN);
        if (playerImage == null)
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        else
            graphicsContext.drawImage(playerImage,x,y,cellWidth,cellHeight);

    }

    private void drawMazeWalls(GraphicsContext graphicsContext, int rows, int cols, double cellHeight, double cellWidth) {
        Image wall = null;
        try {
            wall = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("No such Image.");
        }
        maze.print();
        graphicsContext.setFill(Color.RED);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(maze.getMaze()[i][j] == 1){

                    //it is a wall
                    double x = i * cellHeight;
                    double y = j * cellWidth;
                    if ( wall == null)
                        graphicsContext.fillRect(y, x, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wall,y,x,cellWidth,cellHeight);
                }
            }
        }
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
    }

    public void zoom(ScrollEvent scrollEvent) {
        double zoomTimes = 1.05;
        if(!scrollEvent.isControlDown())
        {
            return;
        }
        if (scrollEvent.getDeltaY() < 0)
        {
            if (getHeight() < getScene().getHeight() - 20 || getWidth() < getScene().getWidth() -20) {
                //we check this condition because we don't want our maze to be smaller than our window
                return;
            }
            setHeight(getHeight() / zoomTimes);
            setWidth(getWidth() / zoomTimes);
        }
        if (scrollEvent.getDeltaY() > 0)
        {
           /* if (getHeight() > 7000 || getWidth() > 7000)
                return;*/

            setHeight(getHeight() * zoomTimes);
            setWidth(getWidth() * zoomTimes);
        }


        draw();

    }
}

