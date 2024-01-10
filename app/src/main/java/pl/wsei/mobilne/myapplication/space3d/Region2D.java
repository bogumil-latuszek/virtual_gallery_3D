package pl.wsei.mobilne.myapplication.space3d;

public class Region2D {
    public final float x;
    public final float y;
    public final float dx;
    public final float dy;

    public Region2D(float x, float y, float dx, float dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }
    public boolean inside(float x, float y) {
        return (x >= this.x) && (x <= this.x + this.dx) && (y >= this.y) && (y <= this.y + this.dy);
    }
}