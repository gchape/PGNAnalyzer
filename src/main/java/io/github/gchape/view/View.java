package io.github.gchape.view;

import io.github.gchape.model.Model;
import io.github.gchape.view.handlers.AnalyzeHandlers;
import io.github.gchape.view.handlers.SelectFilesHandlers;
import javafx.application.Platform;
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
import javafx.stage.FileChooser;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class View {
    private static final View INSTANCE = new View();
    private final Model model = Model.getInstance();

    private final HBox topBar = new HBox();
    private final BorderPane root = new BorderPane();
    private final TextArea textArea = new TextArea();
    private final TreeView<String> treeView = new TreeView<>();

    private final StringProperty textInput = new SimpleStringProperty("");

    private final Button analyze = new Button("Analyze");
    private final Button selectFiles = new Button("Select files");

    private AnalyzeHandlers analyzeHandlers;
    private SelectFilesHandlers selectFilesHandlers;

    private View() {
        composeView();
        configureStyle();
        mapEventHandlers();
        configureBindings();
        configureControls();
    }

    public static View getInstance() {
        return INSTANCE;
    }

    private <T> void configureControl(Class<T> controlClass, Consumer<T> configurer) {
        for (Field field : getClass().getDeclaredFields()) {
            if (controlClass.isAssignableFrom(field.getType())) {
                field.setAccessible(true);

                try {
                    T instance = controlClass.cast(field.get(this));
                    configurer.accept(instance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to configure " + controlClass.getSimpleName(), e);
                }
            }
        }
    }

    private void configureControls() {
        configureControl(FileChooser.class, __ -> __.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("*.pgn", "*.pgn")
        ));

        configureControl(TextArea.class, __ -> {
            __.setWrapText(true);
            __.setEditable(false);
        });
    }

    private void configureStyle() {
        root.getStyleClass().add("root-pane");
        topBar.getStyleClass().add("top-bar");

        treeView.setPrefWidth(105);

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

    public void setAnalyzeHandlers(AnalyzeHandlers analyzeHandlers) {
        this.analyzeHandlers = analyzeHandlers;
    }

    public void setSelectFilesHandlers(SelectFilesHandlers selectFilesHandlers) {
        this.selectFilesHandlers = selectFilesHandlers;
    }
}
