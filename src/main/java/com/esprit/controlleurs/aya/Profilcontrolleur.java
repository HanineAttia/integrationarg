package com.esprit.controlleurs.aya;

import com.esprit.entities.aya.Personne;
import com.esprit.services.aya.Servicepersonne;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static com.esprit.controlleurs.aya.Logincontroller.personneactuelle;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class Profilcontrolleur implements Initializable {

    @FXML
    private Label dateLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label motDePasseLabel;

    @FXML
    private Label nomLabel;

    @FXML
    private ImageView photoView;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label roleLabel;
    public void setPersonne(Personne personne) {
        nomLabel.setText(personne.getNom());
        prenomLabel.setText(personne.getPrenom());
        dateLabel.setText(personne.getDate());
        roleLabel.setText(personne.getRole());
        emailLabel.setText(personne.getEmail());
        motDePasseLabel.setText(personne.getMotDePasse());

        if (personne.getPhoto() != null && !personne.getPhoto().isEmpty()) {
            File imageFile = new File(personne.getPhoto());
            if (imageFile.exists()) {
                Image img = new Image(imageFile.toURI().toString());
                photoView.setImage(img);

                // Cercle pour le clip de l'image
                Circle clip = new Circle(60, 60, 60);
                photoView.setClip(clip);
            } else {
                System.out.println("Image non trouvée : " + personne.getPhoto());
                Image imgDefault = new Image("/images/user_img.jpg");
                photoView.setImage(imgDefault);
            }
        }

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // facultatif : logique d'init si nécessaire
    }

    @FXML
    void deconnecter(ActionEvent event) {
        try {
            String role = personneactuelle.getRole();  // Vérifiez que cette méthode existe
            String fxmlFile;
            FXMLLoader loader;

            if ("Administrateur".equals(role)) {
                fxmlFile = "/Menuad.fxml";
                loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();

                // Récupérer le bon contrôleur
                Menuadcontroller controller = loader.getController();
                controller.setUtilisateurConnecte(personneactuelle);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Menu Administrateur");
                stage.show();

            } else {
                fxmlFile = "/Menu.fxml";
                loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();

                // Récupérer le bon contrôleur
                Menucontroller controller = loader.getController();
                controller.setUtilisateurConnecte(personneactuelle);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Menu Utilisateur");
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void modifierProfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modifierprofil.fxml"));
            Parent root = loader.load();

            Modifierprofilcontrolleur controller = loader.getController();
            controller.initData(Logincontroller.personneactuelle); // <-- Envoi des données ici
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("modifier profil");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void supprimerProfil(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer votre profil ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Servicepersonne service = new Servicepersonne();

                // Supprimer le profil dans la base de données
                service.supprimer(personneactuelle.getId());
                System.out.println("Profil supprimé avec succès.");

                // Redirection vers l'écran de connexion
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
                stage.show();

            } catch (SQLException e) {
                System.err.println("Erreur SQL lors de la suppression : " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Erreur de chargement de la page : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression annulée par l'utilisateur.");
        }

    }


    public void ajouterPhoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg")
        );

        Stage currentStage = (Stage) photoView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(currentStage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            photoView.setImage(image);

            Platform.runLater(() -> {
                photoView.setClip(null); // Reset clip
                double radius = Math.min(photoView.getFitWidth(), photoView.getFitHeight()) / 2;
                Circle clip = new Circle(photoView.getFitWidth() / 2, photoView.getFitHeight() / 2, radius);
                photoView.setClip(clip);
            });

            // Mise à jour de l'objet
            personneactuelle.setPhoto(file.getAbsolutePath());

            // Debug
            System.out.println("Nouveau chemin image : " + file.getAbsolutePath());

            // Mise à jour base de données
            try {
                Servicepersonne service = new Servicepersonne();
                service.modifier(personneactuelle);
                System.out.println("Image mise à jour avec succès.");
            } catch (SQLException e) {
                System.err.println("Erreur SQL : " + e.getMessage());
            }
        } else {
            System.out.println("Aucune image sélectionnée.");
        }
    }

}
