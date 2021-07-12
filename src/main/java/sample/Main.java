package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class Main extends Application {

    public static ProjectOverviewController projectOverviewController;
    public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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
