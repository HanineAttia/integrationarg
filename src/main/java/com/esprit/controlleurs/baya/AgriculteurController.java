package com.esprit.controlleurs.baya;

import com.esprit.entities.baya.Agriculteur;
import com.esprit.entities.baya.Visite;
import com.esprit.services.baya.ServiceAgriculteur;
import com.esprit.services.baya.ServiceVisites;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.esprit.entities.baya.SmsNotificationbaya; // Ajoute cette ligne en haut

public class AgriculteurController {

    @FXML private ListView<Visite> visiteListView;
    @FXML private CheckBox telephoneCheckBox; // Remplacer emailCheckBox par telephoneCheckBox
    @FXML private TextField telephoneField; // Remplacer emailField par telephoneField
    @FXML private Button confirmerButton;
    @FXML private Button retourButton;

    private final ServiceAgriculteur serviceAgriculteur = new ServiceAgriculteur();
    private final ServiceVisites serviceVisites = new ServiceVisites();

    @FXML
    public void initialize() {
        loadVisites();

        // Désactiver le champ de téléphone si la case n'est pas cochée
        telephoneCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            telephoneField.setDisable(!newVal);
        });

        confirmerButton.setOnAction(e -> confirmerAction());

        retourButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChoixUser.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) retourButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Choix de l'utilisateur");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur lors du retour à la page de choix.");
            }
        });
    }

    private void loadVisites() {
        try {
            List<Visite> visites = serviceVisites.getAll();
            visiteListView.setItems(FXCollections.observableArrayList(visites));

            // Afficher une description personnalisée
            visiteListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Visite item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNomVisite() + " - " + item.getDescription() + " (" + item.getDureeVisite() + "h)");
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la récupération des visites.");
        }
    }
    private void confirmerAction() {
        Visite selectedVisite = visiteListView.getSelectionModel().getSelectedItem();

        if (selectedVisite == null) {
            showAlert("Veuillez sélectionner une visite.");
            return;
        }

        // Récupérer le numéro de téléphone si la case est cochée
        String telephone = telephoneCheckBox.isSelected() ? telephoneField.getText() : null;

        if (telephoneCheckBox.isSelected() && (telephone == null || telephone.isBlank())) {
            showAlert("Veuillez saisir votre numéro de téléphone.");
            return;
        }

        // Créer un agriculteur avec le numéro de téléphone
        Agriculteur agriculteur = new Agriculteur(selectedVisite.getIdVisite(), telephone);
        serviceAgriculteur.ajouterAgriculteur(agriculteur);

        // Envoi du SMS si le numéro est fourni
        if (telephone != null && !telephone.isBlank()) {
            String numeroFormatte = telephone.startsWith("+") ? telephone : "+216" + telephone;
            SmsNotificationbaya.sendSms(numeroFormatte);
        }

        try {
            // Supprimer la visite après ajout
            serviceVisites.supprimer(selectedVisite.getIdVisite());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de la suppression de la visite.");
            return;
        }

        showAlert("Demande confirmée, SMS envoyé et visite supprimée !");
        loadVisites(); // Recharger la liste mise à jour
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}