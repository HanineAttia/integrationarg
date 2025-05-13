package com.esprit.controlleurs.hanine;

import com.esprit.entities.hanine.Materiels;
import com.esprit.services.hanine.CartService;
import com.esprit.services.hanine.Materielservices;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ClientMaterielController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private Label pageTitle;
    @FXML private VBox contentPane;
    @FXML private GridPane cardGrid;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label totalMaterialsLabel;
    @FXML private Button viewCartButton;
    @FXML private VBox filterSidebar;
    @FXML private Button filterButton;

    private List<Materiels> allMaterials;
    private CartService cartService;
    private boolean isFilterSidebarVisible = false;
    private CheckBox availableCheckBox;
    private CheckBox outOfStockCheckBox;
    private Map<String, CheckBox> categoryCheckBoxes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cartService = new CartService();
        sortComboBox.setItems(FXCollections.observableArrayList("Nom A à Z", "Nom Z à A", "Prix croissants", "Prix décroissants"));
        sortComboBox.setValue("Nom A à Z");
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateMaterialList());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateMaterialList());
        viewCartButton.setOnAction(event -> showCart());
        filterButton.setOnAction(event -> toggleFilterSidebar());
        setupFilterSidebar();
        showMaterialList();
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

    private void showMaterialList() {
        contentPane.getChildren().clear();
        pageTitle.setText("Catalogue des Matériels Agricoles");
        contentPane.getChildren().addAll(createFilterSortBar(), scrollPane);

        try {
            Materielservices materielServices = new Materielservices();
            allMaterials = materielServices.getAll();
            if (allMaterials == null) {
                allMaterials = new ArrayList<>();
                showAlert(Alert.AlertType.WARNING, "Aucun Matériel", "Aucun matériel trouvé dans la base de données.");
            }
            updateMaterialList();
        } catch (SQLException e) {
            allMaterials = new ArrayList<>();
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", "Impossible de récupérer les matériaux: " + e.getMessage());
            e.printStackTrace();
        }
        if (isFilterSidebarVisible) toggleFilterSidebar();
        filterSidebar.setVisible(false);
    }

    private HBox createFilterSortBar() {
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
        sortComboBox.setStyle("-fx-pref-width: 150; -fx-padding: 8; -fx-border-radius: 5; -fx-background-radius: 5;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        totalMaterialsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        filterSortBar.getChildren().addAll(filterButton, searchField, sortLabel, sortComboBox, spacer, totalMaterialsLabel);
        return filterSortBar;
    }

    private void updateMaterialList() {
        if (allMaterials == null) {
            allMaterials = new ArrayList<>();
        }
        List<Materiels> filteredMaterials = new ArrayList<>(allMaterials);

        String searchText = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        if (!searchText.isEmpty()) {
            filteredMaterials = filteredMaterials.stream()
                    .filter(m -> m.getName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
        }

        if (availableCheckBox != null && outOfStockCheckBox != null) {
            boolean availableSelected = availableCheckBox.isSelected();
            boolean outOfStockSelected = outOfStockCheckBox.isSelected();
            if (availableSelected || outOfStockSelected) {
                filteredMaterials = filteredMaterials.stream()
                        .filter(m -> (availableSelected && m.isDisponibility()) || (outOfStockSelected && !m.isDisponibility()))
                        .collect(Collectors.toList());
            }
        }

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

        totalMaterialsLabel.setText("Total : " + filteredMaterials.size() + " matériels");
        updateCardGrid(filteredMaterials);
    }

    private void updateCardGrid(List<Materiels> materials) {
        cardGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        int maxColumns = 3;

        for (Materiels material : materials) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
            card.setPrefWidth(200);
            card.setAlignment(Pos.CENTER);
            card.setCursor(javafx.scene.Cursor.HAND);

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

            Label nameLabel = new Label(material.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(180);

            Label priceLabel = new Label(String.format("%.2f TND", material.getPrice()));
            priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

            Label statusLabel = new Label(material.isDisponibility() ? "Disponible" : "Non disponible");
            statusLabel.setStyle(material.isDisponibility() ?
                    "-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 2 8; -fx-border-radius: 10;" :
                    "-fx-background-color: #F44336; -fx-text-fill: #FFFFFF; -fx-padding: 2 8; -fx-border-radius: 10;");

            HBox buttonBar = new HBox(5);
            buttonBar.setAlignment(Pos.CENTER);

            Button detailsButton = new Button("Voir détails");
            detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
            detailsButton.setOnAction(event -> showMaterialDetails(material));

            Button addToCartButton = new Button("Ajouter au panier");
            addToCartButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
            addToCartButton.setOnAction(event -> {
                addToCart(material);
                showAddToCartPopup(material, 1); // Default quantity of 1 in grid
            });
            addToCartButton.setDisable(!material.isDisponibility());

            buttonBar.getChildren().addAll(detailsButton, addToCartButton);

            card.getChildren().addAll(imageView, nameLabel, priceLabel, statusLabel, buttonBar);
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

        ScrollPane detailsScrollPane = new ScrollPane();
        detailsScrollPane.setFitToWidth(true);
        detailsScrollPane.setStyle("-fx-background-color: transparent;");

        VBox detailsPane = new VBox(15);
        detailsPane.setPadding(new Insets(20));
        detailsPane.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        detailsPane.setAlignment(Pos.CENTER);
        detailsPane.setMaxWidth(400);

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
        Label priceLabel = new Label("Prix: " + String.format("%.2f TND", material.getPrice()));
        Label dateAjoutLabel = new Label("Date d'ajout: " + material.getDateajout());
        Label disponibilityLabel = new Label("Disponibilité: " + (material.isDisponibility() ? "Oui" : "Non"));
        Label quantiteLabel = new Label("Quantité en stock: " + material.getQuantite());

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Button continueShoppingButton = new Button("Continuer mes achats");
        continueShoppingButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        continueShoppingButton.setOnAction(event -> showMaterialList());

        Button viewCartButtonDetails = new Button("Voir mon panier");
        viewCartButtonDetails.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        viewCartButtonDetails.setOnAction(event -> showCart());

        buttonBar.getChildren().addAll(continueShoppingButton, viewCartButtonDetails);

        detailsPane.getChildren().addAll(largeImage, nameLabel, descriptionLabel, categoryLabel,
                priceLabel, dateAjoutLabel, disponibilityLabel, quantiteLabel, buttonBar);

        // Add bottom section with vertical layout
        VBox bottomSection = new VBox(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10));

        DecimalFormat df = new DecimalFormat("#,###");
        Label priceDisplay = new Label(df.format((int) material.getPrice()) + " DT TTC");
        priceDisplay.setStyle("-fx-font-size: 18px; -fx-text-fill: #F44336; -fx-font-weight: bold;");

        Label availabilityDisplay = new Label("Disponibilité : " + (material.isDisponibility() ? "En stock" : "Hors stock"));
        availabilityDisplay.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50;");

        Spinner<Integer> quantitySpinner = new Spinner<>();
        int maxQuantity = material.getQuantite() > 0 ? material.getQuantite() : 1;
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxQuantity, 1);
        quantitySpinner.setValueFactory(valueFactory);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(70);

        Button addToCartButtonDetails = new Button("Ajouter au Panier");
        addToCartButtonDetails.setStyle("-fx-background-color: #B0BEC5; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        addToCartButtonDetails.setOnAction(event -> {
            int quantity = quantitySpinner.getValue();
            for (int i = 0; i < quantity; i++) {
                addToCart(material);
            }
            showAddToCartPopup(material, quantity);
        });
        addToCartButtonDetails.setDisable(!material.isDisponibility());

        bottomSection.getChildren().addAll(priceDisplay, availabilityDisplay, quantitySpinner, addToCartButtonDetails);

        detailsPane.getChildren().add(bottomSection);

        detailsScrollPane.setContent(detailsPane);
        contentPane.getChildren().add(detailsScrollPane);
        contentPane.setAlignment(Pos.CENTER);
    }

    private void addToCart(Materiels material) {
        try {
            cartService.addToCart(material);
        } catch (Exception e) {
            System.err.println("Error adding to cart: " + e.getMessage());
        }
    }

    private void showAddToCartPopup(Materiels material, int quantity) {
        try {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Confirmation");

            VBox popupLayout = new VBox(10);
            popupLayout.setPadding(new Insets(10));
            popupLayout.setStyle("-fx-background-color: #E0E0E0; -fx-padding: 10;");

            ImageView imageView = new ImageView();
            try {
                Image image = new Image(material.getImageurl(), true);
                imageView.setImage(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                try {
                    Image fallback = new Image(getClass().getResourceAsStream("/image/Drone.png"));
                    imageView.setImage(fallback);
                } catch (Exception ex) {
                    System.err.println("Fallback image /image/Drone.png not found");
                }
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
            }

            Label successMessage = new Label("Produit ajouté au panier avec succès");
            successMessage.setStyle("-fx-font-size: 14px;");

            Label nameLabel = new Label(material.getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            DecimalFormat df = new DecimalFormat("#,###");
            Label priceLabel = new Label(df.format((int) material.getPrice()) + " DT");
            priceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #F44336; -fx-font-weight: bold;");

            Label quantityLabel = new Label("Qté: " + quantity);
            quantityLabel.setStyle("-fx-font-size: 14px;");

            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-size: 12px; -fx-cursor: hand;");
            closeButton.setOnAction(e -> popupStage.close());

            HBox topBar = new HBox(10, closeButton);
            topBar.setAlignment(Pos.TOP_RIGHT);

            List<Materiels> cartItems = cartService.getCartItems();
            double totalPrice = cartItems.stream().mapToDouble(Materiels::getPrice).sum();

            Label cartItemsLabel = new Label("Il y a " + cartItems.size() + " Articles Dans Votre Panier.");
            cartItemsLabel.setStyle("-fx-font-size: 12px;");

            Label totalLabel = new Label("Total : " + df.format((int) totalPrice) + " DT TTC");
            totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #F44336;");

            HBox buttonBar = new HBox(10);
            buttonBar.setAlignment(Pos.CENTER);

            Button continueButton = new Button("Continuer");
            continueButton.setStyle("-fx-background-color: #B0BEC5; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
            continueButton.setOnAction(e -> {
                popupStage.close();
                showMaterialList();
            });

            Button orderButton = new Button("Commander");
            orderButton.setStyle("-fx-background-color: #B0BEC5; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
            orderButton.setOnAction(e -> {
                popupStage.close();
                showOrderConfirmation();
            });

            buttonBar.getChildren().addAll(continueButton, orderButton);

            popupLayout.getChildren().addAll(topBar, imageView, successMessage, nameLabel, priceLabel, quantityLabel, cartItemsLabel, totalLabel, buttonBar);

            Scene popupScene = new Scene(popupLayout, 360, 430);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error showing popup: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher la popup. Veuillez réessayer.");
        }
    }

    private void showCart() {
        contentPane.getChildren().clear();
        pageTitle.setText("Mon Panier");

        ScrollPane cartScrollPane = new ScrollPane();
        cartScrollPane.setFitToWidth(true);
        cartScrollPane.setStyle("-fx-background-color: transparent;");

        VBox cartPane = new VBox(15);
        cartPane.setPadding(new Insets(20));
        cartPane.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        cartPane.setAlignment(Pos.CENTER);
        cartPane.setMaxWidth(500);

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        backButton.setOnAction(event -> showMaterialList());
        HBox backButtonBar = new HBox(backButton);
        backButtonBar.setAlignment(Pos.CENTER_LEFT);
        backButtonBar.setPadding(new Insets(0, 0, 10, 0));

        List<Materiels> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            Label emptyLabel = new Label("Votre panier est vide.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
            cartPane.getChildren().addAll(backButtonBar, emptyLabel);
        } else {
            double totalPrice = 0;
            for (Materiels item : cartItems) {
                HBox itemBox = new HBox(10);
                itemBox.setPadding(new Insets(10));
                itemBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 5;");

                Label nameLabel = new Label(item.getName());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                Label priceLabel = new Label(String.format("%.2f TND", item.getPrice()));
                priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

                Pane spacer = new Pane();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button removeButton = new Button("Retirer");
                removeButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: #FFFFFF; -fx-padding: 5 10; -fx-border-radius: 5; -fx-cursor: hand;");
                removeButton.setOnAction(event -> {
                    cartService.removeFromCart(item);
                    showCart();
                });

                itemBox.getChildren().addAll(nameLabel, priceLabel, spacer, removeButton);
                cartPane.getChildren().add(itemBox);
                totalPrice += item.getPrice();
            }

            Label totalItemsLabel = new Label("Il y a " + cartItems.size() + " Articles Dans Votre Panier.");
            totalItemsLabel.setStyle("-fx-font-size: 12px;");

            DecimalFormat df = new DecimalFormat("#,###");
            Label totalLabel = new Label("Total : " + df.format((int) totalPrice) + " DT TTC");
            totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #F44336; -fx-padding: 10 0;");

            HBox buttonBar = new HBox(10);
            buttonBar.setAlignment(Pos.CENTER);

            Button continueButton = new Button("Continuer");
            continueButton.setStyle("-fx-background-color: #B0BEC5; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
            continueButton.setOnAction(event -> showMaterialList());

            Button orderButton = new Button("Commander");
            orderButton.setStyle("-fx-background-color: #B0BEC5; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
            orderButton.setOnAction(event -> showOrderConfirmation());

            buttonBar.getChildren().addAll(continueButton, orderButton);

            cartPane.getChildren().addAll(backButtonBar, totalItemsLabel, totalLabel, buttonBar);
        }

        cartScrollPane.setContent(cartPane);
        contentPane.getChildren().add(cartScrollPane);
        contentPane.setAlignment(Pos.CENTER);
    }

    private void showOrderConfirmation() {
        contentPane.getChildren().clear();
        pageTitle.setText("Confirmation de Commande");

        VBox confirmationPane = new VBox(20);
        confirmationPane.setPadding(new Insets(20));
        confirmationPane.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        confirmationPane.setAlignment(Pos.CENTER);
        confirmationPane.setMaxWidth(400);

        Label emptyLabel = new Label("Page de confirmation (en développement)");
        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: #FFFFFF; -fx-padding: 8 15; -fx-border-radius: 5; -fx-cursor: hand;");
        backButton.setOnAction(event -> showCart());

        confirmationPane.getChildren().addAll(emptyLabel, backButton);

        contentPane.getChildren().add(confirmationPane);
        contentPane.setAlignment(Pos.CENTER);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}