package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.InvalidationListener;
//import Server.ServerStrategyGenerateMaze;
//import Ser

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {

    private Server GenerateMazeServer;
    private Server SolveMazeServer;

    private Maze maze;
    private Solution solution;
    private int playerRow;
    private int playerCol;
    private int goalRow;
    private int goalCol;


    public MyModel() throws IOException {
        GenerateMazeServer = new Server(5420, 1000, new ServerStrategyGenerateMaze());
        SolveMazeServer = new Server(5421, 1000, new ServerStrategySolveSearchProblem());
        GenerateMazeServer.start();
        SolveMazeServer.start();
    }

    public void generateMaze(int rows, int cols) {
//        maze = generator.generateRandomMaze(rows, cols);
//        setChanged();
//        notifyObservers("maze generated");
//        playerRow = 0;
//        playerCol = 0;
//        notifyMovement();

        try{
            Client client = new Client(InetAddress.getLocalHost(), 5420, new IClientStrategy() {
                public void clientStrategy(InputStream inputStream, OutputStream outputStream){
                    try{
                        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
                        ObjectInputStream fromServer = new ObjectInputStream(inputStream);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject();
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[rows*cols+12];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        maze.print();
                    }
                    catch(IOException | ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }
            });

            client.communicateWithServer();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setFields();
        setChanged();
        notifyObservers("maze generated");
    }

    @Override
    public void exit() {
        GenerateMazeServer.stop();
        SolveMazeServer.stop();
    }

    @Override
    public void save(File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            ObjectOutputStream objectOutput = new ObjectOutputStream(out);
            //Maze toSave=new Maze(new Position(rowPlayer,colPlayer),maze.getGoalPosition(),maze.getMaze());
            objectOutput.writeObject(maze.toByteArray());
            objectOutput.flush();
            objectOutput.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(File file) {

        try {
        InputStream in = null;
        in = new FileInputStream(file);
        ObjectInputStream objectIn = new ObjectInputStream(in);
        byte[] loadedMaze = (byte[]) objectIn.readObject();
        objectIn.close();
        in.close();
        maze = new Maze(loadedMaze);
        setFields();
        setChanged();
        notifyObservers("maze generated");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void setFields() {
        playerRow = maze.getStartPosition().getRowIndex();
        playerCol = maze.getStartPosition().getColumnIndex();
        goalRow = maze.getGoalPosition().getRowIndex();
        goalCol = maze.getGoalPosition().getColumnIndex();
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5421, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        solution = (Solution) fromServer.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setChanged();
        notifyObservers("maze solved");
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }
    @Override
    public void updatePlayerLocation(int direction) {
        switch (direction){
            case 1: //down left
                if (playerRow + 1 < maze.getMaze().length && playerCol - 1 >= 0 && maze.getMaze()[playerRow+1][playerCol-1] ==0 && ((maze.getMaze()[playerRow + 1][playerCol] == 0) || (maze.getMaze()[playerRow][playerCol-1] == 0))){
                    playerRow++;
                    playerCol--;}
                break;
            case 2: //down
                if (playerRow + 1 < maze.getMaze().length && maze.getMaze()[playerRow+1][playerCol] == 0) {
                    playerRow++;
                }
                break;
            case 3: //down right
                if (playerRow + 1 < maze.getMaze().length && playerCol + 1 < maze.getMaze()[0].length && maze.getMaze()[playerRow+1][playerCol+1] ==0 && ((maze.getMaze()[playerRow + 1][playerCol] == 0) || (maze.getMaze()[playerRow][playerCol+1] == 0))) {
                    playerCol++;
                    playerRow++;
                }
                break;
            case 4: //left
                if (playerCol - 1 >= 0 && maze.getMaze()[playerRow][playerCol-1] == 0) {
                    playerCol--;
                }
                break;
            case 6: //right
                if (playerCol + 1 < maze.getMaze()[0].length && maze.getMaze()[playerRow][playerCol+1] == 0) {
                    playerCol++;
                }
                break;
            case 7: //up left
                if (playerRow - 1 >= 0 && playerCol - 1 >= 0 && maze.getMaze()[playerRow-1][playerCol-1] ==0 && ((maze.getMaze()[playerRow - 1][playerCol] == 0)|| (maze.getMaze()[playerRow][playerCol-1] == 0))) {
                    playerCol--;
                    playerRow--;
                }
                break;
            case 8: //up
                if (playerRow - 1 >= 0 && maze.getMaze()[playerRow-1][playerCol] == 0) {
                    playerRow--;
                }
                break;
            case 9: //up right
                if (playerRow - 1 >= 0 && playerCol+  1 < maze.getMaze()[0].length && maze.getMaze()[playerRow-1][playerCol+1] ==0 && ((maze.getMaze()[playerRow - 1][playerCol] == 0) || (maze.getMaze()[playerRow][playerCol+1] == 0))) {
                    playerCol++;
                    playerRow--;
                }
                break;
        }
        notifyMovement();
    }

    private void notifyMovement() {
        setChanged();
        notifyObservers("player moved");
    }

    public void saveMaze(){

    }
}
