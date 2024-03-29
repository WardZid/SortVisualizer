import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class SortController implements Initializable {

    //Sorts available
    private static final String insertion= "Insertion Sort";
    private static final String quick= "Quick Sort";
    private static final String bubble= "Bubble Sort";

    //Sort complexities
    static final String worstInsertion="O(n\u00B2)"; //n^2
    static final String avgInsertion="O(n\u00B2)";
    static final String bestInsertion="O(n)";
    static final String spaceInsertion="O(1)";

    static final String worstQuick="O(n\u00B2)";
    static final String avgQuick="O(n\u00B7log(n))"; //n*log(n)
    static final String bestQuick="O(n\u00B7log(n))";
    static final String spaceQuick="O(n)";

    static final String worstBubble="O(n\u00B2)";
    static final String avgBubble="O(n\u00B2)";
    static final String bestBubble="O(n)";
    static final String spaceBubble="O(1)";

    Thread sortThread;
    Runnable sortRunnable;
    Object beepLock=new Object();//so nothing interferes with the beep

    private String sort=insertion;

    private int size=50;

    private int[] array;

    private Rectangle[] recs;

    private LinearGradient linear;

    @FXML
    private GridPane grid;

    @FXML
    private VBox controlVBox;

    @FXML
    private HBox controlsHBox;

    @FXML
    private Label sizeLbl;

    @FXML
    private Slider sizeSlider;


    //Play Pause Stop Buttons and ImageViews
    @FXML
    private Button playBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Button sortBtn;

    @FXML
    private ToggleGroup sortGroup;

    @FXML
    private Text avgText;

    @FXML
    private Text bestText;

    @FXML
    private Text spaceText;

    @FXML
    private Text worstText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(Thread.getAllStackTraces().keySet().size());
        grid.getChildren().clear();
        grid.setPadding(new Insets(5));
        grid.getRowConstraints().get(0).setValignment(VPos.BOTTOM);

        Stop[] stops = new Stop[] { new Stop(0, Color.WHITE), new Stop(1, Color.GREY)};
        linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        sortGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton btn=(RadioButton) newValue;
            switch (btn.getText()){
                case insertion:
                    sort=insertion;
                    worstText.setText(worstInsertion);
                    avgText.setText(avgInsertion);
                    bestText.setText(bestInsertion);
                    spaceText.setText(spaceInsertion);
                    break;
                case quick:
                    sort=quick;
                    worstText.setText(worstQuick);
                    avgText.setText(avgQuick);
                    bestText.setText(bestQuick);
                    spaceText.setText(spaceQuick);
                    break;
                case bubble:
                    sort=bubble;
                    worstText.setText(worstBubble);
                    avgText.setText(avgBubble);
                    bestText.setText(bestBubble);
                    spaceText.setText(spaceBubble);
                    break;
                default:
                    break;
            }
        });

        playBtn.setDisable(true);
        pauseBtn.setDisable(true);
        stopBtn.setDisable(true);

        worstText.setText(worstInsertion);
        avgText.setText(avgInsertion);
        bestText.setText(bestInsertion);
        spaceText.setText(spaceInsertion);

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
        sortRunnable= () -> {
            playBtn.setDisable(true);
            pauseBtn.setDisable(false);
            stopBtn.setDisable(false);

            controlVBox.setDisable(true);

            sort();
            //pass();

            controlVBox.setDisable(false);
            playBtn.setDisable(true);
            pauseBtn.setDisable(true);
            stopBtn.setDisable(true);
            System.out.println("sort finito");
        };
        System.out.println(Thread.getAllStackTraces().keySet().size());
    }

    @FXML
    void onPlay() {
        System.out.println("onPlay");
        controlsHBox.setDisable(true);
        synchronized (beepLock) {
            System.out.println("doing");
            sortThread.resume();
            playBtn.setDisable(true);
            pauseBtn.setDisable(false);
            stopBtn.setDisable(false);
            System.out.println("finished");
        }
        controlsHBox.setDisable(false);
    }

    @FXML
    void onPause() {
        System.out.println("onPause");
        controlsHBox.setDisable(true);
        synchronized (beepLock) {
            System.out.println("doing");
            sortThread.suspend();
            pauseBtn.setDisable(true);
            playBtn.setDisable(false);
            stopBtn.setDisable(false);
            System.out.println("finished");
        }
        controlsHBox.setDisable(false);
    }

    @FXML
    void onStop() {
        System.out.println("onStop");
        controlsHBox.setDisable(true);
        synchronized (beepLock) {
            System.out.println("doing");
            sortThread.stop();
            playBtn.setDisable(true);
            pauseBtn.setDisable(true);
            stopBtn.setDisable(true);
            System.out.println("finished");
        }
        controlsHBox.setDisable(false);
        controlVBox.setDisable(false);
    }

    public void onSort() {
        sortThread = new Thread(sortRunnable);
        sortThread.start();
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
            array[i]=(i+1);
            recs[i] = new Rectangle(800 / size, array[i]*(600/size));
            recs[i].setFill(linear);
        }
    }
    public void shuffleArray() {
        sortBtn.setDisable(true);

        int n = array.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            slow();
            swapRec(i,change);
            swap(i, change);
        }

        sortBtn.setDisable(false);
    }

    private void swap(int i, int change) {
        Rectangle helpRec=recs[i];
        int helper = array[i];

        recs[i]=recs[change];
        array[i] = array[change];

        recs[change]=helpRec;
        array[change] = helper;
    }

    private void pass(){
        for (int i = 0; i < recs.length; i++) {
            if(i>0)
                colorRec(recs[i-1],Color.GREEN);
            if(i< recs.length-1)
                colorRec(recs[i+1],Color.GREEN);
            colorRec(recs[i],Color.GREEN);
            doTheBeep(array[i]);
            if(i>0)
                colorRec(recs[i-1],linear);
            if(i< recs.length-1)
                colorRec(recs[i+1],linear);
            colorRec(recs[i],linear);
        }
    }

    private void sort(){
        switch (sort){
            case insertion:
                insertionSort();
                break;
            case quick:
                quickSort(0,array.length-1);
                break;
            case bubble:
                bubbleSort();
                break;
            default:
                break;
        }
    }
    private void insertionSort(){
        Rectangle curRec;
        for (int i = 1,j=0,current; i < size; i++,j=i-1) {

            current=array[i];
            curRec=recs[i];
            colorRec(curRec,Color.GREEN);
            doTheBeep(array[i]);
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
    private void quickSort(int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(begin, end);

            quickSort(begin, partitionIndex-1);

            quickSort(partitionIndex+1, end);

        }
    }
    private int partition(int begin, int end) {
        colorRec(recs[end],Color.RED);
        int pivot = array[end];
        int i = (begin-1);
        doTheBeep(array[end]);
        for (int j = begin; j < end; j++) {
            if (array[j] <= pivot) {
                i++;
                colorRec(recs[i],Color.GREEN);
                colorRec(recs[j],Color.BLUE);
                doTheBeep(array[j]);
                swapRec(i,j);
                swap(i,j);

                colorRec(recs[i],linear);
                colorRec(recs[j],linear);
            }
        }
//        slow();
        colorRec(recs[end],linear);
        swapRec(i+1,end);
        swap(i+1,end);

        return i+1;
    }

    private void bubbleSort(){
        for (int i = 0; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if(array[j-1]>array[j]){
                    colorRec(recs[j-1],Color.GREEN);
                    doTheBeep(array[j]);
                    swapRec(j-1,j);
                    swap(j-1,j);
                    slow();
                    colorRec(recs[j],linear);
                }
            }
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

    private void doTheBeep(int i){
        synchronized (beepLock) {
            System.out.println("doing");
            Tone.beep(i * 10);
            System.out.println("finished");
        }
    }
}
