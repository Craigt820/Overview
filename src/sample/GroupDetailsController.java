package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GroupDetailsController implements Initializable {
    @FXML
    private Label close;

    @FXML
    private Label barcode;

    @FXML
    private Label status;

    @FXML
    private Label groupName;

    @FXML
    private TreeView<Overview> ovTree;

    @FXML
    private ListView<Activity> acList;


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
            timeStampLbl.setStyle("-fx-font-size:14; -fx-font-weight:bold;");
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

    public static class Overview extends GridPane {
        private String objID;
        private int pages;
        private String description;
        private String employee;
        private LocalDateTime timeStamp;
        private String status;

        public Overview(String objID, LocalDateTime timeStamp, int pages, String description, String employee, String status) {
            this.pages = pages;
            this.objID = objID;
            this.description = description;
            this.employee = employee;
            this.timeStamp = timeStamp;
            this.status = status;
            RowConstraints constraints = new RowConstraints();
            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            ColumnConstraints col3 = new ColumnConstraints();
            col1.setPercentWidth(50);
            col2.setPercentWidth(50);
            this.getColumnConstraints().addAll(col1, col2, col3);
            this.setVgap(16);
            this.setAlignment(Pos.CENTER);
            this.add(new Label("Status:  " + this.status), 0, 0);
            this.add(new Label("Employee:  " + this.employee), 0, 1);
            this.add(new Label("Description:  " + this.description), 0, 2);
        }

        public Overview() {
        }

        public Overview(String objID, LocalDateTime timeStamp) {
            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            ColumnConstraints col3 = new ColumnConstraints();
            col1.setPercentWidth(50);
            col2.setPercentWidth(50);
            this.getColumnConstraints().addAll(col1, col2, col3);
            this.objID = objID;
            this.timeStamp = timeStamp;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formatter.format(this.timeStamp);
            Label objIDLbl = new Label(this.getObjID());
            objIDLbl.setStyle("-fx-font-weight:bold;");
            this.add(objIDLbl, 0, 0);
            Label timeStampLbl = new Label("Last Updated: " + this.timeStamp.format(formatter));
            timeStampLbl.setStyle("-fx-font-size:13;");
            this.add(timeStampLbl, 1, 0);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public String getEmployee() {
            return employee;
        }

        public void setEmployee(String employee) {
            this.employee = employee;
        }

        public LocalDateTime getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(LocalDateTime timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getObjID() {
            return objID;
        }

        public void setObjID(String objID) {
            this.objID = objID;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ovTree.setRoot(new TreeItem<>());
        ovTree.setShowRoot(false);
        ovTree.setCellFactory(e -> {
            TreeCell<Overview> ovListCell = new TreeCell<Overview>() {
                @Override
                protected void updateItem(Overview item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        this.setText(item.getObjID());
                        this.setGraphic(item);
                        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    } else {
                        this.setText(null);
                        this.setGraphic(null);
                    }
                }
            };
            return ovListCell;
        });
    }

    public Label getClose() {
        return close;
    }

    public void setClose(Label close) {
        this.close = close;
    }

    public Label getBarcode() {
        return barcode;
    }

    public void setBarcode(Label barcode) {
        this.barcode = barcode;
    }

    public Label getStatus() {
        return status;
    }

    public void setStatus(Label status) {
        this.status = status;
    }

    public Label getGroupName() {
        return groupName;
    }

    public void setGroupName(Label groupName) {
        this.groupName = groupName;
    }

    public TreeView<Overview> getOvTree() {
        return ovTree;
    }

    public ListView<Activity> getAcList() {
        return acList;
    }

}
