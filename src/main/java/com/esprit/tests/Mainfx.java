package com.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;



import static javafx.application.Application.launch;

public class Mainfx extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chargement du fichier FXML
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AjouterMateriel.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hanine/AjouterMateriel.fxml"));

        // Configuration de la scène
        Scene scene = new Scene(loader.load(), 800, 600);
        primaryStage.setTitle("TerraCult - Gestion des Matériaux Agricoles");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main (String[]args){
        launch(args);
    }
}

