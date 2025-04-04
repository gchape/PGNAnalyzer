package io.github.gchape.view;

import io.github.gchape.model.Model;
import io.github.gchape.view.events.EventHandlers;
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
    private final FileChooser fileChooser = new FileChooser();
    private final TreeView<String> treeView = new TreeView<>();

    private final Button analyze = new Button("Analyze");
    private final Button saveLog = new Button("Save log");
    private final Button selectFiles = new Button("Select files");

    private EventHandlers eventHandlers;

    private View() {
        composeView();
        configureStyle();
        configureEvents();
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
        analyze.setDisable(true);
        saveLog.setDisable(true);

        configureControl(FileChooser.class, fc -> fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("PGN Files", "*.pgn"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        ));

        configureControl(TextArea.class, ta -> {
            ta.setWrapText(true);
            ta.setEditable(true);
        });
    }

    private void configureStyle() {
        root.getStyleClass().add("root-pane");
        topBar.getStyleClass().add("top-bar");

        treeView.setPrefWidth(90);

        BorderPane.setMargin(textArea, new Insets(0, 10, 0, 0));
    }

    private void configureBindings() {
        treeView.rootProperty().bind(model.fileTreeProperty());
    }

    private void configureEvents() {
        selectFiles.setOnMouseClicked(e -> eventHandlers.fileChooserMouseClickAction(e, fileChooser));
    }

    private void composeView() {
        root.setTop(topBar);
        root.setRight(treeView);
        root.setCenter(textArea);

        topBar.getChildren().addAll(fileSection(), actionSection());
    }

    private HBox actionSection() {
        return new HBox() {{
            setSpacing(10.0);
            setAlignment(Pos.CENTER_RIGHT);

            getChildren().addAll(analyze, saveLog);
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

    public void setEventHandlers(EventHandlers eventHandlers) {
        this.eventHandlers = eventHandlers;
    }
}
