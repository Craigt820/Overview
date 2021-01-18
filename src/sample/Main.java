package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.PrintStream;

@SuppressWarnings("unchecked")
public class Main extends Application {

    public static ProjectOverviewController projectOverviewController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        com.sun.javafx.util.Logging.getCSSLogger().setLevel(sun.util.logging.PlatformLogger.Level.OFF);
        Parent root = FXMLLoader.load(getClass().getResource("ProjectOverview.fxml"));
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root));
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
