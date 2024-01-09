package pl.wsei.mobilne.myapplication.space3d.geometry;

//import android.graphics.Point;

public class Ray {
    public final Point point;
    public final Vector vector;
    public Ray(Point point, Vector vector) {
        this.point = point;
        this.vector = vector;
    }
}