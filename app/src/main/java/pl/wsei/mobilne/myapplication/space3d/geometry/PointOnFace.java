package pl.wsei.mobilne.myapplication.space3d.geometry;

public class PointOnFace {
    public Face face;
    public Point point;

    public PointOnFace(Face face, Point point) {
        this.face = face;
        this.point = point;
    }
    @Override
    public String toString() {
        return this.point.toString() + " at " + this.face.toString();
    }
}