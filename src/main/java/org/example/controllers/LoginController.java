package org.example.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.MainApp;
import org.example.entities.User;
import org.example.services.AuthService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = MainApp.AUTH_SERVICE;

    @FXML
    private void handleLogin() {
        try {
            User u = authService
                    .login(emailField.getText(), passwordField.getText())
                    .orElseThrow(() -> new Exception("Identifiants invalides"));

            if (u.getRole() != null && "En_attente".equals(u.getRole().getName())) {
                messageLabel.setText("Votre compte est en attente d'activation.");
                return;
            }

            if (u.getRole() != null) {
                goToMainLayout(u);
            } else {
                messageLabel.setText("Connexion réussie (utilisateur non-admin)");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            messageLabel.setText("Email ou mot de passe incorrect.");
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/ForgotPassword.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Mot de passe oublié");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors de l'ouverture de la page de réinitialisation.");
        }
    }

    @FXML
    private void goToSignup() throws Exception {
        Stage stage = (Stage) emailField.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/Signup.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Inscription");
    }

    @FXML
    private void handleQRLogin() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Connexion QR");
        dialog.setHeaderText("Entrer votre adresse email pour le QR");
        dialog.setContentText("Email:");

        dialog.showAndWait().ifPresent(email -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        URL url = new URL("http://192.168.1.27:8000/generate-token.php?email=" + email);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder content = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();

                        JSONObject obj = new JSONObject(content.toString());
                        String token = obj.getString("token");
                        String qrUrl = obj.getString("url");

                        javafx.application.Platform.runLater(() -> showQRPopup(qrUrl));

                        while (true) {
                            Thread.sleep(2000);
                            URL check = new URL("http://192.168.1.27:8000/check-token.php?token=" + token);
                            HttpURLConnection c = (HttpURLConnection) check.openConnection();
                            c.setRequestMethod("GET");

                            if (c.getResponseCode() == 200) {
                                Optional<User> optionalUser = authService.findByEmail(email);
                                if (optionalUser.isPresent()) {
                                    User user = optionalUser.get();

                                    javafx.application.Platform.runLater(() -> {
                                        if (!user.isActive()) {
                                            showAlert("Compte inactif", "Votre compte est en attente d'activation.");
                                            return;
                                        }

                                        showAlert("Succès", "QR Code vérifié. Connexion réussie !");
                                        if (user.getRole() != null && "admin".equals(user.getRole().getName())) {
                                            goToMainLayout(user);
                                        } else {
                                            messageLabel.setText("Connexion réussie (utilisateur non-admin)");
                                            // goToUserHome(user); // Optional
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        javafx.application.Platform.runLater(() ->
                                showAlert("Erreur", "Connexion QR échouée."));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });
    }

    private void showQRPopup(String url) {
        ImageView qrView = new ImageView("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + url);
        VBox box = new VBox(10, new Label("Scannez ce QR avec votre téléphone"), qrView);
        box.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Connexion QR");
        popup.setScene(new Scene(box));
        popup.show();
    }

    private void goToMainLayout(User user) {
        try {
            System.out.println("🔐 Connexion utilisateur : " + user.getPrenom() + " " + user.getNom());
            System.out.println("🔐 Rôle : " + (user.getRole() != null ? user.getRole().getName() : "Aucun"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BackofficeLayout.fxml"));
            Parent root = loader.load();

            if(user.getRole().getName().equals("Client")) {
                System.out.println(user.getPrenom() + " " + user.getNom());
                LayoutController layoutCtrl = loader.getController();
                layoutCtrl.setCurrentUser(user);
                layoutCtrl.setClientMode();
                MainApp.LAYOUT_CONTROLLER = layoutCtrl;
            }else{
                LayoutController layoutCtrl = loader.getController();
                layoutCtrl.setCurrentUser(user);
                MainApp.LAYOUT_CONTROLLER = layoutCtrl;
            }


            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Eventra");
            stage.show();
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du chargement de BackofficeLayout.fxml");
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isEmailValidInDatabase(String email) {
        try {
            return MainApp.AUTH_SERVICE.findByEmail(email).isPresent();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Optional if you plan to use later
    private void goToUserHome(User user) {
        showAlert("Bienvenue", "Fonction utilisateur en cours de développement.");
    }
}
