import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SortMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Initial Threads 1: \t"+Thread.getAllStackTraces().keySet().size()+" - "+Thread.getAllStackTraces().keySet());
        try {
            Parent root= FXMLLoader.load(getClass().getResource("Sort.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Sort Visualizer");
            primaryStage.sizeToScene();
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Initial Threads 2: \t"+Thread.getAllStackTraces().keySet().size()+" - "+Thread.getAllStackTraces().keySet());
    }

    @Override
    public void stop(){
        System.out.println("Final Threads: \t\t"+Thread.getAllStackTraces().keySet().size()+" - "+Thread.getAllStackTraces().keySet());
        System.exit(0);
    }
}
