package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Produit;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.esprit.services.samar.ServicesProduit;

public class ModifierProduitController {
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField categoryField;
    private Produit produit;
    private MainAppController mainApp;
    private ServicesProduit serviceProduit = new ServicesProduit();

    public void setMainApp(MainAppController mainApp) {
        this.mainApp = mainApp;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        nameField.setText(produit.getNom());
        priceField.setText(String.valueOf(produit.getPrixUnitaire()));
        categoryField.setText(String.valueOf(produit.getIdCategorie()));
    }

    @FXML
    private void modifierProduit() {
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

            // Mettre à jour le produit
            produit.setNom(nom);
            produit.setPrixUnitaire(prix);
            produit.setIdCategorie(idCategorie);
            serviceProduit.modifier(produit);

            // Rafraîchir la table et fermer la fenêtre
            mainApp.rafraichirTableau();
            fermerFenetre();
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Erreur lors de la modification : " + ex.getMessage());
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
