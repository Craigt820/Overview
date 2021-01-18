package sample;

import com.jfoenix.controls.JFXDrawer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectOverviewController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TabPane colTabPane;
    @FXML
    private JFXDrawer drawer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.projectOverviewController = this;
        setupNewCollection("TEST");
    }

    private ProjectCollectionTabController setupNewCollection(String colName) {
        Collection colObj = new Collection(colName);
        ProjectCollectionTabController projectOverviewController = addCollectionTab(colName);
        projectOverviewController.setCollection(colObj);
        return projectOverviewController;
    }

    private ProjectCollectionTabController addCollectionTab(String colName) {
        Tab tab = new Tab(colName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectCollectionTab.fxml"));
        try {
            Parent parent = (Parent) loader.load();
            ProjectCollectionTabController projectOverviewController = loader.getController();
            tab.setContent(parent);
            colTabPane.getTabs().add(tab);
            return projectOverviewController;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class Box {
        private Hyperlink barcode;
        private ComboBox<String> status;
        private CheckComboBox<String> employees;
        private StringProperty name;
        private int pages;
        private Label details;

        public Box(String barcode, String name, String status, List<String> employees, int pages) {
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
                    drawer.setSidePane(box);
                    drawer.setDefaultDrawerSize(650);
                    drawer.setMinWidth(650);
                    drawer.setMinHeight(root.getHeight());
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
                        drawer.close();
                        drawer.setOnDrawerClosed(e4 -> {
                            drawer.setPrefWidth(0);
                            drawer.setMinWidth(0);
                        });
                    });
                    drawer.open();

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
        private List<Box> boxes = new ArrayList<>();
        private int boxProg;
        private int boxTotal;
        private int pageTotal;
        private int itemProg;
        private int itemTotal;
        private String name;

        public Collection(String name) {
            this.name = name;
        }

        public List<Box> getBoxes() {
            return boxes;
        }

        public void setBoxes(List<Box> boxes) {
            this.boxes = boxes;
        }

        public int getBoxProg() {
            return boxProg;
        }

        public void setBoxProg(int boxProg) {
            this.boxProg = boxProg;
        }

        public int getBoxTotal() {
            return boxTotal;
        }

        public void setBoxTotal(int boxTotal) {
            this.boxTotal = boxTotal;
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

    public AnchorPane getRoot() {
        return root;
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }

    public JFXDrawer getGroupDetailDrawer() {
        return drawer;
    }
}
