import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import javax.print.DocFlavor;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.URL;

public class Particle {

    private double radius;
    private double mass;
    private final double collisionDampening = 0.7;
    private final double deleteTime = 1.5e9;
    private final double atRestAvgThreshold = 10;
    private final double atRestThreshold = 2;
    private double elasticity = 0.7;
    private boolean isElastic = false;
    private boolean playSound = true;
    private Thread soundMachine;
    private Media sound = new Media(new File("./Resources/bonk.wav").toURI().toString());
    private MediaPlayer mediaPlayer = new MediaPlayer(sound);

    private Point2D.Double position = new Point2D.Double();
    private Point2D.Double velocity = new Point2D.Double();
    private Point2D.Double acceleration = new Point2D.Double();
    private Color color;
    private int printCount = 0;
    private long timeSinceStopped = System.nanoTime();
    private long deltaTimeSinceStopped = 0;
    private boolean toBeDeleted = false;
    private boolean atRest = false;
    private long velocityAvgTotal = 0;
    private double velocityAvg = 0;
    private int velocityNum = 0;


    public Particle (double x, double y, double radius) {
        position.x = x;
        position.y = y;
        double red, green, blue;
        do {
            red = Math.random();
            green = Math.random();
            blue = Math.random();
        } while ((red+green+blue)/3 > 2.4);
        color = new Color(red, green, blue, 1);
        this.radius = radius;
        mass = radius*radius;
    }

    public Particle (double x, double y, double radius, double velocityY, double velocityX) {
        position.x = x;
        position.y = y;
        velocity.x = velocityX;
        velocity.y = velocityY;
        double red, green, blue;
        do {
            red = Math.random();
            green = Math.random();
            blue = Math.random();
        } while ((red+green+blue)/3 > 2.4);
        color = new Color(red, green, blue, 1);
        this.radius = radius;
        mass = radius*radius;
    }

    public void updatePos (double deltaTime) {
        deltaTime /= 1e9;
        position.x += deltaPosition(acceleration.x, velocity.x, deltaTime);
        position.y += deltaPosition(acceleration.y, velocity.y, deltaTime);
        velocity.x += deltaVelocity(acceleration.x, deltaTime);
        velocity.y += deltaVelocity(acceleration.y, deltaTime);

/*        if (Math.hypot(velocity.y, velocity.x) < 5000) {
            atRest = true;
        } else {
            atRest = false;
        }*/

        if (Math.hypot(velocity.y, velocity.x) <= atRestAvgThreshold) {
            velocityAvgTotal += Math.hypot(velocity.y, velocity.x);
            velocityNum++;
            velocityAvg = velocityAvgTotal/velocityNum;
            if (velocityAvg < atRestThreshold) {
                atRest = true;
            }
        }

        if (Math.hypot(velocity.y, velocity.x) > atRestAvgThreshold) {
            velocityAvgTotal = 0;
            velocityAvg = 0;
            velocityNum = 0;
            atRest = false;
        }

        if (atRest) {
            deltaTimeSinceStopped = System.nanoTime() - timeSinceStopped;
        } else {
            timeSinceStopped = System.nanoTime();
        }
        if (deltaTimeSinceStopped > deleteTime) {
            toBeDeleted = true;
        }

/*        if (atRest) {
            deltaTimeSinceStopped = System.currentTimeMillis() - timeSinceStopped;
        } else {
            timeSinceStopped = System.currentTimeMillis();
        }
        if (deltaTimeSinceStopped/1000.0 > 8) {
            toBeDeleted = true;
        }*/
        //System.out.println(velocity.y);
    }

    public Point2D.Double getPosition() {
        return position;
    }
    public Point2D.Double getVelocity() {
        return velocity;
    }
    public Point2D.Double getAcceleration() {
        return acceleration;
    }

    public double getVelocityMagnitude() {
        return Math.hypot(velocity.x, velocity.y);
    }
    public double getAccelerationMagnitude() {
        return Math.hypot(acceleration.x, acceleration.y);
    }

    public double getVelocityAngle() {
        return Math.atan2(velocity.y, velocity.x);
    }

    public double getAccelerationAngle() {
        return Math.atan2(acceleration.y, acceleration.x);
    }


    public Color getColor() {
        return color;
    }
    private double deltaPosition(double accel, double velocity, double time) {
        return 0.5 * accel*time*time + velocity*time;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public boolean getToBeDeleted() {
        return toBeDeleted;
    }

    private double deltaVelocity(double accel,  double time) {
        return accel*time;
    }

    public void setElasticy(boolean isElastic) {
        this.isElastic = isElastic;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public void bounceOffWalls(int width, int height) {
        if (position.x - radius < 0) {
            if (Math.abs(velocity.x) > 25) {
                playSound();
            }
            position.x = radius;
            velocity.x = Math.abs(velocity.x) * collisionDampening;
        }

        if (position.y - radius < 0) {
            if (Math.abs(velocity.y) > 25) {
                playSound();
            };
            position.y = radius;
            velocity.y = Math.abs(velocity.y) * collisionDampening;
        }

        if (position.x > width - radius) {
            if (Math.abs(velocity.x) > 25) {
                playSound();
            }
            position.x = width - radius /*- (position.x - (width - radius))*/;
            velocity.x = -Math.abs(velocity.x) * collisionDampening;
        }

        if (position.y > height - radius) {
            if (Math.abs(velocity.y) > 25) {
                playSound();
            }
            position.y = height - radius /*- (position.y - (height - radius))*/;
            velocity.y = /*atRest ? 0 :*/ -Math.abs(velocity.y) * collisionDampening;
        }
    }

    public void bounceOffParticle(Particle otherParticle) {
        elasticity = isElastic? 1 : 0.7;
        Point2D.Double otherParticlePos = otherParticle.getPosition();
        double centerDistance = position.distance(otherParticlePos);
        if (centerDistance < (radius + otherParticle.getRadius())) {
            playSound();
            //color = new Color(1, 1, 0.4, 1);
            double bouncePlaneAngle = Math.atan2(otherParticlePos.y - position.y, otherParticlePos.x - position.x);
/*            if (printCount ==0) {
                System.out.println(String.format("Bounce Angle: %.2f Diff x: %.2f Diff y: %.2f", Math.toDegrees(bouncePlaneAngle), (centerDistance - (radius + otherParticle.radius))*Math.cos(bouncePlaneAngle), (centerDistance - (radius + otherParticle.radius))*Math.sin(bouncePlaneAngle)) );
                printCount++;
            }*/

            /*if (printCount ==0) {
                //System.out.println(String.format("Velocity Angle: %.2f", getVelocityAngle()));
                System.out.println(bouncePlaneAngle);
                printCount++;
            }
*/
            double bouncePlaneAngleSecond = bouncePlaneAngle + Math.PI;
            double normalVectorComponentAngle = bouncePlaneAngle - getVelocityAngle();
            double firstXVelocity = getVelocityMagnitude()*Math.cos(normalVectorComponentAngle)*Math.cos(bouncePlaneAngle);
            double firstYVelocity = getVelocityMagnitude()*Math.cos(normalVectorComponentAngle)*Math.sin(bouncePlaneAngle);
            //System.out.println(Math.toDegrees(bouncePlaneAngle));
            //System.out.println(Math.toDegrees(bouncePlaneAngle));

            double secondXVelocity = otherParticle.getVelocityMagnitude()*Math.cos(normalVectorComponentAngle)*Math.cos(bouncePlaneAngleSecond);
            double secondYVelocity = otherParticle.getVelocityMagnitude()*Math.cos(normalVectorComponentAngle)*Math.sin(bouncePlaneAngleSecond);
            velocity.x -= firstXVelocity;
            velocity.y -= firstYVelocity;
            otherParticle.velocity.x -= secondXVelocity;
            otherParticle.velocity.y -= secondYVelocity;

            double firstResultX = (firstXVelocity*mass + otherParticle.getMass()*(2*secondXVelocity - firstXVelocity))/(mass + otherParticle.getMass());
            double secondResultX = firstXVelocity + firstResultX - secondXVelocity;
            double firstResultY = (firstYVelocity*mass + otherParticle.getMass()*(2*secondYVelocity - firstYVelocity))/(mass + otherParticle.getMass());
            double secondResultY = firstYVelocity + firstResultY - secondYVelocity;

            velocity.x += elasticity*firstResultX;
            velocity.y += elasticity*firstResultY;
            otherParticle.velocity.x += elasticity*secondResultX;
            otherParticle.velocity.y += elasticity*secondResultY;

            position.x -= ((radius + otherParticle.radius) - centerDistance)*Math.cos(bouncePlaneAngle)/2;
            position.y -= ((radius + otherParticle.radius) - centerDistance)*Math.sin(bouncePlaneAngle)/2;

            otherParticle.position.x += ((radius + otherParticle.radius) - centerDistance)*Math.cos(bouncePlaneAngle)/2;
            otherParticle.position.y += ((radius + otherParticle.radius) - centerDistance)*Math.sin(bouncePlaneAngle)/2;

            //System.out.println(Math.toDegrees(bouncePlaneAngle));
            //double normalVectorComponentAngle = bouncePlaneAngle - getVelocityAngle();
            //double normalVectorComponentMagnitutde = getVelocityMagnitude()*Math.cos(normalVectorComponentAngle);


            /*double finalAngleFirst = getVelocityAngle() + ((getVelocityAngle() - bouncePlaneAngle) >= 0?
                    2*(90 - Math.abs(getVelocityAngle() - bouncePlaneAngle))
                    : -2*(90 - Math.abs(getVelocityAngle() - bouncePlaneAngle)));
            velocity.x = getVelocityMagnitude()*Math.cos(finalAngleFirst);
            velocity.y = getVelocityMagnitude()*Math.sin(finalAngleFirst);

            double finalAngleSecond = otherParticle.getVelocityAngle() + ((otherParticle.getVelocityAngle() - bouncePlaneAngleSecond) >= 0?
                    2*(90 - Math.abs(otherParticle.getVelocityAngle() - bouncePlaneAngleSecond))
                    : -2*(90 - Math.abs(otherParticle.getVelocityAngle() - bouncePlaneAngleSecond)));
            otherParticle.velocity.x = otherParticle.getVelocityMagnitude()*Math.cos(finalAngleSecond);
            otherParticle.velocity.y = otherParticle.getVelocityMagnitude()*Math.sin(finalAngleSecond);*/
        }

    }

    private void playSound(){
        if (soundMachine == null && playSound) {
            soundMachine = new Thread(() -> {
                mediaPlayer.play();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {}
                mediaPlayer.stop();
                soundMachine = null;
            });
            soundMachine.start();
        }

    }


}
