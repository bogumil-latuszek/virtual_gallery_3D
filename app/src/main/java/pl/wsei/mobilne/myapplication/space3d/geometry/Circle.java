package pl.wsei.mobilne.myapplication.space3d.geometry;

public class Circle {
    private Point center;
    private float radius;

    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }
    public void translate(float dx, float dy){
        this.center.translate(new Vector3D(dx, dy, 0));
    }

    public Point getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

    //use this only in 2D(it doesn't factor z coordinate)
    public boolean checkIfPointInside(Point point){
        float distanceToCenter = center.findDistanceToPoint(point);
        if(distanceToCenter <= radius){
            return  true;
        }
        return false;
    }

    public Vector3D getVectorToPointFromCenter(Point point){
        float dx = point.x - center.x;
        float dy = point.y - center.y;
        return new Vector3D(dx, dy, 0);
    }
}
