package sample;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.controlsfx.control.tableview2.TableView2;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class GroupDetailsController implements Initializable {

    @FXML
    private VBox box;

    @FXML
    private Label close;

    @FXML
    private Label barcode;

    @FXML
    private Label status;

    @FXML
    private Label groupName;

    @FXML
    private TableView2<Overview> ovTable;

    @FXML
    private TableColumn<Overview, String> idCol;

    @FXML
    private TableColumn<Overview, Integer> countCol;

    @FXML
    private TableColumn<Overview, String> boxCol;

    @FXML
    private TableColumn<Overview, String> statusCol;

    @FXML
    private TableColumn<Overview, String> reportCol;

    @FXML
    private TableColumn<Overview, String> locationCol;

    @FXML
    private TableColumn<Overview, String> conditionCol;

    @FXML
    private TableColumn<Overview, Label> detailsCol;

    @FXML
    private ListView<Activity> acList;

    private static final ArrayList<String> CONDITION = new ArrayList<>(Arrays.asList("Good", "Poor", "Damaged"));


    public static class Activity extends VBox {
        private String objID;
        private LocalDateTime timeStamp;
        private String employee;
        private String description;

        public Activity(String objID, LocalDateTime timeStamp, String employee, String description) {
            this.setPadding(new Insets(8, 8, 8, 8));
            this.objID = objID;
            this.setSpacing(3);
            this.setStyle("-fx-translate-y: 2;");
            this.timeStamp = timeStamp;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.employee = employee;
            this.description = description;
            Hyperlink idLbl = new Hyperlink(this.objID);
            Label timeStampLbl = new Label(this.timeStamp.format(formatter));
            timeStampLbl.setStyle("-fx-font-size:14;");
            Label empLbl = new Label(this.getEmployee());
            empLbl.setStyle("-fx-padding:0 0 0 8; -fx-font-size:15;");
            Label descLbl = new Label("Action: " + this.getDescription());
            descLbl.setStyle("-fx-padding:0 0 0 8; -fx-font-size:15;");
            descLbl.setWrapText(true);
            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            ColumnConstraints col3 = new ColumnConstraints();
            col1.setPercentWidth(60);
            col2.setPercentWidth(40);
            GridPane gridPane = new GridPane();
            gridPane.setVgap(4);
            gridPane.setAlignment(Pos.CENTER);
            gridPane.add(idLbl, 0, 0);
            gridPane.add(timeStampLbl, 1, 0);
            gridPane.add(empLbl, 0, 1);
            gridPane.add(descLbl, 0, 2);

            gridPane.getColumnConstraints().addAll(col1, col3);
            getChildren().addAll(gridPane);
        }

        public String getObjID() {
            return objID;
        }

        public void setObjID(String objID) {
            this.objID = objID;
        }

        public LocalDateTime getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(LocalDateTime timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getEmployee() {
            return employee;
        }

        public void setEmployee(String employee) {
            this.employee = employee;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Location {

    }

    public static class Overview {


        private Hyperlink barcode;
        private StringProperty receivedBy;
        private StringProperty location;
        private StringProperty dateReturned;
        private StringProperty dateComp;
        private StringProperty dateScanned;
        private StringProperty box;
        private BooleanProperty releasedToProd;
        private StringProperty packedBy;
        private StringProperty receiptReport;
        private IntegerProperty count;
        private StringProperty description;
        private StringProperty employee;
        private StringProperty condition;
        private StringProperty status;
        private StringProperty lastUpdated;
        private Label details;

        public Overview(String barcode, String receivedBy, String location, String dateReturned, String dateComp, String dateScanned, String box, Boolean releasedToProd, String packedBy, String receiptReport, Integer count, String description, String employee, String condition, String status, String lastUpdated) {
            this.barcode = new Hyperlink(barcode);
            this.receivedBy = new SimpleStringProperty(receivedBy);
            this.location = new SimpleStringProperty(location);
            this.dateReturned = new SimpleStringProperty(dateReturned);
            this.dateComp = new SimpleStringProperty(dateComp);
            this.dateScanned = new SimpleStringProperty(dateScanned);
            this.box = new SimpleStringProperty(box);
            this.releasedToProd = new SimpleBooleanProperty(releasedToProd);
            this.packedBy = new SimpleStringProperty(packedBy);
            this.receiptReport = new SimpleStringProperty(receiptReport);
            this.count = new SimpleIntegerProperty(count);
            this.description = new SimpleStringProperty(description);
            this.employee = new SimpleStringProperty(employee);
            this.condition = new SimpleStringProperty(condition);
            this.status = new SimpleStringProperty(status);
            this.lastUpdated = new SimpleStringProperty(lastUpdated);
            this.details = new Label();
            this.details.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.details.setGraphic(new ImageView(getClass().getResource("../IMAGES/info.png").toExternalForm()));
            this.details.setTranslateX(-8);
            this.details.setTooltip(new Tooltip("Details"));
            this.details.setOnMousePressed(e -> {
            });
        }

        public Hyperlink getBarcode() {
            return barcode;
        }

        public void setBarcode(Hyperlink barcode) {
            this.barcode = barcode;
        }

        public String getReceivedBy() {
            return receivedBy.get();
        }

        public StringProperty receivedByProperty() {
            return receivedBy;
        }

        public void setReceivedBy(String receivedBy) {
            this.receivedBy.set(receivedBy);
        }

        public String getLocation() {
            return location.get();
        }

        public StringProperty locationProperty() {
            return location;
        }

        public void setLocation(String location) {
            this.location.set(location);
        }

        public String getDateReturned() {
            return dateReturned.get();
        }

        public StringProperty dateReturnedProperty() {
            return dateReturned;
        }

        public void setDateReturned(String dateReturned) {
            this.dateReturned.set(dateReturned);
        }

        public String getDateComp() {
            return dateComp.get();
        }

        public StringProperty dateCompProperty() {
            return dateComp;
        }

        public void setDateComp(String dateComp) {
            this.dateComp.set(dateComp);
        }

        public String getDateScanned() {
            return dateScanned.get();
        }

        public StringProperty dateScannedProperty() {
            return dateScanned;
        }

        public void setDateScanned(String dateScanned) {
            this.dateScanned.set(dateScanned);
        }

        public String getBox() {
            return box.get();
        }

        public StringProperty boxProperty() {
            return box;
        }

        public void setBox(String box) {
            this.box.set(box);
        }

        public boolean isReleasedToProd() {
            return releasedToProd.get();
        }

        public BooleanProperty releasedToProdProperty() {
            return releasedToProd;
        }

        public void setReleasedToProd(boolean releasedToProd) {
            this.releasedToProd.set(releasedToProd);
        }

        public String getPackedBy() {
            return packedBy.get();
        }

        public StringProperty packedByProperty() {
            return packedBy;
        }

        public void setPackedBy(String packedBy) {
            this.packedBy.set(packedBy);
        }

        public String getReceiptReport() {
            return receiptReport.get();
        }

        public StringProperty receiptReportProperty() {
            return receiptReport;
        }

        public void setReceiptReport(String receiptReport) {
            this.receiptReport.set(receiptReport);
        }

        public int getCount() {
            return count.get();
        }

        public IntegerProperty countProperty() {
            return count;
        }

        public void setCount(int count) {
            this.count.set(count);
        }

        public String getDescription() {
            return description.get();
        }

        public StringProperty descriptionProperty() {
            return description;
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        public String getEmployee() {
            return employee.get();
        }

        public StringProperty employeeProperty() {
            return employee;
        }

        public void setEmployee(String employee) {
            this.employee.set(employee);
        }

        public String getCondition() {
            return condition.get();
        }

        public StringProperty conditionProperty() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition.set(condition);
        }

        public String getStatus() {
            return status.get();
        }

        public StringProperty statusProperty() {
            return status;
        }

        public void setStatus(String status) {
            this.status.set(status);
        }

        public String getLastUpdated() {
            return lastUpdated.get();
        }

        public StringProperty lastUpdatedProperty() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated.set(lastUpdated);
        }

        public Label getDetails() {
            return details;
        }

        public void setDetails(Label details) {
            this.details = details;
        }
    }
//        public Overview(String id, LocalDateTime dateScanned, LocalDateTime dateComp, int imgCount, String description, String employee, String status) {
//            this.imgCount = imgCount;
//            this.number = new Label();
//            this.description = description;
//            this.employee = employee;
//            this.dateScanned = dateScanned;
//            this.dateComp = dateComp;
//            this.status = status;
////            RowConstraints constraints = new RowConstraints();
////            ColumnConstraints col1 = new ColumnConstraints();
////            ColumnConstraints col2 = new ColumnConstraints();
////            ColumnConstraints col3 = new ColumnConstraints();
////            col1.setPercentWidth(50);
////            col2.setPercentWidth(50);
////            this.getColumnConstraints().addAll(col1, col2, col3);
////            this.setVgap(16);
////            this.setAlignment(Pos.CENTER);
////            this.add(new Label("Status:  " + this.status), 0, 0);
////            this.add(new Label("Employee:  " + this.employee), 0, 1);
////            this.add(new Label("Description:  " + this.description), 0, 2);


//    public Overview() {
//    }
//
//    public Overview(String id, LocalDateTime timeStamp) {
////            ColumnConstraints col1 = new ColumnConstraints();
////            ColumnConstraints col2 = new ColumnConstraints();
////            ColumnConstraints col3 = new ColumnConstraints();
////            col1.setPercentWidth(50);
////            col2.setPercentWidth(50);
////            this.getColumnConstraints().addAll(col1, col2, col3);
////            this.number = id;
////            this.timeStamp = timeStamp;
////            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
////            formatter.format(this.timeStamp);
////            Label objIDLbl = new Label(this.number);
////            objIDLbl.setStyle("-fx-font-weight:bold;");
////            this.add(objIDLbl, 0, 0);
////            Label timeStampLbl = new Label("Last Updated: " + this.timeStamp.format(formatter));
////            timeStampLbl.setStyle("-fx-font-size:13;");
////            this.add(timeStampLbl, 1, 0);
//    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        boxCol.setCellValueFactory(new PropertyValueFactory<>("box"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
//        reportCol.setCellValueFactory(new PropertyValueFactory<>("report"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));

    }

    public Label getClose() {
        return close;
    }

    public Label getBarcode() {
        return barcode;
    }


    public Label getStatus() {
        return status;
    }

    public Label getGroupName() {
        return groupName;
    }

    public void setGroupName(Label groupName) {
        this.groupName = groupName;
    }

    public TableView2<Overview> getOvTable() {
        return ovTable;
    }

    public ListView<Activity> getAcList() {
        return acList;
    }

}
