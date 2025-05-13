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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.esprit.controlleurs.aya.Logincontroller.personneactuelle;

public class Menuadcontroller {

    @FXML
    private Button btnCommandes;

    @FXML
    private Button btnDeconnecter;

    @FXML
    private Button btnEmploye;

    @FXML
    private Button btnPaiement;

    @FXML
    private Button btnProduitBio;

    @FXML
    private Button btnProfil;

    @FXML
    private Button btnReclamation;

    @FXML
    private Button btnUtilisateur;

    @FXML
    private Button btnVisite;

    @FXML
    private Label labelNomPrenom;

    @FXML
    private ImageView photoView;

    @FXML
    void deconnecter(ActionEvent event) {
        try {
            // Charger le fichier FXML de l'interface de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            // Obtenir la fenêtre actuelle et changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Terracult");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void handleCommandesAction(ActionEvent event) {

    }

    @FXML
    void handleEmployeAction(ActionEvent event) {

    }

    @FXML
    void handlePaiementAction(ActionEvent event) {

    }

    @FXML
    void handleProduitBioAction(ActionEvent event) {

    }

    @FXML
    void handleProfilAction(ActionEvent event) {
        try {
            // Charger la vue de profil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
            Parent root = loader.load();

            // Passer l'objet "personneactuelle" au contrôleur du profil
            Profilcontrolleur profilController = loader.getController();
            profilController.setPersonne(personneactuelle); // Passer l'utilisateur actuel

            // Ouvrir une nouvelle fenêtre avec la scène de profil
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil");
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void handleReclamationAction(ActionEvent event) {

    }

    @FXML
    void handleUtilisateurAction(ActionEvent event) {
        try {
            // Charger l'interface d'affichage avec le tableau
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Affichage.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Affichage");
            currentStage.show();


            // Récupérer le contrôleur de l'interface d'affichage
            Affichercontroller afficherController = loader.getController();

            // Appeler la méthode pour récupérer et afficher les données dans le tableau
            List<Personne> personnes = new Servicepersonne().getAll();  // Vous pouvez utiliser un service approprié
            ObservableList<Personne> observablePersonnes = FXCollections.observableArrayList(personnes);
            afficherController.getTableUtilisateurs().setItems(observablePersonnes);

            // Initialiser les colonnes du tableau dans Affichercontroller
            afficherController.getColId().setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            afficherController.getColNom().setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
            afficherController.getColPrenom().setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
            afficherController.getColEmail().setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            afficherController.getColRole().setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
            afficherController.getColDate().setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
            afficherController.getColMotDePasse().setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMotDePasse()));
            afficherController.getColPhoto().setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoto()));

            // Afficher la nouvelle scène avec le tableau
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            // Gérez les exceptions si nécessaire
        }

    }

    @FXML
    void handleVisiteAction(ActionEvent event) {

    }

    public void setUtilisateurConnecte(Personne personne) {
        labelNomPrenom.setText(personne.getNom() + " " + personne.getPrenom());

        if (personne.getPhoto() != null && !personne.getPhoto().isEmpty()) {
            // Récupère le chemin de l'image
            String imagePath = personne.getPhoto();

            // Vérifie si l'image existe
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Crée une image à partir du fichier
                Image img = new Image(imageFile.toURI().toString());
                photoView.setImage(img);

                // Applique un clip en forme de cercle
                Circle clip = new Circle(24, 24, 24); // Assure-toi que ces valeurs correspondent à la taille de ton ImageView
                photoView.setClip(clip);
            } else {
                // Si l'image n'existe pas, affiche une image par défaut
                System.out.println("Image non trouvée : " + imagePath);
                Image imgDefault = new Image(getClass().getResource("/images/user_img.jpg").toExternalForm());
                photoView.setImage(imgDefault);
            }
        } else {
            // Si le chemin de l'image est null ou vide, utilise l'image par défaut
            Image imgDefault = new Image(getClass().getResource("/images/user_img.jpg").toExternalForm());
            photoView.setImage(imgDefault);
        }
        // Applique un clip en forme de cercle
        Circle clip = new Circle(24, 24, 24); // Assure-toi que ces valeurs correspondent à la taille de ton ImageView
        photoView.setClip(clip);

    }
}
