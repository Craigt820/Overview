package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.commons.dbutils.DbUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.tableview2.TableView2;
import sample.JavaBeans.Group;
import sample.JavaBeans.Item;
import sample.utils.Utils;


import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

import static sample.ConnectionHandler.CONN;
import static sample.ConnectionHandler.createDBConnection;
import static sample.ProjectOverviewController.*;

public class GroupDetailsController implements Initializable {

    @FXML
    private VBox box;

    @FXML
    private Text totalPages, totalEmps;

    @FXML
    private ImageView empDetails;

    @FXML
    private Text itemsComp, itemsTotal;

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
    private TableColumn<Item, CheckComboBox> conditionCol;

    @FXML
    private TableColumn<Item, ImageView> detailsCol;

    @FXML
    private TableColumn<Item, Boolean> compCol;
    @FXML
    private TableColumn<Item, Integer> idCol;
    @FXML
    private TableColumn<Item, String> empCol;

    @FXML
    private TableColumn<Item, Integer> nonFeedCol;

    @FXML
    private TableColumn<Item, ImageView> typeCol;

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
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT m.workstation,m.overridden,m.id, m.`" + job.getUid() + "` as item, m.non_feeder, m.completed, e.name as employee, c.name as collection, m.total, t.name as type,m.conditions,m.comments,m.started_On,m.completed_On FROM " + job.getName() + " m LEFT JOIN employees e ON m.employee_id = e.id LEFT JOIN item_types t ON m.type_id = t.id LEFT JOIN sc_collections c ON m.collection_id = c.id WHERE `" + job.getGroupCol() + "`='" + group.getName() + "'");
            set = ps.executeQuery();
            while (set.next()) {
                String type = set.getString("type");
                if (set.wasNull()) {
                    type = "";
                }
                String collection = set.getString("collection");
                if (set.wasNull()) {
                    collection = "";
                }

                String employee = set.getString("employee");
                if (set.wasNull()) {
                    employee = "";
                }

                final Item item = new Item(set.getInt("m.id"), group.getCollection(), group, set.getString("item"), set.getInt("m.total"), set.getInt("m.non_feeder"), type, set.getInt("m.completed") == 1, employee, set.getString("m.comments"), set.getString("m.started_On"), set.getString("m.completed_On"), set.getString("m.workstation"), Utils.intToBoolean(set.getInt("m.overridden")));
                String condition = set.getString("m.conditions");
                if (condition != null && !condition.isEmpty()) {
                    String[] splitConditions = condition.split(", ");
                    Arrays.stream(splitConditions).forEach(e -> {
                        item.getCondition().getCheckModel().check(e);
                    });
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


    public int getCompItems(Group group) throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        int compItems = 0;
        try {
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT COUNT(`" + job.getUid() + "`) as compItems FROM " + job.getName() + " WHERE group_id=" + group.getId() + " AND completed=1");
            set = ps.executeQuery();
            if (set.next()) {
                compItems = set.getInt("compItems");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }

        return compItems;
    }


    public int getTotalPages(Group group) throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        int compItems = 0;
        try {
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT SUM(total) as totalpgs FROM " + job.getName() + " WHERE group_id=" + group.getId() + "");
            set = ps.executeQuery();
            if (set.next()) {
                compItems = set.getInt("totalpgs");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }

        return compItems;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalcol.setCellValueFactory(new PropertyValueFactory<>("total"));
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));
        ovTable.setEditable(true);
        compCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        compCol.setCellFactory(CheckBoxTableCell.forTableColumn(compCol));
        empCol.setCellFactory(e -> {
            return new TableCell<Item, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !item.isEmpty()) {
                        setText(item);
                        setStyle("-fx-opacity:.8;");
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
        });
        commentsCol.setCellFactory(e -> {
            return new TableCell<Item, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !item.isEmpty()) {
                        setText(item);
                        setStyle("-fx-opacity:.8;");
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
        });
        workstationCol.setCellFactory(e -> {
            return new TableCell<Item, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !item.isEmpty()) {
                        setText(item);
                        setStyle("-fx-opacity:.8;");
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
        });


        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
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

    public Text getTotalPages() {
        return totalPages;
    }


    public Text getTotalEmps() {
        return totalEmps;
    }


    public Text getItemsComp() {
        return itemsComp;
    }


    public Text getItemsTotal() {
        return itemsTotal;
    }


}
