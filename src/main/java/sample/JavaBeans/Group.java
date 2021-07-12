package sample.JavaBeans;


import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;
import sample.GroupDetailsController;
import sample.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static sample.Main.projectOverviewController;
import static sample.ProjectCollectionTabController.EMPLOYEES;
import static sample.ProjectCollectionTabController.STATUS;

public class Group<T extends Control> {
    private VBox root;
    private int id;
    private Hyperlink barcode;
    private ComboBox<String> status;
    private SearchableComboBox<String> location;
    private StringProperty name;
    private Collection collection;
    private StringProperty started_On;
    private StringProperty completed_On;
    private CheckComboBox<String> employee;
    private IntegerProperty total;
    private ImageView details;
    private ObservableList<Item> itemList;

    public Group(int id, Collection collection, String barcode, String name, List<String> emps, String status, String location, String started_On, String completed_On, int total) {
        this.id = id;
        this.collection = collection;
        this.barcode = new Hyperlink(barcode == null || barcode.isEmpty() ? barcode = "N/A" : barcode);
        this.name = new SimpleStringProperty(name);
        this.itemList = FXCollections.observableArrayList();
        this.employee = new CheckComboBox<String>();
        this.employee.getItems().addAll(EMPLOYEES);
        this.employee.setMaxWidth(600);
        if (emps != null) {
            emps.forEach(e -> {
                this.employee.getCheckModel().check(e);
            });
        }
        this.total = new SimpleIntegerProperty(total);
        this.started_On = new SimpleStringProperty(started_On);
        this.completed_On = new SimpleStringProperty(completed_On);
        this.location = new SearchableComboBox<>(FXCollections.observableArrayList("Rack #1", "Rack #2", "Rack #3", "Rack #4", "Rack #5"));
        this.location.getSelectionModel().select(location);
        this.location.setMaxWidth(600);
        this.location.setEditable(true);
        this.location.setMaxHeight(40);
        this.status = new ComboBox<>(FXCollections.observableArrayList(STATUS));
        this.status.setMaxWidth(600);
        this.status.getSelectionModel().select(status);
        this.details = new ImageView(getClass().getResource("/IMAGES/info.png").toExternalForm());
        this.details.setFitHeight(24);
        this.details.setFitWidth(24);
        this.details.setPickOnBounds(true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public SearchableComboBox<String> getLocation() {
        return location;
    }

    public void setLocation(SearchableComboBox<String> location) {
        this.location = location;
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

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getStarted_On() {
        return started_On.get();
    }

    public StringProperty started_OnProperty() {
        return started_On;
    }

    public void setStarted_On(String started_On) {
        this.started_On.set(started_On);
    }

    public String getCompleted_On() {
        return completed_On.get();
    }

    public StringProperty completed_OnProperty() {
        return completed_On;
    }

    public void setCompleted_On(String completed_On) {
        this.completed_On.set(completed_On);
    }

    public CheckComboBox<String> getEmployee() {
        return employee;
    }

    public void setEmployee(CheckComboBox<String> employee) {
        this.employee = employee;
    }

    public int getTotal() {
        return total.get();
    }

    public IntegerProperty totalProperty() {
        return total;
    }

    public void setTotal(int total) {
        this.total.set(total);
    }

    public ImageView getDetails() {
        return details;
    }

    public void setDetails(ImageView details) {
        this.details = details;
    }

    public ObservableList<Item> getItemList() {
        return itemList;
    }

    public void setItemList(ObservableList itemList) {
        this.itemList = itemList;
    }

    public VBox getRoot() {
        return root;
    }

    public void setRoot(VBox root) {
        this.root = root;
    }
}