package io.github.gchape.view;

import io.github.gchape.model.Model;
import io.github.gchape.view.handlers.AnalyzeHandlers;
import io.github.gchape.view.handlers.SelectFilesHandlers;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public enum View {
    INSTANCE;

    private final Model model = Model.INSTANCE;

    private final HBox topBar = new HBox();
    private final BorderPane root = new BorderPane();
    private final TextArea textArea = new TextArea();
    private final TreeView<String> treeView = new TreeView<>();

    private final StringProperty textInput = new SimpleStringProperty("");

    private final Button analyze = new Button("Analyze");
    private final Button selectFiles = new Button("Select files");

    private AnalyzeHandlers analyzeHandlers;
    private SelectFilesHandlers selectFilesHandlers;

    View() {
        composeView();
        configureStyle();
        mapEventHandlers();
        configureBindings();
        configureControls();
    }

    private void configureControls() {
        // TextArea
        {
            textArea.setWrapText(true);
            textArea.setEditable(false);
        }
    }

    private void configureStyle() {
        root.getStyleClass().add("root-pane");
        topBar.getStyleClass().add("top-bar");

        treeView.setPrefWidth(110);

        BorderPane.setMargin(textArea, new Insets(0, 0, 0, 10));
    }

    private void configureBindings() {
        treeView.rootProperty().bind(model.fileTreeProperty());

        analyze.disableProperty().bind(model.analyzeButtonDisabledProperty());
        selectFiles.disableProperty().bind(model.selectFilesButtonDisabledProperty());

        textInput.bind(model.textInputProperty());
    }

    private void mapEventHandlers() {
        analyze.setOnMouseClicked(e -> analyzeHandlers.onMouseClicked(e));
        selectFiles.setOnMouseClicked(e -> selectFilesHandlers.onMouseClicked(e));

        textInput.addListener((__0, __1, newText) -> textArea.appendText(newText));
    }

    private void composeView() {
        root.setTop(topBar);
        root.setLeft(treeView);
        root.setCenter(textArea);

        topBar.getChildren().addAll(fileSection(), actionSection());
    }

    private HBox actionSection() {
        return new HBox() {{
            setSpacing(10.0);
            setAlignment(Pos.CENTER_RIGHT);

            getChildren().add(analyze);
        }};
    }

    private HBox fileSection() {
        return new HBox() {{
            setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(this, Priority.ALWAYS);

            getChildren().add(selectFiles);
        }};
    }

    public Region getRoot() {
        return root;
    }

    public void setAnalyzeHandlers(final AnalyzeHandlers analyzeHandlers) {
        this.analyzeHandlers = analyzeHandlers;
    }

    public void setSelectFilesHandlers(final SelectFilesHandlers selectFilesHandlers) {
        this.selectFilesHandlers = selectFilesHandlers;
    }
}
