package xyz.blockers.pick;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.blockers.pick.utilities.DragWindowHandler;
import xyz.blockers.pick.utilities.MoveInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Window {
    private final Stage stage=new Stage(StageStyle.TRANSPARENT);
    private PickOne<String> pickOne;
    private final Text label=new Text();
    static Config config;
    static Gson gson=new Gson();
    static void init(){
        try {
            String fig=Tools.read("Config.json");
            config=gson.fromJson(fig,Config.class);
        }catch (Exception exception){
            config=new Config();
            try {
                Tools.write(gson.toJson(config));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public Window(){
        init();
        build();
    }



    ArrayList<Font> fonts=new ArrayList<>();
    private void  build(){
        stage.initOwner(Blank_Stage);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOpacity(config.nameWindowOpacity);
        stage.setWidth(config.width);
        stage.setHeight(config.height);
        StackPane p = new StackPane();
        p.setStyle("-fx-background-radius: "+config.radius+";-fx-background-color: "+config.backgroundColor);
        //p.setBackground(new Background(new BackgroundFill(Color.valueOf(config.backgroundColor),null,null)));
        p.getChildren().add(label);
        StackPane.setAlignment(label, Pos.CENTER);

        Text text=new Text("Tap Anywhere to Close");
        text.setFont(new Font(24));
        text.setStyle("-fx-font-weight:lighter;-fx-pie-color: "+config.backgroundColor);
        p.getChildren().add(text);
        StackPane.setAlignment(text,Pos.BOTTOM_CENTER);
        for(double i= config.fontMax;i> config.fontMin;i-= config.fontInterval){
            fonts.add(new Font(config.fontStyle, i));
        }
        label.setFont(fonts.get(0));
        String fileName= config.fileName;
        BufferedReader reader = null;
        try {
            reader=new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            error("file isn't existed : "+fileName);
        } catch (IOException e) {
            error("file is corrupted\n"+e);
        }
        assert reader!=null;
        ArrayList<String> list=new ArrayList<>();
        String s;
        try {
            while ((s = reader.readLine()) != null) {
                list.add(s);
            }
        }catch (Exception e){

            error("file is corrupted : "+ e);

        }
        System.out.println(list);
        pickOne=new PickOne<>(list,config.nameRecoverTime);

        Scene scene=new Scene(p);
        stage.setScene(scene);


        scene.setFill(Color.TRANSPARENT);
        //label.setOnMouseClicked(this::hide);
        p.setOnMouseClicked(e->{
            if(e.getButton()==MouseButton.SECONDARY){
                menu.show(p,e.getScreenX(),e.getScreenY());
            }else if(e.getButton()==MouseButton.PRIMARY){
                this.hide();
            }
        });

    }
    private static final int defaultTimes=5;
    private void animation(){
        try {
            for (var i : fonts) {
                crossThread(()->label.setFont(i));
                Thread.sleep(config.fontTimeInterval);
            }
        }catch (Exception ignored){
        }
    }
    private static void error(String i){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fatal Error");
        alert.setContentText(i);
        alert.showAndWait();
        Platform.exit();
        System.exit(1);
    }

    Thread currentThread;
    public void pick(){
        stage.setAlwaysOnTop(true);
        if(currentThread!=null){
            try {
                currentThread.stop();
            }catch (Exception e){
                e.printStackTrace();

            }

        }
        if(!stage.isShowing()){
            stage.show();
        }else {
            setColor(Color.BLACK);
        }
        int times=PickOne.randint(defaultTimes);
        currentThread=new Thread(()->{
            for(int i=0;i<times;i++){
                String o= pickOne.pick(false);
                crossThread(()->label.setText(o));
                animation();
            }

            var t=pickOne.pick(true);
            System.out.println(t);
            crossThread(()->label.setText(t));
            animation();
            setColor(Color.RED);
        });
        currentThread.start();

    }
    private void crossThread(Runnable runnable){
        Platform.runLater(runnable);
    }

    private void  hide(){
        currentThread.stop();
        setColor(Color.BLACK);
        stage.hide();
    }
    public void setColor(Color color){
        label.setFill(color);
    }

    private static final Stage Blank_Stage=new Stage(StageStyle.UTILITY);
    static {
        Blank_Stage.setOpacity(0);
        Blank_Stage.show();
    }
    //-----------------------------------------------------------------------------------------
    private static ServerSocket ss;
    private static ContextMenu menu;
    private static Alert about;
    public static void start() {


        Window window=new Window();

        if(config.isEnableParallelRuining==0){
            try {
                ss = new ServerSocket(config.port);
            } catch (IOException e) {
                error("Programme is running");
                e.printStackTrace();

            }
        }



        Text text=new Text(" 抽   \n 一   \n 王   \n 八   ");
        text.setFont(new Font("等线",22));

        var p=new Pane(text);
        p.setStyle("-fx-background-radius: "+config.bootRadius+";-fx-background-color: "+config.bootColor);
        Scene scene=new Scene(p);
        scene.setFill(Color.TRANSPARENT);


        Stage stage=new Stage(StageStyle.TRANSPARENT);

        stage.setScene(scene);

        stage.setOpacity(config.bootOpacity);
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        menu=new ContextMenu();
        MenuItem re=new MenuItem("Reset");
        re.setOnAction(e->{
            config=new Config();
            try {
                Tools.write(gson.toJson(config));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        MenuItem item=new MenuItem("Quit");



        MenuItem item1=new MenuItem("What's New");
        about=new Alert(Alert.AlertType.INFORMATION);
        about.initModality(Modality.NONE);
        about.setTitle("What's New:");
        about.setHeaderText("About Pick");

        StackPane pane=new StackPane();
        TextArea textArea=new TextArea(
                "This is the third vision[\n" +
                        "   1.Java swing version\n" +
                        "   2.C# winForm version\n" +
                        "   3.JavaFX version(this)" +
                        "]:\n"+
                "1)Build with JavaFX\n" +
                "2)GPU acceleration enabled\n" +
                "3)Build up stability\n" +
                "4)Cross platform:this programme could run on Windows,Linux even Mac OS"
        );
        textArea.setWrapText(true);
        pane.getChildren().add(new Text("Build with JavaFX"));
        textArea.setEditable(false);
        textArea.setFont(new Font(20));
        pane.getChildren().add(textArea);
        about.getDialogPane().setContent(pane);
        item1.setOnAction(e->{
            about.show();
        });
        item.setOnAction(e->{
            Platform.exit();
            System.exit(0);
        });

        menu.getItems().addAll(re,item1,item);

        MoveInfo moveInfo=new MoveInfo();
        p.setOnMouseClicked(e->{
            if(e.getButton()==MouseButton.PRIMARY){
                if(moveInfo.isMoved){
                    moveInfo.isMoved=false;
                    return;
                }
                window.pick();
                return;
            }
            if(e.getButton()== MouseButton.SECONDARY){
                menu.show(stage,e.getScreenX(),e.getScreenY());
            }
        });

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Blank_Stage);
        stage.show();

        moveInfo.x= (int) (screenRectangle.getWidth()-stage.getWidth()+config.bootRadius);
        moveInfo.y= (int) (screenRectangle.getHeight()-config.taskBarHeight- stage.getHeight());


        DragWindowHandler handler=new DragWindowHandler(stage,moveInfo);
        text.setOnMousePressed(handler);
        text.setOnMouseDragged(handler);

        new Thread(()->{
            try{
                while (true) {
                    System.out.println("" + moveInfo.x + "      " + moveInfo.y);
                    stage.setX(moveInfo.x);
                    stage.setY(moveInfo.y);
                    Thread.sleep(1000 * 60 * 2);
                }
            }catch (Exception ignored){

            }
        }).start();

        stage.setAlwaysOnTop(true);
        text.setLayoutX(0);
        text.setLayoutY(20);
    }

}
