package View;

import java.io.IOException;

public interface IView {

    void generateMaze(String row, String col) throws IOException;

    void solveMaze();
}
