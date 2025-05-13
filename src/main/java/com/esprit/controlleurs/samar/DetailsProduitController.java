package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.esprit.services.asma.PanierService;
import com.esprit.services.samar.ServicesProduit;


public class DetailsProduitController {
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private Label categoryLabel;
    @FXML private Button ajouterPanierButton;
    private Produit produit;
    private MainAppController mainApp;
    private ServicesProduit serviceProduit = new ServicesProduit();
    private boolean isAdmin = false;

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setMainApp(MainAppController mainApp) {
        this.mainApp = mainApp;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        nameLabel.setText(produit.getNom());
        priceLabel.setText(String.valueOf(produit.getPrixUnitaire()));
        categoryLabel.setText(String.valueOf(produit.getIdCategorie()));
        // Cacher le bouton si l'utilisateur est un administrateur
        if (isAdmin && ajouterPanierButton != null) {
            ajouterPanierButton.setVisible(false);
        }
    }

    @FXML
    private void modifierProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduit.fxml"));
            Parent root = loader.load();
            ModifierProduitController controller = loader.getController();
            controller.setMainApp(mainApp);
            controller.setProduit(produit);

            Stage modifyStage = new Stage();
            modifyStage.setTitle("Modifier Produit");
            modifyStage.setScene(new Scene(root, 300, 200));
            modifyStage.initOwner(nameLabel.getScene().getWindow());
            modifyStage.show();

            // Fermer la fenêtre de détails
            Stage currentStage = (Stage) nameLabel.getScene().getWindow();
            currentStage.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification : " + ex.getMessage());
        }
    }

    @FXML
    private void supprimerProduit() {
        // Afficher une confirmation avant suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de supprimer ce produit ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceProduit.supprimer(produit.getIdProduit());
                    mainApp.rafraichirTableau();
                    Stage stage = (Stage) nameLabel.getScene().getWindow();
                    stage.close();
                } catch (Exception ex) {
                    afficherAlerte("Erreur", "Erreur lors de la suppression : " + ex.getMessage());
                }
            }
        });
    }
    @FXML
    private void ajouterAuPanier() {
        if (produit != null) {
            PanierService panierService = new PanierService();
            panierService.ajouterProduit(produit);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout au panier");
            alert.setHeaderText(null);
            alert.setContentText("Produit ajouté au panier !");
            alert.showAndWait();

            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.close();
        }
    }


    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
