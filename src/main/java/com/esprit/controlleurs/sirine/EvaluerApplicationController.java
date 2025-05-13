package com.esprit.controlleurs.sirine;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class EvaluerApplicationController {

    @FXML
    private HBox starsBox;

    @FXML
    private Label noteLabel;

    private final int totalStars = 5;
    private int currentRating = 0;

    @FXML
    public void initialize() {
        starsBox.getChildren().clear();
        for (int i = 1; i <= totalStars; i++) {
            ImageView star = new ImageView();
            String emptyStarPath = "/jaune.png";
            String filledStarPath = "/vide.png";
            Image emptyStarImage = new Image(getClass().getResource(emptyStarPath) != null ?
                    getClass().getResource(emptyStarPath).toExternalForm() :
                    getClass().getClassLoader().getResource(emptyStarPath).toExternalForm());
            if (emptyStarImage.isError()) {
                System.err.println("Image non trouvée : " + emptyStarPath);
                continue;
            }
            star.setImage(emptyStarImage);
            star.setFitHeight(40);
            star.setFitWidth(40);
            int rating = i;

            star.setOnMouseClicked((MouseEvent e) -> {
                updateStars(rating);
                noteLabel.setText("Merci pour votre note : " + rating + "/5");
            });

            starsBox.getChildren().add(star);
        }
    }

    private void updateStars(int rating) {
        currentRating = rating;
        for (int i = 0; i < totalStars; i++) {
            ImageView star = (ImageView) starsBox.getChildren().get(i);
            String filledStarPath = "/jaune.png";
            String emptyStarPath = "/vide.png";
            Image filledStarImage = new Image(getClass().getResource(filledStarPath) != null ?
                    getClass().getResource(filledStarPath).toExternalForm() :
                    getClass().getClassLoader().getResource(filledStarPath).toExternalForm());
            Image emptyStarImage = new Image(getClass().getResource(emptyStarPath) != null ?
                    getClass().getResource(emptyStarPath).toExternalForm() :
                    getClass().getClassLoader().getResource(emptyStarPath).toExternalForm());
            if (i < rating && !filledStarImage.isError()) {
                star.setImage(filledStarImage);
            } else if (!emptyStarImage.isError()) {
                star.setImage(emptyStarImage);
            }
        }
    }

    @FXML
    private void submitRating() {
        if (currentRating > 0) {
            noteLabel.setText("Note soumise : " + currentRating + "/5. Merci !");
            noteLabel.setStyle("-fx-text-fill: green;");
        } else {
            noteLabel.setText("Veuillez sélectionner une note avant de soumettre.");
            noteLabel.setStyle("-fx-text-fill: red;");
        }
    }
}