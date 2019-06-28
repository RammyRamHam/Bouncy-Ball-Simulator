import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public class Main extends Application{

    private static final int canvasWidth = 1230;
    private static final int canvasHeight = 700;
    private double frameLengthMillis = 1;

    private long previousMouseTime = 0;
    private GameLoop gameLoop = GameLoop.getInstance();
    private ArrayList<Particle> particles = new ArrayList<>();
    private boolean sliderUpdate = true;
    private double mouseVelocityY = 0;
    private double mouseVelocityX = 0;
    private double oldMouseX = 0;
    private double oldMouseY = 0;

    //private VBox vPane = new VBox();
    private GridPane mainPane = new GridPane();
    private GridPane settingsPane = new GridPane();
    private HBox selectorPane = new HBox();
    private Pane border = new Pane();
    /*private HBox sliderPane = new HBox();
    private VBox sliderVals = new VBox();
    private VBox sliderText = new VBox();
    private VBox sliders = new VBox();*/
    private RadioButton particleSelect = new RadioButton("Particle");
    private RadioButton orbitSelector = new RadioButton("Place Orbit");
    private ToggleGroup typeSelector = new ToggleGroup();
    private Button removeOrbit = new Button("Remove Orbit");
    private Button clearButton = new Button("Clear");
    //private Button placeOrbit = new Button("Place orbit");
    private CheckBox despawnCheckbox = new CheckBox("Despawn Particles");
    private CheckBox collsionCheckbox = new CheckBox("Collisions");
    private CheckBox elasticityCheckbox = new CheckBox("Elastic collisions");
    private CheckBox soundCheckbox = new CheckBox("Sound");
    private Text radiusTitle = new Text("Particle Radius: ");
    private Text yAccelTitle = new Text("Y Acceleration: ");
    private Text xAccelTitle = new Text("X Acceleration: ");
    private Text orbitVelocityTitle = new Text("Orbit Velocity: ");
    private TextField radiusVal = new TextField();
    private TextField yAccelVal = new TextField();
    private TextField xAccelVal = new TextField();
    private TextField orbitVelocityVal = new TextField();
    private TextArea infoText = new TextArea();
    private Slider radiusSlider = new Slider();
    private Slider yAccelSlider = new Slider();
    private Slider xAccelSlider = new Slider();
    private Slider orbitVelocitySlider = new Slider();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {

        window.setTitle("Big Bounce");
        window.setResizable(false);
        Scene scene = new Scene(mainPane);
        window.setScene(scene);
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //mainPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"), CornerRadii.EMPTY, Insets.EMPTY)));
        particleSelect.setToggleGroup(typeSelector);
        orbitSelector.setToggleGroup(typeSelector);
        particleSelect.setSelected(true);
        radiusSlider.setMinWidth(550);
        xAccelSlider.setMinWidth(550);
        yAccelSlider.setMinWidth(550);
        orbitVelocitySlider.setMinWidth(550);
        radiusSlider.setMin(1);
        radiusSlider.setMax(250);
        yAccelSlider.setMin(-100000);
        yAccelSlider.setMax(100000);
        xAccelSlider.setMin(-100000);
        xAccelSlider.setMax(100000);
        orbitVelocitySlider.setMin(-10000);
        orbitVelocitySlider.setMax(10000);
        radiusVal.setText("20");
        yAccelVal.setText("1000");
        xAccelVal.setText("0");
        orbitVelocityVal.setText("500");
        radiusVal.setMaxWidth(70);
        yAccelVal.setMaxWidth(70);
        xAccelVal.setMaxWidth(70);
        orbitVelocityVal.setMaxWidth(70);
        infoText.setMinWidth(100);
        infoText.setEditable(false);
        border.setMinHeight(2);
        border.setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"), CornerRadii.EMPTY, Insets.EMPTY)));
        infoText.setText(
                "This is a bouncy ball simulator.\n" +
                "Place particles by clicking in the area below.\n" +
                "You can throw particles by dragging and releasing.\n" +
                "You can place an orbit by selecting \"Place Orbit\". Particles will orbit around this.\n" +
                "Select \"Despawn Particles\" to toggle if particles despawn when at rest.\n" +
                "You can disable collisions between particles by selecting \"Collisions\".\n" +
                "You can use the sliders to the left to adjust aspects of the simulator such as:\n" +
                "Radius of placed particles, acceleration in the x or y direction, and the orbit velocity.");

        despawnCheckbox.setSelected(true);
        collsionCheckbox.setSelected(true);
        elasticityCheckbox.setSelected(false);
        soundCheckbox.setSelected(true);

        //sliderPane.setBorder(new Border(new BorderStroke(Paint.valueOf("Black"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));

        selectorPane.getChildren().addAll(particleSelect, orbitSelector, removeOrbit, clearButton, despawnCheckbox, collsionCheckbox, elasticityCheckbox, soundCheckbox);
        //slidersTitles.getChildren().addAll(radiusTitle, yAccelTitle, xAccelTitle);
        settingsPane.add(radiusTitle, 0, 0);
        settingsPane.add(yAccelTitle, 0, 1);
        settingsPane.add(xAccelTitle, 0, 2);
        settingsPane.add(orbitVelocityTitle, 0, 3);
        settingsPane.add(radiusVal, 1, 0);
        settingsPane.add(yAccelVal, 1, 1);
        settingsPane.add(xAccelVal, 1, 2);
        settingsPane.add(orbitVelocityVal, 1, 3);
        settingsPane.add(radiusSlider, 2, 0);
        settingsPane.add(yAccelSlider, 2, 1);
        settingsPane.add(xAccelSlider, 2, 2);
        settingsPane.add(orbitVelocitySlider, 2, 3);
        //sliderText.getChildren().addAll(radiusTitle, yAccelTitle, xAccelTitle);
        //sliderVals.getChildren().addAll(radiusVal, yAccelVal, xAccelVal);
        //sliders.getChildren().addAll(radiusSlider, yAccelSlider, xAccelSlider);
        //sliderPane.getChildren().addAll(sliderText, sliderVals, sliders);
        mainPane.add(selectorPane, 0, 0);
        mainPane.add(settingsPane, 0, 1);
        mainPane.add(border, 0, 2, GridPane.REMAINING, 1);
        mainPane.add(canvas, 0, 3, GridPane.REMAINING, 1);
        mainPane.add(infoText, 1, 0, 1, 2);


        settingsPane.setPadding(new Insets(10));
        particleSelect.setPadding(new Insets(10));
        orbitSelector.setPadding(new Insets(10));
        removeOrbit.setPadding(new Insets(10));
        clearButton.setPadding(new Insets(10));
        despawnCheckbox.setPadding(new Insets(10));
        collsionCheckbox.setPadding(new Insets(10));
        elasticityCheckbox.setPadding(new Insets(10));
        soundCheckbox.setPadding(new Insets(10));

        removeOrbit.setOnMouseClicked(e -> {
            gameLoop.setOrbit(null);
        });

        clearButton.setOnMouseClicked(e -> {
            gameLoop.clearParticles();
            gameLoop.setOrbit(null);
        });

        /*placeOrbit.setOnMouseClicked(e -> {
            gc.fillOval(e.getX() - orbitRadius, e.getY() - orbitRadius, orbitRadius * 2, orbitRadius * 2);
            gameLoop.setOrbit(e);
        });*/

        canvas.setOnMouseDragged(e -> {
            double deltaTime = (System.nanoTime() - previousMouseTime)/1e9;
            mouseVelocityY = (e.getY() - oldMouseY) / deltaTime;
            mouseVelocityX = (e.getX() - oldMouseX) / deltaTime;

            oldMouseY = e.getY();
            oldMouseX = e.getX();
            previousMouseTime = System.nanoTime();
        });

        canvas.setOnMouseReleased(e -> {

            if (particleSelect.isSelected()) {
                gameLoop.addParticle(e, mouseVelocityY, mouseVelocityX);
            } else if (orbitSelector.isSelected()) {
                gameLoop.setOrbit(new Orbit(e.getX(), e.getY()));
            }

            mouseVelocityY = 0;
            mouseVelocityX = 0;
        });

/*
        particleSelect.setOnMouseClicked(e -> {
            gameLoop.setOrbit(null);
        });
*/

        radiusSlider.setOnMousePressed(e -> {
            sliderUpdate = false;
        });

        radiusSlider.setOnMouseReleased(e -> {
            //radiusVal.setText(String.format("%.2f", radiusSlider.getValue()));
            sliderUpdate = true;
        });

        yAccelSlider.setOnMousePressed(e -> {
            sliderUpdate = false;
        });

        yAccelSlider.setOnMouseReleased(e -> {
            //yAccelVal.setText(String.format("%.2f", yAccelSlider.getValue()));
            sliderUpdate = true;
        });

        xAccelSlider.setOnMousePressed(e -> {
            sliderUpdate = false;
        });

        xAccelSlider.setOnMouseReleased(e -> {
            //AccelVal.setText(String.format("%.2f", xAccelSlider.getValue()));
            sliderUpdate = true;
        });

        orbitVelocitySlider.setOnMousePressed(e -> {
            sliderUpdate = false;
        });

        orbitVelocitySlider.setOnMouseReleased(e -> {
            //AccelVal.setText(String.format("%.2f", xAccelSlider.getValue()));
            sliderUpdate = true;
        });

        window.show();

        gameLoop.setCanvasDimensions(canvasWidth, canvasHeight);
        gameLoop.setRadiusSlider(radiusSlider);
        gameLoop.setyAccelSlider(yAccelSlider);
        gameLoop.setxAccelSlider(xAccelSlider);
        gameLoop.setOrbitVelocitySlider(orbitVelocitySlider);
        gameLoop.runGameLoop(window);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(frameLengthMillis), event -> {
            gameLoop.setDespawnParticles(despawnCheckbox.isSelected());
            gameLoop.setCollisions(collsionCheckbox.isSelected());
            gameLoop.setElasticity(elasticityCheckbox.isSelected());
            gameLoop.setPlaySound(soundCheckbox.isSelected());
            gc.clearRect(0, 0, canvasWidth, canvasHeight);

            particles = gameLoop.getParticles();
            for (Particle p : particles) {
                gc.setFill(p.getColor());
                gc.fillOval(p.getPosition().x - p.getRadius(), p.getPosition().y - p.getRadius(), p.getRadius() * 2, p.getRadius() * 2);
            }

            if (gameLoop.getOrbitPlaced()) {
                gc.setFill(gameLoop.getOrbit().getColor());
                gc.fillOval(gameLoop.getOrbit().getOrbitX() - gameLoop.getOrbit().getRadius(), gameLoop.getOrbit().getOrbitY() - gameLoop.getOrbit().getRadius(),
                        gameLoop.getOrbit().getRadius() * 2, gameLoop.getOrbit().getRadius() * 2);
            }

            updateSlider(radiusSlider, radiusVal);
            updateSlider(yAccelSlider, yAccelVal);
            updateSlider(xAccelSlider, xAccelVal);
            updateSlider(orbitVelocitySlider, orbitVelocityVal);
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void updateSlider(Slider slider, TextField text) {
        if (!text.getText().equals("") && sliderUpdate) {
            try {
                slider.setValue(Double.parseDouble(text.getText()));
            } catch (NumberFormatException e) {
                /*e.printStackTrace();*/
            }
        } else if(text.getText().equals("")) {

        } else {
            text.setText(String.format("%.2f", slider.getValue()));
        }

    }

}
