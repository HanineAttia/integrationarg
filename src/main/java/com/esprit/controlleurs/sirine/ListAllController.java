package com.esprit.controlleurs.sirine;

import com.esprit.entities.sirine.EntiteGlobale;
import com.esprit.entities.sirine.Motifs;
import com.esprit.entities.sirine.Reclamation;
import com.esprit.entities.sirine.Reponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.esprit.services.sirine.ServiceMotifs;
import com.esprit.services.sirine.ServiceRec;
import com.esprit.services.sirine.ServiceRep;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListAllController {

    @FXML
    private TableView<EntiteGlobale> allTable;

    @FXML
    private TableColumn<EntiteGlobale, String> typeColumn;

    @FXML
    private TableColumn<EntiteGlobale, String> nomColumn;

    @FXML
    private TableColumn<EntiteGlobale, String> contenuColumn;

    @FXML
    private TableColumn<EntiteGlobale, String> statutColumn;

    @FXML
    private TableColumn<EntiteGlobale, String> idReclamationColumn;

    @FXML
    private Button addMotifButton;

    @FXML
    private Button addReclamationButton;

    @FXML
    private Button addReponseButton;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label messageLabel;

    private ServiceMotifs serviceMotifs;
    private ServiceRec serviceRec;
    private ServiceRep serviceRep;

    @FXML
    public void initialize() {
        try {
            System.out.println("Initialisation des services...");
            serviceMotifs = new ServiceMotifs();
            serviceRec = new ServiceRec();
            serviceRep = new ServiceRep();
            System.out.println("Services initialisés avec succès.");
        } catch (IllegalStateException | SQLException e) {
            messageLabel.setText("Erreur lors de l'initialisation des services : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            System.err.println("Erreur d'initialisation : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Configure les colonnes
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        contenuColumn.setCellValueFactory(cellData -> cellData.getValue().contenuProperty());
        statutColumn.setCellValueFactory(cellData -> cellData.getValue().statutProperty());
        idReclamationColumn.setCellValueFactory(cellData -> cellData.getValue().idReclamationProperty());

        loadAllEntities();

        // Active/Désactive les boutons modifier et supprimer selon la sélection
        allTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean disable = newSelection == null;
            modifyButton.setDisable(disable);
            deleteButton.setDisable(disable);
        });
    }

    private void loadAllEntities() {
        try {
            System.out.println("Chargement de toutes les entités...");
            List<EntiteGlobale> entities = new ArrayList<>();

            // Charger les motifs
            List<Motifs> motifs = serviceMotifs.afficher();
            System.out.println("Motifs chargés : " + motifs.size());
            for (Motifs motif : motifs) {
                entities.add(new EntiteGlobale(motif.getId(), "Motif", motif.getNom(), motif.getDescription(), "", ""));
            }

            // Charger les réclamations
            List<Reclamation> reclamations = serviceRec.afficher();
            System.out.println("Réclamations chargées : " + reclamations.size());
            for (Reclamation rec : reclamations) {
                entities.add(new EntiteGlobale(rec.getId(), "Réclamation", rec.getNomMotif(), rec.getContenu(), rec.getStatut(), ""));
            }

            // Charger les réponses
            List<Reponse> reponses = serviceRep.afficher();
            System.out.println("Réponses chargées : " + reponses.size());
            for (Reponse rep : reponses) {
                entities.add(new EntiteGlobale(rep.getId(), "Réponse", rep.getContenu(), rep.getContenu(), "", String.valueOf(rep.getIdReclamation())));
            }

            // Vérifier si des entités ont été ajoutées
            if (entities.isEmpty()) {
                messageLabel.setText("Aucune donnée à afficher. Ajoutez des motifs, réclamations ou réponses.");
                messageLabel.setStyle("-fx-text-fill: orange;");
            } else {
                allTable.getItems().setAll(entities);
                messageLabel.setText("Données chargées avec succès : " + entities.size() + " entités.");
                messageLabel.setStyle("-fx-text-fill: green;");
            }
            System.out.println("Entités chargées avec succès : " + entities.size());
        } catch (SQLException e) {
            messageLabel.setText("Erreur SQL lors du chargement des données : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            System.err.println("Erreur SQL dans loadAllEntities : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            messageLabel.setText("Erreur inattendue lors du chargement des données : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            System.err.println("Erreur inattendue dans loadAllEntities : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addMotif() {
        try {
            System.out.println("Ouverture du formulaire AjoutMotif...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutMotif.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Le fichier FXML '/AjoutMotif.fxml' est introuvable.");
            }
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Motif");
            stage.showAndWait();
            loadAllEntities();
            System.out.println("Fermeture du formulaire AjoutMotif.");
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            System.err.println("Erreur dans addMotif : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addReclamation() {
        try {
            System.out.println("Ouverture du formulaire AjoutReclamation...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutReclamation.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Le fichier FXML '/AjoutReclamation.fxml' est introuvable.");
            }
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Réclamation");
            stage.showAndWait();
            loadAllEntities();
            System.out.println("Fermeture du formulaire AjoutReclamation.");
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            System.err.println("Erreur dans addReclamation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addReponse() {
        try {
            System.out.println("Ouverture du formulaire AjoutReponse...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutReponse.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Le fichier FXML '/AjoutReponse.fxml' est introuvable.");
            }
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une Réponse");
            stage.showAndWait();
            loadAllEntities();
            System.out.println("Fermeture du formulaire AjoutReponse.");
        } catch (IOException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            System.err.println("Erreur dans addReponse : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void modifyEntity() {
        EntiteGlobale selectedEntity = allTable.getSelectionModel().getSelectedItem();
        if (selectedEntity == null) {
            messageLabel.setText("Veuillez sélectionner une entité !");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            System.out.println("Modification de l'entité : " + selectedEntity.getType());
            if (selectedEntity.getType().equals("Motif")) {
                Motifs selectedMotif = serviceMotifs.afficher().stream()
                        .filter(m -> m.getId() == selectedEntity.getId())
                        .findFirst()
                        .orElse(null);
                if (selectedMotif == null) {
                    messageLabel.setText("Motif non trouvé !");
                    messageLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMotif.fxml"));
                if (loader.getLocation() == null) {
                    throw new IOException("Le fichier FXML '/ModifierMotif.fxml' est introuvable.");
                }
                Parent root = loader.load();
                ModifierMotifController controller = loader.getController();
                controller.setMotif(selectedMotif);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier un Motif");
                stage.showAndWait();
            } else if (selectedEntity.getType().equals("Réclamation")) {
                Reclamation selectedReclamation = serviceRec.afficher().stream()
                        .filter(r -> r.getId() == selectedEntity.getId())
                        .findFirst()
                        .orElse(null);
                if (selectedReclamation == null) {
                    messageLabel.setText("Réclamation non trouvée !");
                    messageLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierRec.fxml"));
                if (loader.getLocation() == null) {
                    throw new IOException("Le fichier FXML '/ModifierReclamation.fxml' est introuvable.");
                }
                Parent root = loader.load();
                ModifierRecController controller = loader.getController();
                controller.setReclamation(selectedReclamation);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier une Réclamation");
                stage.showAndWait();
            } else if (selectedEntity.getType().equals("Réponse")) {
                Reponse selectedReponse = serviceRep.afficher().stream()
                        .filter(r -> r.getId() == selectedEntity.getId())
                        .findFirst()
                        .orElse(null);
                if (selectedReponse == null) {
                    messageLabel.setText("Réponse non trouvée !");
                    messageLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierRep.fxml"));
                if (loader.getLocation() == null) {
                    throw new IOException("Le fichier FXML '/ModifierReponse.fxml' est introuvable.");
                }
                Parent root = loader.load();
                ModifierRepController controller = loader.getController();
                controller.setReponse(selectedReponse);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier une Réponse");
                stage.showAndWait();
            }
            loadAllEntities();
            System.out.println("Fermeture du formulaire de modification.");
        } catch (IOException | SQLException e) {
            messageLabel.setText("Erreur lors de l'ouverture du formulaire de modification : " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            System.err.println("Erreur dans modifyEntity : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteEntity() {
        EntiteGlobale selectedEntity = allTable.getSelectionModel().getSelectedItem();
        if (selectedEntity == null) {
            messageLabel.setText("Veuillez sélectionner une entité à supprimer !");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette entité ?");
        alert.setContentText(selectedEntity.getType() + " : " + selectedEntity.getNom());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (selectedEntity.getType().equals("Motif")) {
                    serviceMotifs.supprimer(selectedEntity.getId());
                } else if (selectedEntity.getType().equals("Réclamation")) {
                    serviceRec.supprimer(selectedEntity.getId());
                } else if (selectedEntity.getType().equals("Réponse")) {
                    serviceRep.supprimer(selectedEntity.getId());
                }
                loadAllEntities();
                messageLabel.setText(selectedEntity.getType() + " supprimé avec succès.");
                messageLabel.setStyle("-fx-text-fill: green;");
            } catch (SQLException e) {
                messageLabel.setText("Erreur lors de la suppression : " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
                System.err.println("Erreur dans deleteEntity : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}