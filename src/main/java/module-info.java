module com.fileviewer.fileviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.swing;


    opens com.fileviewer.fileviewer to javafx.fxml;
    exports com.fileviewer.fileviewer;
    exports com.fileviewer.tokenizer;
    opens com.fileviewer.tokenizer to javafx.fxml;
}