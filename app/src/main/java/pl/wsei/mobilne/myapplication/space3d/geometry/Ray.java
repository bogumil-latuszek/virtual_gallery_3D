package pl.wsei.mobilne.myapplication.space3d.geometry;

//import android.graphics.Point;

public class Ray {
    public final Point point;
    public final Vector3D vector3D;
    public Ray(Point point, Vector3D vector3D) {
        this.point = point;
        this.vector3D = vector3D;
    }
}