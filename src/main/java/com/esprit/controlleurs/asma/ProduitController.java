package com.esprit.controlleurs.asma;

import com.esprit.entities.samar.Produit;
import com.esprit.services.asma.PanierService;
import com.esprit.services.samar.ServicesProduit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ProduitController {
    @FXML
    private ListView<Produit> produitList;

    @FXML
    private Spinner<Integer> quantiteSpinner;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button voirPanierButton;

    @FXML
    private Button annulerButton;

    private ServicesProduit produitService = new ServicesProduit();
    private PanierService panierService = new PanierService();

    public void initialize() {
        System.out.println("Initialisation de ProduitController");
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.getAllProduits());
        produitList.setItems(produits);

        quantiteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        ajouterButton.setOnAction(e -> {
            Produit selected = produitList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int qte = quantiteSpinner.getValue();
                panierService.ajouterProduit(selected, qte);
                System.out.println("Ajouté au panier : " + selected.getNom() + " x" + qte);
            } else {
                new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un produit.").show();
            }
        });

        voirPanierButton.setOnAction(e -> {
            System.out.println("Bouton Voir Panier cliqué");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo4/Controller/panier.fxml"));
                if (loader.getLocation() == null) {
                    throw new IOException("Fichier panier.fxml introuvable");
                }
                Scene scene = new Scene(loader.load());

                PanierController panierController = loader.getController();
                panierController.setPanierService(panierService);

                Stage stage = new Stage();
                stage.setTitle("Mon Panier");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement du panier : " + ex.getMessage()).show();
            }
        });

        annulerButton.setOnAction(e -> {
            System.out.println("Bouton Annuler cliqué");
            produitList.getSelectionModel().clearSelection();
            quantiteSpinner.getValueFactory().setValue(1);
            new Alert(Alert.AlertType.INFORMATION, "Sélection annulée et quantité réinitialisée").show();
        });
    }

    public PanierService getPanierService() {
        return panierService;
    }
}
