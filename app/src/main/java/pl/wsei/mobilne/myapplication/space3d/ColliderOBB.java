package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class ColliderOBB{

    public Vector3D v_front_upper_left;
    public Vector3D v_front_upper_right;
    public Vector3D v_front_down_left;
    public Vector3D v_front_down_right;
    public Vector3D v_back_upper_left;
    public Vector3D v_back_upper_right;
    public Vector3D v_back_down_left;
    public Vector3D v_back_down_right;

    public ColliderOBB(Vector3D v_front_upper_left, Vector3D v_front_upper_right, Vector3D v_front_down_left, Vector3D v_front_down_right, Vector3D v_back_upper_left, Vector3D v_back_upper_right, Vector3D v_back_down_left, Vector3D v_back_down_right) {
        this.v_front_upper_left = v_front_upper_left;
        this.v_front_upper_right = v_front_upper_right;
        this.v_front_down_left = v_front_down_left;
        this.v_front_down_right = v_front_down_right;
        this.v_back_upper_left = v_back_upper_left;
        this.v_back_upper_right = v_back_upper_right;
        this.v_back_down_left = v_back_down_left;
        this.v_back_down_right = v_back_down_right;
    }

    public boolean CollisionDetected(ColliderOBB otherCollider){

        ColliderOBB thisColliderAligned = this.GetAlignedToOwner();
        ColliderOBB otherColliderAligned = otherCollider.GetAlignedToOwner();
        //check 3 normals of this collider
            //first calculate the normal from one face
            //for each vertex of this collider, get the dot product of that vertex and projection axis, then save it as either thisColliderMinValue or thisColliderMaxValue if it exceeds either of them.
            //do the same for otherCollider
            //check if there's any overlap between value ranges of those 2 colliders. If there isn't, there's no collision. end function by returning false. Otherwise continue to next steps, but remember to save the axis and the amount of overlap. It can be used to revert the intersection
        //check 3 normals of other collider
        //check 9 intersections
        return false;
    }

    public  float[] getModelMatrix(){
        // call to delegate from owner:
        // return this.getModelMatrixFromOwner()
        return null;
    }
    public ColliderOBB GetAlignedToOwner(){
        float[] modelMatrix = this.getModelMatrix();
        ColliderOBB transformedThisCollider = this.GetTransformedOBB(this, modelMatrix);
        return  transformedThisCollider;
    }
    public ColliderOBB GetTransformedOBB(ColliderOBB colliderOBB, float[] transformationMatrix){
        Vector3D v_front_upper_left_transformed = transformVector3D(colliderOBB.v_front_upper_left, transformationMatrix) ;
        Vector3D v_front_upper_right_transformed = transformVector3D(colliderOBB.v_front_upper_right, transformationMatrix) ;
        Vector3D v_front_down_left_transformed = transformVector3D(colliderOBB.v_front_down_left, transformationMatrix) ;
        Vector3D v_front_down_right_transformed = transformVector3D(colliderOBB.v_front_down_right, transformationMatrix) ;
        Vector3D v_back_upper_left_transformed = transformVector3D(colliderOBB.v_back_upper_left, transformationMatrix) ;
        Vector3D v_back_upper_right_transformed = transformVector3D(colliderOBB.v_back_upper_right, transformationMatrix) ;
        Vector3D v_back_down_left_transformed = transformVector3D(colliderOBB.v_back_down_left, transformationMatrix) ;
        Vector3D v_back_down_right_transformed = transformVector3D(colliderOBB.v_back_down_right, transformationMatrix) ;
        return new ColliderOBB(v_front_upper_left_transformed, v_front_upper_right_transformed, v_front_down_left_transformed, v_front_down_right_transformed, v_back_upper_left_transformed, v_back_upper_right_transformed, v_back_down_left_transformed, v_back_down_right_transformed);
    }

    public Vector3D transformVector3D(Vector3D vector3D, float[] transformationMatrix){
        float[] vector4D = {vector3D.x, vector3D.y, vector3D.z, 0};
        float[] targetVector4D = new  float[4];
        Matrix.multiplyMV(targetVector4D, 0, transformationMatrix, 0, vector4D, 0 );
        return new Vector3D(targetVector4D[0], targetVector4D[1], targetVector4D[2]);
    }


    public Vector3D calculateFaceNormal(Vector3D upper_left, Vector3D down_left, Vector3D down_right){
        Vector3D fromDLToUL = down_left.substract(upper_left);
        Vector3D fromDLToDR = down_left.substract(down_right);
        Vector3D normalVector = fromDLToUL.crossProduct(fromDLToDR);
        return normalVector;
    }

    public Vector3D findFaceCenter(Vector3D upper_left, Vector3D upper_right, Vector3D down_left, Vector3D down_right){
        float x_center = (upper_left.x + upper_right.x + down_left.x + down_right.x)/4;
        float y_center = (upper_left.y + upper_right.y + down_left.y + down_right.y)/4;
        float z_center = (upper_left.z + upper_right.z + down_left.z + down_right.z)/4;
        Vector3D faceCenter = new Vector3D(x_center, y_center, z_center);
        return faceCenter;
    }

    public void draw(int aPositionLocation, int uColorLocation,
                     int uMatrixLocation, float[] modelMatrix, float[] viewProjectionMatrix){
        //use to visualise normals
        ColliderOBB transformedThisCollider = this.GetTransformedOBB(this, modelMatrix);
        //for now let's just draw normal vector of front face:
        Vector3D frontFaceNormalVector = this.calculateFaceNormal(v_front_upper_left, v_front_down_left, v_front_down_right);
        Vector3D frontFaceNormalOrigin = this.findFaceCenter(v_front_upper_left, v_front_upper_right, v_front_down_left, v_front_down_right);

        int COORDS_PER_VERTEX = 3;
        float[] lineColor = {0f, 0f, 1f}; // opengl requires color as float in range 0-1
        VertexArray vertexArray = new VertexArray(new float[]{
                frontFaceNormalOrigin.x, frontFaceNormalOrigin.y, frontFaceNormalOrigin.z,
                frontFaceNormalOrigin.x + frontFaceNormalVector.x, frontFaceNormalOrigin.y + frontFaceNormalVector.y, frontFaceNormalOrigin.z + frontFaceNormalVector.z});  // <-- Vertices

        GLES20.glUniform4f(uColorLocation, lineColor[0], lineColor[1], lineColor[2], 1.0f);

        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);

        GLES20.glLineWidth(5.0f);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);

        int offsetToStart_inVertexData = 0;
        int vertex_count = 2;
        GLES20.glDrawArrays(GLES20.GL_LINES, offsetToStart_inVertexData, vertex_count);

    }


}
