package sample;

import impl.org.controlsfx.skin.SearchableComboBoxSkin;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;
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
import sample.utils.DBUtils;
import sample.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sample.ConnectionHandler.createDBConnection;
import static sample.Main.projectOverviewController;
import static sample.utils.Utils.dateTextFormatter;

public class ProjectCollectionTabController extends DBUtils<Group> implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private Text totalPages, totalEmps;

    @FXML
    private ImageView empDetails;

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
    private TableColumn2<Group, ImageView> details;

    private ProjectCollectionTabController controller;

    private sample.JavaBeans.Collection collection;

    public static final ArrayList<String> STATUS = new ArrayList<>(Arrays.asList("Inactive", "Prepping", "Scanning", "Cropping", "QC", "Shipment", "Completed"));
    public static ObservableList<String> CONDITION;
    public static ObservableList<String> EMPLOYEES;

    static {
        try {
            EMPLOYEES = getEmployees();
            CONDITION = getConditions();
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
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT COUNT(total) as totalItems FROM `" + ProjectOverviewController.job.getName() + "` union ALL select count(total) as compItems from `" + ProjectOverviewController.job.getName() + "` Where completed=1 LIMIT 2");
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

    private static ObservableList<String> getConditions() throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        ObservableList<String> employees = FXCollections.observableArrayList();

        try {
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT name from item_condition");
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

    private static ObservableList<String> getEmployees() throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        ObservableList<String> employees = FXCollections.observableArrayList();

        try {
            connection = createDBConnection();
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
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT DISTINCT e.name as emps,SUM(total) as sumTotal FROM `" + ProjectOverviewController.job.getName() + "_g` g INNER JOIN employees e ON g.employee = e.id GROUP BY employee;");
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
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT g.id,g.barcode,g.name,g.total,gs.name,g.collection_id,e.name,g.scanned,g.started_On,g.completed_On FROM `" + ProjectOverviewController.job.getName() + "_g` g LEFT JOIN sc_group_status gs ON g.status_id=gs.id LEFT JOIN employees e ON g.employee = e.id");
            set = ps.executeQuery();
            while (set.next()) {
                List<String> empList = null;
                String emps = set.getString("e.name");
                if (emps != null && !emps.isEmpty()) {
                    empList = Arrays.asList(emps.split(","));
                }
                Group group = new Group(set.getInt("g.id"), collection, set.getString("g.barcode"), set.getString("g.name"), empList, set.getString("gs.name"), "", set.getString("g.started_On"), set.getString("g.completed_On"), set.getInt("g.total"));
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

    @Override
    public void updateItem(Group item, String sql) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = ConnectionHandler.createDBConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Main.LOGGER.log(Level.SEVERE, "There was an error updating an item!", e);

        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
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
            controller = this;
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
                        Rectangle rectangle = getStatusRect(item);
                        setText(item.getSelectionModel().getSelectedItem());
                        setContentDisplay(ContentDisplay.CENTER);

                        //Update graphic back to previously selected value - Rectangle
                        getItem().showingProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                if (!newValue) {
                                    setText(item.getSelectionModel().getSelectedItem());
                                    setContentDisplay(ContentDisplay.CENTER);
                                    setGraphic(rectangle);
                                }
                            }
                        });

                        setOnMouseClicked(e -> {
                            setGraphic(item);
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            getItem().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                                @Override
                                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                    updateItem(item, false);
                                    Rectangle rectangle = getStatusRect(item);
                                    setGraphic(rectangle);
                                    controller.updateItem((Group) getTableRow().getItem(), "UPDATE `" + ProjectOverviewController.job.getName() + "_g` SET status_id=(SELECT id FROM sc_group_status WHERE name='" + newValue + "') WHERE id=?");
                                }
                            });
                        });

                    } else {
                        setGraphic(null);
                    }
                }

                private Rectangle getStatusRect(ComboBox<String> item) {
                    Rectangle rectangle = new Rectangle(116, 32);
                    rectangle.setArcHeight(16);
                    rectangle.setArcWidth(16);
                    switch (item.getSelectionModel().getSelectedItem()) {
                        case "Prepping":
                            rectangle.setFill(Color.rgb(100, 0, 142));
                            break;
                        case "Scanning":
                            rectangle.setFill(Color.rgb(30, 77, 143));
                            break;
                        case "Cropping":
                            rectangle.setFill(Color.rgb(0, 143, 74));
                            break;
                        case "QC":
                            rectangle.setFill(Color.rgb(143, 80, 0));
                            break;
                        case "Completed":
                            rectangle.setFill(Color.rgb(25, 143, 0));
                            break;
                        case "Inactive":
                            rectangle.setFill(Color.LIGHTGRAY);
                            break;
                    }
                    setStyle("-fx-font-size:12;-fx-font-weight:bold;-fx-text-fill:white;");
                    setGraphic(rectangle);
                    return rectangle;
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
                        setStyle("-fx-opacity:1;-fx-font-size:1em;-fx-font-weight:bold;");
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
                        setStyle("-fx-opacity:1;-fx-font-size:1em;-fx-font-weight:bold;");
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
                                    Tooltip tooltip = new Tooltip(("Work Days: ") + dateTextFormatter(TimeUnit.DAYS.convert(duration.getSeconds(), TimeUnit.SECONDS), TimeUnit.HOURS.convert(duration.getSeconds(), TimeUnit.SECONDS) % 24, TimeUnit.MINUTES.convert(duration.getSeconds(), TimeUnit.SECONDS) % 60, TimeUnit.SECONDS.convert(duration.getSeconds(), TimeUnit.SECONDS) % 60));
                                    tooltip.setStyle("-fx-text-fill:white;");
                                    setTooltip(tooltip);
                                    getTooltip().setAutoHide(false);
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                    } else {
                        setText(null);
                        setGraphic(null);
                        setTooltip(null);
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
//                        item.skinProperty().addListener(new ChangeListener<Skin<?>>() {
//                            @Override
//                            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
//                                CheckComboBoxSkin boxSkin = (CheckComboBoxSkin) newValue;
//                                //                                box.getButtonCell().setGraphic();
//                                ObservableList<Node> nodes = boxSkin.getChildren();
//                                System.out.println(nodes);
//                            }
//                        });
                        setGraphic(item);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });

        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        total.setCellFactory(e -> {
            return new TableCell<Group, Integer>() {

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(Utils.formatNumber(item.toString()));
                    } else {
                        setText(null);
                        setGraphic(null);
                    }
                }
            };
        });

        details.setCellValueFactory(new PropertyValueFactory<>("details"));
        details.setCellFactory(e -> {
            return new TableCell<Group, ImageView>() {
                @Override
                protected void updateItem(ImageView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setGraphic(item);
                        setTooltip(new Tooltip("Details"));
                        getTooltip().setStyle("-fx-text-fill:white;");
                        setOnMousePressed(e -> {
                            Group group = (Group) getTableRow().getItem();
                            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GroupDetails.fxml"));
                            try {
                                VBox box = loader.load();
                                projectOverviewController.getGroupDetailDrawer().setSidePane(box);
                                projectOverviewController.getGroupDetailDrawer().setDefaultDrawerSize(root.getWidth());
                                projectOverviewController.getGroupDetailDrawer().setMinWidth(root.getWidth());
                                projectOverviewController.getGroupDetailDrawer().setMinHeight(root.getHeight());
                                GroupDetailsController detailsController = loader.getController();
                                detailsController.getGroupTab().setText(group.getName());
//                GroupDetailsController.Activity b1 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Selected");
//                GroupDetailsController.Activity b2 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Being Scanned");
//                GroupDetailsController.Activity b3 = new GroupDetailsController.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Completed");
//                GroupDetailsController.Activity b4 = new GroupDetailsController.Activity("#84333", LocalDateTime.now(), "Cindy M.", "Item Edited");
//                detailsController.getAcList().getItems().addAll(b1, b2, b3, b4);
                                CompletableFuture.runAsync(() -> {
                                    try {
                                        ProgressIndicator indicator = new ProgressIndicator();
                                        detailsController.getOvTable().setPlaceholder(indicator);
                                        group.getItemList().setAll(detailsController.getGroupItems(group));
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }

                                }).thenRunAsync(() -> {
                                    Platform.runLater(() -> {
                                        try {
                                            detailsController.getItemsTotal().setText(String.valueOf(group.getItemList().size()));
                                            detailsController.getItemsComp().setText(String.valueOf(detailsController.getCompItems(group)));
                                            detailsController.getTotalPages().setText(Utils.formatNumber(String.valueOf(detailsController.getTotalPages(group))));
                                            detailsController.getTotalEmps().setText(String.valueOf(group.getEmployee().getCheckModel().getCheckedItems().size()));
                                        } catch (SQLException exception) {
                                            exception.printStackTrace();
                                        }
                                    });
                                }).thenRunAsync(() -> {
                                    detailsController.getOvTable().getItems().setAll(group.getItemList());
                                    detailsController.getOvTable().refresh();
                                });

                                detailsController.getClose().setOnMouseClicked(e3 -> {
                                    projectOverviewController.getGroupDetailDrawer().close();
                                    projectOverviewController.getGroupDetailDrawer().setOnDrawerClosed(e4 -> {
                                        projectOverviewController.getGroupDetailDrawer().setPrefWidth(0);
                                        projectOverviewController.getGroupDetailDrawer().setMinWidth(0);
                                    });
                                });
                                projectOverviewController.getGroupDetailDrawer().open();

                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        });

                    } else {
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
        }).thenRunAsync(() -> {
            try {
                totalPages.setText(Utils.formatNumber(String.valueOf(getPgSubTotal())));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            groupsComp.setText(Utils.formatNumber(String.valueOf(invTable.getItems().stream().filter(e -> e.getStatus().getSelectionModel().getSelectedItem().equals("Completed")).count())));
            groupsTotal.setText(Utils.formatNumber(String.valueOf(invTable.getItems().size())));
        }).join();

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


    public int getPgSubTotal() throws SQLException {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        int compItems = 0;
        try {
            connection = createDBConnection();
            ps = connection.prepareStatement("SELECT SUM(total) as totalpgs FROM `" + ProjectOverviewController.job.getName() + "`");
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


    private void initOverview() throws SQLException {
        //Employee Handling - With Totals
        ObservableList<Employee> activeEmps = getActiveEmps();
        activeEmps.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee o1, Employee o2) {
                return Integer.compare(o1.getTotal(), o2.getTotal());
            }
        });
        Collections.reverse(activeEmps);
        StringBuilder builder = new StringBuilder();
        List<String> empList = IntStream.range(0, activeEmps.size()).mapToObj(idx -> {
            return (idx + 1) + ". " + activeEmps.get(idx).name + " - " + Utils.formatNumber(String.valueOf(activeEmps.get(idx).total));
        }).collect(Collectors.toList());
        empList.forEach(e -> builder.append(e).append("\n"));
        totalEmps.setText(String.valueOf(activeEmps.size()));
        Tooltip tooltip = new Tooltip(builder.toString());
        tooltip.setAutoHide(false);
        Tooltip.install(empDetails, tooltip);
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
