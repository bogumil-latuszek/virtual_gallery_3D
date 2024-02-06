package pl.wsei.mobilne.myapplication.space3d.geometry;

//import android.graphics.Point;

public class Plane {
    public final Point point;
    public final Vector3D normal;
    public Plane(Point point, Vector3D normal) {
        this.point = point;
        this.normal = normal;
    }
}