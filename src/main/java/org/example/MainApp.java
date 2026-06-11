package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controllers.LayoutController;
import org.example.services.MailService;
import org.example.services.AuthService;
import org.example.utils.MyDataBase;
import org.example.utils.QRLoginHttpServer; // ✅ Make sure this exists

public class MainApp extends Application {
    public static AuthService AUTH_SERVICE;
    public static LayoutController LAYOUT_CONTROLLER;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String gmailUser = "rlahbaieb@gmail.com";
        String gmailAppPass = "tmar ujpi dmcl aepv";

        MailService mailService = new MailService(gmailUser, gmailAppPass);
        AUTH_SERVICE = new AuthService(MyDataBase.getInstance().getConnection(), mailService);

        // ✅ Start embedded HTTP server to listen for QR login
        new Thread(() -> QRLoginHttpServer.start(AUTH_SERVICE)).start();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Connexion");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
