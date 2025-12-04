module fr.ece.javaprojetfinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens fr.ece.javaprojetfinal to javafx.fxml;
    exports fr.ece.javaprojetfinal;
}