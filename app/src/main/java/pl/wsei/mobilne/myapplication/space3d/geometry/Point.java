package pl.wsei.mobilne.myapplication.space3d.geometry;

import static java.lang.Math.sqrt;

public class Point{
    public final float x;
    public final float y;
    public final float z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    public Point translate(Vector vector) {
        return new Point(
                x + vector.x,
                y + vector.y,
                z + vector.z);
    }

    public float findDistanceToPoint(Point b){
        float dx = this.x - b.x;
        float dy = this.y - b.y;
        float distance = (float) sqrt(dx * dx + dy * dy); //TODO:check if sqrt takes float as param
        return distance;
    }
}