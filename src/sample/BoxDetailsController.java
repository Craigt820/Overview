package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BoxDetailsController implements Initializable {
    @FXML
    private Label close;

    @FXML
    private Label barcode;

    @FXML
    private Label status;

    @FXML
    private Label boxName;

    @FXML
    private ListView<?> ovList;

    @FXML
    private ListView<Activity> acList;

//    public class Overview{
//        private
//    }

    public static class Activity extends VBox {
        private String objID;
        private LocalDateTime timeStamp;
        private String employee;
        private String description;

        public Activity(String objID, LocalDateTime timeStamp, String employee, String description) {
            this.setPadding(new Insets(8, 8, 8, 8));
            this.objID = objID;
            this.setSpacing(3);
            this.setStyle("-fx-translate-y: -8;");
            this.timeStamp = timeStamp;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.employee = employee;
            this.description = description;
            Hyperlink idLbl = new Hyperlink(this.objID);
            idLbl.setStyle("-fx-font-size:16; -fx-font-weight:bold;");
            Label timeStampLbl = new Label(this.timeStamp.format(formatter));
            timeStampLbl.setStyle("-fx-font-size:14; -fx-font-weight:bold;");
            Label empLbl = new Label(this.getEmployee());
            empLbl.setStyle("-fx-padding:0 0 0 8; -fx-font-size:15;");
            Label descLbl = new Label(this.getDescription());
            descLbl.setStyle("-fx-padding:0 0 0 8; -fx-font-size:15;");

            descLbl.setWrapText(true);
            ColumnConstraints col1 = new ColumnConstraints();
            ColumnConstraints col2 = new ColumnConstraints();
            ColumnConstraints col3 = new ColumnConstraints();
            col1.setPercentWidth(70);
            col2.setPercentWidth(30);

            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.add(idLbl, 0, 0);
            gridPane.add(timeStampLbl, 1, 0);
            gridPane.add(empLbl, 0, 1);
            gridPane.add(descLbl, 0, 2);

            gridPane.getColumnConstraints().addAll(col1, col2);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

    public Label getBoxName() {
        return boxName;
    }

    public void setBoxName(Label boxName) {
        this.boxName = boxName;
    }

    public ListView<?> getOvList() {
        return ovList;
    }

    public ListView<Activity> getAcList() {
        return acList;
    }

}
