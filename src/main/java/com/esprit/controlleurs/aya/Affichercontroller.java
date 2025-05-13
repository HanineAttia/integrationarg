package com.esprit.controlleurs.aya;

import com.esprit.entities.aya.Personne;
import com.esprit.services.aya.Servicepersonne;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import static com.esprit.controlleurs.aya.Logincontroller.personneactuelle;

public class Affichercontroller {

    @FXML
    private TableColumn<Personne, Integer> colId;

    @FXML
    private TableColumn<Personne, String> colNom;

    @FXML
    private TableColumn<Personne, String> colPrenom;

    @FXML
    private TableColumn<Personne, String> colEmail;

    @FXML
    private TableColumn<Personne, String> colMotDePasse;

    @FXML
    private TableColumn<Personne, String> colDate;

    @FXML
    private TableColumn<Personne, String> colRole;

    @FXML
    private TableColumn<Personne, String> colPhoto;

    @FXML
    private TableView<Personne> tableUtilisateurs;

    // Getters pour chaque élément
    public TableView<Personne> getTableUtilisateurs() {
        return tableUtilisateurs;
    }

    public TableColumn<Personne, Integer> getColId() {
        return colId;
    }

    public TableColumn<Personne, String> getColNom() {
        return colNom;
    }

    public TableColumn<Personne, String> getColPrenom() {
        return colPrenom;
    }

    public TableColumn<Personne, String> getColEmail() {
        return colEmail;
    }

    public TableColumn<Personne, String> getColMotDePasse() {
        return colMotDePasse;
    }

    public TableColumn<Personne, String> getColDate() {
        return colDate;
    }

    public TableColumn<Personne, String> getColRole() {
        return colRole;
    }

    public TableColumn<Personne, String> getColPhoto() {
        return colPhoto;
    }

    @FXML
    void ajouterUtilisateur(ActionEvent event) {
        try {
            // Charger l'interface Ajouterad.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouterad.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Ajouter utilisateur");
            currentStage.show();


            // Fermer la fenêtre actuelle
           /* Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();*/

        } catch (IOException e) {
            e.printStackTrace();
            // Vous pouvez afficher un message d'erreur si nécessaire
        }
        // Implémenter la logique pour ajouter un utilisateur
    }
    private Servicepersonne servicepersonne = new Servicepersonne();
    public void loadPersonnes() {
        try {
            // Récupérer la liste des personnes
            ObservableList<Personne> personnes = FXCollections.observableArrayList(servicepersonne.getAll());

            // Remplir le tableau avec les données
            tableUtilisateurs.setItems(personnes);

            // Configurer les colonnes du tableau
            colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
            colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
            colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
            colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
            colMotDePasse.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMotDePasse()));
            colPhoto.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoto()));
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer l'exception si nécessaire
        }
    }

    @FXML
    void supprimerUtilisateur(ActionEvent event) {
        // Récupérer l'utilisateur sélectionné dans la table
        Personne selectedPersonne = tableUtilisateurs.getSelectionModel().getSelectedItem();

        if (selectedPersonne == null) {
            // Afficher un message d'erreur si aucun utilisateur n'est sélectionné
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }

        // Demander confirmation avant de supprimer
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Supprimer l'utilisateur de la base de données
                    new Servicepersonne().supprimer(selectedPersonne.getId());

                    // Rafraîchir la liste des utilisateurs dans la table
                    loadPersonnes();  // Cette méthode doit mettre à jour la table avec les utilisateurs restants

                    // Afficher un message de confirmation
                    showAlert(Alert.AlertType.INFORMATION, "Utilisateur supprimé avec succès !");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression de l'utilisateur.");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void retourMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menuad.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur associé
            Menuadcontroller controller = loader.getController();

            // Transmettre les données (ex: utilisateur connecté)
            controller.setUtilisateurConnecte(personneactuelle);

            // Afficher la nouvelle scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu administrateur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}