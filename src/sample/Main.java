package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    //Dont Touch main Method, its not needed for anything cause JavaFx has its back
    public static void main(String[] args) {launch(args);}


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Layout.fxml"));
        primaryStage.setTitle("Musich Compiler");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();




    }


}
