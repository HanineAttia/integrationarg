package com.esprit.controlleurs.baya;


import com.esprit.entities.baya.Visite;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.baya.ServiceVisites;
import com.esprit.utils.MyDataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class AjouterVisiteController {

    @FXML
    private TextField nomVisiteField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField dureeVisiteField;
    @FXML
    private TextField statutField;

    private ServiceVisites serviceVisites;
    private VisiteController visiteController;
    // Référence vers VisiteController

    @FXML
    public void initialize() {
        // Initialiser le service de visites
        Connection connection = MyDataBase.getInstance().getConnection();
        serviceVisites = new ServiceVisites(connection);
        statutField.setText("disponible"); // Statut par défaut
    }

    // Méthode pour définir le VisiteController
    public void setVisiteController(VisiteController visiteController) {
        this.visiteController = visiteController;
    }

    @FXML
    private void ajouterVisite() {
        try {
            // Récupérer les données du formulaire
            String nomVisite = nomVisiteField.getText();
            String description = descriptionField.getText();
            float dureeVisite = Float.parseFloat(dureeVisiteField.getText());
            String statut = statutField.getText();

            // Créer une nouvelle visite
            Visite visite = new Visite(nomVisite, description, dureeVisite);
            visite.setStatut(statut);

            // Ajouter la visite via le service
            serviceVisites.ajouter(visite);

            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Ajout avec succès !");
            alert.showAndWait();

            // Fermer la fenêtre actuelle
            Stage stage = (Stage) nomVisiteField.getScene().getWindow();
            stage.close();

            // Réouvrir la liste des visites
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Visite.fxml"));
            Stage newStage = new Stage();
            newStage.setScene(new Scene(loader.load()));
            newStage.setTitle("Liste des Visites");
            newStage.show();

            // Rafraîchir la table dans VisiteController
            if (visiteController != null) {
                visiteController.refreshTable();
            }

        } catch (NumberFormatException e) {
            showError("Veuillez entrer une durée valide.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ajout.");
        }
    }

    @FXML
    private void retour() {
        Stage stage = (Stage) nomVisiteField.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}