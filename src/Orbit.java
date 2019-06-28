import javafx.scene.paint.Color;

import java.awt.geom.Point2D;

public class Orbit {

    private double radius = 10;
    private double velocityMagnitude;

    private Point2D.Double position = new Point2D.Double();
    private Color color = new Color(0, 0, 0, 1);



    public Orbit(double x, double y) {
        position.x = x;
        position.y = y;
    }

    public Orbit(double x, double y, double radius) {
        position.x = x;
        position.y = y;

        this.radius = radius;
    }

    public double getOrbitX() {
        return position.x;
    }

    public double getOrbitY() {
        return position.y;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public double getVelocityMagnitude() {
        return velocityMagnitude;
    }

    public Color getColor() {
        return color;
    }

    public double getRadius() {
        return radius;
    }
}
