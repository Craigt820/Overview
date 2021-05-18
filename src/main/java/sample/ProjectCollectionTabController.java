package sample;

import impl.org.controlsfx.skin.CheckComboBoxSkin;
import impl.org.controlsfx.skin.SearchableComboBoxSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.commons.collections4.map.SingletonMap;
import org.apache.commons.dbutils.DbUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;
import org.controlsfx.control.textfield.CustomTextField;
import sample.JavaBeans.Collection;
import sample.JavaBeans.Group;
import sample.utils.Comparators;
import sample.utils.Utils;

import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sample.Main.CONN;
import static sample.utils.Utils.dateTextFormatter;

public class ProjectCollectionTabController implements Initializable {

    @FXML
    private VBox root;

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
    private TableColumn2<Group, String> started_On;

    @FXML
    private TableColumn2<Group, String> comp_On;

    @FXML
    private TableColumn2<Group, SearchableComboBox<String>> location;

    @FXML
    private TableColumn2<Group, String> name;

    @FXML
    private TableColumn2<Group, CheckComboBox<String>> employee;

    @FXML
    private TableColumn2<Group, Integer> total;

    @FXML
    private TableColumn2<Group, Label> details;

    private sample.JavaBeans.Collection collection;

    public static final ArrayList<String> STATUS = new ArrayList<>(Arrays.asList("Inactive", "Prepping", "Scanning", "Cropping", "QC", "Shipment", "Completed"));
    public static ObservableList<String> EMPLOYEES;

    static {
        try {
            EMPLOYEES = getEmployees();
        } catch (SQLException ex) {
            ex.printStackTrace();
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
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
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

    private static ObservableList<Employee> getActiveEmps() throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        ObservableList<Employee> emps = FXCollections.observableArrayList();
        try {
            connection = DriverManager.getConnection(CONN, "User", "idi8tangos88admin");
            ps = connection.prepareStatement("SELECT distinct employees as emps,SUM(total) as sumTotal FROM tracking.sc_groups WHERE job_id=5 GROUP BY employees;");
            set = ps.executeQuery();
            while (set.next()) {
                emps.add(new Employee(set.getString("emps"), set.getInt("sumTotal")));
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
                Group group = new Group(set.getInt("id"), collection, set.getString("barcode"), set.getString("name"), new ArrayList<String>(Arrays.asList(set.getString("employees").split(","))), set.getString("gs.name"), "", set.getString("g.started_On"), set.getString("g.completed_On"), set.getInt("total"));
                group.setRoot(root);
                groups.add(group);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
        return groups;
    }

    public static class Employee {
        private String name;
        private int total;

        public Employee(String name, int total) {
            this.name = name;
            this.total = total;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }


    @Override
    public void initialize(URL loc, ResourceBundle resources) {
        try {
            initOverview();
        } catch (SQLException ex) {
            ex.printStackTrace();
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
        employee.setComparator(new Comparators.CheckComboCompare());
        employee.setCellFactory(e -> {
            return new TableCell<Group, CheckComboBox<String>>() {
                @Override
                protected void updateItem(CheckComboBox<String> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setGraphic(item);
                    } else {
                        setGraphic(null);
                        setText(null);
                    }
                }
            };
        });
        status.setComparator(new Comparators.ComboCompare<>());
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setCellFactory(e -> {
            return new TableCell<Group, ComboBox<String>>() {
                @Override
                protected void updateItem(ComboBox<String> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {

                        Rectangle rectangle = new Rectangle(88, 26);
                        rectangle.setArcHeight(16);
                        rectangle.setArcWidth(16);
                        switch (item.getSelectionModel().getSelectedItem()) {
                            case "Scanning":
                                rectangle.setFill(Color.rgb(39, 100, 186));
                                break;
                            case "Completed":
                                rectangle.setFill(Color.rgb(43, 186, 30));
                                break;
                            case "Cropping":
                                rectangle.setFill(Color.rgb(186, 39, 20));
                                break;
                            case "Inactive":
                                rectangle.setFill(Color.BLACK);
                                break;
                        }

                        setStyle("-fx-font-weight:bold;-fx-text-fill:white;");
                        setGraphic(rectangle);
                        setText(item.getSelectionModel().getSelectedItem());
                        setContentDisplay(ContentDisplay.CENTER);
                        setOnMouseClicked(e -> {
                            setGraphic(item);
                            setText("");
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        });
                    }
                }
            };
        });
        started_On.setCellValueFactory(new PropertyValueFactory<>("started_On"));
        started_On.setCellFactory(e -> {
            return new TableCell<Group, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !item.isEmpty()) {
                        setText(item);
                        setStyle("-fx-opacity:.9;");
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
        });
        comp_On.setCellValueFactory(new PropertyValueFactory<>("completed_On"));
        comp_On.setCellFactory(e -> {
            return new TableCell<Group, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !item.isEmpty()) {
                        setText(item);
                        setStyle("-fx-opacity:.9;");
                        Group group = (Group) getTableRow().getItem();
                        if (group != null) {
                            String completed = getText();
                            String started = group.getStarted_On();
                            if (started != null && !started.isEmpty()) {
                                try {
                                    LocalDateTime fComp = Utils.formatDateTime(completed);
                                    LocalDateTime fStarted = Utils.formatDateTime(started);
                                    long diff = TimeUnit.DAYS.convert(fComp.getDayOfYear() - fStarted.getDayOfYear(), TimeUnit.DAYS);
                                    int days = 0;
                                    if (diff > 1) {
                                        for (int i = (int) diff; i > 1; i--) {
                                            int dayOfWeek = fComp.minusDays(i).getDayOfWeek().getValue();
                                            boolean isWeekend = dayOfWeek >= 6;
                                            if (isWeekend) {
                                                days += 1;
                                            }
                                        }
                                    }

                                    Duration duration = Duration.between(fStarted, fComp.minusDays(days));
                                    setTooltip(new Tooltip(("Work Days: ") + dateTextFormatter(TimeUnit.DAYS.convert(duration.getSeconds(), TimeUnit.SECONDS), TimeUnit.HOURS.convert(duration.getSeconds(), TimeUnit.SECONDS) % 24, TimeUnit.MINUTES.convert(duration.getSeconds(), TimeUnit.SECONDS) % 60, TimeUnit.SECONDS.convert(duration.getSeconds(), TimeUnit.SECONDS) % 60)));
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                    } else {
                        setText("-");
                        setGraphic(null);
                        setTooltip(null);
                    }
                }
            };
        });
        name.setCellFactory(e -> {
            return new TableCell<Group, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !item.isEmpty()) {
                        setText(item.toString());
                        setStyle("-fx-font-size:1.16em;-fx-font-weight:bold;");
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
        });

        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        location.setEditable(true);
        location.setCellFactory(e -> {
            return new TableCell<Group, SearchableComboBox<String>>() {

                @Override
                public void startEdit() {
                    super.startEdit();

                }

                @Override
                public void commitEdit(SearchableComboBox<String> newValue) {
                    super.commitEdit(newValue);
                }

                @Override
                protected void updateItem(SearchableComboBox<String> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setGraphic(item);
                        item.skinProperty().addListener(new ChangeListener<Skin<?>>() {
                            @Override
                            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                                SearchableComboBoxSkin skin = (SearchableComboBoxSkin) newValue;
                                CustomTextField searchField = (CustomTextField) skin.getChildren().get(1);
                                searchField.textProperty().addListener(new ChangeListener<String>() {
                                    @Override
                                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                        System.out.println(newValue);
                                        if (newValue.equals(" ")) {
                                            System.out.println(getText());
                                            item.getItems().add(getText());
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        setGraphic(null);
                        setText(null);
                    }

                }
            };
        });
        barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        employee.setCellValueFactory(new PropertyValueFactory<>("employee"));
        employee.setCellFactory(e -> {
            return new TableCell<Group, CheckComboBox<String>>() {
                @Override
                protected void updateItem(CheckComboBox<String> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        item.skinProperty().addListener(new ChangeListener<Skin<?>>() {
                            @Override
                            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                                CheckComboBoxSkin boxSkin = (CheckComboBoxSkin) newValue;
                                //                                box.getButtonCell().setGraphic();
                                ObservableList<Node> nodes = boxSkin.getChildren();
                                System.out.println(nodes);
                            }
                        });
                        setGraphic(item);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
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
//        invTable.getFixedColumns().add(details);
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

    private void initOverview() throws SQLException {
        ObservableList<Employee> activeEmps = getActiveEmps();
        activeEmps.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee o1, Employee o2) {
                return Integer.compare(o1.getTotal(), o2.getTotal());
            }
        });
        StringBuilder builder = new StringBuilder();
        List<String> empList = IntStream.range(0, activeEmps.size()).mapToObj(idx -> {
            return (idx + 1) + " " + activeEmps.get(idx).name + " " + activeEmps.get(idx).total;
        }).collect(Collectors.toList());
        empList.forEach(e -> builder.append(e).append("\n"));
        totalEmps.setText(String.valueOf(activeEmps.size()));
        Tooltip.install(totalEmps, new Tooltip(builder.toString()));
        Map<Integer, Integer> items = getItems();
        itemsTotal.setText(Utils.formatNumber(items.values().toArray()[0].toString()));
        itemsComp.setText(Utils.formatNumber(items.keySet().toArray()[0].toString()));
    }


    public sample.JavaBeans.Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public VBox getRoot() {
        return root;
    }

}
