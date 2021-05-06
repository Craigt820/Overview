package sample;

import com.jfoenix.controls.JFXDrawer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectOverviewController implements Initializable {

    @FXML
    private Region opaque;
    @FXML
    private AnchorPane root;
    @FXML
    private TabPane colTabPane;
    @FXML
    private JFXDrawer drawer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.projectOverviewController = this;
        setupNewCollection("Batch 1");
    }

    private ProjectCollectionTabController setupNewCollection(String colName) {
        final Collection colObj = new Collection(colName);
        final ProjectCollectionTabController projectOverviewController = initCollection(colName);
        projectOverviewController.setCollection(colObj);
        return projectOverviewController;
    }

    private ProjectCollectionTabController initCollection(String colName) {
        final Tab tab = new Tab(colName);
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/ProjectCollection.fxml"));
        try {
            final Parent parent = (Parent) loader.load();
            final ProjectCollectionTabController projectOverviewController = loader.getController();
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


    public AnchorPane getRoot() {
        return root;
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }

    public JFXDrawer getGroupDetailDrawer() {
        return drawer;
    }

    public Region getOpaque() {
        return opaque;
    }
}
