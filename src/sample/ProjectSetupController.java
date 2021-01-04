package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckListView;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectSetupController implements Initializable {
    private ExcelHandler excelHandler;
    private File importFile;
    private final ObservableList<String> headers = FXCollections.observableArrayList();
    private ListProvider listProvider = new ListProvider();

    @FXML
    VBox root;
    @FXML
    Tab manifestTab, specsTab, settingsTab, scanPathTab, columnsTab, completeTab;
    @FXML
    private Label importFileLbl;
    @FXML
    private Button removeImportBtn;
    @FXML
    private ListView<GridPane> importList;

    @FXML
    private TextField createColName;

    @FXML
    private ListView<GridPane> createList;

    @FXML
    private CheckBox importOn, createOn;

    @FXML
    private Button importBtn, createColBtn;

    @FXML
    private TitledPane backupPane;

    @FXML
    private Button backupBtn;
    @FXML
    private CheckBox backupOn;
    @FXML
    private TextField backupPath;

    @FXML
    private TitledPane jpgPane, tiffPane, pdfPane, commentsPane;
    @FXML
    private TitledPane importPane, createPane;

    @FXML
    private CheckBox jpgOn;

    @FXML
    private CheckComboBox<String> jpgMode;

    @FXML
    private CheckComboBox<String> jpgDPI;

    @FXML
    private CheckBox tiffOn;

    @FXML
    private CheckComboBox<String> tiffDPI;

    @FXML
    private CheckComboBox<String> tiffMode;

    @FXML
    private CheckComboBox<String> tiffComp;

    @FXML
    private CheckComboBox<String> tiffFormat;

    @FXML
    private CheckBox pdfOn;

    @FXML
    private CheckComboBox<String> pdfDPI;

    @FXML
    private CheckComboBox<String> pdfMode;

    @FXML
    private CheckComboBox<String> pdfComp;

    @FXML
    private CheckComboBox<String> pdfFormat;

    @FXML
    private TextArea comments;

    @FXML
    private CheckBox commentsOn;

    @FXML
    private CheckBox multipgOn;

    @FXML
    private CheckBox userEntryOn;

    @FXML
    private TitledPane procStructPane;
    @FXML
    private CheckComboBox<String> procStructCombo;

    @FXML
    private TitledPane boxTrackPane;

    @FXML
    private CheckBox boxTrackOn;

    @FXML
    private ComboBox<String> boxTrackCol;

    @FXML
    private ListView<HBox> subPathList;
    @FXML
    private HBox pathPrevRoot;
    @FXML
    private ListView<?> ulColumns;
    @FXML
    private TitledPane defaultColPane;
    @FXML
    private CheckComboBox<String> defaultColCombo;

    @FXML
    private TitledPane confirmPane;
    @FXML
    private Button submitBtn;

    @FXML
    void addSubPath() {
        HBox box = new HBox();
        box.setSpacing(8);
        Label label = new Label("Sub Path " + (subPathList.getItems().size() + 1));
        label.setStyle("-fx-font-size:14; -fx-padding:8; -fx-font-weight:bold;");
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8));
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMinHeight(40);
        comboBox.setMinWidth(600);
        Button button = new Button("REMOVE");
        Label pathPrevLbl = new Label();
        pathPrevLbl.setStyle("-fx-font-size:16;");
        button.setOnAction(e -> {
            subPathList.getItems().removeIf(e2 -> e2.equals(box));
            pathPrevRoot.getChildren().removeIf(e2 -> e2.equals(pathPrevLbl));
            subPathList.setMinHeight(subPathList.getItems().size() * 80);
        });
        button.setStyle("-fx-background-color: transparent;-fx-font-size:14; -fx-font-weight:bold;-fx-text-fill:#d02b06;");
        box.getChildren().addAll(label, comboBox, button);
        subPathList.getItems().add(box);
        subPathList.setMinHeight(subPathList.getItems().size() * 80);
        subPathList.setPrefHeight(subPathList.getItems().size() * 80);
        pathPrevRoot.getChildren().add(pathPrevLbl);
        listProvider.addSubscriber(comboBox.getItems());
        comboBox.getSelectionModel().selectFirst();

        if (excelHandler != null) { //Excel file has been found, use example binding
            pathPrevLbl.setText(excelHandler.getExamples().get(comboBox.getSelectionModel().getSelectedItem()) + "   \\   ");
            pathPrevLbl.setTooltip(new Tooltip(label.getText() + " : " + comboBox.getSelectionModel().getSelectedItem()));

            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue != null && !newValue.isEmpty()) {
                        pathPrevLbl.setText(excelHandler.getExamples().get(newValue) + "   \\   ");
                        pathPrevLbl.setTooltip(new Tooltip(label.getText() + " : " + newValue));
                    }
                }
            });
        } else {
            pathPrevLbl.textProperty().bind(Bindings.concat(comboBox.getSelectionModel().selectedItemProperty(), "   \\  "));
        }
    }

    @FXML
    void backupBrowse() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File path = directoryChooser.showDialog(root.getScene().getWindow());
        if (path != null && path.exists()) {
            backupPath.setText(path.getPath());
        }
    }

    public class ListProvider { //This class is used for updating combo-box values

        private ObservableList<String> provider;
        private ObservableList<ObservableList<String>> subscribers = FXCollections.observableArrayList();

        public ListProvider() {
        }

        private void addSubscriber(ObservableList<String> list) {
            subscribers.add(list);
            list.addAll(provider);
            this.provider.addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    while (c.next()) {

                        if (c.wasUpdated() || c.wasReplaced() || c.wasAdded() || c.wasPermutated()) {
                            updateSubscribers(c.getList().subList(c.getFrom(), c.getTo()));
                        }

                        for (String e : c.getRemoved()) {
                            removefromSubscribers(e);
                        }

                        for (String e : c.getAddedSubList()) {
                            addToSubscribers(e);
                        }


                    }
                }
            });

        }

        private void updateSubscribers(List<?> changes) {
            for (int i = 0; i < subscribers.size(); i++) {
                for (int k = 0; k < subscribers.get(i).size(); k++) {
                    subscribers.get(i).set(k, changes.get(0).toString());
                    break;
                }
            }
        }

        private void removefromSubscribers(String data) {
            for (int i = 0; i < subscribers.size(); i++) {
                for (int j = 0; j < subscribers.get(i).size(); j++) {
                    if (subscribers.get(i).get(j).equals(data)) {
                        subscribers.get(i).remove(data);
                    }
                }
            }
        }

        private void addToSubscribers(String data) {
            for (int i = 0; i < subscribers.size(); i++) {
                subscribers.get(i).add(data);
            }
        }
    }


    @FXML
    void createInsertColumn() {
        String newCol = createColName.getText();
        if (newCol != null && !newCol.isEmpty() && !this.headers.contains(newCol)) {
            this.headers.add(newCol);
            Label label = new Label(newCol);
            Button delete = new Button("REMOVE");
            Button edit = new Button("EDIT");
            delete.setStyle("-fx-background-color:white; -fx-text-fill: #d02b06; -fx-font-size:15; -fx-font-weight:bold;");
            edit.setStyle("-fx-background-color:white; -fx-text-fill: #ab7915; -fx-font-size:15; -fx-font-weight:bold;");

            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            ColumnConstraints col3 = new ColumnConstraints();
            col1.setPercentWidth(65);
            col2.setPercentWidth(15);
            col3.setPercentWidth(20);
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.add(label, 0, 0);
            gridPane.add(edit, 1, 0);
            gridPane.add(delete, 2, 0);

            gridPane.getColumnConstraints().addAll(col1, col2, col3);

            label.setContentDisplay(ContentDisplay.RIGHT);
            edit.setOnAction(e -> {
                showEditableCellField(null, label);
            });

            delete.setOnAction(e -> {
                createList.getItems().removeIf(e2 -> e2.equals(gridPane));
                headers.remove(newCol);
                createList.setMinHeight(createList.getItems().size() * 60);
            });

            createList.getItems().add(gridPane);
            if (createList.getItems().size() == 1) {
                createList.setMinHeight(60);
            } else {
                createList.setMinHeight(createList.getItems().size() * 60);
                createList.setPrefHeight(createList.getItems().size() * 60);
            }
            createColName.setText(null);

//            boxTrackCol.getItems().clear();
//            boxTrackCol.getItems().addAll(headers);

        }
    }

    public class PaneAssets {
        private BooleanProperty isActive = new SimpleBooleanProperty(false);
        private TitledPane pane;
        private CheckBox toggle;
        private ObservableList<Node> nodes;

        public PaneAssets(TitledPane pane, CheckBox toggle, ObservableList<Node> nodes) {
            this.nodes = nodes;
            this.toggle = toggle;
            this.pane = pane;
            this.nodes.forEach(e2 -> e2.setDisable(true));
            this.pane.setExpanded(false);
            if (toggle != null) {
                this.toggle.selectedProperty().addListener(e -> {
                    if (this.toggle.isSelected()) {
                        this.pane.setExpanded(true);
                        this.nodes.forEach(e2 -> e2.setDisable(false));
                        isActive.set(true);
                    } else {
                        this.pane.setExpanded(false);
                        this.nodes.forEach(e2 -> e2.setDisable(true));
                        isActive.set(false);
                    }
                });
            }
        }

        public boolean isActive() {
            return isActive.get();
        }

        public TitledPane getPane() {
            return pane;
        }

        public void setPane(TitledPane pane) {
            this.pane = pane;
        }

        public CheckBox getToggle() {
            return toggle;
        }

        public void setToggle(CheckBox toggle) {
            this.toggle = toggle;
        }


        public BooleanProperty isActiveProperty() {
            return isActive;
        }

        public void setIsActive(boolean isActive) {
            this.isActive.set(isActive);
        }

        public ObservableList<Node> getNodes() {
            return nodes;
        }

        public void setNodes(ObservableList<Node> nodes) {
            this.nodes = nodes;
        }
    }

    @FXML
    void removeImportFile() {
        this.headers.clear();
        this.importFile = null;
        this.importList.getItems().clear();
        this.importFileLbl.setText("");
        this.importList.setMinHeight(importList.getItems().size() * 80);
        removeImportBtn.setDisable(true);
        subPathList.getItems().clear();
        pathPrevRoot.getChildren().subList(1, pathPrevRoot.getChildren().size()).clear();
    }

    @FXML
    void browseImportFile() {
        final FileChooser directoryChooser = new FileChooser();
        importFile = directoryChooser.showOpenDialog(root.getScene().getWindow());
        excelHandler = new ExcelHandler(importFile);
        if (excelHandler.getFile() != null) {
            importFileLbl.setText(excelHandler.getFile().getName());
        }
        ArrayList<ExcelHandler.ExcelHeader> headers = excelHandler.readHeaders(importFile);
        if (!headers.isEmpty()) {
            removeImportBtn.setDisable(false);
            this.headers.addAll(headers.stream().map(ExcelHandler.ExcelHeader::getData).collect(Collectors.toList()));
            for (ExcelHandler.ExcelHeader header : headers) {
                if (header.getData() != null && !header.getData().isEmpty()) {
                    Label label = new Label(header.getData());
                    Button delete = new Button("REMOVE");
                    Button edit = new Button("EDIT");
                    delete.setStyle("-fx-background-color:white; -fx-text-fill: #d02b06; -fx-font-size:15; -fx-font-weight:bold;");
                    edit.setStyle("-fx-background-color:white; -fx-text-fill: #ab7915; -fx-font-size:15; -fx-font-weight:bold;");

                    ColumnConstraints col1 = new ColumnConstraints();
                    ColumnConstraints col2 = new ColumnConstraints();
                    ColumnConstraints col3 = new ColumnConstraints();
                    col1.setPercentWidth(60);
                    col2.setPercentWidth(10);
                    col3.setPercentWidth(30);
                    GridPane gridPane = new GridPane();
                    gridPane.setAlignment(Pos.CENTER);
                    gridPane.add(label, 0, 0);
                    gridPane.add(edit, 1, 0);
                    gridPane.add(delete, 2, 0);

                    gridPane.getColumnConstraints().addAll(col1, col2, col3);

                    label.setContentDisplay(ContentDisplay.RIGHT);
                    edit.setOnAction(e -> {
                        showEditableCellField(header, label);
                    });

                    delete.setOnAction(e -> {
                        this.headers.removeIf(e2 -> e2.equals(header.getData()));
                        importList.getItems().removeIf(e2 -> e2.equals(gridPane));
                        importList.setMinHeight(importList.getItems().size() * 80);
                        if (this.headers.isEmpty()) {
                            this.importFile = null;
                            this.removeImportBtn.setDisable(true);
                            this.importList.getItems().clear();
                            this.importFileLbl.setText("");
                        }
                    });
                    if (importList.getItems().size() == 1) {
                        importList.setMinHeight(60);
                    } else {
                        importList.setMinHeight(importList.getItems().size() * 80);
                        importList.setPrefHeight(importList.getItems().size() * 80);
                    }
                    importList.getItems().add(gridPane);
                }
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot find and/or read headers!");
            alert.show();
        }
    }

    private void showEditableCellField(ExcelHandler.ExcelHeader header, Label label) {
        TextField field = new TextField(label.getText());
        field.setStyle("-fx-background-color:transparent;");
        label.setGraphic(field);
        label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        field.requestFocus();
        field.positionCaret(0);
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                resetCellLabel(header, label, field);
            }
        });
    }

    private void resetCellLabel(ExcelHandler.ExcelHeader header, Label label, TextField field) {
        int ind = headers.indexOf(label.getText());
        headers.set(ind, field.getText());
        label.setText(field.getText());
        label.setContentDisplay(ContentDisplay.TEXT_ONLY);
        if (header != null) {
            header.setData(field.getText());
        }
    }

    @FXML
    private void submit() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listProvider.provider = headers;
        listProvider.addSubscriber(boxTrackCol.getItems());
        List<Tab> tabs = new ArrayList<>();
        //tabs.add(manifestTab); First tab shouldn't be disabled during initialization
        tabs.add(specsTab);
        tabs.add(settingsTab);
        tabs.add(scanPathTab);
        tabs.add(columnsTab);
        tabs.add(completeTab);

        //Check for any imported/created headers, then handle the other tab's functionality
        tabs.forEach(e -> {
            e.setDisable(true);

            createList.getItems().addListener(new ListChangeListener<GridPane>() {
                @Override
                public void onChanged(Change<? extends GridPane> c) {
                    if (!c.getList().isEmpty()) {
                        e.setDisable(false);
                    } else {
                        e.setDisable(true);
                    }
                }
            });
            importList.getItems().addListener(new ListChangeListener<GridPane>() {
                @Override
                public void onChanged(Change<? extends GridPane> c) {
                    if (!c.getList().isEmpty()) {
                        e.setDisable(false);
                    } else {
                        e.setDisable(true);
                    }
                }
            });
        });

        //Group all TitledPane nodes to handle their functionality in a cleaner way
        PaneAssets boxTrackAssets = new PaneAssets(boxTrackPane, boxTrackOn, FXCollections.observableArrayList(boxTrackCol));
        PaneAssets importAssets = new PaneAssets(importPane, importOn, FXCollections.observableArrayList(importList, importBtn));
        PaneAssets createAssets = new PaneAssets(createPane, createOn, FXCollections.observableArrayList(createList, createColName, createColBtn));
        PaneAssets jpgAssets = new PaneAssets(jpgPane, jpgOn, FXCollections.observableArrayList(jpgDPI, jpgMode));
        PaneAssets tiffAssets = new PaneAssets(tiffPane, tiffOn, FXCollections.observableArrayList(tiffDPI, tiffMode, tiffComp, tiffFormat));
        PaneAssets pdfAssets = new PaneAssets(pdfPane, pdfOn, FXCollections.observableArrayList(pdfDPI, pdfMode, pdfComp, pdfFormat));
        PaneAssets commentAssets = new PaneAssets(commentsPane, commentsOn, FXCollections.observableArrayList(comments));
        PaneAssets backupAssets = new PaneAssets(backupPane, backupOn, FXCollections.observableArrayList(backupPath, backupBtn));
//        PaneAssets procStructAssets = new PaneAssets(procStructPane, null, FXCollections.observableArrayList(procStructCombo));
//        PaneAssets completeAssets = new PaneAssets(confirmPane, null, FXCollections.observableArrayList(submitBtn));

        List<CheckComboBox<String>> dpiList = new ArrayList<>();
        dpiList.add(jpgDPI);
        dpiList.add(tiffDPI);
        dpiList.add(pdfDPI);
        dpiList.forEach(e -> {
            e.getItems().addAll(Arrays.asList("200", "300", "400", "500", "600"));
            e.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    StringBuilder builder = new StringBuilder();
                    c.getList().forEach(e -> {
                        builder.append(e);
                        builder.append(" DPI, ");
                    });
                    if (!builder.toString().isEmpty()) {
                        e.setTitle(builder.toString().substring(0, builder.length() - 2));
                    } else {
                        e.setTitle("DPI");
                    }
                }
            });
        });
        List<CheckComboBox<String>> modeList = new ArrayList<>();
        modeList.add(jpgMode);
        modeList.add(tiffMode);
        modeList.add(pdfMode);
        modeList.forEach(e -> {
            e.getItems().addAll(Arrays.asList("B/W 1 Bit", "Grayscale", "Color"));
            e.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    StringBuilder builder = new StringBuilder();
                    c.getList().forEach(e -> {
                        builder.append(e);
                        builder.append(", ");
                    });
                    if (!builder.toString().isEmpty()) {
                        e.setTitle(builder.toString().substring(0, builder.length() - 2));
                    } else {
                        e.setTitle("Mode");
                    }
                }
            });
        });
        List<CheckComboBox<String>> compList = new ArrayList<>();
        compList.add(tiffComp);
        compList.forEach(e -> {
            e.getItems().addAll(Arrays.asList("Uncompressed", "LZW"));
            e.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    StringBuilder builder = new StringBuilder();
                    c.getList().forEach(e -> {
                        builder.append(e);
                        builder.append(", ");
                    });
                    if (!builder.toString().isEmpty()) {
                        e.setTitle(builder.toString().substring(0, builder.length() - 2));
                    } else {
                        e.setTitle("Compression");
                    }
                }
            });
        });
        List<CheckComboBox<String>> formatList = new ArrayList<>();
        formatList.add(tiffFormat);
        formatList.add(pdfFormat);
        formatList.forEach(e -> {
            e.getItems().addAll(Arrays.asList("Single-Paged", "Multi-Paged"));
            e.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    StringBuilder builder = new StringBuilder();
                    c.getList().forEach(e -> {
                        builder.append(e);
                        builder.append(", ");
                    });
                    if (!builder.toString().isEmpty()) {
                        e.setTitle(builder.toString().substring(0, builder.length() - 2));
                    } else {
                        e.setTitle("Format");
                    }
                }
            });
        });

        //TODO: These will be replaced dynamically through the database (Extra Default Cols & Process Steps can be added and removed later)
        procStructCombo.getItems().addAll(FXCollections.observableArrayList(Arrays.asList("Prepping", "Scanning", "QCing", "Cropping", "Rescans", "Shipment")));
        procStructCombo.getCheckModel().checkAll();
        procStructCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                StringBuilder builder = new StringBuilder();
                c.getList().forEach(e -> {
                    builder.append(e).append(" , ");
                });

                if (!builder.toString().isEmpty()) {
                    procStructCombo.setTitle(builder.substring(0, builder.length() - 2));
                }
            }
        });
        defaultColCombo.getItems().addAll(FXCollections.observableArrayList(Arrays.asList("Row", "Completed", "Queued", "Computer", "Pages", "Name", "Date", "Transfer Date", "Upload #", "Comments")));
        defaultColCombo.getCheckModel().checkAll();
        defaultColCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                StringBuilder builder = new StringBuilder();

                c.getList().forEach(e -> {
                    builder.append(e).append(" , ");
                });
                if (!builder.toString().isEmpty()) {
                    defaultColCombo.setTitle(builder.substring(0, builder.length() - 2));
                }
            }
        });
    }
}
