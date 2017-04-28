package spacegame;

import javafx.animation.RotateTransition;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Created by Max on 28.04.2017.
 */
public class RotatingAsteroid {

    private Circle c;

    private RotateTransition animation;
    private double asteroidSpeed;
    private double duratonAnimation;

    public RotatingAsteroid(int r, int x, int y,Image img,String name,double duration,double speed){
        c = new Circle();
        c.setRadius(r);
        c.setFill(new ImagePattern(img));
        c.setTranslateX(x);
        c.setTranslateY(y);
        c.setUserData(name);

        this.duratonAnimation = duration;
        this.asteroidSpeed = speed;

    }

    public void setAnimation() {
        animation = new RotateTransition(javafx.util.Duration.seconds(this.duratonAnimation), this.c);
        animation.setByAngle(360);
        animation.play();
    }

    public void setAnimation(double duraton) {
        animation = new RotateTransition(javafx.util.Duration.seconds(duraton), this.c);
        animation.setByAngle(360);
        animation.play();
    }

    public void stopAnimation(){
        animation.stop();
    }

    public Circle getCircle() {
        return c;
    }

    public double getAsteroidSpeed() {
        return asteroidSpeed;
    }

    public void setAsteroidSpeed(double s) {
            asteroidSpeed = s;
    }

}
