package sample;

import com.jfoenix.controls.JFXDrawer;
import com.sun.xml.internal.ws.util.CompletedFuture;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.apache.commons.dbutils.DbUtils;
import org.controlsfx.control.CheckComboBox;
import sample.JavaBeans.Collection;
import sample.JavaBeans.Job;
import sample.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ProjectOverviewController implements Initializable {

    @FXML
    private Region opaque;
    @FXML
    private AnchorPane root;
    @FXML
    private TabPane colTabPane;
    @FXML
    private JFXDrawer drawer;

    public static Job job;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.projectOverviewController = this;
        job = getJobInfo("NAACP001");
        CompletableFuture.supplyAsync(()->{
            return getCollections();
        }).thenAccept(col->{
            col.forEach(e->{
                Platform.runLater(()->{
                    setupNewCollection(e);
                });
            });
        });
    }


    private ArrayList<String> getCollections() {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        ArrayList<String> collections = new ArrayList<>();

        try {
            connection = ConnectionHandler.createDBConnection();
            ps = connection.prepareStatement("SELECT id,name from sc_collections WHERE job_id=?");
            ps.setInt(1,job.getId());
            set = ps.executeQuery();
            while (set.next()) {
                collections.add(set.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Main.LOGGER.log(Level.SEVERE, "There was an error getting the collections from the db!", e);

        } finally {
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
        return collections;
    }


    private Job getJobInfo(String jobID) {
        Connection connection = null;
        ResultSet set = null;
        PreparedStatement ps = null;
        Job job = null;

        try {
            connection = ConnectionHandler.createDBConnection();
            ps = connection.prepareStatement("SELECT id,client_id,user_entry,uid,group_col,complete,job_id,description from Projects WHERE job_id='" + jobID + "'");
            set = ps.executeQuery();
            while (set.next()) {
                job = new Job(set.getInt("id"), set.getString("job_id"), Utils.intToBoolean(set.getInt("user_entry")), Utils.intToBoolean(set.getInt("complete")), set.getString("uid"), set.getString("group_col"), set.getString("description"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Main.LOGGER.log(Level.SEVERE, "There was an error getting the jobs from the db!", e);

        } finally {
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
        return job;
    }

    private ProjectCollectionTabController setupNewCollection(String colName) {
        final Collection colObj = new Collection(colName);
        final ProjectCollectionTabController projectOverviewController = initCollection(colName);
        projectOverviewController.setCollection(colObj);
        return projectOverviewController;
    }

    private ProjectCollectionTabController initCollection(String colName) {
        final Tab tab = new Tab(colName);
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/ProjectCollection.fxml"));
        try {
            final Parent parent = (Parent) loader.load();
            final ProjectCollectionTabController projectOverviewController = loader.getController();
            tab.setContent(parent);
            MenuItem menu = new MenuItem("Refresh");
            menu.setOnAction(e -> {
                try {
                    loader.setRoot(FXMLLoader.load(getClass().getResource("../FXML/ProjectCollection.fxml")));
                    tab.setContent(loader.getRoot());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            });
            tab.setContextMenu(new ContextMenu(menu));
            colTabPane.getTabs().add(tab);
            return projectOverviewController;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public AnchorPane getRoot() {
        return root;
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }

    public JFXDrawer getGroupDetailDrawer() {
        return drawer;
    }

    public Region getOpaque() {
        return opaque;
    }
}
