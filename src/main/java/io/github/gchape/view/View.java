package io.github.gchape.view;

import io.github.gchape.model.Model;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class View {
    private static final View INSTANCE = new View();
    private static final Model model = Model.getInstance();

    private final HBox topBar = new HBox();
    private final BorderPane root = new BorderPane();

    private final TextArea textArea = new TextArea();
    private final TreeItem<String> treeRoot = new TreeItem<>();
    private final TreeView<String> treeView = new TreeView<>(treeRoot);

    private final Button analyze = new Button("Analyze");
    private final Button chessboard = new Button("Chessboard");
    private final Button chooseFiles = new Button("Choose files");

    {
        root.getStyleClass().add("root-pane");
        topBar.getStyleClass().add("top-bar");

        textArea.setWrapText(true);
        textArea.setEditable(true);

        treeRoot.setExpanded(true);
        treeView.setPrefWidth(115);

        analyze.disableProperty().bind(chooseFiles.disableProperty().not());
        chessboard.disableProperty().bind(analyze.disableProperty());

        chooseFiles.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PGN Files", "*.pgn"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(((Node) e.getSource()).getScene().getWindow());

            if (selectedFiles != null) {
                model.getSelectedFiles().clear();
                model.getSelectedFiles().addAll(selectedFiles);
            }
        });

        BorderPane.setMargin(textArea, new Insets(0, 10, 0, 0));

        root.setTop(topBar);
        root.setRight(treeView);
        root.setCenter(textArea);

        topBar.getChildren().addAll(fileSection(), actionSection());
    }

    private View() {
    }

    public static View getInstance() {
        return INSTANCE;
    }

    private HBox actionSection() {
        return new HBox() {{
            getChildren().addAll(analyze, chessboard);
            setSpacing(10.0);
            setAlignment(Pos.CENTER_RIGHT);
        }};
    }

    private HBox fileSection() {
        return new HBox() {{
            getChildren().add(chooseFiles);
            setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(this, Priority.ALWAYS);
        }};
    }

    public Region getRoot() {
        return root;
    }
}
