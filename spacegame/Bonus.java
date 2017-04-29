package spacegame;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Random;

public class Bonus {
    private AnimationTimer generateBonus;

    private AnimationTimer checkCrashOrOutwardHealth;
    private AnimationTimer checkCrashOrOutwardSpeed;
    private AnimationTimer boostSpeed;
    private Circle bonusHealth;
    private Circle bonusSpeed;

    private Image imgHPBonus;
    private Image imgSpeedBonus;

    private Random r = new Random();

    public Bonus(){

        try {
            imgHPBonus = new Image(getClass().getResourceAsStream("resources/img/help_hp.png"));
            imgSpeedBonus = new Image(getClass().getResourceAsStream("resources/img/help_speed.png"));
        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Resource file not found!");
            alert.showAndWait();
            System.exit(0);
        }

        if ( generateBonus == null ){

            generateBonus = new AnimationTimer() {

                private long lastUpdateHealth = 0;
                private long lastUpdateSpeed = 0;
                @Override
                public void handle(long now) {
                    if ( now - lastUpdateHealth >= 26100_000_000L ) {
                        if ( lastUpdateHealth != 0 ) {
                            setHPBonus();
                        }
                        lastUpdateHealth = now;
                    }

                    if ( now - lastUpdateSpeed >= 20100_000_000L ) {
                        if ( lastUpdateSpeed != 0 ) {
                            setSpeedBonus();
                        }
                        lastUpdateSpeed = now;
                    }
                }
            };
            generateBonus.start();

        }

    }

    private void setSpeedBonus(){
        int choose = r.nextInt(2);

        if ( choose == 0 ) {
            bonusSpeed = new Circle();
            bonusSpeed.setRadius(11);
            bonusSpeed.setFill(new ImagePattern(imgSpeedBonus));
            bonusSpeed.setTranslateX(r.nextInt(Main.widthWindow - 15));
            bonusSpeed.setTranslateY(-50);
            bonusSpeed.setEffect(new DropShadow(20, Color.RED));

            Main.root.getChildren().addAll(bonusSpeed);

            checkCrashOrOutwardSpeed = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    Shape intersects = Shape.intersect(bonusSpeed, Main.p.getRocket());

                    if (intersects.getBoundsInLocal().getWidth() != -1) {
                        bonusSpeed.setVisible(false);
                        Main.root.getChildren().remove(bonusSpeed);

                        Main.p.setSpeedRocket(5.0);

                        boostSpeed = new AnimationTimer() {
                            private long lastUpdate = 0;

                            @Override
                            public void handle(long now) {
                                if (now - lastUpdate >= 8000_000_000L) {
                                    if (lastUpdate != 0) {
                                        Main.p.setSpeedRocket(2.8);
                                        this.stop();
                                    }
                                    lastUpdate = now;
                                }
                            }
                        };
                        boostSpeed.start();

                        this.stop();
                    }

                    if (bonusSpeed.getTranslateY() > (Main.heightWindow + 20)) {
                        bonusSpeed.setVisible(false);
                        Main.root.getChildren().remove(bonusSpeed);
                        this.stop();
                    }

                    bonusSpeed.setTranslateY(bonusSpeed.getTranslateY() + 2.0);
                }
            };
            checkCrashOrOutwardSpeed.start();
        }

    }

    private void setHPBonus(){
       int choose = r.nextInt(2);

       if (choose == 0){
           bonusHealth = new Circle();
           bonusHealth.setRadius(11);
           bonusHealth.setFill(new ImagePattern(imgHPBonus));
           bonusHealth.setTranslateX(r.nextInt(Main.widthWindow - 15));
           bonusHealth.setTranslateY(-50);
           bonusHealth.setEffect(new DropShadow(20, Color.WHITE));

           Main.root.getChildren().addAll(bonusHealth);

           checkCrashOrOutwardHealth = new AnimationTimer() {
               @Override
               public void handle(long now) {
                   Shape intersects = Shape.intersect(bonusHealth,Main.p.getRocket());

                   if(intersects.getBoundsInLocal().getWidth() != -1) {
                       bonusHealth.setVisible(false);
                       Main.root.getChildren().remove(bonusHealth);

                       if (Main.p.getHealthRocket() < 5 ) {
                           int nowHP = Main.p.getHealthRocket();
                           Main.p.setHealthRocket(++nowHP);

                           Main.refreshHP();
                       }

                       this.stop();
                   }

                   if (bonusHealth.getTranslateY() > (Main.heightWindow + 20)){
                       bonusHealth.setVisible(false);
                       Main.root.getChildren().remove(bonusHealth);
                       this.stop();
                   }

                   bonusHealth.setTranslateY(bonusHealth.getTranslateY() + 2.0);
               }
           };
           checkCrashOrOutwardHealth.start();
       }
    }

    public void setEnd(){
        if (generateBonus != null)
        generateBonus.stop();

        if (checkCrashOrOutwardHealth != null)
        checkCrashOrOutwardHealth.stop();

        if (checkCrashOrOutwardSpeed != null)
            checkCrashOrOutwardSpeed.stop();

        if (boostSpeed != null)
            boostSpeed = null;

        Main.root.getChildren().remove(bonusHealth);
        Main.root.getChildren().remove(bonusSpeed);
        bonusHealth = null;
        bonusSpeed = null;
    }
}
