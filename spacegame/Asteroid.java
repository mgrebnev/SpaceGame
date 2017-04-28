package spacegame;

import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Max on 15.04.2017.
 */
public class Asteroid {

    private Image firstAsteroid;
    private Image secondAsteroid;
    private Image thirdAsteroid;
    private Image fourthAsteroid;

    private double firstSpeedAsteroid = 2.5;
    private double secondSpeedAsteroid = 1.9;
    private double thirdSpeedAsteroid = 0.9;
    private double fourthSpeedAsteroid = 0.4;

    private double[] speedAsteroids =  new double[]{
            2.5 , 1.9 , 0.9 , 0.4
    };

    private double fasterSpeed = 2.5;

    private long timeCreateAsteroid = 1100_000_000L;

    private int count = 0;

    private Random r = new Random();

    private List<RotatingAsteroid> obj = new ArrayList<RotatingAsteroid>();

    private AnimationTimer checkCrash = new AnimationTimer() {
        @Override
        public void handle(long now) {
            isCrash();
        }
    };

    private AnimationTimer createAsteroid = new AnimationTimer() {
        private long lastUpdate = 0;

        @Override
        public void handle(long now) {
            if ( now - lastUpdate >= timeCreateAsteroid ) {
                setNewAsteroid();
                lastUpdate = now;
            }
        }
    };

    private AnimationTimer moveAsteroid = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for ( int i = 0; i < obj.size(); i++ ){
                if ( obj.get(i) != null ) {

                    if (!obj.get(i).getCircle().getUserData().equals("crashAsteroidLeft") &
                        !obj.get(i).getCircle().getUserData().equals("crashAsteroidRight")) {

                         obj.get(i).setAnimation();
                         obj.get(i).getCircle().setTranslateY(obj.get(i).getCircle().getTranslateY()
                                 + obj.get(i).getAsteroidSpeed());
                    }

                    /*Если астероид столкнулся с ракетой то он отскакивает */
                    if (obj.get(i).getCircle().getUserData().equals("crashAsteroidLeft")) {
                        obj.get(i).setAnimation(0.4);
                        obj.get(i).getCircle().setTranslateY(obj.get(i).getCircle().getTranslateY() + speedAsteroids[3]);
                        obj.get(i).getCircle().setTranslateX(obj.get(i).getCircle().getTranslateX() - speedAsteroids[0]);
                    }

                    if (obj.get(i).getCircle().getUserData().equals("crashAsteroidRight")) {
                        obj.get(i).setAnimation(0.4);
                        obj.get(i).getCircle().setTranslateY(obj.get(i).getCircle().getTranslateY() + speedAsteroids[3]);
                        obj.get(i).getCircle().setTranslateX(obj.get(i).getCircle().getTranslateX() + speedAsteroids[0]);
                    }

                    /*Если астероид вышел за диапозоны поля, то он удаляется с поля и с коллекции */
                    if (obj.get(i).getCircle().getTranslateY() > (Main.heightWindow + 20)) {
                        obj.get(i).stopAnimation();
                        obj.get(i).getCircle().setVisible(false);
                        Main.root.getChildren().remove(obj.get(i));
                        obj.remove(obj.get(i));
                        Main.scores += 20;
                        Main.refreshScores(Main.scores);
                    }
                }
            }
        }
    };

    public Asteroid(){

        try {
            firstAsteroid = new Image(getClass().getResourceAsStream("resources/img/asteroid_1.png"));

            secondAsteroid = new Image(getClass().getResourceAsStream("resources/img/asteroid_2.png"));

            thirdAsteroid = new Image(getClass().getResourceAsStream("resources/img/asteroid_3.png"));

            fourthAsteroid = new Image(getClass().getResourceAsStream("resources/img/asteroid_4.png"));

        }catch(Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Resource file not found!");
            alert.showAndWait();
            System.exit(0);
        }

        createAsteroid.start();
        moveAsteroid.start();
        checkCrash.start();
    }

    private void isCrash(){
        for ( int i = 0; i < obj.size(); i++ ){
            Shape intersects = Shape.intersect(obj.get(i).getCircle(),Main.p.getRocket());

            if(intersects.getBoundsInLocal().getWidth() != -1){
                if (obj.get(i).getCircle().getId() == null) {
                    if (obj.get(i).getCircle().getTranslateX() > Main.p.getRocket().getTranslateX()) {
                        obj.get(i).getCircle().setUserData("crashAsteroidRight");
                        Main.p.setSuperBreak(1);
                    } else {
                        if (obj.get(i).getCircle().getTranslateX() < Main.p.getRocket().getTranslateX()) {
                            obj.get(i).getCircle().setUserData("crashAsteroidLeft");
                            Main.p.setSuperBreak(0);
                        } else {
                            obj.get(i).getCircle().setUserData("crashAsteroidRight");
                            Main.p.setSuperBreak(1);
                        }
                    }
                    System.out.println(++count);

                    int nowHP = Main.p.getHealthRocket();
                    Main.p.setHealthRocket(--nowHP);

                    Main.refreshHP();
                    obj.get(i).getCircle().setId("!");
                }

            }

        }
    }

    private void setNewAsteroid(){
        int chooseAsteroid = this.r.nextInt(10);

        /*Выпадение маленького астероида*/
        if ( chooseAsteroid >= 0 & chooseAsteroid <= 3 ){

            RotatingAsteroid a = new RotatingAsteroid(15,
                    this.r.nextInt(Main.widthWindow - 30),
                    -50,
                    firstAsteroid,
                    "firstA",
                    0.3,
                    this.speedAsteroids[0]);

            obj.add(a);
            Main.root.getChildren().addAll(a.getCircle());
        }

        /*Выпадение меньше среднего астероида*/
        if ( chooseAsteroid >= 4 & chooseAsteroid <= 6 ){

            RotatingAsteroid b = new RotatingAsteroid(18,
                    this.r.nextInt(Main.widthWindow - 36),
                    -50,
                    secondAsteroid,
                    "secondA",
                    0.5,
                    this.speedAsteroids[1]);

            obj.add(b);
            Main.root.getChildren().addAll(b.getCircle());
        }

        /*Выпадение среднего астероида*/
        if ( chooseAsteroid >= 7 & chooseAsteroid <= 8 ){

            RotatingAsteroid c = new RotatingAsteroid(30,
                    this.r.nextInt(Main.widthWindow - 90),
                    -60,
                    thirdAsteroid,
                    "thirdA",
                    0.6,
                    this.speedAsteroids[2]);

            obj.add(c);
            Main.root.getChildren().addAll(c.getCircle());
        }

        /*Выпадение большого астероида*/
        if ( chooseAsteroid == 9 ){

            RotatingAsteroid d = new RotatingAsteroid(32,
                    this.r.nextInt(Main.widthWindow - 120),
                    -60,
                    fourthAsteroid,
                    "fourthA",
                    0.8,
                    this.speedAsteroids[3]);

            obj.add(d);
            Main.root.getChildren().addAll(d.getCircle());
        }

        Main.refreshHP();
        Main.refreshScores(Main.scores);
    }

    public void setSpeed(double a){
        if ( this.speedAsteroids[0] <= 9.1 ) {
            for ( int i = 0; i < speedAsteroids.length; i++ ){
                this.speedAsteroids[i] += a;
            }

            for ( int i = 0; i < obj.size(); i++ ){
                if ( obj.get(i).getCircle().getUserData().equals("firstA")){
                    obj.get(i).setAsteroidSpeed(speedAsteroids[0]);
                }
                if ( obj.get(i).getCircle().getUserData().equals("secondA")){
                    obj.get(i).setAsteroidSpeed(speedAsteroids[1]);
                }
                if ( obj.get(i).getCircle().getUserData().equals("thirdA")){
                    obj.get(i).setAsteroidSpeed(speedAsteroids[2]);
                }
                if ( obj.get(i).getCircle().getUserData().equals("fourthA")){
                    obj.get(i).setAsteroidSpeed(speedAsteroids[3]);
                }
            }

        }

        System.out.println(this.speedAsteroids[0]);
    }

    public void setTimeCreateAsteroid(long timeCreate){
        if (timeCreateAsteroid >= 400_000_000L){
            timeCreateAsteroid -= timeCreate;
        }

    }

    public void setEnd(){
        for ( int i = 0; i < obj.size(); i++ ){
            Main.root.getChildren().remove(obj.get(i).getCircle());
        }
        obj.clear();
        checkCrash.stop();
        moveAsteroid.stop();
        createAsteroid.stop();
    }

}
