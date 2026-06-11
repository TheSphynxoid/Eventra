package org.example.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.example.MainApp;
import org.example.entities.User;
import org.example.services.UserServices;

import java.util.List;
import java.util.stream.Collectors;

public class UserListController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idCol;
    @FXML private TableColumn<User, String> nomCol;
    @FXML private TableColumn<User, String> prenomCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> telephoneCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TableColumn<User, String> statutCol;

    @FXML private TextField searchField;

    private final UserServices userServices = new UserServices();
    private ObservableList<User> allUsers;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        nomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        prenomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrenom()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        telephoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroTelephone()));
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRole() != null ? c.getValue().getRole().getName() : "")
        );

        statutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean actif = getTableRow().getItem().isActive();
                    setText(actif ? "Actif" : "Inactif");
                    setStyle("-fx-text-fill: " + (actif ? "green" : "red") + "; -fx-font-weight: bold;");
                }
            }
        });

        searchField.setOnKeyReleased(this::filtrerUtilisateurs);
        afficherUtilisateurs();
    }

    private void afficherUtilisateurs() {
        try {
            List<User> users = userServices.getAll();
            allUsers = FXCollections.observableArrayList(users);
            userTable.setItems(allUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrerUtilisateurs(KeyEvent event) {
        String keyword = searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            userTable.setItems(allUsers);
        } else {
            ObservableList<User> filtered = allUsers.stream()
                    .filter(user -> user.getNom().toLowerCase().contains(keyword)
                            || user.getPrenom().toLowerCase().contains(keyword)
                            || user.getEmail().toLowerCase().contains(keyword)
                            || user.getNumeroTelephone().toLowerCase().contains(keyword))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            userTable.setItems(filtered);
        }
    }

    @FXML
    private void goToAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Node form = loader.load();
            MainApp.LAYOUT_CONTROLLER.loadContentNode(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierUtilisateur() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un utilisateur à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Node form = loader.load();
            UserFormController ctrl = loader.getController();
            ctrl.setUserToEdit(selected);
            MainApp.LAYOUT_CONTROLLER.loadContentNode(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerUtilisateur() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer cet utilisateur ?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userServices.supprimer(selected.getId());
                    allUsers.remove(selected);
                    userTable.setItems(FXCollections.observableArrayList(allUsers));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
