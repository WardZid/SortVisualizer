import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

public class SortController implements Initializable {

    private int size=50;

    private int[] array;

    private Rectangle[] recs;

    private LinearGradient linear;

    @FXML
    private GridPane grid;

    @FXML
    private Label sizeLbl;

    @FXML
    private Slider sizeSlider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grid.getChildren().clear();
        grid.setPadding(new Insets(5));
        grid.getRowConstraints().get(0).setValignment(VPos.BOTTOM);

        Stop[] stops = new Stop[] { new Stop(0, Color.WHITE), new Stop(1, Color.GREY)};
        linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);


        sizeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            sizeLbl.textProperty().setValue(
                    String.valueOf(newValue.intValue()));
            size=newValue.intValue();
            initArrays();
            buildGrid();
        });

        initArrays();
        shuffleArray();
        buildGrid();
    }

    public void onSort() {
        System.out.println("sort");
        new Thread(() -> {
            insertionSort();
            insertionSort();
        }).start();

        System.out.println("sort started");
    }

    public void onShuffle() {
        System.out.println("shuffle");
        new Thread(this::shuffleArray).start();
    }

    private void buildGrid(){
        grid.getChildren().clear();
        for(int i=0;i<size;i++){
            grid.add(recs[i],i,0);
        }
    }

    public void initArrays(){
        recs =new Rectangle[size];
        array=new int[size];
        for (int i = 0; i < size; i++) {
            array[i]=(i+1)*(600/size);
            recs[i] = new Rectangle(800 / size, array[i]);
            recs[i].setFill(linear);
        }
    }
    public void shuffleArray() {
        int n = array.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            slow();
            swapRec(i,change);
            swap(i, change);
        }
    }

    private void swap(int i, int change) {
        Rectangle helpRec=recs[i];
        int helper = array[i];

        recs[i]=recs[change];
        array[i] = array[change];

        recs[change]=helpRec;
        array[change] = helper;
    }

    private void insertionSort(){
        Rectangle curRec;
        for (int i = 1,j=0,current; i < size; i++,j=i-1) {

            current=array[i];
            curRec=recs[i];
            colorRec(curRec,Color.GREEN);
            Tone.beep(array[i]*10);
            while (j>=0 && current< array[j]){
                array[j+1] =array[j];
                add(recs[j],j+1);
                recs[j+1]=recs[j];
                j-=1;
            }
            array[j+1]=current;
            recs[j+1]=curRec;
            add(curRec,j+1);
            slow();
            colorRec(curRec,linear);
        }
    }
    private void colorRec(Rectangle rectangle, Paint p){
        final Rectangle rc=rectangle;
        javafx.application.Platform.runLater(() -> rc.setFill(p));
    }

    private void slow(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void remove(int index){
        final int ind=index;
        javafx.application.Platform.runLater(() -> grid.getChildren().remove(recs[ind]));
    }

    private void add(Rectangle rectangle, int index){
        final Rectangle rect=rectangle;
        final int ind =index;
        javafx.application.Platform.runLater(() -> {
            grid.getChildren().remove(rect);
            grid.add(rect,ind,0);
        });
    }

    private void swapRec(int ind1,int ind2){
        remove(ind1);
        remove(ind2);
        add(recs[ind1],ind2);
        add(recs[ind2],ind1);
    }
}
