package spacegame;

import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sun.security.provider.certpath.Vertex;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * Created by Max on 15.04.2017.
 */
public class Main extends Application{
    static final int widthWindow = 330;
    static final int heightWindow = 430;
    static BorderPane root = new BorderPane();
    static HashMap<KeyCode, Boolean> keys = new HashMap<>();

    static List<Circle> stars = new ArrayList<Circle>();
    static double speedStars = 0.2;

    static Player p;
    static Asteroid a;
    static Bonus b;
    static int scores = 0;
    static int tempScores = 100;


    static Random r = new Random();
    static MediaPlayer mediaPlayer;
    static int previousTrack = 2;

    static ImageView HPimgView;

    static boolean isStart = false;

    private AnimationTimer checkStatus;

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*Создание окна*/
        primaryStage.setWidth(widthWindow);
        primaryStage.setHeight(heightWindow);
        primaryStage.setTitle("");
        primaryStage.setResizable(false);

        /*Установка окна по центру*/
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

        Scene myscene = new Scene(root);

        /*Загружаем фон, устанавливаем звезды и ловим ошибки*/
        try {
            Image background = new Image(getClass().getResourceAsStream("resources/img/blackback.png"));

            ImageView seeBackground = new ImageView(background);

            seeBackground.setTranslateY(-140);
            root.getChildren().addAll(seeBackground);

            setStars();
            AnimationTimer moveStars = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    for ( int i = 0; i < stars.size(); i++ ){
                        if ( stars.get(i).getTranslateY() > heightWindow ){
                            stars.get(i).setTranslateY(-5);
                        }
                        stars.get(i).setTranslateY(stars.get(i).getTranslateY() + speedStars);
                    }
                }
            };
            moveStars.start();

        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Resource file not found!");
            alert.showAndWait();
            System.exit(0);
        }

        /*Включаем музыку*/
        playMedia();

        /*Выводим очки*/
        Main.refreshScores(scores);

        /*Создаем и выводим меню*/
        VBox userMenu = new VBox();

        Label startGame = new Label("New Game");
        startGame.setFont(Font.font("Courier New",30));
        startGame.setTextFill(Color.YELLOW);
        startGame.setStyle("-fx-padding: 9px;");
        startGame.setCursor(Cursor.HAND);
        startGame.setOnMouseEntered(event -> {
            startGame.setTextFill(Color.RED);
        });
        startGame.setOnMouseExited(event -> {
            startGame.setTextFill(Color.YELLOW);
        });

        Label exitGame = new Label("Quit");
        exitGame.setFont(Font.font("Courier New",30));
        exitGame.setTextFill(Color.YELLOW);
        exitGame.setCursor(Cursor.HAND);
        exitGame.setOnMouseEntered(event -> {
            exitGame.setTextFill(Color.RED);
        });
        exitGame.setOnMouseExited(event -> {
            exitGame.setTextFill(Color.YELLOW);
        });
        userMenu.setAlignment(Pos.CENTER);
        userMenu.getChildren().addAll(startGame,exitGame);

        startGame.setOnMouseClicked(event -> {
                    if (isStart)
                        return;

                    if (checkStatus != null)
                        checkStatus.stop();

                    isStart = true;

                    AnimationTimer fadeMenu = new AnimationTimer() {
                        private long lastUpdate = 0;
                        @Override
                        public void handle(long now) {
                                if (userMenu.getOpacity() <= 0){
                                    this.stop();
                                    userMenu.setVisible(false);
                                }else {
                                    userMenu.setOpacity(userMenu.getOpacity() - 0.01);
                                    lastUpdate = now;
                                }
                        }
                    };
                    fadeMenu.start();
                    this.scores = 0;
                    this.tempScores = 100;

                    /*Отлов нажатия клавиш и всавка в мапу*/
                    myscene.setOnKeyPressed(eventOne -> keys.put(eventOne.getCode(), true));
                    myscene.setOnKeyReleased(eventSecond -> {
                        keys.put(eventSecond.getCode(), false);
                    });

                    /*Создаем основные объекты*/
                    p = new Player(KeyCode.A,KeyCode.D);
                    a = new Asteroid();
                    b = new Bonus();
                    checkStatus = new AnimationTimer() {
                        @Override
                        public void handle(long now) {
                            /*Если хп кончилось то все убираем и выводим меню*/
                            if (p.getHealthRocket() == 0) {
                                speedStars = 0.2;
                                a.setEnd();
                                p.setEnd();
                                b.setEnd();
                                userMenu.setOpacity(0);
                                userMenu.setVisible(true);
                                AnimationTimer showMenu = new AnimationTimer() {
                                    @Override
                                    public void handle(long now) {
                                        if (userMenu.getOpacity() >= 1){
                                            this.stop();
                                            isStart = false;
                                        }else {
                                            userMenu.setOpacity(userMenu.getOpacity() + 0.05);
                                        }
                                    }
                                };
                                showMenu.start();
                                checkStatus.stop();
                            }
                            
                            /*Проверяем очки для дальнейшего увеличения скорости*/
                            addSpeed(a);
                        }
                    };
                    checkStatus.start();
                });

        exitGame.setOnMouseClicked(event -> {
            System.exit(0);
        });

        /*Выводим окно*/
        root.setCenter(userMenu);
        primaryStage.setScene(myscene);
        primaryStage.show();
    }

    private void addSpeed(Asteroid a){
        if (scores >= tempScores){
            tempScores += new Random().nextInt(100)+100;

            a.setSpeed(0.2);
            a.setTimeCreateAsteroid(60_000_000L);

            System.out.println("Level UP!");

            if ( speedStars <= 5.4 ){
                speedStars += 0.2;
            }
        }
    }

    public static void refreshHP(){
        root.getChildren().remove(HPimgView);

        HPimgView = new ImageView(new Image(Main.class.getResourceAsStream("resources/img/HP_5.png")));
        HPimgView.setTranslateX(200 + (5-p.getHealthRocket()) * 25);

        root.getChildren().addAll(HPimgView);
    }

    public static void refreshScores(int scores){
        Label nowScores = new Label("Score: " + String.valueOf(scores));

        nowScores.setPadding(new Insets(2,0,0,0));
        nowScores.setTextFill(Color.YELLOW);
        nowScores.setStyle("-fx-font-weight: bold");
        nowScores.setFont(Font.font("Courier New",17));

        root.setTop(nowScores);
    }

    private void setStars(){
        Random r = new Random();
        for ( int i = 0; i < 50; i++ ){
            Circle star = new Circle();
            star.setRadius(r.nextInt(2) + 1);
            star.setFill(Color.WHITE);
            star.setTranslateX(r.nextFloat() * ((widthWindow - 20) - 10) + 10);
            star.setTranslateY(r.nextFloat() * ((heightWindow - 20) - 10) + 10);
            star.setEffect(getBlur());
            stars.add(star);
            root.getChildren().addAll(star);
        }
    }

    static GaussianBlur getBlur(){
        GaussianBlur bb = new GaussianBlur();
        bb.setRadius(2.8);
        return bb;
    }

    /*Метод воспроизводит трэк*/
    void playMedia() {
        String mp3 = getClass().getResource("resources/music/space_3.mp3").toString();

        mediaPlayer = new MediaPlayer(new Media(mp3));
        mediaPlayer.setVolume(0.4);
        mediaPlayer.play();

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer(getRandomMedia());
                mediaPlayer.setVolume(0.4);
                mediaPlayer.setOnEndOfMedia(this);

                mediaPlayer.stop();
                mediaPlayer.play();
            }
        });

    }

    /*Метод возвращает рандомный трек*/
    Media getRandomMedia(){
        int choose = r.nextInt(3);
        String URI = "";

        while(choose == previousTrack){
            choose = r.nextInt(3);
        }

        if ( choose == 0 )
            URI = getClass().getResource("resources/music/space_1.mp3").toString();

        if ( choose == 1 )
            URI = getClass().getResource("resources/music/space_2.mp3").toString();

        if ( choose == 2 )
            URI = getClass().getResource("resources/music/space_3.mp3").toString();


        previousTrack = choose;

        return  new Media(URI);
    }

    public static void main(String[] args){
        launch(args);
    }


}
