package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.esprit.services.samar.ServicesProduit;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainAppController implements Initializable {

    @FXML private TableView<Produit> productTable;
    @FXML private TableColumn<Produit, Integer> idColumn;
    @FXML private TableColumn<Produit, String> nameColumn;
    @FXML private TableColumn<Produit, Float> priceColumn;
    @FXML private TableColumn<Produit, Integer> categoryColumn;
    @FXML private Button addButton;

    private Stage primaryStage;
    private ServicesProduit serviceProduit = new ServicesProduit();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idProduit"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("idCategorie"));

        // Gestion du double-clic
        productTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2 && productTable.getSelectionModel().getSelectedItem() != null) {
                Produit produitSelectionne = productTable.getSelectionModel().getSelectedItem();
                ouvrirFenetreDetailsProduit(primaryStage, produitSelectionne);
            }
        });

        rafraichirTableau();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void ouvrirFenetreAjoutProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduits.fxml"));
            Parent root = loader.load();
            AjouterProduitController controller = loader.getController();
            controller.setMainApp(this);

            Stage addStage = new Stage();
            addStage.setTitle("Ajouter Produit");
            addStage.setScene(new Scene(root, 300, 200));
            addStage.initOwner(primaryStage);
            addStage.show();
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout : " + ex.getMessage());
        }
    }

    private void ouvrirFenetreDetailsProduit(Stage parentStage, Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduit.fxml"));
            Parent root = loader.load();
            DetailsProduitController controller = loader.getController();
            controller.setMainApp(this);
            controller.setProduit(produit);
            controller.setIsAdmin(true); // ← Important si tu veux masquer le bouton pour admin

            Stage detailsStage = new Stage();
            detailsStage.setTitle("Détails Produit");
            detailsStage.setScene(new Scene(root, 300, 200));
            detailsStage.initOwner(parentStage);
            detailsStage.show();
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Erreur lors de l'ouverture des détails : " + ex.getMessage());
        }
    }

    public void rafraichirTableau() {
        try {
            productTable.getItems().clear();
            productTable.getItems().addAll(serviceProduit.getAll());
        } catch (SQLException ex) {
            afficherAlerte("Erreur", "Erreur lors du chargement des produits : " + ex.getMessage());
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

