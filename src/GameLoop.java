import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.ArrayList;

public class GameLoop {

    private static GameLoop instance = null;

    private GameLoop() {}

    public static GameLoop getInstance() {
        if (instance == null) {
            instance = new GameLoop();
        }
        return instance;
    }

    private long previousTime;
    private boolean particleToBeAdded = false;
    private boolean clearParticles = false;
    private boolean despawnParticles = true;
    private boolean collisions = true;
    private boolean elasticity = true;
    private boolean playSound = true;
    private ArrayList<Particle> particles = new ArrayList<>();
    private Orbit orbit;
    private javafx.scene.input.MouseEvent mouse;
    private int canvasWidth = 0;
    private int canvasHeight = 0;
    double velocityY, velocityX;

    private Slider radiusSlider;
    private Slider yAccelSlider;
    private Slider xAccelSlider;
    private Slider orbitVelocitySlider;

    public void runGameLoop(Stage window) {

        previousTime = System.nanoTime();
        new Thread (() -> {
            ArrayList<Particle> particles = new ArrayList<>();
            ArrayList<Integer> particlesToRemove = new ArrayList<>();
            while (window.isShowing()) {
                try {
                    Thread.sleep(0, 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long deltaTime = (System.nanoTime() - previousTime);

                if (particleToBeAdded) {
                    particles.add(new Particle(mouse.getX(), mouse.getY(), radiusSlider.getValue(), velocityY, velocityX));
                    particleToBeAdded = false;
                }

                for (Particle p : particles) {
                    if (!p.getToBeDeleted() || !despawnParticles) {
                        p.setElasticy(elasticity);
                        p.setPlaySound(playSound);
                        if (getOrbitPlaced()) {
                            double angleFromCenter = Math.atan2(p.getPosition().y - orbit.getOrbitY(), p.getPosition().x - orbit.getOrbitX());
                            p.getVelocity().setLocation(-Math.sin(angleFromCenter)*orbitVelocitySlider.getValue(), Math.cos(angleFromCenter)*orbitVelocitySlider.getValue());
                        } else {
                            p.getAcceleration().setLocation(xAccelSlider.getValue(), yAccelSlider.getValue());
                        }
                        p.updatePos(deltaTime);
                        p.bounceOffWalls(canvasWidth, canvasHeight);
                    } else {
                        particlesToRemove.add(particles.indexOf(p));
                        //System.out.println(particles.indexOf(p));
                    }
                }

                for (int i = 0; i < particlesToRemove.size(); i++) {
                    particles.remove((particlesToRemove.get(i)) - i);
                }
                particlesToRemove.clear();

                if (collisions) {
                    for (int i = 0; i < particles.size() - 1; i++) {
                        for (int j = i + 1; j < particles.size(); j++) {
                            particles.get(j).bounceOffParticle(particles.get(i));
                        }
                    }
                }

                if (clearParticles) {
                    particles.clear();
                    clearParticles = false;
                }

                this.particles = particles;

                previousTime = System.nanoTime();
            }
        }).start();
    }

    public void addParticle(javafx.scene.input.MouseEvent e, double velocityY, double velocityX) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        particleToBeAdded = true;
        mouse = e;

    }

    public void clearParticles() {
        clearParticles = true;
    }

    public void setDespawnParticles(boolean despawnParticles) {
        this.despawnParticles = despawnParticles;
    }
    public void setCollisions(boolean collisions) {
        this.collisions = collisions;
    }

    public void setElasticity(boolean elasticity) {
        this.elasticity = elasticity;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public void setCanvasDimensions(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public void setOrbit(Orbit orbit) {
        this.orbit = orbit;
    }

    public Orbit getOrbit() {
        return orbit;
    }

    public boolean getOrbitPlaced() {
        return orbit == null? false : true;
    }

    public ArrayList<Particle> getParticles() {
        return particles;
    }

    public void setRadiusSlider(Slider radiusSlider) {
        this.radiusSlider = radiusSlider;
    }

    public void setyAccelSlider(Slider yAccelSlider) {
        this.yAccelSlider = yAccelSlider;
    }

    public void setxAccelSlider(Slider xAccelSlider) {
        this.xAccelSlider = xAccelSlider;
    }
    public void setOrbitVelocitySlider(Slider orbitVelocitySlider) {
        this.orbitVelocitySlider = orbitVelocitySlider;
    }
}
