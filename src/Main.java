//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//public class Main extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("MyView.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}

import Model.IModel;
import Model.*;
import View.MyViewController;
import ViewModel.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View/MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze"); //change name
        primaryStage.setScene(new Scene(root, 400, 400));


        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setMyViewModel(viewModel);
        viewModel.addObserver(view);
        view.setStage(primaryStage);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e->viewModel.exit());
    }


    public static void main(String[] args) {
        launch(args);
    }
}

