package com.esprit.controlleurs.hanine;

import com.esprit.entities.hanine.Materiels;
import com.esprit.entities.hanine.Reservation;
import com.esprit.services.hanine.Materielservices;
import com.esprit.services.hanine.ReservationServices;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TechnicienController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebar;
    @FXML private Button hamburgerButton;
    @FXML private Label pageTitle;
    @FXML private ImageView logo;
    @FXML private Button viewMaterialsButton;
    @FXML private Button viewReservationsButton;
    @FXML private Button homeButton;
    @FXML private VBox contentPane;
    @FXML private GridPane cardGrid;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField searchField;
    @FXML private Label totalMaterialsLabel;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button filterButton;
    @FXML private VBox filterSidebar;
    @FXML private Button selectMaterialsButton;
    @FXML private Button reserveSelectedButton;

    private boolean isSidebarVisible = false;
    private boolean isFilterSidebarVisible = false;
    private boolean isSelectionMode = false;
    private List<Materiels> allMaterials;
    private List<Reservation> technicianReservations;
    private List<Materiels> selectedMaterials = new ArrayList<>();
    private final int ID_TECHNICIEN = 1; // For now, hardcoding technician ID; replace with proper authentication
    private CheckBox availableCheckBox;
    private CheckBox outOfStockCheckBox;
    private Map<String, CheckBox> categoryCheckBoxes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (contentPane == null || cardGrid == null || rootPane == null || scrollPane == null || filterSidebar == null || filterButton == null || selectMaterialsButton == null || reserveSelectedButton == null) {
            System.err.println("FXML injection failed: one or more fields are null.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur d'initialisation");
            alert.setContentText("Certains composants FXML n'ont pas été injectés correctement.");
            alert.showAndWait();
            return;
        }

        hamburgerButton.setOnAction(event -> toggleSidebar());
        viewMaterialsButton.setOnAction(event -> showMaterialList());
        viewReservationsButton.setOnAction(event -> showReservationsList());
        homeButton.setOnAction(event -> showMaterialList());
        selectMaterialsButton.setOnAction(event -> toggleSelectionMode());
        reserveSelectedButton.setOnAction(event -> showReservationFormForSelected());

        sortComboBox.setItems(FXCollections.observableArrayList("Nom A à Z", "Nom Z à A", "Prix croissants", "Prix décroissants"));
        sortComboBox.setValue("Nom A à Z");
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateMaterialList());

        filterButton.setOnAction(event -> toggleFilterSidebar());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateMaterialList());

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0 && !isFilterSidebarVisible && filterSidebar.isVisible()) {
                filterSidebar.setTranslateX(newVal.doubleValue());
            }
        });

        showMaterialList();
    }

    private void toggleSidebar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        if (isSidebarVisible) {
            transition.setToX(-250);
            sidebar.setPrefWidth(0);
        } else {
            transition.setToX(0);
            sidebar.setPrefWidth(250);
        }
        transition.play();
        isSidebarVisible = !isSidebarVisible;
    }

    private void toggleFilterSidebar() {
        if (!isFilterSidebarVisible) {
            filterSidebar.setVisible(true);
        }
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), filterSidebar);
        if (isFilterSidebarVisible) {
            transition.setToX(filterSidebar.getPrefWidth());
            filterSidebar.setPrefWidth(0);
        } else {
            transition.setToX(0);
            filterSidebar.setPrefWidth(250);
        }
        transition.play();
        isFilterSidebarVisible = !isFilterSidebarVisible;
    }

    private void setupFilterSidebar() {
        filterSidebar.getChildren().clear();
        filterSidebar.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 20;");
        filterSidebar.setVisible(false);

        HBox availabilityHeader = new HBox(10);
        Label availabilityLabel = new Label("Disponibilité");
        availabilityLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Button availabilityToggle = new Button("▼");
        availabilityToggle.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-font-size: 12px;");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        availabilityHeader.getChildren().addAll(availabilityLabel, spacer, availabilityToggle);
        VBox availabilitySection = new VBox(10);
        availabilitySection.setVisible(false);
        availableCheckBox = new CheckBox("Disponible");
        outOfStockCheckBox = new CheckBox("Hors stock");
        availabilitySection.getChildren().addAll(availableCheckBox, outOfStockCheckBox);

        availabilityToggle.setOnAction(event -> {
            availabilitySection.setVisible(!availabilitySection.isVisible());
            availabilityToggle.setText(availabilitySection.isVisible() ? "▲" : "▼");
        });

        HBox categoryHeader = new HBox(10);
        Label categoryLabel = new Label("Catégorie");
        categoryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Button categoryToggle = new Button("▼");
        categoryToggle.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-font-size: 12px;");
        Pane categorySpacer = new Pane();
        HBox.setHgrow(categorySpacer, Priority.ALWAYS);
        categoryHeader.getChildren().addAll(categoryLabel, categorySpacer, categoryToggle);
        VBox categorySection = new VBox(10);
        categorySection.setVisible(false);
        categoryCheckBoxes = new HashMap<>();
        for (String cat : Arrays.asList("Machinerie", "Outils", "Irrigation")) {
            CheckBox cb = new CheckBox(cat);
            categoryCheckBoxes.put(cat, cb);
            categorySection.getChildren().add(cb);
        }

        categoryToggle.setOnAction(event -> {
            categorySection.setVisible(!categorySection.isVisible());
            categoryToggle.setText(categorySection.isVisible() ? "▲" : "▼");
        });

        Button confirmButton = new Button("Confirmer");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        confirmButton.setOnAction(event -> {
            updateMaterialList();
            toggleFilterSidebar();
        });

        filterSidebar.getChildren().addAll(availabilityHeader, availabilitySection, categoryHeader, categorySection, confirmButton);
    }

    private void toggleSelectionMode() {
        isSelectionMode = !isSelectionMode;
        selectMaterialsButton.setText(isSelectionMode ? "Annuler Sélection" : "Sélectionner des Matériaux");
        reserveSelectedButton.setVisible(isSelectionMode);
        selectedMaterials.clear();
        updateMaterialList();
    }

    private void showMaterialList() {
        contentPane.getChildren().clear();
        pageTitle.setText("Liste des Matériaux");

        try {
            Materielservices materielServices = new Materielservices();
            allMaterials = materielServices.getAll();
            if (allMaterials == null) {
                allMaterials = new ArrayList<>();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucun Matériel");
                alert.setHeaderText(null);
                alert.setContentText("Aucun matériel trouvé dans la base de données.");
                alert.showAndWait();
            }

            HBox filterSortBar = new HBox(10);
            filterSortBar.setAlignment(Pos.CENTER_LEFT);
            filterSortBar.setPadding(new Insets(10));
            filterSortBar.setStyle("-fx-padding: 10;");

            filterButton.setText("Filtrer par");
            filterButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");

            searchField.setPromptText("Rechercher par nom...");
            searchField.setStyle("-fx-pref-width: 200; -fx-padding: 8; -fx-border-radius: 5; -fx-background-radius: 5;");

            Label sortLabel = new Label("Trier par:");
            sortLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            selectMaterialsButton.setText("Sélectionner des Matériaux");
            selectMaterialsButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");

            reserveSelectedButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
            reserveSelectedButton.setVisible(false);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            totalMaterialsLabel.setText("Total : 0 matériels");

            filterSortBar.getChildren().addAll(filterButton, searchField, sortLabel, sortComboBox, selectMaterialsButton, reserveSelectedButton, spacer, totalMaterialsLabel);

            updateMaterialList();

            contentPane.getChildren().addAll(filterSortBar, scrollPane);

            if (isSidebarVisible) toggleSidebar();
        } catch (SQLException e) {
            allMaterials = new ArrayList<>();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible de récupérer les matériaux: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void updateMaterialList() {
        if (allMaterials == null) {
            allMaterials = new ArrayList<>();
        }
        List<Materiels> filteredMaterials = new ArrayList<>(allMaterials);

        // Apply search filter
        String searchText = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        if (!searchText.isEmpty()) {
            filteredMaterials = filteredMaterials.stream()
                    .filter(m -> m.getName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
        }

        // Apply availability filter
        if (availableCheckBox != null && outOfStockCheckBox != null) {
            boolean filterAvailable = availableCheckBox.isSelected();
            boolean filterOutOfStock = outOfStockCheckBox.isSelected();
            if (filterAvailable || filterOutOfStock) {
                filteredMaterials = filteredMaterials.stream()
                        .filter(m -> {
                            if (filterAvailable && filterOutOfStock) return true;
                            else if (filterAvailable) return m.isDisponibility();
                            else if (filterOutOfStock) return !m.isDisponibility();
                            return false;
                        })
                        .collect(Collectors.toList());
            }
        }

        // Apply category filter
        if (categoryCheckBoxes != null && !categoryCheckBoxes.isEmpty()) {
            List<String> selectedCategories = categoryCheckBoxes.entrySet().stream()
                    .filter(entry -> entry.getValue().isSelected())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (!selectedCategories.isEmpty()) {
                filteredMaterials = filteredMaterials.stream()
                        .filter(m -> selectedCategories.contains(m.getCategorie()))
                        .collect(Collectors.toList());
            }
        }

        // Apply sorting
        String sortOption = sortComboBox.getValue() != null ? sortComboBox.getValue() : "Nom A à Z";
        filteredMaterials.sort((m1, m2) -> {
            switch (sortOption) {
                case "Nom A à Z": return m1.getName().compareToIgnoreCase(m2.getName());
                case "Nom Z à A": return m2.getName().compareToIgnoreCase(m1.getName());
                case "Prix croissants": return Double.compare(m1.getPrice(), m2.getPrice());
                case "Prix décroissants": return Double.compare(m2.getPrice(), m1.getPrice());
                default: return 0;
            }
        });

        totalMaterialsLabel.setText("Total : " + filteredMaterials.size() + " matériels");

        updateCardGrid(filteredMaterials);
    }

    private void updateCardGrid(List<Materiels> materials) {
        cardGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        int maxColumns = 3;

        for (Materiels material : materials) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
            card.setPrefWidth(200);
            card.setAlignment(Pos.CENTER);
            card.setCursor(javafx.scene.Cursor.HAND);

            CheckBox selectCheckBox = new CheckBox();
            selectCheckBox.setVisible(isSelectionMode);
            selectCheckBox.setSelected(selectedMaterials.contains(material));
            selectCheckBox.setOnAction(event -> {
                if (selectCheckBox.isSelected()) {
                    selectedMaterials.add(material);
                } else {
                    selectedMaterials.remove(material);
                }
            });

            Label nameLabel = new Label(material.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(180);

            ImageView imageView = new ImageView();
            try {
                Image image = new Image(material.getImageurl(), true);
                imageView.setImage(image);
                imageView.setFitWidth(180);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                try {
                    Image fallback = new Image(getClass().getResourceAsStream("/image/Drone.png"));
                    imageView.setImage(fallback);
                } catch (Exception ex) {
                    System.err.println("Fallback image /image/Drone.png not found");
                }
                imageView.setFitWidth(180);
                imageView.setFitHeight(150);
            }

            Button reserveButton = new Button("Réserver");
            reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
            reserveButton.setOnAction(event -> showReservationForm(material));
            reserveButton.setVisible(!isSelectionMode);

            card.getChildren().addAll(selectCheckBox, nameLabel, imageView, reserveButton);

            card.setOnMouseClicked(event -> {
                if (!isSelectionMode) showMaterialDetails(material);
            });

            cardGrid.add(card, column, row);
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }

    private void showMaterialDetails(Materiels material) {
        contentPane.getChildren().clear();
        pageTitle.setText("Détails du Matériel");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox detailsPane = new VBox(15);
        detailsPane.setPadding(new Insets(20));
        detailsPane.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        detailsPane.setAlignment(Pos.CENTER);
        detailsPane.setMaxWidth(400);

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
        backButton.setOnAction(event -> showMaterialList());
        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10, 0, 10, 10));

        ImageView largeImage = new ImageView();
        try {
            Image image = new Image(material.getImageurl(), true);
            largeImage.setImage(image);
            largeImage.setFitWidth(300);
            largeImage.setFitHeight(250);
            largeImage.setPreserveRatio(true);
        } catch (Exception e) {
            try {
                Image fallback = new Image(getClass().getResourceAsStream("/image/Drone.png"));
                largeImage.setImage(fallback);
            } catch (Exception ex) {
                System.err.println("Fallback image /image/Drone.png not found");
            }
            largeImage.setFitWidth(300);
            largeImage.setFitHeight(250);
        }

        Label nameLabel = new Label("Nom: " + material.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label descriptionLabel = new Label("Description: " + material.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(350);

        Label categoryLabel = new Label("Catégorie: " + material.getCategorie());
        Label priceLabel = new Label("Prix: " + String.format("%.2f", material.getPrice()));
        Label dateAjoutLabel = new Label("Date d'ajout: " + material.getDateajout());
        Label disponibilityLabel = new Label("Disponibilité: " + (material.isDisponibility() ? "Oui" : "Non"));
        Label quantiteLabel = new Label("Quantité en stock: " + material.getQuantite());

        Button reserveButton = new Button("Réserver ce matériel");
        reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        reserveButton.setOnAction(event -> showReservationForm(material));

        detailsPane.getChildren().addAll(topBar, largeImage, nameLabel, descriptionLabel, categoryLabel,
                priceLabel, dateAjoutLabel, disponibilityLabel, quantiteLabel, reserveButton);

        scrollPane.setContent(detailsPane);
        contentPane.getChildren().add(scrollPane);

        contentPane.setAlignment(Pos.CENTER);
    }

    private void showReservationForm(Materiels material) {
        if (!material.isDisponibility() || material.getQuantite() <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Matériel Indisponible");
            alert.setHeaderText(null);
            alert.setContentText("Ce matériel n'est pas disponible pour la réservation.");
            alert.showAndWait();
            return;
        }

        contentPane.getChildren().clear();
        pageTitle.setText("Réserver un Matériel");

        GridPane reservationForm = new GridPane();
        reservationForm.setHgap(10);
        reservationForm.setVgap(10);
        reservationForm.setStyle("-fx-background-color: #F9F9F9; -fx-padding: 20; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label materialLabel = new Label("Matériel:");
        materialLabel.setStyle("-fx-font-weight: bold;");
        TextField materialField = new TextField(material.getName());
        materialField.setEditable(false);
        reservationForm.add(materialLabel, 0, 0);
        reservationForm.add(materialField, 1, 0);

        Label dateDebutLabel = new Label("Date Début:");
        dateDebutLabel.setStyle("-fx-font-weight: bold;");
        DatePicker dateDebutField = new DatePicker();
        dateDebutField.setValue(LocalDate.now());
        reservationForm.add(dateDebutLabel, 0, 1);
        reservationForm.add(dateDebutField, 1, 1);

        Label dateFinLabel = new Label("Date Fin:");
        dateFinLabel.setStyle("-fx-font-weight: bold;");
        DatePicker dateFinField = new DatePicker();
        dateFinField.setValue(LocalDate.now().plusDays(1));
        reservationForm.add(dateFinLabel, 0, 2);
        reservationForm.add(dateFinField, 1, 2);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.BOTTOM_RIGHT);
        Button submitButton = new Button("Réserver");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-cursor: hand; -fx-border-radius: 5;");
        submitButton.setOnAction(event -> createReservation(Collections.singletonList(material), dateDebutField, dateFinField));
        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-cursor: hand; -fx-border-radius: 5;");
        cancelButton.setOnAction(event -> showMaterialList());
        buttonBar.getChildren().addAll(submitButton, cancelButton);
        reservationForm.add(buttonBar, 1, 3, 2, 1);

        contentPane.getChildren().add(reservationForm);

        if (isSidebarVisible) toggleSidebar();
    }

    private void showReservationFormForSelected() {
        if (selectedMaterials.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune Sélection");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner au moins un matériel à réserver.");
            alert.showAndWait();
            return;
        }

        for (Materiels material : selectedMaterials) {
            if (!material.isDisponibility() || material.getQuantite() <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Matériel Indisponible");
                alert.setHeaderText(null);
                alert.setContentText("Le matériel " + material.getName() + " n'est pas disponible pour la réservation.");
                alert.showAndWait();
                return;
            }
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Réserver les Matériaux Sélectionnés");
        alert.setHeaderText("Formulaire de Réservation");

        GridPane reservationForm = new GridPane();
        reservationForm.setHgap(10);
        reservationForm.setVgap(10);
        reservationForm.setPadding(new Insets(20));

        Label materialsLabel = new Label("Matériaux:");
        materialsLabel.setStyle("-fx-font-weight: bold;");
        TextArea materialsField = new TextArea();
        materialsField.setEditable(false);
        materialsField.setPrefRowCount(3);
        materialsField.setText(selectedMaterials.stream().map(Materiels::getName).collect(Collectors.joining("\n")));
        reservationForm.add(materialsLabel, 0, 0);
        reservationForm.add(materialsField, 1, 0);

        Label dateDebutLabel = new Label("Date Début:");
        dateDebutLabel.setStyle("-fx-font-weight: bold;");
        DatePicker dateDebutField = new DatePicker();
        dateDebutField.setValue(LocalDate.now());
        reservationForm.add(dateDebutLabel, 0, 1);
        reservationForm.add(dateDebutField, 1, 1);

        Label dateFinLabel = new Label("Date Fin:");
        dateFinLabel.setStyle("-fx-font-weight: bold;");
        DatePicker dateFinField = new DatePicker();
        dateFinField.setValue(LocalDate.now().plusDays(1));
        reservationForm.add(dateFinLabel, 0, 2);
        reservationForm.add(dateFinField, 1, 2);

        alert.getDialogPane().setContent(reservationForm);

        ButtonType reserveButton = new ButtonType("Réserver");
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(reserveButton, cancelButton);

        alert.showAndWait().ifPresent(result -> {
            if (result == reserveButton) {
                createReservation(selectedMaterials, dateDebutField, dateFinField);
                toggleSelectionMode();
            }
        });
    }

    private void createReservation(List<Materiels> materials, DatePicker dateDebutField, DatePicker dateFinField) {
        if (dateDebutField.getValue() == null || dateFinField.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Dates manquantes");
            alert.setContentText("Veuillez sélectionner les dates de début et de fin.");
            alert.showAndWait();
            return;
        }

        LocalDate dateDebut = dateDebutField.getValue();
        LocalDate dateFin = dateFinField.getValue();

        if (dateDebut.isAfter(dateFin)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Dates invalides");
            alert.setContentText("La date de début doit être antérieure à la date de fin.");
            alert.showAndWait();
            return;
        }

        try {
            ReservationServices reservationServices = new ReservationServices();
            Materielservices materielServices = new Materielservices();
            List<String> unavailableMaterials = new ArrayList<>();

            for (Materiels material : materials) {
                if (material.getQuantite() == 1) {
                    List<Reservation> existingReservations = reservationServices.getAll().stream()
                            .filter(r -> r.getIdMateriel() == material.getId())
                            .collect(Collectors.toList());

                    boolean hasOverlap = false;
                    for (Reservation existing : existingReservations) {
                        LocalDate existingStart = LocalDate.parse(existing.getDateDebut());
                        LocalDate existingEnd = LocalDate.parse(existing.getDateFin());

                        if (!(dateFin.isBefore(existingStart) || dateDebut.isAfter(existingEnd))) {
                            hasOverlap = true;
                            break;
                        }
                    }

                    if (hasOverlap) {
                        unavailableMaterials.add(material.getName());
                    }
                }
            }

            if (!unavailableMaterials.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Matériel Indisponible");
                alert.setHeaderText("Conflit de réservation");
                alert.setContentText("Les matériels suivants ne sont pas disponibles pour les dates sélectionnées:\n" +
                        String.join("\n", unavailableMaterials));
                alert.showAndWait();
                return;
            }

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de Réservation");
            confirmationAlert.setHeaderText("Confirmer votre réservation");
            confirmationAlert.setContentText("Voulez-vous confirmer la réservation pour " + materials.size() + " matériel(s) ?");

            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeNo) {
                return;
            }

            for (Materiels material : materials) {
                Reservation reservation = new Reservation(
                        material.getId(),
                        ID_TECHNICIEN,
                        dateDebut.toString(),
                        dateFin.toString(),
                        LocalDate.now().toString(),
                        "Pending"
                );
                reservationServices.ajouter(reservation);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Réservation effectuée");
            alert.setContentText("Votre réservation pour " + materials.size() + " matériel(s) a été enregistrée avec succès.");
            alert.showAndWait();

            showMaterialList();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible d'enregistrer la réservation: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void showEditReservationForm(Reservation reservation) {
        Materielservices materielServices = new Materielservices();
        Materiels material = null;
        try {
            material = materielServices.getAll().stream()
                    .filter(m -> m.getId() == reservation.getIdMateriel())
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible de récupérer les informations du matériel: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
            return;
        }

        if (material == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Matériel introuvable");
            alert.setContentText("Le matériel associé à cette réservation n'a pas été trouvé.");
            alert.showAndWait();
            return;
        }

        contentPane.getChildren().clear();
        pageTitle.setText("Modifier la Réservation");

        GridPane reservationForm = new GridPane();
        reservationForm.setHgap(10);
        reservationForm.setVgap(10);
        reservationForm.setStyle("-fx-background-color: #F9F9F9; -fx-padding: 20; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label materialLabel = new Label("Matériel:");
        materialLabel.setStyle("-fx-font-weight: bold;");
        TextField materialField = new TextField(material.getName());
        materialField.setEditable(false);
        reservationForm.add(materialLabel, 0, 0);
        reservationForm.add(materialField, 1, 0);

        Label dateDebutLabel = new Label("Date Début:");
        dateDebutLabel.setStyle("-fx-font-weight: bold;");
        DatePicker dateDebutField = new DatePicker();
        dateDebutField.setValue(LocalDate.parse(reservation.getDateDebut()));
        reservationForm.add(dateDebutLabel, 0, 1);
        reservationForm.add(dateDebutField, 1, 1);

        Label dateFinLabel = new Label("Date Fin:");
        dateFinLabel.setStyle("-fx-font-weight: bold;");
        DatePicker dateFinField = new DatePicker();
        dateFinField.setValue(LocalDate.parse(reservation.getDateFin()));
        reservationForm.add(dateFinLabel, 0, 2);
        reservationForm.add(dateFinField, 1, 2);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.BOTTOM_RIGHT);
        Button submitButton = new Button("Modifier");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-cursor: hand; -fx-border-radius: 5;");
        submitButton.setOnAction(event -> {
            if (dateDebutField.getValue() == null || dateFinField.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Dates manquantes");
                alert.setContentText("Veuillez sélectionner les dates de début et de fin.");
                alert.showAndWait();
                return;
            }

            LocalDate dateDebut = dateDebutField.getValue();
            LocalDate dateFin = dateFinField.getValue();

            if (dateDebut.isAfter(dateFin)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Dates invalides");
                alert.setContentText("La date de début doit être antérieure à la date de fin.");
                alert.showAndWait();
                return;
            }

            try {
                ReservationServices reservationServices = new ReservationServices();
                List<Reservation> existingReservations = reservationServices.getAll().stream()
                        .filter(r -> r.getIdMateriel() == reservation.getIdMateriel() && r.getIdReservation() != reservation.getIdReservation())
                        .collect(Collectors.toList());

                boolean hasOverlap = false;
                for (Reservation existing : existingReservations) {
                    LocalDate existingStart = LocalDate.parse(existing.getDateDebut());
                    LocalDate existingEnd = LocalDate.parse(existing.getDateFin());

                    if (!(dateFin.isBefore(existingStart) || dateDebut.isAfter(existingEnd))) {
                        hasOverlap = true;
                        break;
                    }
                }

                if (hasOverlap) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Conflit de Réservation");
                    alert.setHeaderText("Matériel indisponible");
                    alert.setContentText("Les nouvelles dates entrent en conflit avec une autre réservation pour ce matériel.");
                    alert.showAndWait();
                    return;
                }

                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirmation de Modification");
                confirmationAlert.setHeaderText("Confirmer la modification");
                confirmationAlert.setContentText("Voulez-vous confirmer la modification de la réservation ?");
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Reservation updatedReservation = new Reservation(
                            reservation.getIdReservation(),
                            reservation.getIdMateriel(),
                            reservation.getIdTechnicien(),
                            dateDebut.toString(),
                            dateFin.toString(),
                            reservation.getDateReservation(),
                            reservation.getStatut()
                    );
                    reservationServices.modifier(updatedReservation);
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Succès");
                    success.setHeaderText(null);
                    success.setContentText("Réservation modifiée avec succès.");
                    success.showAndWait();
                    showReservationsList();
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur de base de données");
                alert.setContentText("Impossible de modifier la réservation: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-cursor: hand; -fx-border-radius: 5;");
        cancelButton.setOnAction(event -> showReservationsList());
        buttonBar.getChildren().addAll(submitButton, cancelButton);
        reservationForm.add(buttonBar, 1, 3, 2, 1);

        contentPane.getChildren().add(reservationForm);

        if (isSidebarVisible) toggleSidebar();
    }

    private void showReservationsList() {
        contentPane.getChildren().clear();
        pageTitle.setText("Mes Réservations");

        try {
            ReservationServices reservationServices = new ReservationServices();
            technicianReservations = reservationServices.getReservationsByTechnicien(ID_TECHNICIEN);
            if (technicianReservations == null || technicianReservations.isEmpty()) {
                technicianReservations = new ArrayList<>();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucune Réservation");
                alert.setHeaderText(null);
                alert.setContentText("Vous n'avez aucune réservation.");
                alert.showAndWait();
            }

            LocalDate today = LocalDate.now();
            for (Reservation reservation : technicianReservations) {
                LocalDate startDate = LocalDate.parse(reservation.getDateDebut());
                LocalDate endDate = LocalDate.parse(reservation.getDateFin());
                String currentStatus = reservation.getStatut();

                if (endDate.isBefore(today) && currentStatus.equals("Pending")) {
                    reservation.setStatut("Expired");
                    reservationServices.updateStatus(reservation.getIdReservation(), "Expired");
                } else if (startDate.isAfter(today) && currentStatus.equals("Pending")) {
                    reservation.setStatut("Upcoming");
                    reservationServices.updateStatus(reservation.getIdReservation(), "Upcoming");
                } else if (startDate.isBefore(today) && endDate.isAfter(today) && currentStatus.equals("Pending")) {
                    reservation.setStatut("Active");
                    reservationServices.updateStatus(reservation.getIdReservation(), "Active");
                }
            }

            ScrollPane reservationsScrollPane = new ScrollPane();
            reservationsScrollPane.setFitToWidth(true);
            reservationsScrollPane.setStyle("-fx-background: #FFFFFF; -fx-border-color: transparent;");

            VBox reservationsPane = new VBox(10);
            reservationsPane.setPadding(new Insets(20));
            reservationsPane.setAlignment(Pos.CENTER);

            for (Reservation reservation : technicianReservations) {
                Materielservices materielServices = new Materielservices();
                Materiels material = materielServices.getAll().stream()
                        .filter(m -> m.getId() == reservation.getIdMateriel())
                        .findFirst()
                        .orElse(null);

                VBox reservationCard = new VBox(5);
                reservationCard.setPadding(new Insets(10));
                reservationCard.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                reservationCard.setPrefWidth(400);
                reservationCard.setAlignment(Pos.CENTER_LEFT);

                Label materialLabel = new Label("Matériel: " + (material != null ? material.getName() : "Inconnu"));
                materialLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                Label dateDebutLabel = new Label("Date Début: " + reservation.getDateDebut());
                Label dateFinLabel = new Label("Date Fin: " + reservation.getDateFin());
                Label dateReservationLabel = new Label("Date de Réservation: " + reservation.getDateReservation());
                Label statutLabel = new Label("Statut: " + reservation.getStatut());

                HBox buttonBar = new HBox(10);
                buttonBar.setAlignment(Pos.CENTER_RIGHT);

                Button editButton = new Button("Modifier");
                editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
                editButton.setDisable(!reservation.getStatut().equals("Upcoming"));
                editButton.setOnAction(event -> showEditReservationForm(reservation));
                buttonBar.getChildren().add(editButton);

                Button cancelButton = new Button("Annuler");
                cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
                cancelButton.setDisable(!reservation.getStatut().equals("Pending") && !reservation.getStatut().equals("Upcoming"));
                cancelButton.setOnAction(event -> {
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Confirmer l'Annulation");
                    confirmation.setHeaderText("Annuler la réservation");
                    confirmation.setContentText("Êtes-vous sûr de vouloir annuler cette réservation ?");
                    Optional<ButtonType> result = confirmation.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            reservationServices.supprimer(reservation.getIdReservation());
                            Alert success = new Alert(Alert.AlertType.INFORMATION);
                            success.setTitle("Succès");
                            success.setHeaderText(null);
                            success.setContentText("Réservation annulée avec succès.");
                            success.showAndWait();
                            showReservationsList();
                        } catch (SQLException e) {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Erreur");
                            error.setHeaderText("Erreur lors de l'annulation");
                            error.setContentText("Impossible d'annuler la réservation: " + e.getMessage());
                            error.showAndWait();
                            e.printStackTrace();
                        }
                    }
                });
                buttonBar.getChildren().add(cancelButton);

                reservationCard.getChildren().addAll(materialLabel, dateDebutLabel, dateFinLabel, dateReservationLabel, statutLabel, buttonBar);
                reservationsPane.getChildren().add(reservationCard);
            }

            Button backButton = new Button("Retour");
            backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
            backButton.setOnAction(event -> showMaterialList());
            reservationsPane.getChildren().add(backButton);

            reservationsScrollPane.setContent(reservationsPane);
            contentPane.getChildren().add(reservationsScrollPane);

            if (isSidebarVisible) toggleSidebar();
        } catch (SQLException e) {
            technicianReservations = new ArrayList<>();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible de récupérer les réservations: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}