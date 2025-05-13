package com.esprit.controlleurs.hanine;

import com.esprit.entities.hanine.Materiels;
import com.esprit.services.hanine.Materielservices;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AjouterMaterielcontroller implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebar;
    @FXML private Button hamburgerButton;
    @FXML private Label pageTitle;
    @FXML private ImageView logo;
    @FXML private Button viewMaterialsButton;
    @FXML private Button addMaterialButton;
    @FXML private Button homeButton;
    @FXML private VBox contentPane;
    @FXML private GridPane addMaterialForm;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField imageUrlField;
    @FXML private ComboBox<String> categoryField;
    @FXML private TextField priceField;
    @FXML private DatePicker dateAjoutField;
    @FXML private CheckBox disponibilityField;
    @FXML private Spinner<Integer> quantiteField;
    @FXML private Button submitButton;
    @FXML private Button importImageButton;
    @FXML private Button cancelButton;
    @FXML private Button generateDescriptionButton;
    @FXML private VBox filterSidebar;
    @FXML private Button filterButton;
    @FXML private ComboBox<String> sortComboBox;

    private FileChooser fileChooser;
    private boolean isSidebarVisible = false;
    private boolean isFilterSidebarVisible = false;
    private List<Materiels> allMaterials;
    private CheckBox availableCheckBox;
    private CheckBox outOfStockCheckBox;
    private Map<String, CheckBox> categoryCheckBoxes;
    private GridPane cardGrid;
    private ScrollPane scrollPane;
    private TextField searchField;
    private Label totalMaterialsLabel;
    private Materiels selectedMaterial;
    private final String API_TOKEN;
    private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/bart-base";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public AjouterMaterielcontroller() throws IOException {
        this.API_TOKEN = loadApiToken();
    }

    private String loadApiToken() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            if (is == null) {
                throw new IOException("Cannot find config.properties");
            }
            props.load(is);
            String token = props.getProperty("huggingface.api.token");
            if (token == null || token.isEmpty()) {
                throw new IOException("API token not found in config.properties");
            }
            return token;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (contentPane == null || addMaterialForm == null || rootPane == null || filterSidebar == null ||
                filterButton == null || sortComboBox == null || quantiteField == null) {
            System.err.println("FXML injection failed: one or more fields are null");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur d'initialisation");
            alert.setContentText("Certains composants FXML n'ont pas été injectés correctement.");
            alert.showAndWait();
            return;
        }

        hamburgerButton.setOnAction(event -> toggleSidebar());
        viewMaterialsButton.setOnAction(event -> showMaterialList());
        addMaterialButton.setOnAction(event -> showAddMaterialForm());
        homeButton.setOnAction(event -> showMaterialList());
        filterButton.setOnAction(event -> toggleFilterSidebar());

        ObservableList<String> categories = FXCollections.observableArrayList(
                "Machinerie", "Outils", "Semences", "Engrais", "Irrigation"
        );
        categoryField.setItems(categories);

        // Initialize quantiteField
        SpinnerValueFactory<Integer> quantiteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0);
        quantiteField.setValueFactory(quantiteFactory);
        // Disable disponibilityField if quantite is 0 and ensure it's unchecked
        quantiteField.valueProperty().addListener((obs, oldValue, newValue) -> {
            disponibilityField.setDisable(newValue == 0);
            if (newValue == 0) {
                disponibilityField.setSelected(false);
            }
        });
        // Initial check to ensure consistency
        if (quantiteField.getValue() == 0) {
            disponibilityField.setDisable(true);
            disponibilityField.setSelected(false);
        }

        // Initialize sortComboBox
        sortComboBox.setItems(FXCollections.observableArrayList("Nom A à Z", "Nom Z à A", "Prix croissants", "Prix décroissants"));
        sortComboBox.setValue("Nom A à Z");
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateMaterialList());

        // Initialize filter sidebar
        setupFilterSidebar();

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

        // Disponibilité section
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

        // Catégorie section
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

        // Confirmer button
        Button confirmButton = new Button("Confirmer");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        confirmButton.setOnAction(event -> {
            updateMaterialList();
            toggleFilterSidebar();
        });

        filterSidebar.getChildren().addAll(availabilityHeader, availabilitySection, categoryHeader, categorySection, confirmButton);
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

            // Create the filter and sort bar
            HBox filterSortBar = new HBox(10);
            filterSortBar.setAlignment(Pos.CENTER_LEFT);
            filterSortBar.setPadding(new Insets(10));
            filterSortBar.setStyle("-fx-padding: 10;");

            // Add filter button
            filterButton.setText("Filtrer par");
            filterButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");

            // Add search field
            searchField = new TextField();
            searchField.setPromptText("Rechercher par nom...");
            searchField.setStyle("-fx-pref-width: 200; -fx-padding: 8; -fx-border-radius: 5; -fx-background-radius: 5;");
            searchField.textProperty().addListener((obs, oldVal, newVal) -> updateMaterialList());

            // Add sort label and ComboBox
            Label sortLabel = new Label("Trier par:");
            sortLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            sortComboBox.setStyle("-fx-pref-width: 150; -fx-padding: 8; -fx-border-radius: 5; -fx-background-radius: 5;");

            // Add spacer to push total label to the right
            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Add total materials label
            totalMaterialsLabel = new Label("Total : 0 matériels");
            totalMaterialsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

            filterSortBar.getChildren().addAll(filterButton, searchField, sortLabel, sortComboBox, spacer, totalMaterialsLabel);

            // Card grid
            cardGrid = new GridPane();
            cardGrid.setHgap(20);
            cardGrid.setVgap(20);
            cardGrid.setPadding(new Insets(20));
            cardGrid.setAlignment(Pos.CENTER);

            // Populate grid
            updateMaterialList();

            // ScrollPane for the card grid
            scrollPane = new ScrollPane(cardGrid);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: #FFFFFF; -fx-border-color: transparent;");

            // Add filterSortBar and scrollPane to contentPane
            contentPane.getChildren().addAll(filterSortBar, scrollPane);

            // Ensure filter sidebar is hidden initially
            if (isFilterSidebarVisible) toggleFilterSidebar();
            filterSidebar.setVisible(false);
        } catch (SQLException e) {
            allMaterials = new ArrayList<>();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible de récupérer les matériaux: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }

        if (isSidebarVisible) toggleSidebar();
    }

    private void updateMaterialList() {
        if (allMaterials == null) {
            allMaterials = new ArrayList<>();
        }
        // Filter materials
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
            boolean availableSelected = availableCheckBox.isSelected();
            boolean outOfStockSelected = outOfStockCheckBox.isSelected();
            if (availableSelected || outOfStockSelected) {
                filteredMaterials = filteredMaterials.stream()
                        .filter(m -> (availableSelected && m.isDisponibility()) || (outOfStockSelected && !m.isDisponibility()))
                        .collect(Collectors.toList());
            }
        }

        // Apply category filter
        if (categoryCheckBoxes != null) {
            List<String> selectedCategories = categoryCheckBoxes.entrySet().stream()
                    .filter(e -> e.getValue() != null && e.getValue().isSelected())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (!selectedCategories.isEmpty()) {
                filteredMaterials = filteredMaterials.stream()
                        .filter(m -> selectedCategories.contains(m.getCategorie()))
                        .collect(Collectors.toList());
            }
        }

        // Sort materials
        String sortOption = sortComboBox.getValue() != null ? sortComboBox.getValue() : "Nom A à Z";
        filteredMaterials.sort((m1, m2) -> {
            switch (sortOption) {
                case "Nom A à Z":
                    return m1.getName().compareToIgnoreCase(m2.getName());
                case "Nom Z à A":
                    return m2.getName().compareToIgnoreCase(m1.getName());
                case "Prix croissants":
                    return Double.compare(m1.getPrice(), m2.getPrice());
                case "Prix décroissants":
                    return Double.compare(m2.getPrice(), m1.getPrice());
                default:
                    return 0;
            }
        });

        // Update total materials label
        totalMaterialsLabel.setText("Total : " + filteredMaterials.size() + " matériels");

        updateCardGrid(cardGrid, filteredMaterials);
    }

    private void updateCardGrid(GridPane cardGrid, List<Materiels> materials) {
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

            HBox buttonBar = new HBox(5);
            buttonBar.setAlignment(Pos.CENTER);

            Button editButton = new Button("Modifier");
            editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
            editButton.setOnAction(event -> showEditMaterialForm(material));

            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
            deleteButton.setOnAction(event -> deleteMaterial(material));

            buttonBar.getChildren().addAll(editButton, deleteButton);

            card.getChildren().addAll(nameLabel, imageView, buttonBar);

            card.setOnMouseClicked(event -> showMaterialDetails(material));

            cardGrid.add(card, column, row);
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }

    private void deleteMaterial(Materiels material) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer le matériel");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer le matériel \"" + material.getName() + "\" ?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Materielservices materielServices = new Materielservices();
                materielServices.supprimer(material.getId());

                // Update the local list
                allMaterials.removeIf(m -> m.getId() == material.getId());

                // Refresh the material list
                updateMaterialList();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("Matériel supprimé");
                successAlert.setContentText("Le matériel \"" + material.getName() + "\" a été supprimé avec succès.");
                successAlert.showAndWait();
            } catch (SQLException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Erreur de base de données");
                errorAlert.setContentText("Impossible de supprimer le matériel: " + e.getMessage());
                errorAlert.showAndWait();
                e.printStackTrace();
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

        detailsPane.getChildren().addAll(topBar, largeImage, nameLabel, descriptionLabel, categoryLabel,
                priceLabel, dateAjoutLabel, disponibilityLabel, quantiteLabel);

        scrollPane.setContent(detailsPane);
        contentPane.getChildren().add(scrollPane);

        contentPane.setAlignment(Pos.CENTER);
    }

    private void showAddMaterialForm() {
        contentPane.getChildren().clear();
        pageTitle.setText("Ajouter un Matériel Agricole");
        contentPane.getChildren().add(addMaterialForm);

        if (isSidebarVisible) toggleSidebar();
        if (isFilterSidebarVisible) toggleFilterSidebar();

        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );
        }
    }

    private void showEditMaterialForm(Materiels material) {
        if (material == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun matériel sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un matériel à modifier.");
            alert.showAndWait();
            return;
        }

        selectedMaterial = material;
        contentPane.getChildren().clear();
        pageTitle.setText("Modifier un Matériel Agricole");

        GridPane editMaterialForm = new GridPane();
        editMaterialForm.setHgap(10);
        editMaterialForm.setVgap(10);
        editMaterialForm.setStyle("-fx-background-color: #F9F9F9; -fx-padding: 20; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label idLabel = new Label("ID:");
        idLabel.setStyle("-fx-font-weight: bold;");
        TextField editIdField = new TextField(String.valueOf(material.getId()));
        editIdField.setEditable(false);
        editMaterialForm.add(idLabel, 0, 0);
        editMaterialForm.add(editIdField, 1, 0);

        Label nameLabel = new Label("Nom:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        TextField editNameField = new TextField(material.getName());
        editMaterialForm.add(nameLabel, 0, 1);
        editMaterialForm.add(editNameField, 1, 1);

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setStyle("-fx-font-weight: bold;");
        TextArea editDescriptionField = new TextArea(material.getDescription());
        editDescriptionField.setPrefHeight(100);
        editMaterialForm.add(descriptionLabel, 0, 2);
        editMaterialForm.add(editDescriptionField, 1, 2);

        Button editGenerateDescriptionButton = new Button("Generate");
        editGenerateDescriptionButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-cursor: hand; -fx-border-radius: 5;");
        editGenerateDescriptionButton.setOnAction(event -> generateDescription(editNameField, editDescriptionField));
        editMaterialForm.add(editGenerateDescriptionButton, 2, 2);

        Label imageUrlLabel = new Label("URL Image:");
        imageUrlLabel.setStyle("-fx-font-weight: bold;");
        TextField editImageUrlField = new TextField(material.getImageurl());
        Button editImportImageButton = new Button("Importer une photo");
        editImportImageButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-cursor: hand; -fx-border-radius: 5;");
        editImportImageButton.setOnAction(event -> importImageForEdit(editImageUrlField));
        editMaterialForm.add(imageUrlLabel, 0, 3);
        editMaterialForm.add(editImageUrlField, 1, 3);
        editMaterialForm.add(editImportImageButton, 2, 3);

        Label categoryLabel = new Label("Catégorie:");
        categoryLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> editCategoryField = new ComboBox<>();
        editCategoryField.setItems(FXCollections.observableArrayList("Machinerie", "Outils", "Semences", "Engrais", "Irrigation"));
        editCategoryField.setValue(material.getCategorie());
        editMaterialForm.add(categoryLabel, 0, 4);
        editMaterialForm.add(editCategoryField, 1, 4);

        Label priceLabel = new Label("Prix:");
        priceLabel.setStyle("-fx-font-weight: bold;");
        TextField editPriceField = new TextField(String.format("%.2f", material.getPrice()));
        editMaterialForm.add(priceLabel, 0, 5);
        editMaterialForm.add(editPriceField, 1, 5);

        Label dateAjoutLabel = new Label("Date Ajout:");
        dateAjoutLabel.setStyle("-fx-font-weight: bold;");
        DatePicker editDateAjoutField = new DatePicker();
        try {
            editDateAjoutField.setValue(LocalDate.parse(material.getDateajout()));
        } catch (Exception e) {
            editDateAjoutField.setValue(LocalDate.now());
            System.err.println("Invalid date format for dateAjout: " + material.getDateajout());
        }
        editMaterialForm.add(dateAjoutLabel, 0, 6);
        editMaterialForm.add(editDateAjoutField, 1, 6);

        Label quantiteLabel = new Label("Quantité:");
        quantiteLabel.setStyle("-fx-font-weight: bold;");
        Spinner<Integer> editQuantiteField = new Spinner<>();
        SpinnerValueFactory<Integer> editQuantiteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, material.getQuantite());
        editQuantiteField.setValueFactory(editQuantiteFactory);
        editMaterialForm.add(quantiteLabel, 0, 7);
        editMaterialForm.add(editQuantiteField, 1, 7);

        Label disponibilityLabel = new Label("Disponibilité:");
        disponibilityLabel.setStyle("-fx-font-weight: bold;");
        CheckBox editDisponibilityField = new CheckBox();
        editDisponibilityField.setSelected(material.isDisponibility());
        editDisponibilityField.setDisable(material.getQuantite() == 0);
        editQuantiteField.valueProperty().addListener((obs, oldValue, newValue) -> {
            editDisponibilityField.setDisable(newValue == 0);
            if (newValue == 0) {
                editDisponibilityField.setSelected(false);
            }
        });
        editMaterialForm.add(disponibilityLabel, 0, 8);
        editMaterialForm.add(editDisponibilityField, 1, 8);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.BOTTOM_RIGHT);
        Button updateButton = new Button("Mettre à jour");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-cursor: hand; -fx-border-radius: 5;");
        updateButton.setOnAction(event -> updateMaterial(
                editIdField, editNameField, editDescriptionField, editImageUrlField,
                editCategoryField, editPriceField, editDateAjoutField, editDisponibilityField, editQuantiteField
        ));
        Button cancelUpdateButton = new Button("Annuler");
        cancelUpdateButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-cursor: hand; -fx-border-radius: 5;");
        cancelUpdateButton.setOnAction(event -> showMaterialList());
        buttonBar.getChildren().addAll(updateButton, cancelUpdateButton);
        editMaterialForm.add(buttonBar, 1, 9, 2, 1);

        contentPane.getChildren().add(editMaterialForm);

        if (isSidebarVisible) toggleSidebar();
        if (isFilterSidebarVisible) toggleFilterSidebar();
    }

    private void importImageForEdit(TextField imageUrlField) {
        try {
            if (rootPane == null || rootPane.getScene() == null || rootPane.getScene().getWindow() == null) {
                throw new IllegalStateException("La fenêtre n'est pas disponible.");
            }

            if (fileChooser == null) {
                fileChooser = new FileChooser();
                fileChooser.setTitle("Sélectionner une image");
            }

            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

            if (selectedFile != null) {
                String imageUrl = selectedFile.toURI().toString();
                imageUrlField.setText(imageUrl);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Image importée");
                alert.setHeaderText(null);
                alert.setContentText("L'image a été importée avec succès!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur lors de l'importation");
            alert.setHeaderText("Impossible d'importer l'image");
            alert.setContentText("Erreur : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void updateMaterial(TextField idField, TextField nameField, TextArea descriptionField,
                                TextField imageUrlField, ComboBox<String> categoryField,
                                TextField priceField, DatePicker dateAjoutField, CheckBox disponibilityField,
                                Spinner<Integer> quantiteField) {
        // Validate mandatory fields
        List<String> missingFields = new ArrayList<>();
        if (idField.getText() == null || idField.getText().trim().isEmpty()) missingFields.add("ID");
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) missingFields.add("Nom");
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) missingFields.add("Description");
        if (imageUrlField.getText() == null || imageUrlField.getText().trim().isEmpty()) missingFields.add("URL Image");
        if (categoryField.getValue() == null) missingFields.add("Catégorie");
        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) missingFields.add("Prix");
        if (dateAjoutField.getValue() == null) missingFields.add("Date d'ajout");

        if (!missingFields.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champs obligatoires manquants");
            alert.setContentText("Veuillez remplir tous les champs obligatoires : " + String.join(", ", missingFields));
            alert.showAndWait();
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String imageUrl = imageUrlField.getText().trim();
            String category = categoryField.getValue();
            double price = Double.parseDouble(priceField.getText().trim());
            String dateAjout = dateAjoutField.getValue().toString();
            int quantite = quantiteField.getValue();
            boolean disponibility = quantite > 0 && disponibilityField.isSelected();

            System.out.println("Updating material: ID=" + id + ", Name=" + name + ", Description=" + description +
                    ", ImageURL=" + imageUrl + ", Category=" + category + ", Price=" + price +
                    ", DateAjout=" + dateAjout + ", Disponibility=" + disponibility + ", Quantite=" + quantite);

            Materiels updatedMaterial = new Materiels(id, name, description, imageUrl, category, price, dateAjout, disponibility, quantite);
            Materielservices materielServices = new Materielservices();
            materielServices.modifier(updatedMaterial);

            // Update the local list
            allMaterials.removeIf(m -> m.getId() == id);
            allMaterials.add(updatedMaterial);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Matériel modifié");
            alert.setContentText("Le matériel a été mis à jour avec succès.");
            alert.setOnCloseRequest(event -> showMaterialList());
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Entrée invalide");
            alert.setContentText("Veuillez vérifier que l'ID et le prix sont des nombres valides.");
            alert.showAndWait();
            e.printStackTrace();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible de modifier le matériel : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur inattendue");
            alert.setContentText("Une erreur s'est produite lors de la modification : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void importImage() {
        try {
            if (rootPane == null || rootPane.getScene() == null || rootPane.getScene().getWindow() == null) {
                throw new IllegalStateException("La fenêtre n'est pas disponible. Assurez-vous que le contrôleur est correctement initialisé.");
            }

            if (fileChooser == null) {
                fileChooser = new FileChooser();
                fileChooser.setTitle("Sélectionner une image");
            }

            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

            if (selectedFile != null) {
                String imageUrl = selectedFile.toURI().toString();
                imageUrlField.setText(imageUrl);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Image importée");
                alert.setHeaderText(null);
                alert.setContentText("L'image a été importée avec succès!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucune image sélectionnée");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner une image.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur lors de l'importation");
            alert.setHeaderText("Impossible d'importer l'image");
            alert.setContentText("Erreur : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void addMaterial() {
        // Validate mandatory fields
        List<String> missingFields = new ArrayList<>();
        if (idField.getText() == null || idField.getText().trim().isEmpty()) missingFields.add("ID");
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) missingFields.add("Nom");
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) missingFields.add("Description");
        if (imageUrlField.getText() == null || imageUrlField.getText().trim().isEmpty()) missingFields.add("URL Image");
        if (categoryField.getValue() == null) missingFields.add("Catégorie");
        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) missingFields.add("Prix");
        if (dateAjoutField.getValue() == null) missingFields.add("Date d'ajout");

        if (!missingFields.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Champs obligatoires manquants");
            alert.setContentText("Veuillez remplir tous les champs obligatoires : " + String.join(", ", missingFields));
            alert.showAndWait();
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String imageUrl = imageUrlField.getText().trim();
            String category = categoryField.getValue();
            double price = Double.parseDouble(priceField.getText().trim());
            String dateAjout = dateAjoutField.getValue().toString();
            int quantite = quantiteField.getValue();
            boolean disponibility = quantite > 0 && disponibilityField.isSelected();

            System.out.println("Adding material: ID=" + id + ", Name=" + name + ", Description=" + description +
                    ", ImageURL=" + imageUrl + ", Category=" + category + ", Price=" + price +
                    ", DateAjout=" + dateAjout + ", Disponibility=" + disponibility + ", Quantite=" + quantite);

            Materiels materiel = new Materiels(id, name, description, imageUrl, category, price, dateAjout, disponibility, quantite);
            Materielservices materielServices = new Materielservices();
            materielServices.ajouter(materiel);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Matériel ajouté");
            alert.setContentText("Le matériel a été ajouté à la base de données.");
            alert.showAndWait();

            idField.clear();
            nameField.clear();
            descriptionField.clear();
            imageUrlField.clear();
            categoryField.setValue(null);
            priceField.clear();
            dateAjoutField.setValue(null);
            disponibilityField.setSelected(false);
            quantiteField.getValueFactory().setValue(0);

            showMaterialList();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Entrée invalide");
            alert.setContentText("Veuillez vérifier que l'ID et le prix sont des nombres valides.");
            alert.showAndWait();
            e.printStackTrace();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible d'ajouter le matériel : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur inattendue");
            alert.setContentText("Une erreur s'est produite lors de l'ajout : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelAdd() {
        idField.clear();
        nameField.clear();
        descriptionField.clear();
        imageUrlField.clear();
        categoryField.setValue(null);
        priceField.clear();
        dateAjoutField.setValue(null);
        disponibilityField.setSelected(false);
        quantiteField.getValueFactory().setValue(0);

        showMaterialList();
    }

    @FXML
    private void generateDescription() {
        generateDescription(nameField, descriptionField);
    }

    private void generateDescription(TextField nameField, TextArea descriptionField) {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer le nom du matériel avant de générer une description.");
            alert.showAndWait();
            return;
        }

        String prompt = String.format(
                "Générer une description technique et concise en français pour un matériel agricole nommé '%s'. Incluez : capacité de semis (en litres) si applicable, largeur de travail (en mètres) si applicable, profondeur de semis (en cm) si applicable, puissance moteur requise (en ch) si applicable, options de précision (e.g., GPS, dosage variable) si applicable, et caractéristiques de confort (e.g., climatisation, ergonomie) si applicable. Utilisez une terminologie agricole précise. Ne répétez pas cette instruction dans la réponse.",
                name
        );

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                // Validate API token with a test request
                Request testRequest = new Request.Builder()
                        .url("https://api-inference.huggingface.co/status")
                        .header("Authorization", "Bearer " + API_TOKEN)
                        .get()
                        .build();
                try (Response testResponse = client.newCall(testRequest).execute()) {
                    System.out.println("API Token Test - HTTP Status: " + testResponse.code());
                    if (testResponse.code() == 401) {
                        return "Erreur: Jeton API invalide ou non autorisé. Veuillez vérifier le jeton dans config.properties.";
                    }
                } catch (IOException e) {
                    System.out.println("API Token Test Failed: " + e.getMessage());
                    return getFallbackDescription(name);
                }

                int maxRetries = 3;
                for (int attempt = 1; attempt <= maxRetries; attempt++) {
                    System.out.println("API Call Attempt: " + attempt + " for " + API_URL);
                    String jsonBody = String.format(
                            "{\"inputs\": \"%s\", \"parameters\": {\"max_new_tokens\": 200, \"temperature\": 0.7}}",
                            prompt
                    );
                    RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(API_URL)
                            .header("Authorization", "Bearer " + API_TOKEN)
                            .post(body)
                            .build();
                    try (Response response = client.newCall(request).execute()) {
                        System.out.println("HTTP Status: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            String responseBody = response.body().string();
                            System.out.println("Raw API Response: " + responseBody);
                            JsonNode jsonNode = mapper.readTree(responseBody);
                            String generatedText;
                            if (jsonNode.isArray() && jsonNode.size() > 0 && jsonNode.get(0).has("generated_text")) {
                                generatedText = jsonNode.get(0).get("generated_text").asText();
                            } else if (jsonNode.has("generated_text")) {
                                generatedText = jsonNode.get("generated_text").asText();
                            } else if (jsonNode.isTextual()) {
                                generatedText = jsonNode.asText();
                            } else {
                                System.out.println("Error: Unexpected response structure");
                                return getFallbackDescription(name);
                            }
                            System.out.println("Generated Text Before Stripping: " + generatedText);
                            // Remove the prompt if present
                            if (generatedText.contains(prompt)) {
                                generatedText = generatedText.replace(prompt, "").trim();
                            }
                            System.out.println("Generated Text After Stripping: " + generatedText);
                            // Trim and validate
                            generatedText = generatedText.trim();
                            if (generatedText.isEmpty()) {
                                System.out.println("Error: Generated text is empty after processing");
                                return getFallbackDescription(name);
                            }
                            return generatedText;
                        } else if (response.code() == 404) {
                            String errorMessage = response.body() != null ? response.body().string() : "Modèle non trouvé.";
                            System.out.println("Error: HTTP 404 - " + errorMessage + " for " + API_URL);
                            return "Erreur: Modèle non disponible sur l'API. Veuillez vérifier l'accès au modèle facebook/bart-base.";
                        } else if (response.code() == 503 && attempt < maxRetries) {
                            System.out.println("503 Error: Model loading, retrying after 10 seconds...");
                            Thread.sleep(10000); // Wait 10 seconds
                            continue;
                        } else {
                            String errorMessage = response.body() != null ? response.body().string() : response.message();
                            System.out.println("Error: HTTP " + response.code() + " - " + errorMessage);
                            return "Erreur: " + response.code() + " - " + errorMessage;
                        }
                    } catch (IOException e) {
                        System.out.println("IOException on attempt " + attempt + ": " + e.getMessage());
                        if (attempt == maxRetries) {
                            return getFallbackDescription(name);
                        }
                        Thread.sleep(5000); // Wait 5 seconds before retrying
                    }
                }
                return getFallbackDescription(name);
            }
        };

        task.setOnSucceeded(e -> {
            String result = task.getValue();
            if (result.startsWith("Erreur")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de la génération");
                alert.setContentText(result);
                alert.showAndWait();
                descriptionField.setText(getFallbackDescription(name));
            } else {
                descriptionField.setText(result);
            }
        });
        task.setOnFailed(e -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la génération");
            alert.setContentText("Erreur: Impossible de générer la description. Utilisation de la description par défaut.");
            alert.showAndWait();
            descriptionField.setText(getFallbackDescription(name));
        });
        new Thread(task).start();
    }

    private String getFallbackDescription(String name) {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("semoir", "Le %s est un semoir agricole avec une trémie de %d L, une largeur de travail de %.1f m, une profondeur de semis de %d à %d cm, et requiert %d ch. Il est équipé d'un guidage GPS pour une précision optimale et d'une cabine climatisée avec siège ergonomique.");
        descriptions.put("tracteur", "Le %s est un tracteur robuste de %d ch, doté d'un système GPS pour une navigation précise. Sa cabine climatisée offre un siège ergonomique et un tableau de bord numérique pour un confort maximal.");
        descriptions.put("moissonneuse", "Le %s est une moissonneuse performante avec une largeur de coupe de %.1f m et une trémie de %d L. Elle requiert %d ch et inclut des capteurs de rendement. La cabine climatisée assure un confort ergonomique.");
        descriptions.put("charrue", "Le %s est une charrue efficace avec une largeur de travail de %.1f m, adaptée pour un labour de %d à %d cm de profondeur. Elle requiert %d ch et est conçue pour une productivité optimale.");
        descriptions.put("pulverisateur", "Le %s est un pulvérisateur moderne avec une cuve de %d L et une rampe de %.1f m. Il inclut un système de dosage variable et requiert %d ch. La cabine offre un confort ergonomique.");

        // Add more categories for variety
        descriptions.put("herse", "Le %s est une herse agricole avec une largeur de travail de %.1f m, idéale pour préparer le sol avant semis. Elle requiert %d ch et est conçue pour une durabilité accrue.");
        descriptions.put("faucheuse", "Le %s est une faucheuse avec une largeur de coupe de %.1f m, parfaite pour les prairies. Elle requiert %d ch et offre une coupe nette et précise.");

        String key = "default";
        for (String k : descriptions.keySet()) {
            if (name.toLowerCase().contains(k)) {
                key = k;
                break;
            }
        }

        Random rand = new Random();
        if (key.equals("default")) {
            return String.format(
                    "Le %s est un matériel agricole polyvalent, conçu pour optimiser les tâches agricoles avec une capacité adaptée, une précision accrue grâce à des technologies modernes, et un confort optimal pour l'opérateur.",
                    name
            );
        } else if (key.equals("semoir")) {
            return String.format(
                    descriptions.get(key),
                    name, 2000 + rand.nextInt(1500), 3.5 + rand.nextDouble() * 3, 1 + rand.nextInt(3), 5 + rand.nextInt(5), 90 + rand.nextInt(60)
            );
        } else if (key.equals("tracteur")) {
            return String.format(
                    descriptions.get(key),
                    name, 100 + rand.nextInt(100)
            );
        } else if (key.equals("moissonneuse")) {
            return String.format(
                    descriptions.get(key),
                    name, 4.5 + rand.nextDouble() * 3, 6000 + rand.nextInt(4000), 200 + rand.nextInt(150)
            );
        } else if (key.equals("charrue")) {
            return String.format(
                    descriptions.get(key),
                    name, 1.5 + rand.nextDouble() * 2.5, 15 + rand.nextInt(15), 25 + rand.nextInt(15), 70 + rand.nextInt(50)
            );
        } else if (key.equals("pulverisateur")) {
            return String.format(
                    descriptions.get(key),
                    name, 1000 + rand.nextInt(1500), 8.0 + rand.nextDouble() * 7, 60 + rand.nextInt(40)
            );
        } else if (key.equals("herse")) {
            return String.format(
                    descriptions.get(key),
                    name, 2.0 + rand.nextDouble() * 3, 50 + rand.nextInt(50)
            );
        } else if (key.equals("faucheuse")) {
            return String.format(
                    descriptions.get(key),
                    name, 1.8 + rand.nextDouble() * 2, 40 + rand.nextInt(40)
            );
        }
        return String.format(
                "Le %s est un matériel agricole conçu pour des performances optimales dans diverses tâches agricoles.",
                name
        );
    }
}