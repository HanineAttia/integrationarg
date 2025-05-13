package com.esprit.controlleurs.asma;


import com.esprit.entities.asma.Commande;
import com.esprit.services.asma.CommandeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

public class CommandeController {
    @FXML
    private ListView<String> commandesList;

    @FXML
    private Label totalCommandesLabel;

    public void initialize() {
        try {
            CommandeService service = new CommandeService();
            ObservableList<String> data = FXCollections.observableArrayList();
            double totalGlobal = 0;

            for (Commande c : service.getCommandesByUser(1)) {
                data.add("Commande #" + c.getIdCommande() + " - Total : " + c.getTotal() + " DT - Le " + c.getDateCommande().toLocalDate());
                totalGlobal += c.getTotal();
            }

            commandesList.setItems(data);
            totalCommandesLabel.setText("Total général de vos commandes : " + totalGlobal + " DT");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des commandes : " + e.getMessage()).show();
        }
    }
}
