package fr.ece.javaprojetfinal;

import fr.ece.javaprojetfinal.basics.Projet;
import fr.ece.javaprojetfinal.basics.ProjetDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class HomeProjetAdmincontroller {
    @FXML
    private Label usernameSpot;

    @FXML
    private ListView<Projet> projectsList;

    /**
     * Call this after loading FXML.
     * If isAdmin==true the controller will load projects where the user is the Responsable.
     * If isAdmin==false it will load projects from utilisateurs_projet (membership).
     */
    public void setUser(int userId, String username, boolean isAdmin) {
        if (usernameSpot != null) {
            usernameSpot.setText(username);
        }
        loadProjectsByRole(userId, isAdmin);
    }

    private void loadProjectsByRole(int userId, boolean isAdmin) {
        ProjetDAO dao = new ProjetDAO();
        try {
            List<Projet> projets;
            if (isAdmin) {
                System.out.println("[HomeProjetAdmincontroller] loading projects where user is Responsable -> " + userId);
                projets = dao.findByResponsableId(userId);
            } else {
                System.out.println("[HomeProjetAdmincontroller] loading projects by membership -> " + userId);
                projets = dao.findByUserId(userId);
            }
            final List<Projet> finalProjets = projets;
            Platform.runLater(() -> {
                if (projectsList != null) {
                    projectsList.getItems().setAll(finalProjets);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        if (projectsList != null) {
            projectsList.setPlaceholder(new Label("Aucun projet pour cet utilisateur"));
            projectsList.setCellFactory(new Callback<>() {
                private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                @Override
                public ListCell<Projet> call(ListView<Projet> lv) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(Projet item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                String text = item.getNom();
                                if (item.getDateEcheance() != null) {
                                    text += " — due " + fmt.format(item.getDateEcheance());
                                }
                                setText(text);
                            }
                        }
                    };
                }
            });
        }
    }
}
