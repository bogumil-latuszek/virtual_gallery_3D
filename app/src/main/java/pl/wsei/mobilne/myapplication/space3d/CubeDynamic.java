package pl.wsei.mobilne.myapplication.space3d;

import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class CubeDynamic extends Cuboid{
    public CubeDynamic(float dx, float dy, float dz, float locateAtX, float locateAtZ) {
        super(dx, dy, dz, locateAtX, locateAtZ);
        //float[] vs = this.vertexSequenceForDrawingFaces;
        Vector3D v_front_upper_left = new Vector3D(vertexArray.get(0),vertexArray.get(1),vertexArray.get(2));
        Vector3D v_front_upper_right = new Vector3D(vertexArray.get(3),vertexArray.get(4),vertexArray.get(5));
        Vector3D v_front_down_left = new Vector3D(vertexArray.get(6),vertexArray.get(7),vertexArray.get(8));
        Vector3D v_front_down_right = new Vector3D(vertexArray.get(9),vertexArray.get(10),vertexArray.get(11));
        Vector3D v_back_upper_left = new Vector3D(vertexArray.get(12),vertexArray.get(13),vertexArray.get(14));
        Vector3D v_back_upper_right = new Vector3D(vertexArray.get(15),vertexArray.get(16),vertexArray.get(17));
        Vector3D v_back_down_left = new Vector3D(vertexArray.get(18),vertexArray.get(19),vertexArray.get(20));
        Vector3D v_back_down_right= new Vector3D(vertexArray.get(21),vertexArray.get(22),vertexArray.get(23));
        this.collider = new ColliderOBB(v_front_upper_left, v_front_upper_right, v_front_down_left, v_front_down_right, v_back_upper_left, v_back_upper_right, v_back_down_left, v_back_down_right );
    }
    private float position_x;
    private float position_y;
    private float position_z;

    private float rotation_x;
    private float rotation_y;
    private float rotation_z;

    private ColliderOBB collider;


    @Override
    public void draw(int aPositionLocation, int uColorLocation, int uMatrixLocation, float[] viewProjectionMatrix) {
        super.draw(aPositionLocation, uColorLocation, uMatrixLocation, viewProjectionMatrix);
        collider.draw(aPositionLocation, uColorLocation,uMatrixLocation, this.modelMatrix, viewProjectionMatrix);
    }
}
