module JavaProjetFinal {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.ece.javaprojetfinal to javafx.fxml;

    exports fr.ece.javaprojetfinal;

}
