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
    private AnimationTimer checkCrashOrOutward;
    private Circle bonus;
    private Image imgBonus;
    private Random r = new Random();

    public Bonus(){

        try {
            imgBonus = new Image(getClass().getResourceAsStream("resources/img/help_hp.png"));
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

                private long lastUpdate = 0;
                @Override
                public void handle(long now) {
                    if ( now - lastUpdate >= 26100_000_000L ) {
                        if ( lastUpdate != 0 ) {
                            setBonus();
                        }
                        lastUpdate = now;
                    }
                }
            };
            generateBonus.start();

        }

    }

    private void setBonus(){
       int choose = r.nextInt(2);

       if (choose == 0){
           bonus = new Circle();
           bonus.setRadius(11);
           bonus.setFill(new ImagePattern(imgBonus));
           bonus.setTranslateX(r.nextInt(Main.widthWindow - 15));
           bonus.setTranslateY(-50);
           bonus.setEffect(new DropShadow(20, Color.WHITE));

           Main.root.getChildren().addAll(bonus);

           checkCrashOrOutward = new AnimationTimer() {
               @Override
               public void handle(long now) {
                   Shape intersects = Shape.intersect(bonus,Main.p.getRocket());

                   if(intersects.getBoundsInLocal().getWidth() != -1) {
                       bonus.setVisible(false);
                       Main.root.getChildren().remove(bonus);

                       if (Main.p.getHealthRocket() < 5 ) {
                           int nowHP = Main.p.getHealthRocket();
                           Main.p.setHealthRocket(++nowHP);

                           Main.refreshHP();
                       }

                       this.stop();
                   }

                   if (bonus.getTranslateY() > (Main.heightWindow + 20)){
                       bonus.setVisible(false);
                       Main.root.getChildren().remove(bonus);
                       this.stop();
                   }

                   bonus.setTranslateY(bonus.getTranslateY() + 2.0);
               }
           };
           checkCrashOrOutward.start();
       }
    }

    public void setEnd(){
        if (generateBonus != null)
        generateBonus.stop();

        if (checkCrashOrOutward != null)
        checkCrashOrOutward.stop();

        Main.root.getChildren().remove(bonus);
        bonus = null;
    }
}
