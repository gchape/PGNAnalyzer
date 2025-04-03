module io.github.gchape.pgnanalyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.github.gchape to javafx.fxml;
    exports io.github.gchape;
}