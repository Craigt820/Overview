package sample;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static sample.Main.projectOverviewController;

public class ProjectCollectionTabController implements Initializable {

    @FXML
    private ScrollPane root;

    @FXML
    private Text totalPages;

    @FXML
    private Text itemProg;

    @FXML
    private Text groupProg;

    @FXML
    private CustomTextField invSearch;

    @FXML
    private TableView2<Group> invTable;

    @FXML
    private TableColumn2<Group, Hyperlink> barcode;

    @FXML
    private TableColumn2<Group, ComboBox<String>> status;

    @FXML
    private TableColumn2<Group, ComboBox<String>> location;

    @FXML
    private TableColumn2<Group, String> name;

    @FXML
    private TableColumn2<Group, Integer> pallet;

    @FXML
    private TableColumn2<Group, Integer> pages;

    @FXML
    private TableColumn2<Group, Label> details;

    private Collection collection;

    private static final ArrayList<String> STATUS = new ArrayList<>(Arrays.asList("Inactive", "Received", "Scanned", "Returned"));

    @Override
    public void initialize(URL loc, ResourceBundle resources) {
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        pallet.setCellValueFactory(new PropertyValueFactory<>("pallet"));
        pages.setCellValueFactory(new PropertyValueFactory<>("pages"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
        invTable.setColumnFixingEnabled(true);
        invTable.setRowFixingEnabled(true);
        invTable.getItems().addAll(Arrays.asList(new Group("330034", "Box 4", 4, "Scanning", "Rack #1", 22), new Group("330034", "Pallet 23", 3, "Inactive", "Rack #4", 2222), new Group("330034", "Pallet 24", 3, "Completed", "Rack #3", 2242), new Group("330034", "Pallet 25", 2, "Inactive", "Rack #3", 2442), new Group("330034", "Pallet 26", 2, "Inactive", "Rack #2", 2222)));
        invTable.getFixedColumns().add(details);
        final FilteredList<Group> filteredData = new FilteredList<Group>(invTable.getItems(), p -> true);
        // Use textProperty
        invSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(p -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                final String lowerCaseFilter = newValue.toLowerCase();

                if (p.name.get().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (p.barcode.toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (p.getStatus().getSelectionModel().getSelectedItem().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (!p.getLocation().getSelectionModel().getSelectedItem().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        final SortedList<Group> sortedData = new SortedList<Group>(filteredData);
        sortedData.comparatorProperty().bind((ObservableValue<? extends Comparator<? super Group>>) invTable.comparatorProperty());
        invTable.setItems(sortedData);
    }

    public class Group {
        private Hyperlink barcode;
        private ComboBox<String> status;
        private ComboBox<String> location;
        private StringProperty name;
        private IntegerProperty pallet;
        private IntegerProperty pages;
        private Label details;

        public Group(String barcode, String name, int pallet, String status, String location, int pages) {
            this.barcode = new Hyperlink("#" + barcode);
            this.name = new SimpleStringProperty(name);
            this.pallet = new SimpleIntegerProperty(pallet);
            this.pages = new SimpleIntegerProperty(pages);
            this.location = new ComboBox<String>(FXCollections.observableArrayList("Rack #1", "Rack #2", "Rack #3", "Rack #4", "Rack #5"));
            this.location.getSelectionModel().select(location);
            this.location.setMinWidth(300);
            this.status = new ComboBox<String>(FXCollections.observableArrayList(STATUS));
            this.getStatus().getSelectionModel().select(status);
            this.details = new Label();
            this.details.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.details.setGraphic(new ImageView(getClass().getResource("../IMAGES/info.png").toExternalForm()));
            this.details.setTranslateX(-8);
            this.details.setTooltip(new Tooltip("Details"));
            this.details.setOnMousePressed(e -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GroupDetails.fxml"));
                try {
                    VBox box = loader.load();
                    projectOverviewController.getGroupDetailDrawer().setSidePane(box);
                    projectOverviewController.getGroupDetailDrawer().setDefaultDrawerSize(root.getWidth());
                    projectOverviewController.getGroupDetailDrawer().setMinWidth(root.getWidth());
                    projectOverviewController.getGroupDetailDrawer().setMinHeight(root.getHeight());
                    GroupDetailsController groupDetailsController = loader.getController();
//                    groupDetailsController.getBarcode().setText(this.getBarcode().getText());
//                    groupDetailsController.getGroupName().setText(this.name.get());

                    String selStatus = this.status.getSelectionModel().getSelectedItem();
                    if (selStatus == null || selStatus.isEmpty()) {
                        selStatus = "Inactive";
                    }
                    GroupDetailsController.Activity b1 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Selected");
                    GroupDetailsController.Activity b2 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Being Scanned");
                    GroupDetailsController.Activity b3 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Completed");
                    GroupDetailsController.Activity b4 = new GroupDetailsController.Activity("#84333", LocalDateTime.now(), "Cindy M.", "Item Edited");

                    groupDetailsController.getAcList().getItems().addAll(b1, b2, b3, b4);
//                    GroupDetailsController.Overview ab1 = new GroupDetailsController.Overview("", LocalDateTime.now(), 2202, "Test", "Craig T.", "Completed");
                    GroupDetailsController.Overview overview = new GroupDetailsController.Overview("#34390", LocalDateTime.now().toString(), "Rack #25", LocalDateTime.now().toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(), "5", false, "John M.", "None", 20400, "", "John H.", "Good", "Inactive", LocalDateTime.now().toString());
                    groupDetailsController.getOvTable().getItems().add(overview);

//                    TreeItem<GroupDetailsController.Overview> ovSubTreeItem = new TreeItem(ab1);
//                    a1.getChildren().add(ovSubTreeItem);
//
//                    groupDetailsController.getStatus().setText(selStatus);
                    groupDetailsController.getClose().setOnMouseClicked(e3 -> {
                        projectOverviewController.getGroupDetailDrawer().close();
                        projectOverviewController.getGroupDetailDrawer().setOnDrawerClosed(e4 -> {
                            projectOverviewController.getGroupDetailDrawer().setPrefWidth(0);
                            projectOverviewController.getGroupDetailDrawer().setMinWidth(0);
                        });
                    });
                    projectOverviewController.getGroupDetailDrawer().open();

                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            });
        }

        public Hyperlink getBarcode() {
            return barcode;
        }

        public void setBarcode(Hyperlink barcode) {
            this.barcode = barcode;
        }

        public ComboBox<String> getStatus() {
            return status;
        }

        public void setStatus(ComboBox<String> status) {
            this.status = status;
        }

        public ComboBox<String> getLocation() {
            return location;
        }

        public void setLocation(ComboBox<String> location) {
            this.location = location;
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public int getPallet() {
            return pallet.get();
        }

        public IntegerProperty palletProperty() {
            return pallet;
        }

        public void setPallet(int pallet) {
            this.pallet.set(pallet);
        }

        public int getPages() {
            return pages.get();
        }

        public IntegerProperty pagesProperty() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages.set(pages);
        }

        public Label getDetails() {
            return details;
        }

        public void setDetails(Label details) {
            this.details = details;
        }
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
}
