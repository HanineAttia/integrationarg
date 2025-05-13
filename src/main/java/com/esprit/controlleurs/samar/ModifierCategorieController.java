package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Categorie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.samar.ServiceCategorie;


public class ModifierCategorieController {

    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionField;

    private Categorie currentCategorie;

    public void setCategorie(Categorie categorie) {
        this.currentCategorie = categorie;
        nomField.setText(categorie.getNom());
        descriptionField.setText(categorie.getDescription());
    }

    @FXML
    public void handleModifier() throws Exception {
        String nom = nomField.getText();
        String description = descriptionField.getText();

        if (nom.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Champs manquants");
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        currentCategorie.setNom(nom);
        currentCategorie.setDescription(description);
        ServiceCategorie serviceCategorie = new ServiceCategorie();

        serviceCategorie.modifier(currentCategorie);

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Modification réussie");
        success.setHeaderText(null);
        success.setContentText("La catégorie a été modifiée avec succès !");
        success.showAndWait();

        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

}