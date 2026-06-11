package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.example.MainApp;
import org.example.entities.Sponsor;
import org.example.entities.User;
import org.example.entities.Role;

import java.io.File;
import java.util.Objects;

public class LayoutController {

    @FXML private Button btnOrganizateur;
    @FXML private Button btnEvent;
    @FXML private VBox sidebar;
    @FXML private Button btnDashboard, btnUsers, btnTickets, btnReclamations, btnSponsors, btnLogout;
    @FXML private StackPane contentPane;

    @FXML private Label userNameLabel;
    @FXML private Label profileLabel;
    @FXML private ImageView topbarProfileImage;

    private String ReclamInterface = "/AdminReclamations.fxml";
    private String EventInterface = "/AfficherEvent.fxml";
    private String SponsorInterface = "/";

    private User currentUser;

    protected void onUserSet() {
        Role role = currentUser.getRole();
        String roleName = (role != null) ? role.getName() : "En_attente";

        String fullName = currentUser.getPrenom() + " " + currentUser.getNom();
        profileLabel.setText(fullName);

        // Load profile picture if exists
        if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
            File imgFile = new File(System.getProperty("user.dir"), currentUser.getProfilePicture());
            if (imgFile.exists()) {
                topbarProfileImage.setImage(new Image(imgFile.toURI().toString()));
                topbarProfileImage.setFitWidth(30);
                topbarProfileImage.setFitHeight(30);
                Circle clip = new Circle(15, 15, 15);
                topbarProfileImage.setClip(clip);
            }
        }

        // Par défaut, tout est masqué
        btnDashboard.setVisible(false);
        btnDashboard.setDisable(false);
        btnUsers.setVisible(false);
        btnTickets.setVisible(false);
        btnReclamations.setVisible(false);
        btnSponsors.setVisible(false);
        btnEvent.setVisible(false); // Si tu veux aussi restreindre "Evènements"
        btnLogout.setVisible(true); // Toujours visible
        btnOrganizateur.setVisible(false);

        // Affiche uniquement ce qui est permis selon le rôle
        switch (roleName) {
            case "admin":
                btnDashboard.setVisible(true);
                btnUsers.setVisible(true);
                btnTickets.setVisible(true);
                btnReclamations.setVisible(true);
                btnSponsors.setVisible(true);
                btnEvent.setVisible(true);
                btnOrganizateur.setVisible(true);

                break;
            case "Gestionnaire_des_tickets":
                btnTickets.setVisible(true);
                break;
            case "Agent_de_reclamation":
                btnReclamations.setVisible(true);
                break;
            case "Responsable_sponsor":
                btnSponsors.setVisible(true);
                break;
            case "Organisateur":
                btnEvent.setVisible(true);
                btnOrganizateur.setVisible(true);
                btnSponsors.setVisible(true);
                loadContent("/AfficherEvent.fxml");
                break;
            case "Sponsor":
                btnSponsors.setVisible(true);
            case "Client":
                btnReclamations.setVisible(false);
                loadContent("/ParticipantInterface.fxml");
        }

        // Par défaut, tu peux le rediriger vers sa page principale.
        if (btnDashboard.isVisible()) {
            loadContent("/Dashboard.fxml");
        } else if (btnReclamations.isVisible()) {
            loadContent("/AdminReclamations.fxml");
        } else if (btnTickets.isVisible()) {
            loadContent("/Tickets.fxml");
        } else if (btnSponsors.isVisible()) {
            goSponsors();
        } else if (btnEvent.isVisible()) {
            loadContent("/AfficherEvent.fxml");
        }
    }


    @FXML private void goDashboard()    { loadContent("/Dashboard.fxml"); }
    @FXML private void goUsers()        { loadContent("/UserList.fxml"); }
    @FXML private void goEvents()       { loadContent(EventInterface); }
    @FXML private void goTickets()      { loadContent("/TicketView.fxml"); }
    @FXML private void goReclamations() { loadContent(ReclamInterface); }
    @FXML private void goSponsors()     {
//        loadContent("/SponsorVueOrganisateur.fxml");
        try {
            FXMLLoader loader = null;
            System.out.println("Loading Sponsor View");
            if(Objects.equals(currentUser.getRole().getName(), "admin")){
                System.out.println("Admin Mode");
                loader = new FXMLLoader(getClass().getResource("/SponsorVueOrganisateur.fxml"));
            }else if(currentUser.getRole().getName().equals("Sponsor")){
                loader = new FXMLLoader(getClass().getResource("/SponsorDemandeListView.fxml"));
            }
            else{
                loader = new FXMLLoader(getClass().getResource("/SponsorVueOrganisateur.fxml"));
            }

//            SponsorDemandeListController controller = loader.getController();

            Node node = loader.load();
            if(currentUser.getRole().getName().equals("Sponsor")){
                SponsorDemandeListController controller = loader.getController();
                Sponsor s = new Sponsor();
                s.setId(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getId());
                s.setNomEntreprise(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getNom());
                s.setEmail(MainApp.LAYOUT_CONTROLLER.getCurrentUser().getEmail());
                controller.setCurrentSponsor(s);
            }
            contentPane.getChildren().setAll(node);
        } catch (Exception ex) {
            ex.printStackTrace();
        }}

    @FXML private void goProfile() {
        loadContent("/Profile.fxml");
    }

    @FXML private void logout() {
        try {
            Stage st = (Stage) sidebar.getScene().getWindow();
            st.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Login.fxml"))));
            st.setTitle("Connexion");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadContent(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(node);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadContentNode(Node node) {
        contentPane.getChildren().setAll(node);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        onUserSet();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @FXML
    private void showUserMenu(MouseEvent event) {
        ContextMenu menu = new ContextMenu();

        MenuItem editProfile = new MenuItem("Modifier Profil");
        MenuItem logoutItem  = new MenuItem("Déconnexion");

        editProfile.setOnAction(e -> goProfile());
        logoutItem.setOnAction(e -> logout());

        menu.getItems().addAll(editProfile, logoutItem);
        menu.show(profileLabel, Side.BOTTOM, 0, 0);
    }

    public void goOrganizer(ActionEvent actionEvent) {
        loadContent("/AjouterEvent.fxml");
    }

    public void setClientMode() {
        btnDashboard.setVisible(false);
        btnUsers.setVisible(false);
        btnTickets.setVisible(false);
        btnReclamations.setVisible(true);
        btnSponsors.setVisible(false);
        btnEvent.setVisible(true); // Si tu veux aussi restreindre "Evènements"
        btnLogout.setVisible(true); // Toujours visible
        btnOrganizateur.setVisible(false);

        ReclamInterface = "/AjouterReclamation.fxml";
        EventInterface = "/ParticipantInterface.fxml";
    }

    public void SetManagementMode(){
        ReclamInterface = "/AdminReclamation.fxml";
        EventInterface = "/AfficherEvent.fxml";
    }
}
