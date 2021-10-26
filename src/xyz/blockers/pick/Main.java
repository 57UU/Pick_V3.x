package xyz.blockers.pick;

import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Window.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
