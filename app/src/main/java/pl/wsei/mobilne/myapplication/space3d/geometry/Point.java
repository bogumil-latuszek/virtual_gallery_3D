package pl.wsei.mobilne.myapplication.space3d.geometry;

import static java.lang.Math.sqrt;

public class Point{
    public  float x;
    public  float y;
    public  float z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    public Point translate(Vector3D vector3D) { // this is used in ray collision
        return new Point(
                x + vector3D.x,
                y + vector3D.y,
                z + vector3D.z);
    }

    public void  move(float dx, float dy){
        move( dx, dy,0f);
    }
    public void move(float dx, float dy, float dz){
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    public float findDistanceToPoint(Point b){
        float dx = this.x - b.x;
        float dy = this.y - b.y;
        float distance = (float) sqrt(dx * dx + dy * dy); //TODO:check if sqrt takes float as param
        return distance;
    }
}