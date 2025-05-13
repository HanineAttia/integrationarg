package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Produit;
import com.esprit.entities.samar.SmsNotification;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.samar.ServicesProduit;

public class AjouterProduitController {
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField categoryField;
    private MainAppController mainApp;
    private ServicesProduit serviceProduit = new ServicesProduit();

    public void setMainApp(MainAppController mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void ajouterProduit() {
        try {
            // Valider les entrées
            String nom = nameField.getText().trim();
            if (nom.isEmpty()) {
                afficherAlerte("Erreur", "Le nom est requis");
                return;
            }

            float prix;
            try {
                prix = Float.parseFloat(priceField.getText().trim());
            } catch (NumberFormatException ex) {
                afficherAlerte("Erreur", "Prix unitaire invalide");
                return;
            }

            int idCategorie;
            try {
                idCategorie = Integer.parseInt(categoryField.getText().trim());
            } catch (NumberFormatException ex) {
                afficherAlerte("Erreur", "ID catégorie invalide");
                return;
            }

            // Créer et enregistrer le produit
            Produit produit = new Produit(nom, prix, idCategorie);
            serviceProduit.ajouter(produit);
// ✅ Envoyer un SMS après l'ajout
            SmsNotification.sendSms(nom);

            // Rafraîchir la table et fermer la fenêtre
            mainApp.rafraichirTableau();
            fermerFenetre();
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Erreur lors de l'ajout : " + ex.getMessage());
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void afficherAlerte(String titre, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
