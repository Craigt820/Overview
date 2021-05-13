package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SuppressWarnings("unchecked")
public class Main extends Application {

    public static ProjectOverviewController projectOverviewController;
    public static final String CONN = "jdbc:mysql://192.168.1.147/Tracking?useSSL=false";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/ProjectOverview.fxml"));
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public ProjectOverviewController getProjectOverviewController() {
        return projectOverviewController;
    }

    public void setProjectOverviewController(ProjectOverviewController projectOverviewController) {
        this.projectOverviewController = projectOverviewController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
