package com.esprit.controlleurs.baya;

import com.esprit.entities.baya.Employes;
import com.esprit.services.baya.ServiceEmployes;
import com.esprit.utils.MyDataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ChoixUserController {

    @FXML
    private Button adminButton;

    @FXML
    private Button agriButton;

    @FXML
    public void initialize() {
        adminButton.setOnAction(event -> {
            try {
                loadScene(event, "/MenuPrincipal.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        agriButton.setOnAction(event -> {
            try {
                loadScene(event, "/Agriculteur.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(" "); // Assure-toi que le titre est approprié
        stage.show();
    }

    public static class EmployesController {

        @FXML
        private TableView<Employes> employeTable;
        @FXML
        private TableColumn<Employes, Integer> idEmployeColumn;
        @FXML
        private TableColumn<Employes, String> nomPrenomColumn;
        @FXML
        private Button ajouterButton;
        @FXML
        private Button supprimerButton;
        @FXML
        private Button voirPlusButton;
        @FXML
        private Button retourButton;

        private ServiceEmployes serviceEmployes;
        private ObservableList<Employes> employeList;

        @FXML
        public void initialize() {
            Connection connection = MyDataBase.getInstance().getConnection();
            serviceEmployes = new ServiceEmployes(connection);

            // Configuration des colonnes de la table
            idEmployeColumn.setCellValueFactory(new PropertyValueFactory<>("idEmploye"));
            nomPrenomColumn.setCellValueFactory(cellData -> {
                Employes employe = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty(employe.getNom() + " " + employe.getPrenom());
            });

            // Charger les employés
            chargerEmployes();

            // Activer/Désactiver les boutons selon la sélection
            employeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                supprimerButton.setDisable(newSelection == null);
                voirPlusButton.setDisable(newSelection == null);
            });
        }

        private void chargerEmployes() {
            try {
                employeList = FXCollections.observableArrayList(serviceEmployes.getAll());
                employeTable.setItems(employeList);
            } catch (SQLException e) {
                montrerErreur("Erreur lors du chargement des employés.");
                e.printStackTrace();
            }
        }

        @FXML
        private void ajouterEmploye() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEmploye.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Ajouter un Employé");
                stage.initModality(Modality.APPLICATION_MODAL);

                AjouterEmployeController controller = loader.getController();
                controller.setEmployesController(this);

                stage.showAndWait();
            } catch (IOException e) {
                montrerErreur("Erreur lors de l'ouverture de la fenêtre d'ajout.");
                e.printStackTrace();
            }
        }

        @FXML
        private void supprimerEmploye() {
            Employes selectedEmploye = employeTable.getSelectionModel().getSelectedItem();
            if (selectedEmploye != null) {
                try {
                    serviceEmployes.supprimer(selectedEmploye.getIdEmploye());
                    montrerInfo("Employé supprimé avec succès !");
                    chargerEmployes();
                } catch (SQLException e) {
                    montrerErreur("Erreur lors de la suppression de l'employé.");
                    e.printStackTrace();
                }
            }
        }

        @FXML
        private void voirDetails() {
            Employes selectedEmploye = employeTable.getSelectionModel().getSelectedItem();
            if (selectedEmploye != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsEmployes.fxml"));
                    Stage stage = new Stage();
                    stage.setScene(new Scene(loader.load()));
                    stage.setTitle("Détails de l'Employé");
                    stage.initModality(Modality.APPLICATION_MODAL);

                    DetailsEmployesController controller = loader.getController();
                    controller.setEmploye(selectedEmploye);
                    controller.setEmployesController(this.serviceEmployes);

                    stage.showAndWait();
                } catch (IOException e) {
                    montrerErreur("Erreur lors de l'ouverture des détails.");
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
                Stage currentStage = (Stage) retourButton.getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                montrerErreur("Erreur lors du retour au menu principal.");
                e.printStackTrace();
            }
        }

        public void rafraichirTableau() {
            chargerEmployes();
        }

        private void montrerErreur(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(message);
            alert.showAndWait();
        }

        private void montrerInfo(String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}