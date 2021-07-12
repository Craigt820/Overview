package sample.JavaBeans;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.CheckComboBox;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static sample.ProjectCollectionTabController.CONDITION;

public class Item {

    public Image folderIcon = new Image(Item.class.getResourceAsStream("/IMAGES/folder.png"));
    public Image fileIcon = new Image(Item.class.getResourceAsStream("/IMAGES/book.png"));
    public BooleanProperty overridden; //Tracks if the user want to complete a non-existing item (Physical material may not exist)
    public BooleanProperty exists; //File/Folder
    public SimpleIntegerProperty id;
    public SimpleStringProperty started_On;
    public SimpleStringProperty completed_On;
    public SimpleStringProperty employee;
    public Label delete;
    public SimpleStringProperty name;
    public ImageView type;
    public SimpleBooleanProperty completed;
    public ImageView details;
    public SimpleStringProperty comments;
    public CheckComboBox<String> condition;
    public List<String> scanners;
    public Collection collection;
    public Group group;
    public SimpleIntegerProperty nonFeeder;
    public SimpleIntegerProperty total;
    public Path location;
    public List<Image> previews;
    public SimpleMapProperty<String, String> projColumns;
    public SimpleStringProperty workstation;

    public SimpleIntegerProperty countProperty() {
        if (total == null) {
            total = new SimpleIntegerProperty(this, "count");
        }
        return total;
    }


//    public static void removeItem(Item item) {
//        Connection connection = null;
//        ResultSet set = null;
//        PreparedStatement ps = null;
//        int key = 0;
//        try {
//            connection = ConnectionHandler.createDBConnection();
//            ps = connection.prepareStatement("DELETE FROM `" + Main.jsonHandler.getSelJobID() + "` WHERE id=" + item.getId() + "");
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            DbUtils.closeQuietly(ps);
//            DbUtils.closeQuietly(connection);
//        }
//    }

    public Item(int id, Collection collection, Group group, String name, int total, int non_feeder, String type, boolean completed, String employee, String comments, String startedOn, String completedOn, String workstation, boolean overridden) {
        this();
        this.overridden.set(overridden);
        this.exists = new SimpleBooleanProperty(false);
        this.id.set(id);
        this.group = group;
        this.collection = collection;
        this.name.set(name);
        this.total.set(total);
        this.nonFeeder.set(non_feeder);
        this.employee = new SimpleStringProperty(employee);
        initType(type);
        this.completed.set(completed);
        this.comments.set(comments == null || comments.isEmpty() ? comments = "" : comments);
        this.started_On.set(startedOn);
        this.completed_On.set(completedOn);
        this.workstation.set(workstation == null || workstation.isEmpty() ? workstation = "N/A" : workstation);
    }

    public void initType(String type) {
        switch (type) {
            case "Folder":
            case "Root":
                Platform.runLater(() -> {
                    this.type.setImage(folderIcon);
                });
                break;
            case "Multi-Paged":
                Platform.runLater(() -> {
                    this.type.setImage(fileIcon);
                });
                break;
        }
        this.type.setFitWidth(24);
        this.type.setFitHeight(24);
        Tooltip.install(this.type, new Tooltip(type));
    }

    public Item() {
        this.overridden = new SimpleBooleanProperty();
        this.details = new ImageView(getClass().getResource("/IMAGES/info.png").toExternalForm());
        this.details.setFitHeight(24);
        this.details.setFitWidth(24);
        Tooltip.install(this.details, new Tooltip("Details"));
        this.details.getStyleClass().add("view");
        this.id = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty();
        this.nonFeeder = new SimpleIntegerProperty(0);
        this.completed = new SimpleBooleanProperty(false);
//        this.completed.setContextMenu(new ContextMenu());
//        final MenuItem override = new MenuItem("Override");
//        override.setOnAction(e -> {
//            this.overridden.set(true);
//            this.exists.set(true);
//            this.completed.setSelected(true);
////            updateSelected(this);
//        });
//        this.completed.getContextMenu().getItems().add(override);
        this.comments = new SimpleStringProperty();
        this.type = new ImageView();
        this.delete = new Label("Remove");
        this.delete.getStyleClass().add("detailBtn");
        this.delete.setStyle("-fx-font-size:1em;-fx-text-fill: red; -fx-opacity: .8;");
        this.total = new SimpleIntegerProperty(0);
        this.condition = new CheckComboBox<>(CONDITION);
        this.projColumns = new SimpleMapProperty<>();
        this.projColumns.set(FXCollections.observableHashMap());
        this.started_On = new SimpleStringProperty();
        this.completed_On = new SimpleStringProperty();
        this.workstation = new SimpleStringProperty();
        this.previews = new ArrayList<>();
    }

    public boolean isExists() {
        return exists.get();
    }

    public BooleanProperty existsProperty() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists.set(exists);
    }

    public boolean isOverridden() {
        return overridden.get();
    }

    public BooleanProperty overriddenProperty() {
        return overridden;
    }

    public void setOverridden(boolean overridden) {
        this.overridden.set(overridden);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public Label getDelete() {
        return delete;
    }

    public void setDelete(Label delete) {
        this.delete = delete;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Path getLocation() {
        return location;
    }

    public void setLocation(Path location) {
        this.location = location;
    }

    public ImageView getType() {
        return type;
    }

    public void setType(ImageView type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    public ImageView getDetails() {
        return details;
    }

    public void setDetails(ImageView details) {
        this.details = details;
    }

    public String getComments() {
        return comments.get();
    }

    public SimpleStringProperty commentsProperty() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments.set(comments);
    }

    public Image getFolderIcon() {
        return folderIcon;
    }

    public void setFolderIcon(Image folderIcon) {
        this.folderIcon = folderIcon;
    }

    public Image getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(Image fileIcon) {
        this.fileIcon = fileIcon;
    }

    public CheckComboBox<String> getCondition() {
        return condition;
    }

    public void setCondition(CheckComboBox<String> condition) {
        this.condition = condition;
    }

    public ObservableMap<String, String> getProjColumns() {
        return projColumns.get();
    }

    public SimpleMapProperty<String, String> projColumnsProperty() {
        return projColumns;
    }

    public void setProjColumns(ObservableMap<String, String> projColumns) {
        this.projColumns.set(projColumns);
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public List<String> getScanners() {
        return scanners;
    }

    public void setScanners(List<String> scanners) {
        this.scanners = scanners;
    }

    public Collection getCollection() {
        return collection;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getStarted_On() {
        return started_On.get();
    }

    public SimpleStringProperty started_OnProperty() {
        return started_On;
    }

    public void setStarted_On(String started_On) {
        this.started_On.set(started_On);
    }

    public String getCompleted_On() {
        return completed_On.get();
    }

    public SimpleStringProperty completed_OnProperty() {
        return completed_On;
    }

    public void setCompleted_On(String completed_On) {
        this.completed_On.set(completed_On);
    }

    public int getNonFeeder() {
        return nonFeeder.get();
    }

    public SimpleIntegerProperty nonFeederProperty() {
        return nonFeeder;
    }

    public void setNonFeeder(int nonFeeder) {
        this.nonFeeder.set(nonFeeder);
    }

    public int getTotal() {
        return total.get();
    }

    public SimpleIntegerProperty totalProperty() {
        return total;
    }

    public void setTotal(int total) {
        this.total.set(total);
    }

    public String getEmployee() {
        return employee.get();
    }

    public SimpleStringProperty employeeProperty() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee.set(employee);
    }

    public String getWorkstation() {
        return workstation.get();
    }

    public SimpleStringProperty workstationProperty() {
        return workstation;
    }

    public void setWorkstation(String workstation) {
        this.workstation.set(workstation);
    }

    public List<Image> getPreviews() {
        return previews;
    }

    public void setPreviews(List<Image> previews) {
        this.previews = previews;
    }

    @Override
    public String toString() {
        return "Item{" +
                "overridden=" + overridden +
                ", exists=" + exists +
                ", id=" + id +
                ", started_On=" + started_On +
                ", completed_On=" + completed_On +
                ", delete=" + delete +
                ", name=" + name +
                ", type=" + type +
                ", completed=" + completed +
                ", details=" + details +
                ", comments=" + comments +
                ", conditions=" + condition +
                ", scanners=" + scanners +
                ", collection=" + collection +
                ", group=" + group +
                ", nonFeeder=" + nonFeeder +
                ", total=" + total +
                ", location=" + location +
                ", previews=" + previews +
                ", projColumns=" + projColumns +
                ", workstation=" + workstation +
                '}';
    }
}