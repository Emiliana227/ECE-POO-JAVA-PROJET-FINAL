package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import fr.ece.javaprojetfinal.basics.SettingsLauncher;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ViewProjectUsercontroller extends BaseController {

    @FXML private TableView<Projet> projectsTable;
    @FXML private TableColumn<Projet, String> nameCol;
    @FXML private TableColumn<Projet, String> descCol;
    @FXML private TableColumn<Projet, String> creationDateCol;
    @FXML private TableColumn<Projet, String> dueDateCol;
    @FXML private TableColumn<Projet, String> statusCol;
    @FXML private TableColumn<Projet, String> responsableCol;
    @FXML private TableColumn<Projet, Projet> actionsCol;

    @FXML private TextField searchField;
    @FXML private Button logoutbtn;
    @FXML private Button parametresbtn;
    @FXML private Button calendrierbtn;
    @FXML private Button projetsbtn;
    @FXML private Button tachesbtn;
    @FXML private Label usernameSpot;

    private final ObservableList<Projet> projects = FXCollections.observableArrayList();
    private final ObservableList<Projet> filtered = FXCollections.observableArrayList();
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected boolean checkPagePermissions() {
        return getSession().isUser();
    }
    @FXML
    private void initialize() {
        initializeSession();

        usernameSpot.setText(getCurrentUsername());

        // Table columns
        nameCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNom()));
        descCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDescription()));

        creationDateCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getDateCreation() != null ? dateFmt.format(c.getValue().getDateCreation()) : ""
        ));

        dueDateCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getDateEcheance() != null ? dateFmt.format(c.getValue().getDateEcheance()) : ""
        ));

        statusCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getStatut() != null ? c.getValue().getStatut() : ""
        ));

        responsableCol.setCellValueFactory(c -> {
            String name = "";
            try {
                ProjetDAO dao = new ProjetDAO();
                String n = dao.getResponsableNameByProjetId(c.getValue().getId());
                if (n != null) name = n;
            } catch (SQLException ex) {
                // ignore, leave blank
            }
            return new ReadOnlyStringWrapper(name);
        });

        actionsCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button access = new Button("Accéder");

            {
                access.setStyle("-fx-background-radius:6; -fx-background-color:#3b82f6; -fx-text-fill:white;");
                access.setOnAction(e -> {
                    Projet p = getItem();
                    if (p != null) openProjectInSameWindow(p);
                });
            }

            @Override
            protected void updateItem(Projet item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : access);
            }
        });

        projectsTable.setItems(filtered);
        projectsTable.setPlaceholder(new Label("Aucun projet assigné."));

        if (logoutbtn != null) logoutbtn.setOnAction(this::handleLogout);
        if (parametresbtn != null) parametresbtn.setOnAction(evt ->
                SettingsLauncher.openParametresForUser(getCurrentUserId(), parametresbtn)
        );
        if (calendrierbtn != null) calendrierbtn.setOnAction(this::openCalendar);
        if (tachesbtn != null) tachesbtn.setOnAction(this::openTasksPage);

        loadProjectsForUser();

        if (searchField != null) {
            searchField.addEventHandler(KeyEvent.KEY_RELEASED, e -> applyFilter());
        }
    }

    private void loadProjectsForUser() {
        ProjetDAO dao = new ProjetDAO();
        try {
            List<Projet> result = dao.findByUserId(getCurrentUserId());
            projects.setAll(result);
            filtered.setAll(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void applyFilter() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            filtered.setAll(projects);
            return;
        }
        String lower = q.toLowerCase();
        List<Projet> f = projects.stream()
                .filter(p -> (p.getNom() != null && p.getNom().toLowerCase().contains(lower))
                        || (p.getDescription() != null && p.getDescription().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
        filtered.setAll(f);
    }

    private void openProjectInSameWindow(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InsideProjetUser.fxml"));
            Parent root = loader.load();

            InsideProjetUsercontroller ctrl = loader.getController();
            ctrl.setProject(projet);

            Stage stage = (Stage) projectsTable.getScene().getWindow();
            Scene old = stage.getScene();
            Scene scene = new Scene(root);
            if (old != null) scene.getStylesheets().addAll(old.getStylesheets());
            stage.setScene(scene);
            stage.setTitle("Projet - " + projet.getNom());
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir le projet.");
        }
    }

    private void openCalendar(javafx.event.ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/Calendar.fxml", "Mon Calendrier");
    }

    private void openTasksPage(javafx.event.ActionEvent e) {
        openSimplePage("/fr/ece/javaprojetfinal/HomeUsertaches.fxml", "Mes tâches - " + getCurrentUsername());
    }

    private void openSimplePage(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) projectsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setMaximized(true);
        } catch (IOException ex) {
            showError("Navigation impossible.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
