package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Categorie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.esprit.services.samar.ServiceCategorie;

import java.util.Optional;

public class DetailCategorieController {

    @FXML
    private Label nomLabel;

    @FXML
    private Label descriptionLabel;

    private Categorie currentCategorie;

    public void setCategorie(Categorie categorie) {
        this.currentCategorie = categorie;
        nomLabel.setText("Nom : " + categorie.getNom());
        descriptionLabel.setText("Description : " + categorie.getDescription());
    }

    /*@FXML
    public void handleModifier() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCategorie.fxml"));
        Parent root = loader.load();
        ModifierCategorieController controller = loader.getController();
        controller.setCategorie(currentCategorie);
        Stage stage = (Stage) nomLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }*/

    @FXML
    public void handleSupprimer() throws Exception {
        // Créer une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la catégorie");
        alert.setContentText("Es-tu sûr de vouloir supprimer cette catégorie ?");

        // Attendre la réponse de l'utilisateur
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Si confirmé, supprimer
            ServiceCategorie serviceCategorie = new ServiceCategorie();

            serviceCategorie.supprimer(currentCategorie.getIdCategorie());

            // Retour à la fenêtre principale
            Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } else {
            // Sinon, ne rien faire (annulé)
            System.out.println("Suppression annulée.");
        }
    }


}