package pl.wsei.mobilne.myapplication.space3d.geometry;

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
}