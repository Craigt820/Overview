package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ProjectOverviewController implements Initializable {

    @FXML
    private Label totalPages;

    @FXML
    private Label itemProgress;

    @FXML
    private Label boxProgress;

    @FXML
    private TableView<Collection> assetTable;

    @FXML
    private TableColumn<Collection, ComboBox<String>> stageStatus;

    @FXML
    private TableColumn<Collection, ComboBox<String>> stageEmp;

    @FXML
    private TableColumn<Collection, String> title;

    @FXML
    private TableColumn<Collection, Integer> pages;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stageStatus.setCellValueFactory(new PropertyValueFactory<>("stageStatus"));
        stageEmp.setCellValueFactory(new PropertyValueFactory<>("stageEmp"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        pages.setCellValueFactory(new PropertyValueFactory<>("pages"));

        assetTable.getItems().addAll(Arrays.asList(new Collection("Box 22",22),new Collection("Box 23",2222),new Collection("Box 24",2242),new Collection("Box 25",2442),new Collection("Box 26",2222)));
    }

    public class Collection {
        private ComboBox<String> stageStatus;
        private ComboBox<String> stageEmp;
        private String title;
        private int pages;

        public Collection(String title, int pages) {
            this.title = title;
            this.pages = pages;
            this.stageEmp = new ComboBox<String>(FXCollections.observableArrayList("Alpha","Beta","Gamma","Omega"));
            this.stageStatus = new ComboBox<String>(FXCollections.observableArrayList("-","Prepping","Scanning","QC","Rescans","Shipment","Complete"));
        }

        public ComboBox<String> getStageTitle() {
            return stageStatus;
        }

        public void setStageTitle(ComboBox<String> stageStatus) {
            this.stageStatus = stageStatus;
        }

        public ComboBox<String> getStageStatus() {
            return stageStatus;
        }

        public void setStageStatus(ComboBox<String> stageStatus) {
            this.stageStatus = stageStatus;
        }

        public ComboBox<String> getStageEmp() {
            return stageEmp;
        }

        public void setStageEmp(ComboBox<String> stageEmp) {
            this.stageEmp = stageEmp;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }
    }

}
