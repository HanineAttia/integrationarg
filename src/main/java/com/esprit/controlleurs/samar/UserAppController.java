package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.esprit.services.samar.ServicesProduit;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class UserAppController implements Initializable {
    // TableView, colonnes, serviceProduit, etc.
    @FXML
    private TableView<Produit> productTable;
    @FXML private TableColumn<Produit, Integer> idColumn;
    @FXML private TableColumn<Produit, String> nameColumn;
    @FXML private TableColumn<Produit, Float> priceColumn;
    @FXML private TableColumn<Produit, Integer> categoryColumn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Configurer les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idProduit"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("idCategorie"));

        // Gestion du double-clic sur une ligne du tableau
        productTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && productTable.getSelectionModel().getSelectedItem() != null) {
                Produit produitSelectionne = productTable.getSelectionModel().getSelectedItem();
                Produit primaryStage = null;
                ouvrirFenetreDetailsProduit(produitSelectionne);
            }
        });

        // Charger les produits dans la table
        rafraichirTableau();
    }

    private void ouvrirFenetreDetailsProduit(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProduit.fxml"));
            Parent root = loader.load();
            DetailsProduitController controller = loader.getController();
            controller.setProduit(produit);
            controller.setIsAdmin(false); // << Ici utilisateur

            Stage stage = new Stage();
            stage.setTitle("Détails Produit");
            stage.setScene(new Scene(root, 300, 200));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rafraichirTableau() {
        try {
            ServicesProduit servicesProduit=new ServicesProduit();
            productTable.getItems().clear();
            productTable.getItems().addAll(servicesProduit.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
