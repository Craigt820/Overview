package sample;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.apache.commons.collections4.map.SingletonMap;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;
import org.controlsfx.control.textfield.CustomTextField;
import sample.JavaBeans.Collection;
import sample.JavaBeans.Group;
import sample.utils.Comparators;
import sample.utils.Utils;

import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static sample.Main.CONN;

public class ProjectCollectionTabController implements Initializable {

    @FXML
    private ScrollPane root;

    @FXML
    private Text totalPages, totalEmps;

    @FXML
    private Text itemsComp, itemsTotal;

    @FXML
    private Text groupsComp, groupsTotal;

    @FXML
    private CustomTextField invSearch;

    @FXML
    private TableView2<Group> invTable;

    @FXML
    private TableColumn2<Group, Hyperlink> barcode;

    @FXML
    private TableColumn2<Group, ComboBox<String>> status;

    @FXML
    private TableColumn2<Group, ComboBox<String>> location;

    @FXML
    private TableColumn2<Group, String> name;

    @FXML
    private TableColumn2<Group, CheckComboBox<String>> employee;

    @FXML
    private TableColumn2<Group, Integer> total;

    @FXML
    private TableColumn2<Group, Label> details;

    private sample.JavaBeans.Collection collection;

    public static final ArrayList<String> STATUS = new ArrayList<>(Arrays.asList("Inactive", "Prepped", "Scanned", "Cropped", "QC'd", "Shipped", "Delivered", "Completed"));
    public static ObservableList<String> EMPLOYEES;

    static {
        try {
            EMPLOYEES = getEmployees();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static Map<Integer, Integer> getItems() throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        SingletonMap<Integer, Integer> singletonMap = null;

        try {
            connection = DriverManager.getConnection(CONN, "User", "idi8tangos88admin");
            ps = connection.prepareStatement("SELECT COUNT(total) as totalItems FROM tracking.jib002 union ALL select count(total) as compItems from tracking.jib002 Where completed=1 LIMIT 2");
            set = ps.executeQuery();
            while (set.next()) {
                int totalItems = set.getInt("totalItems");
                set.next();
                int compItems = set.getInt("totalItems");

                singletonMap = new SingletonMap<>(compItems, totalItems);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            set.close();
            ps.close();
            connection.close();
        }
        return singletonMap;
    }

    private static ObservableList<String> getEmployees() throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        ObservableList<String> employees = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection(CONN, "User", "idi8tangos88admin");
            ps = connection.prepareStatement("SELECT id,name,dept_id,roll_id,loc_id from employees");
            set = ps.executeQuery();
            while (set.next()) {
                employees.add(set.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            set.close();
            ps.close();
            connection.close();
        }
        return employees;
    }

    private static int countEmployees() throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        int emps = 0;
        try {
            connection = DriverManager.getConnection(CONN, "User", "idi8tangos88admin");
            ps = connection.prepareStatement("SELECT COUNT(DISTINCT employees) as numEmps FROM tracking.sc_groups WHERE job_id=5;");
            set = ps.executeQuery();
            if (set.next()) {
                emps = set.getInt("numEmps");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            set.close();
            ps.close();
            connection.close();
        }
        return emps;
    }

    private ObservableList<Group> getGroups(Collection collection) throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        ObservableList<Group> groups = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection(CONN, "User", "idi8tangos88admin");
            ps = connection.prepareStatement("SELECT g.id,p.job_id,g.barcode,g.name,g.total,gs.name,g.collection_id,g.employees,g.scanned,g.started_On,g.completed_On FROM sc_groups g INNER JOIN projects p ON g.job_id=p.id INNER JOIN sc_group_status gs ON g.status_id=gs.id WHERE p.job_id=?");
            ps.setString(1, "JIB002");
            set = ps.executeQuery();
            while (set.next()) {
                Group group = new Group(set.getInt("id"), collection, set.getString("barcode"), set.getString("name"), new ArrayList<String>(Arrays.asList(set.getString("employees").split(","))), set.getString("gs.name"), "", set.getInt("total"));
                group.setRoot(root);
                groups.add(group);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            set.close();
            ps.close();
            connection.close();
        }
        return groups;
    }


    @Override
    public void initialize(URL loc, ResourceBundle resources) {
        try {
            totalEmps.setText(String.valueOf(countEmployees()));
            Map<Integer, Integer> items = getItems();
            itemsTotal.setText(Utils.formatNumber(items.values().toArray()[0].toString()));
            itemsComp.setText(Utils.formatNumber(items.keySet().toArray()[0].toString()));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        name.setComparator(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String o1StringPart = o1.replaceAll("\\d", "");
                String o2StringPart = o2.replaceAll("\\d", "");

                if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
                    return extractInt(o1) - extractInt(o2);
                }
                return o1.compareTo(o2);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });

        location.setComparator(new Comparators.ComboCompare<>());
        employee.setComparator(new Comparators.CheckComboCompare());
        status.setComparator(new Comparators.ComboCompare<>());
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        employee.setCellValueFactory(new PropertyValueFactory<>("employee"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
        details.setCellFactory(e -> {
            return new TableCell<Group, Label>() {
                @Override
                protected void updateItem(Label item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item!=null){
                        setGraphic(item);
                    }else{
                        setGraphic(null);
                    }
                }
            };
        });
        invTable.setColumnFixingEnabled(true);
        invTable.setRowFixingEnabled(true);
        CompletableFuture.supplyAsync(() -> {
            ObservableList<Group> groups = null;
            try {
                groups = getGroups(collection);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return groups;
        }).thenApplyAsync(groups -> {
            invTable.getItems().addAll(groups);
            return null;
        }).join();
        totalPages.setText(Utils.formatNumber(String.valueOf(invTable.getItems().stream().mapToInt(e -> e.totalProperty().get()).sum())));
        groupsComp.setText(Utils.formatNumber(String.valueOf(invTable.getItems().stream().filter(e -> e.getStatus().getSelectionModel().getSelectedItem().equals("Completed")).count())));
        groupsTotal.setText(Utils.formatNumber(String.valueOf(invTable.getItems().size())));
        invTable.getFixedColumns().add(details);
        final FilteredList<Group> filteredData = new FilteredList<Group>(invTable.getItems(), p -> true);
        invSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(p -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                final String lowerCaseFilter = newValue.toLowerCase();

                if (p.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (p.getBarcode().getText().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        final SortedList<Group> sortedData = new SortedList<Group>(filteredData);
        sortedData.comparatorProperty().bind((ObservableValue<? extends Comparator<? super Group>>) invTable.comparatorProperty());
        invTable.setItems(sortedData);
    }


    public sample.JavaBeans.Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public ScrollPane getRoot() {
        return root;
    }

}
