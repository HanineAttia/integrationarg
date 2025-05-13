package com.esprit.controlleurs.baya;

import com.esprit.entities.baya.Visite;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.esprit.services.baya.ServiceVisites;
import com.esprit.utils.MyDataBase;
import com.esprit.utils.PdfGenerator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class VisiteController {

    @FXML
    private TableView<Visite> visiteTable;
    @FXML
    private TableColumn<Visite, Integer> idVisiteColumn;
    @FXML
    private TableColumn<Visite, String> nomVisiteColumn;
    @FXML
    private TableColumn<Visite, String> descriptionColumn;
    @FXML
    private TableColumn<Visite, Float> dureeVisiteColumn;
    @FXML
    private TableColumn<Visite, String> statutColumn;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;
    @FXML
    private Button retourButton;

    private ServiceVisites serviceVisites;
    private ObservableList<Visite> visiteList;

    @FXML
    public void initialize() {
        Connection connection = MyDataBase.getInstance().getConnection();
        serviceVisites = new ServiceVisites(connection);

        // Configuration des colonnes du tableau
        idVisiteColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdVisite()).asObject());
        nomVisiteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomVisite()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        dureeVisiteColumn.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getDureeVisite()).asObject());
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));

        // Charger les visites
        loadVisites();

        // Activer/Désactiver les boutons "Modifier" et "Supprimer" selon la sélection
        visiteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = newSelection != null;
            modifierButton.setDisable(!selected);
            supprimerButton.setDisable(!selected);
        });
    }

    // Méthode pour rafraîchir la table
    public void refreshTable() {
        loadVisites();
    }

    private void loadVisites() {
        try {
            visiteList = FXCollections.observableArrayList(serviceVisites.getAll());
            visiteTable.setItems(visiteList);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des visites : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showAjouterVisite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVisite.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ajouter une Visite");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Passer ce contrôleur à AjouterVisiteController
            AjouterVisiteController controller = loader.getController();
            controller.setVisiteController(this);

            stage.showAndWait();
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showModifierVisite() {
        Visite selectedVisite = visiteTable.getSelectionModel().getSelectedItem();
        if (selectedVisite != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVisite.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Modifier une Visite");
                stage.initModality(Modality.APPLICATION_MODAL);

                // Passer la visite sélectionnée et ce contrôleur
                ModifierVisiteController controller = loader.getController();
                controller.setVisite(selectedVisite);
                controller.setVisiteController(this);

                stage.showAndWait();
            } catch (IOException e) {
                showError("Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void supprimerVisite() {
        Visite selectedVisite = visiteTable.getSelectionModel().getSelectedItem();
        if (selectedVisite != null) {
            try {
                serviceVisites.supprimer(selectedVisite.getIdVisite());
                showInfo("Suppression avec succès !");
                loadVisites();
            } catch (SQLException e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void retourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuPrincipal.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Menu Principal");
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) visiteTable.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showError("Erreur lors du retour au menu principal : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }@FXML
    private void handleGenererPdf(ActionEvent event) {
        try {
            // Récupérer les statistiques ou les données nécessaires
            Map<Integer, Integer> stats = serviceVisites.getVisiteDemandeeStats();

            // Définir le chemin du fichier PDF
            String projectDir = System.getProperty("user.dir");  // Récupère le répertoire du projet
            String fileDir = projectDir + "/generated-pdfs";  // Dossier pour stocker le PDF
            File dir = new File(fileDir);

            // Créer le répertoire s'il n'existe pas
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath = fileDir + "/visites_statistiques.pdf";  // Chemin complet du fichier PDF

            // Générer le PDF avec les statistiques
            PdfGenerator.generateVisiteStatsPdf(stats, filePath);

            // Afficher un message d'information
            showInfo("Le fichier PDF a été généré avec succès : " + filePath);

            // Ouvrir le PDF généré
            if (Desktop.isDesktopSupported()) {
                File pdfFile = new File(filePath);
                if (pdfFile.exists()) {
                    Desktop.getDesktop().open(pdfFile);  // Ouvre automatiquement le fichier
                } else {
                    showError("Erreur : Le fichier PDF n'a pas été trouvé.");
                }
            }

        } catch (Exception e) {
            showError("Erreur lors de la génération ou de l'ouverture du PDF : " + e.getMessage());
        }
    }


}