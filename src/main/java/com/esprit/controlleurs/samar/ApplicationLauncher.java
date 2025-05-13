package com.esprit.controlleurs.samar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML depuis src/main/resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainApp.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur

            MainAppController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            // Configurer la scène
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setTitle("Liste des Produits");
            primaryStage.setScene(scene);

            // Afficher la fenêtre
            primaryStage.show();
        } catch (Exception ex) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement de l'interface : " + ex.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}