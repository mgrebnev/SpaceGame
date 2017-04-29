package spacegame;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Created by Max on 15.04.2017.
 */
public class Player {

    private Rectangle rocket;

    private double SpeedRocket = 2.8;
    private double widthRocket;
    private double heightRocket;
    private int healthRocket = 5;

    private RotateTransition animation =  null;

    private KeyCode keyLeft;
    private KeyCode keyRight;

    private AnimationTimer checkMove = new AnimationTimer() {
        @Override
        public void handle(long now) {
            update();
        }
    };

    public Player(KeyCode left, KeyCode right){
        try{
            /*Загружаем ракету и устанавливаем координаты*/
            Image background = new Image(getClass().getResourceAsStream("resources/img/super_line_rocket.png"));
            rocket = new Rectangle();

            rocket.setWidth(36);
            rocket.setHeight(34);
            rocket.setSmooth(false);
            rocket.setFill(new ImagePattern(background));
            rocket.setTranslateX(Main.widthWindow/2 - background.getWidth()+12);
            rocket.setTranslateY(Main.heightWindow - background.getHeight()*2);
            rocket.setEffect(new DropShadow(25, Color.AQUA));

            widthRocket = background.getWidth();
            heightRocket = background.getHeight();

            /*Устанавливаем количество ХП и назначаем клавиши движения ракеты*/
            this.healthRocket = 5;

            keyLeft = left;
            keyRight = right;

            Main.root.getChildren().addAll(rocket);
        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Resource file not found!");
            alert.showAndWait();
            System.exit(0);
        }

        checkMove.start();
    }

    private void update() {
        if (isPressed(keyLeft)) {
            rocket.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("resources/img/super_left_rocket.png"))));
            moveLeft();
        }else if (isPressed(keyRight)) {
            rocket.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("resources/img/super_right_rocket.png"))));
            moveRight();
        }else{
            rocket.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("resources/img/super_line_rocket.png"))));
        }
    }

    private void moveLeft(){
        double nowX = rocket.getTranslateX();
        if ( nowX - (SpeedRocket) >= this.widthRocket*0.2 ){
            rocket.setTranslateX(nowX - SpeedRocket);
        }
    }
    private void moveRight(){
        double nowX = rocket.getTranslateX();
        if ( nowX + SpeedRocket <= (Main.widthWindow - (this.widthRocket*1.2 + this.SpeedRocket))){
            rocket.setTranslateX(nowX + SpeedRocket);
        }
    }

    private boolean isPressed(KeyCode key) {
        return Main.keys.getOrDefault(key,false);
    }

    public void setSuperBreak(int positionCrash){
        if (animation == null){
            if (positionCrash == 0) {
                animation = new RotateTransition(javafx.util.Duration.seconds(0.3), this.rocket);
                animation.setByAngle(70);
                animation.setAutoReverse(true);
                animation.setCycleCount(2);
                animation.play();
            }
            if (positionCrash == 1) {
                animation = new RotateTransition(javafx.util.Duration.seconds(0.3), this.rocket);
                animation.setByAngle(-70);
                animation.setAutoReverse(true);
                animation.setCycleCount(2);
                animation.play();
            }

        }

        if (animation.getStatus().equals(Animation.Status.STOPPED)){
            animation = null;
            setSuperBreak(positionCrash);
        }

    }

    public void setSpeedRocket(double speedRocket) {
        SpeedRocket = speedRocket;
    }

    public int getHealthRocket() {
        return healthRocket;
    }

    public void setHealthRocket(int healthRocket) {
        this.healthRocket = healthRocket;
    }

    public Rectangle getRocket() {
        return rocket;
    }

    public void setEnd(){
        checkMove.stop();
        Main.root.getChildren().remove(rocket);
        rocket = null;
    }
}

