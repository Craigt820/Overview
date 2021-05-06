//package sample;
//
//import com.jfoenix.controls.JFXDrawer;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.collections.FXCollections;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Parent;
//import javafx.scene.control.*;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//import org.controlsfx.control.CheckComboBox;
//
//import java.io.IOException;
//import java.net.URL;
//import java.time.LocalDateTime;
//import java.util.*;
//
//public class ProjectOverviewController_ implements Initializable {
//
//    @FXML
//    private Region opaque;
//    @FXML
//    private AnchorPane root;
//    @FXML
//    private TabPane colTabPane;
//    @FXML
//    private JFXDrawer drawer;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        Main.projectOverviewController = this;
//        setupNewCollection("TEST");
//    }
//
//    private ProjectCollectionTabController setupNewCollection(String colName) {
//        final Collection colObj = new Collection(colName);
//        final ProjectCollectionTabController projectOverviewController = initCollection(colName);
//        projectOverviewController.setCollection(colObj);
//        return projectOverviewController;
//    }
//
//    private ProjectCollectionTabController initCollection(String colName) {
//        final Tab tab = new Tab(colName);
//        final FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/ProjectCollection_(IRS).fxml"));
//        try {
//            final Parent parent = (Parent) loader.load();
//            final ProjectCollectionTabController projectOverviewController = loader.getController();
//            tab.setContent(parent);
//            colTabPane.getTabs().add(tab);
//            return projectOverviewController;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public class Box {
//        private Hyperlink barcode;
//        private ComboBox<String> status;
//        private CheckComboBox<String> employees;
//        private StringProperty name;
//        private int pages;
//        private Label details;
//
//        public Box(String barcode, String name, String status, List<String> employees, int pages) {
//            this.barcode = new Hyperlink("#" + barcode);
//            this.name = new SimpleStringProperty(name);
//            this.pages = pages;
//            this.employees = new CheckComboBox<String>(FXCollections.observableArrayList("Alpha", "Beta", "Gamma", "Omega"));
//            employees.forEach(e -> {
//                this.employees.getCheckModel().check(e);
//            });
//            this.employees.setMinWidth(300);
//            this.status = new ComboBox<String>(FXCollections.observableArrayList("Inactive", "Prepping", "Scanning", "QC", "Rescans", "Shipment", "Complete"));
//            this.getStatus().getSelectionModel().select(status);
//            this.details = new Label();
//            this.details.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//            this.getDetails().getGraphic();
//            this.details.setGraphic(new ImageView(getClass().getResource("info.png").toExternalForm()));
//            this.details.setTooltip(new Tooltip("Details"));
//            this.details.setOnMousePressed(e -> {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/FXML/GroupDetails.fxml"));
//                try {
//                    VBox box = loader.load();
//                    drawer.setSidePane(box);
//                    drawer.setDefaultDrawerSize(650);
//                    drawer.setMinWidth(650);
//                    drawer.setMinHeight(root.getHeight());
//                    GroupDetailsController_ controller = loader.getController();
//                    controller.getBarcode().setText(this.getBarcode().getText());
//                    controller.getGroupName().setText(this.name.get());
//
//                    String selStatus = this.status.getSelectionModel().getSelectedItem();
//                    if (selStatus == null || selStatus.isEmpty()) {
//                        selStatus = "Inactive";
//                    }
//                    GroupDetailsController_.Activity b1 = new GroupDetailsController_.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Selected");
//                    GroupDetailsController_.Activity b2 = new GroupDetailsController_.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Being Scanned");
//                    GroupDetailsController_.Activity b3 = new GroupDetailsController_.Activity("#34390", LocalDateTime.now(), "Craig T.", "Item Completed");
//                    GroupDetailsController_.Activity b4 = new GroupDetailsController_.Activity("#84333", LocalDateTime.now(), "Cindy M.", "Item Edited");
//
//                    controller.getAcList().getItems().addAll(b1, b2, b3, b4);
//                    GroupDetailsController_.Overview ab1 = new GroupDetailsController_.Overview("", LocalDateTime.now(), 2202, "Test", "Craig T.", "Completed");
//                    TreeItem<GroupDetailsController_.Overview> a1 = new TreeItem<>(new GroupDetailsController_.Overview("#34390", LocalDateTime.now()));
//                    TreeItem<GroupDetailsController_.Overview> a2 = new TreeItem<>(new GroupDetailsController_.Overview("#34391", LocalDateTime.now()));
//                    TreeItem<GroupDetailsController_.Overview> a3 = new TreeItem<>(new GroupDetailsController_.Overview("#34392", LocalDateTime.now()));
//                    TreeItem<GroupDetailsController_.Overview> a4 = new TreeItem<>(new GroupDetailsController_.Overview("#34393", LocalDateTime.now()));
//
//                    TreeItem<GroupDetailsController_.Overview> ovSubTreeItem = new TreeItem<>(ab1);
//                    a1.getChildren().add(ovSubTreeItem);
//                    controller.getOvTree().getRoot().getChildren().addAll(a1, a2, a3, a4);
//
//                    controller.getStatus().setText(selStatus);
//                    controller.getClose().setOnMouseClicked(e3 -> {
//                        drawer.close();
//                        drawer.setOnDrawerClosed(e4 -> {
//                            drawer.setPrefWidth(0);
//                            drawer.setMinWidth(0);
//                        });
//                    });
//                    drawer.open();
//
//                } catch (IOException e2) {
//                    e2.printStackTrace();
//                }
//            });
//        }
//
//
//        public Hyperlink getBarcode() {
//            return barcode;
//        }
//
//        public void setBarcode(Hyperlink barcode) {
//            this.barcode = barcode;
//        }
//
//        public ComboBox<String> getStatus() {
//            return status;
//        }
//
//        public void setStatus(ComboBox<String> status) {
//            this.status = status;
//        }
//
//        public CheckComboBox<String> getEmployees() {
//            return employees;
//        }
//
//        public void setEmployees(CheckComboBox<String> employees) {
//            this.employees = employees;
//        }
//
//        public String getName() {
//            return name.get();
//        }
//
//        public StringProperty nameProperty() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name.set(name);
//        }
//
//        public int getPages() {
//            return pages;
//        }
//
//        public void setPages(int pages) {
//            this.pages = pages;
//        }
//
//        public Label getDetails() {
//            return details;
//        }
//
//        public void setDetails(Label details) {
//            this.details = details;
//        }
//    }
//
//
//    public AnchorPane getRoot() {
//        return root;
//    }
//
//    public void setRoot(AnchorPane root) {
//        this.root = root;
//    }
//
//    public JFXDrawer getGroupDetailDrawer() {
//        return drawer;
//    }
//
//    public Region getOpaque() {
//        return opaque;
//    }
//}
