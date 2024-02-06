package pl.wsei.mobilne.myapplication.space3d.geometry;

public class Vector4D {
    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public Vector4D(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector3D convertTo3DVector(){ //4th dimension(w) is lost
        return new Vector3D(this.x, this.y, this.z);
    }
}
