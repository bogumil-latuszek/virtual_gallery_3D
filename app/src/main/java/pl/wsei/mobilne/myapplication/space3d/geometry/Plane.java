package pl.wsei.mobilne.myapplication.space3d.geometry;

//import android.graphics.Point;

public class Plane {
    public final Point point;
    public final Vector normal;
    public Plane(Point point, Vector normal) {
        this.point = point;
        this.normal = normal;
    }
}