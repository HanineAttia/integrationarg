package com.esprit.controlleurs.aya;


import com.esprit.entities.aya.Personne;
import com.esprit.services.aya.Servicepersonne;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Logincontroller {
    static Personne personneactuelle;
    @FXML
    private TextField emailField;

    @FXML
    private ImageView logoImageView;

    @FXML
    private PasswordField passwordField;

    @FXML
    void handleForgotPassword(ActionEvent event) {

    }

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String motDePasse = passwordField.getText().trim();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        Servicepersonne service = new Servicepersonne();
        try {
            List<Personne> personnes = service.getAll();
            boolean found = false;

            for (Personne p : personnes) {
                if (p.getEmail().equals(email) && p.getMotDePasse().equals(motDePasse)) {
                    found = true;
                    System.out.println("Connexion réussie : " + p.getNom());
                    personneactuelle = p;
                    // Sélection de l'interface selon le rôle
                    String fxmlPath;
                    if ("Administrateur".equalsIgnoreCase(p.getRole())) {
                        fxmlPath = "/Menuad.fxml";
                    } else {
                        fxmlPath = "/Menu.fxml";
                    }

                    // Chargement de l'interface
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent root = loader.load();

                    // Passage des informations de l'utilisateur au contrôleur
                    Object controller = loader.getController();
                    if (controller instanceof Menucontroller) {
                        ((Menucontroller) controller).setUtilisateurConnecte(p);
                    } else if (controller instanceof Menuadcontroller) {
                        ((Menuadcontroller) controller).setUtilisateurConnecte(p);
                    }

                    Scene scene = new Scene(root);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    break;
                }
            }

            if (!found) {
                showAlert(Alert.AlertType.ERROR, "Connexion échouée", "Email ou mot de passe incorrect.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlertt(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la connexion.");
        }
    }

    private void showAlertt(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleSignup(ActionEvent event) throws IOException {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouterpersonne.fxml"));
            Parent root = loader.load();

            // Crée une nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Créer un compte - Terracult");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Fermer la fenêtre actuelle (celle du login)
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'interface d'inscription.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String erreur, String s) {

        Alert alert = new Alert(alertType);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        // Créer un cercle (clip) au centre de l’image
        Circle circle = new Circle(45, 45, 45);
        logoImageView.setClip(circle);
    }

}
