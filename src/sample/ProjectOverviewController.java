package sample;

import com.jfoenix.controls.JFXDrawer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectOverviewController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private Label totalPages;

    @FXML
    private Label itemProgress;

    @FXML
    private Label boxProgress;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private TableView2<Box> invTable;

    @FXML
    private TableColumn2<Box, Hyperlink> barcode;

    @FXML
    private TableColumn2<Box, ComboBox<String>> status;

    @FXML
    private TableColumn2<Box, CheckComboBox<String>> employees;

    @FXML
    private TableColumn2<Box, String> name;

    @FXML
    private TableColumn2<Box, Integer> pages;

    @FXML
    private TableColumn2<Box, Label> details;

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
        invTable.getItems().addAll(Arrays.asList(new Box("330034", "Box 22", "Prepping", Arrays.asList("Alpha", "Beta"), 22), new Box("330034", "Box 23", "Inactive", Collections.emptyList(), 2222), new Box("330034", "Box 24", "Completed", Arrays.asList("Alpha", "Beta"), 2242), new Box("330034", "Box 25", "Inactive", Collections.emptyList(), 2442), new Box("330034", "Box 26", "Inactive", Collections.emptyList(), 2222)));
        invTable.getFixedColumns().add(details);
    }

    public class Box {
        private Hyperlink barcode;
        private ComboBox<String> status;
        private CheckComboBox<String> employees;
        private StringProperty name;
        private int pages;
        private Label details;

        public Box(String barcode, String name, String status, List<String> employees, int pages) {
            this.barcode = new Hyperlink(barcode);
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("BoxDetails.fxml"));
                try {
                    VBox box = loader.load();
                    drawer.setSidePane(box);
                    drawer.setDefaultDrawerSize(650);
                    drawer.setMinWidth(650);
                    drawer.setMinHeight(root.getHeight());
                    BoxDetailsController controller = loader.getController();
                    controller.getBarcode().setText("#"+this.getBarcode().getText());
                    controller.getBoxName().setText(this.name.get());

                    String selStatus = this.status.getSelectionModel().getSelectedItem();
                    if (selStatus == null || selStatus.isEmpty()) {
                        selStatus = "Inactive";
                    }
                    BoxDetailsController.Activity b1 = new BoxDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Selected");
                    BoxDetailsController.Activity b2 = new BoxDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Being Scanned");
                    BoxDetailsController.Activity b3 = new BoxDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Completed");
                    BoxDetailsController.Activity b4 = new BoxDetailsController.Activity("#84333", LocalDateTime.now(), "Cindy M.", "Item Edited");

                    controller.getAcList().getItems().addAll(b1, b2, b3, b4);
                    BoxDetailsController.Overview ab1 = new BoxDetailsController.Overview("", LocalDateTime.now(), "Test", "Craig T.", "Completed");
                    TreeItem<BoxDetailsController.Overview> a1 = new TreeItem<>(new BoxDetailsController.Overview("#34390",LocalDateTime.now()));
                    TreeItem<BoxDetailsController.Overview> a2 = new TreeItem<>(new BoxDetailsController.Overview("#34391",LocalDateTime.now()));
                    TreeItem<BoxDetailsController.Overview> a3 = new TreeItem<>(new BoxDetailsController.Overview("#34392",LocalDateTime.now()));
                    TreeItem<BoxDetailsController.Overview> a4 = new TreeItem<>(new BoxDetailsController.Overview("#34393",LocalDateTime.now()));

                    TreeItem<BoxDetailsController.Overview> ovSubTreeItem = new TreeItem<>(ab1);
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

}
