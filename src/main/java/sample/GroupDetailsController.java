package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.commons.dbutils.DbUtils;
import org.controlsfx.control.tableview2.TableView2;
import sample.JavaBeans.Group;
import sample.JavaBeans.Item;
import sample.utils.Utils;

import java.awt.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

import static sample.Main.CONN;

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
    private Tab groupTab;

    @FXML
    private TableView2<Item> ovTable;

    @FXML
    private TableColumn<Item, String> nameCol;

    @FXML
    private TableColumn<Item, Integer> totalcol;

    @FXML
    private TableColumn<Item, String> conditionCol;

    @FXML
    private TableColumn<Item, Label> detailsCol;

    @FXML
    private TableColumn<Item, CheckBox> compCol;

    @FXML
    private TableColumn<Item, String> empCol;

    @FXML
    private TableColumn<Item, Integer> nonFeedCol;

    @FXML
    private TableColumn<Item, Label> typeCol;

    @FXML
    private TableColumn<Item, String> commentsCol;

    @FXML
    private TableColumn<Item, String> workstationCol;


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

    public ObservableList<? extends Item> getGroupItems(Group group) throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        ObservableList<Item> group_items = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection(CONN, "User", "idi8tangos88admin");
            ps = connection.prepareStatement("SELECT m.workstation,m.overridden,m.id,g.id as group_id, g.name group_name, m.name as item,m.non_feeder, m.completed, e.name as employee, c.name as collection, m.total, t.name as type,m.conditions,m.comments,m.started_On,m.completed_On FROM JIB002 m INNER JOIN employees e ON m.employee_id = e.id INNER JOIN sc_groups g ON m.group_id = g.id INNER JOIN item_types t ON m.type_id = t.id INNER JOIN sc_collections c ON m.collection_id = c.id WHERE group_id=" + group.getId() + "");
            set = ps.executeQuery();
            while (set.next()) {
                final Item item = new Item(set.getInt("m.id"), group.getCollection(), group, set.getString("item"), set.getInt("m.total"), set.getInt("m.non_feeder"), set.getString("type"), set.getInt("m.completed") == 1, set.getString("employee"), set.getString("m.comments"), set.getString("m.started_On"), set.getString("m.completed_On"), set.getString("m.workstation"), Utils.intToBoolean(set.getInt("m.overridden")));
                String condition = set.getString("m.conditions");
                if (condition != null && !condition.isEmpty()) {
                    String[] splitConditions = condition.split(", ");
                    item.getConditions().setAll(Arrays.asList(splitConditions));
                }
                group_items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }

        return group_items;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalcol.setCellValueFactory(new PropertyValueFactory<>("total"));
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("conditions"));
        compCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        compCol.setComparator(new Comparator<CheckBox>() {
            @Override
            public int compare(CheckBox o1, CheckBox o2) {
                return Boolean.compare(o1.isSelected(), o2.isSelected());
            }
        });
        empCol.setCellValueFactory(new PropertyValueFactory<>("employee"));
        nonFeedCol.setCellValueFactory(new PropertyValueFactory<>("nonFeeder"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        commentsCol.setCellValueFactory(new PropertyValueFactory<>("comments"));
        workstationCol.setCellValueFactory(new PropertyValueFactory<>("workstation"));
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

    public TableView2<Item> getOvTable() {
        return ovTable;
    }

    public ListView<Activity> getAcList() {
        return acList;
    }

    public Tab getGroupTab() {
        return groupTab;
    }

}
