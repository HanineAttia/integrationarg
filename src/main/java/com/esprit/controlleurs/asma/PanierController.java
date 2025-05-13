package com.esprit.controlleurs.asma;

import com.esprit.entities.asma.PanierProduit;
import com.esprit.services.asma.PanierService;
import com.esprit.services.asma.StripeService;
import com.stripe.model.checkout.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.IOException;
import java.util.List;

public class PanierController {

    @FXML
    private ListView<PanierProduit> panierList;

    @FXML
    private Label totalLabel;

    private PanierService panierService;

    public void setPanierService(PanierService panierService) {
        this.panierService = panierService;
        updatePanier();
    }

    public void initialize() {
        if (panierService == null) {
            panierService = new PanierService();
        }
        updatePanier();

        panierList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PanierProduit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item.getProduit().getNom() + " x" + item.getQuantite() + " (" + item.getTotal() + " DT)");
                    Button updateBtn = new Button("Modifier");
                    Button deleteBtn = new Button("Supprimer");

                    updateBtn.setOnAction(e -> {
                        TextInputDialog dialog = new TextInputDialog(String.valueOf(item.getQuantite()));
                        dialog.setTitle("Modifier la quantité");
                        dialog.setHeaderText("Modifier la quantité de " + item.getProduit().getNom());
                        dialog.setContentText("Nouvelle quantité:");
                        dialog.showAndWait().ifPresent(val -> {
                            try {
                                int qte = Integer.parseInt(val);
                                panierService.modifierQuantite(item.getProduit(), qte);
                                updatePanier();
                            } catch (NumberFormatException ex) {
                                new Alert(Alert.AlertType.ERROR, "Quantité invalide !").show();
                            }
                        });
                    });

                    deleteBtn.setOnAction(e -> {
                        panierService.supprimerProduit(item.getProduit());
                        updatePanier();
                    });

                    HBox box = new HBox(10, label, updateBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
    }

    private void updatePanier() {
        if (panierService != null) {
            ObservableList<PanierProduit> items = FXCollections.observableArrayList(panierService.getContenuPanier());
            panierList.setItems(items);
            totalLabel.setText("Total : " + panierService.calculerTotal() + " DT");
        } else {
            System.out.println("⚠️ panierService est null !");
        }
    }

    @FXML
    private void payerCommande() {
        StripeService stripeService = new StripeService();
        List<PanierProduit> panierProduits = panierService.getContenuPanier();

        try {
            Session session = stripeService.createCheckoutSession(panierProduits);
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();
            engine.load(session.getUrl());

            Stage paymentStage = new Stage();
            paymentStage.setTitle("Paiement en ligne sécurisé");
            paymentStage.setScene(new Scene(webView, 900, 700));
            paymentStage.show();

            engine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
                if (newLoc.startsWith("http://localhost:4242/success")) {
                    paymentStage.close();
                    boolean result = panierService.payerPanier();
                    if (result) {
                        updatePanier();
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo4/commandes.fxml"));
                            Scene scene = new Scene(loader.load());
                            Stage stage = new Stage();
                            stage.setTitle("Mes commandes");
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        new Alert(Alert.AlertType.INFORMATION, "Commande validée avec succès !").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement de la commande.").show();
                    }
                }
                if (newLoc.startsWith("http://localhost:4242/cancel")) {
                    paymentStage.close();
                    new Alert(Alert.AlertType.WARNING, "Paiement annulé.").show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur Stripe : " + e.getMessage()).show();
        }
    }

    @FXML
    private void voirHistorique() {
        System.out.println("Bouton Historique cliqué");
        try {
            System.out.println("Chargement de commandes.fxml...");
            java.net.URL fxmlUrl = getClass().getResource("/com/example/demo4/Controller/commandes.fxml");
            if (fxmlUrl == null) {
                System.out.println("Erreur : commandes.fxml introuvable");
                new Alert(Alert.AlertType.ERROR, "Fichier commandes.fxml introuvable.").show();
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            System.out.println("FXMLLoader créé");
            Scene scene = new Scene(loader.load());
            System.out.println("Scene créée");
            Stage stage = new Stage();
            stage.setTitle("Historique des achats");
            stage.setScene(scene);
            stage.show();
            System.out.println("Fenêtre affichée");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'historique : " + e.getMessage()).show();
        }
    }
}