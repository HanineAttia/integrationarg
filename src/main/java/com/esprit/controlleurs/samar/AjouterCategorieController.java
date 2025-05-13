package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Categorie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.samar.ServiceCategorie;

public class AjouterCategorieController {

    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionField;
    @FXML
    private Button ajouterButton;

    @FXML
    public void handleAjouter() throws Exception {
        String nom = nomField.getText();
        String description = descriptionField.getText();

        // Vérification : les champs sont-ils remplis ?
        if (nom.isEmpty() || description.isEmpty()) {
            // Afficher une alerte d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs avant de continuer.");
            alert.showAndWait();
            return; // Stopper l'ajout
        }

        // Si tout est OK, continuer avec l'ajout
        ServiceCategorie service = new ServiceCategorie();
        Categorie nouvelleCategorie = new Categorie(nom, description);
        service.ajouter(nouvelleCategorie);

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Ajout réussi");
        success.setHeaderText(null);
        success.setContentText("La catégorie a été ajoutée avec succès !");
        success.showAndWait();

        // ✅ Rafraîchir la table dans Main.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.refreshTable();

        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}