package com.esprit.controlleurs.samar;

import com.esprit.entities.samar.Categorie;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import com.esprit.services.samar.ServiceCategorie;

import java.sql.SQLException;

public class MainController {

    @FXML
    private TableView<Categorie> categorieTable;

    @FXML
    private TableColumn<Categorie, Integer> idCol;

    @FXML
    private TableColumn<Categorie, String> nomCol;

    @FXML
    private TableColumn<Categorie, String> descCol;


    @FXML
    public void handleAjouterCategorie() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/AjouterCategorie.fxml"));
        Stage stage = (Stage) categorieTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void handleVoirDetails() throws Exception {
        Categorie selected = categorieTable.getSelectionModel().getSelectedItem();

        // Vérifier si une catégorie est sélectionnée
        if (selected == null) {
            // Afficher un message d’erreur ou une alerte
            System.out.println("Veuillez sélectionner une catégorie.");
            return; // Sortir de la méthode si aucune catégorie n'est sélectionnée
        }

        // Si une catégorie est sélectionnée, charger la scène de détails
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailCategorie.fxml"));
        Parent root = loader.load();
        DetailCategorieController controller = loader.getController();
        controller.setCategorie(selected);
        Stage stage = (Stage) categorieTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
    @FXML
    public void initialize() {
        nomCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nom"));
        descCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));

        try {
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() throws SQLException {
        ServiceCategorie service = new ServiceCategorie();
        categorieTable.setItems(FXCollections.observableArrayList(service.getAll()));
    }
}