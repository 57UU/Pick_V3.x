package xyz.blockers.pick.utilities;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DragWindowHandler implements EventHandler<MouseEvent> {
    private Stage primaryStage; //primaryStage为start方法头中的Stage
    private double oldStageX;
    private double oldStageY;
    private double oldScreenX;
    private double oldScreenY;
    MoveInfo moveInfo;
    public DragWindowHandler(Stage primaryStage,MoveInfo moveInfo) { //构造器
        this.primaryStage = primaryStage;
        this.moveInfo=moveInfo;
    }

    @Override
    public void handle(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {    //鼠标按下的事件
            this.oldStageX = this.primaryStage.getX();
            this.oldStageY = this.primaryStage.getY();
            this.oldScreenX = e.getScreenX();
            this.oldScreenY = e.getScreenY();

        } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {  //鼠标拖动的事件
            moveInfo.isMoved=true;
            moveInfo.x=e.getScreenX() - this.oldScreenX + this.oldStageX;
            moveInfo.y=e.getScreenY() - this.oldScreenY + this.oldStageY;
            this.primaryStage.setX(moveInfo.x);
            this.primaryStage.setY(moveInfo.y);
        }
    }

}
