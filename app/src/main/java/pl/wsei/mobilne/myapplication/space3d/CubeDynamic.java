package pl.wsei.mobilne.myapplication.space3d;

public class CubeDynamic extends Cuboid{
    public CubeDynamic(float dx, float dy, float dz, float locateAtX, float locateAtZ) {
        super(dx, dy, dz, locateAtX, locateAtZ);
    }
    private float position_x;
    private float position_y;
    private float position_z;

    private float rotation_x;
    private float rotation_y;
    private float rotation_z;

    private ColliderOBB collider;
}
