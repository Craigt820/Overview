package sample;

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
    private TableColumn2<Group, CheckComboBox<String>> employees;

    @FXML
    private TableColumn2<Group, String> name;

    @FXML
    private TableColumn2<Group, Integer> pages;

    @FXML
    private TableColumn2<Group, Label> details;

    private ProjectOverviewController.Collection collection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        employees.setCellValueFactory(new PropertyValueFactory<>("employees"));
        barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        pages.setCellValueFactory(new PropertyValueFactory<>("pages"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
        invTable.setColumnFixingEnabled(true);
        invTable.setRowFixingEnabled(true);
        invTable.getItems().addAll(Arrays.asList(new Group("330034", "Box 22", "Prepping", Arrays.asList("Alpha", "Beta"), 22), new Group("330034", "Box 23", "Inactive", Collections.emptyList(), 2222), new Group("330034", "Box 24", "Completed", Arrays.asList("Alpha", "Beta"), 2242), new Group("330034", "Box 25", "Inactive", Collections.emptyList(), 2442), new Group("330034", "Box 26", "Inactive", Collections.emptyList(), 2222)));
        invTable.getFixedColumns().add(details);
        FilteredList<Group> filteredData = new FilteredList<Group>(invTable.getItems(), p -> true);
        // Use textProperty
        invSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(p -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (p.name.get().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (p.barcode.toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (p.getStatus().getSelectionModel().getSelectedItem().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (!p.getEmployees().getCheckModel().getCheckedItems().stream().map(String::toLowerCase).filter(e -> e.contains(lowerCaseFilter)).collect(Collectors.toList()).isEmpty()) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        SortedList<Group> sortedData = new SortedList<Group>(filteredData);
        sortedData.comparatorProperty().bind((ObservableValue<? extends Comparator<? super Group>>) invTable.comparatorProperty());
        invTable.setItems(sortedData);

    }

    public class Group {
        private Hyperlink barcode;
        private ComboBox<String> status;
        private CheckComboBox<String> employees;
        private StringProperty name;
        private int pages;
        private Label details;

        public Group(String barcode, String name, String status, List<String> employees, int pages) {
            this.barcode = new Hyperlink("#" + barcode);
            this.name = new SimpleStringProperty(name);
            this.pages = pages;
            this.employees = new CheckComboBox<String>(FXCollections.observableArrayList("Alpha", "Beta", "Gamma", "Omega"));
            employees.forEach(e -> {
                this.employees.getCheckModel().check(e);
            });
            this.employees.setMinWidth(300);
            this.status = new ComboBox<String>(FXCollections.observableArrayList("Inactive", "Prepping", "Scanning", "QC", "Rescans", "Shipment", "Complete"));
            this.getStatus().getSelectionModel().select(status);
            this.details = new Label();
            this.details.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.getDetails().getGraphic();
            this.details.setGraphic(new ImageView(getClass().getResource("info.png").toExternalForm()));
            this.details.setTooltip(new Tooltip("Details"));
            this.details.setOnMousePressed(e -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("GroupDetails.fxml"));
                try {
                    VBox box = loader.load();
                    projectOverviewController.getGroupDetailDrawer().setSidePane(box);
                    projectOverviewController.getGroupDetailDrawer().setDefaultDrawerSize(650);
                    projectOverviewController.getGroupDetailDrawer().setMinWidth(650);
                    projectOverviewController.getGroupDetailDrawer().setMinHeight(projectOverviewController.getRoot().getHeight());
                    GroupDetailsController controller = loader.getController();
                    controller.getBarcode().setText(this.getBarcode().getText());
                    controller.getGroupName().setText(this.name.get());

                    String selStatus = this.status.getSelectionModel().getSelectedItem();
                    if (selStatus == null || selStatus.isEmpty()) {
                        selStatus = "Inactive";
                    }
                    GroupDetailsController.Activity b1 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Selected");
                    GroupDetailsController.Activity b2 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Being Scanned");
                    GroupDetailsController.Activity b3 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Completed");
                    GroupDetailsController.Activity b4 = new GroupDetailsController.Activity("#84333", LocalDateTime.now(), "Cindy M.", "Item Edited");

                    controller.getAcList().getItems().addAll(b1, b2, b3, b4);
                    GroupDetailsController.Overview ab1 = new GroupDetailsController.Overview("", LocalDateTime.now(), 2202, "Test", "Craig T.", "Completed");
                    TreeItem<GroupDetailsController.Overview> a1 = new TreeItem<>(new GroupDetailsController.Overview("#34390", LocalDateTime.now()));
                    TreeItem<GroupDetailsController.Overview> a2 = new TreeItem<>(new GroupDetailsController.Overview("#34391", LocalDateTime.now()));
                    TreeItem<GroupDetailsController.Overview> a3 = new TreeItem<>(new GroupDetailsController.Overview("#34392", LocalDateTime.now()));
                    TreeItem<GroupDetailsController.Overview> a4 = new TreeItem<>(new GroupDetailsController.Overview("#34393", LocalDateTime.now()));

                    TreeItem<GroupDetailsController.Overview> ovSubTreeItem = new TreeItem<>(ab1);
                    a1.getChildren().add(ovSubTreeItem);
                    controller.getOvTree().getRoot().getChildren().addAll(a1, a2, a3, a4);

                    controller.getStatus().setText(selStatus);
                    controller.getClose().setOnMouseClicked(e3 -> {
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

        public CheckComboBox<String> getEmployees() {
            return employees;
        }

        public void setEmployees(CheckComboBox<String> employees) {
            this.employees = employees;
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

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public Label getDetails() {
            return details;
        }

        public void setDetails(Label details) {
            this.details = details;
        }
    }

    public class Collection {
        private List<Group> groups = new ArrayList<>();
        private int groupProg;
        private int groupTotal;
        private int pageTotal;
        private int itemProg;
        private int itemTotal;
        private String name;

        public Collection(String name) {
            this.name = name;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }

        public int getGroupProg() {
            return groupProg;
        }

        public void setGroupProg(int groupProg) {
            this.groupProg = groupProg;
        }

        public int getGroupTotal() {
            return groupTotal;
        }

        public void setGroupTotal(int groupTotal) {
            this.groupTotal = groupTotal;
        }

        public int getPageTotal() {
            return pageTotal;
        }

        public void setPageTotal(int pageTotal) {
            this.pageTotal = pageTotal;
        }

        public int getItemProg() {
            return itemProg;
        }

        public void setItemProg(int itemProg) {
            this.itemProg = itemProg;
        }

        public int getItemTotal() {
            return itemTotal;
        }

        public void setItemTotal(int itemTotal) {
            this.itemTotal = itemTotal;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


    }

    public ProjectOverviewController.Collection getCollection() {
        return collection;
    }

    public void setCollection(ProjectOverviewController.Collection collection) {
        this.collection = collection;
    }
}
