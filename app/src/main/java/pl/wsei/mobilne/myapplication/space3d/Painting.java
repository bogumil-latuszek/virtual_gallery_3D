package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class Painting {
    //model matrix defines position, rotation etc
    private int textureID;
    private float[] modelMatrix= new float[16];
    private float[] rotationMatrix= new float[16];
    private float[] translationMatrix= new float[16];
    private Point centralPoint;
    private float width;
    private float height;
    private static final int COORDS_PER_VERTEX = 3;  // X, Y, Z
    private static final int COORDS_PER_TEXTURE_COORDINATE = 2;  // S, T
    private final VertexArray vertexArray;  // <-- Vertices
    private final VertexArray texturePointsArray;  // <-- texture coordinates
    private final IndexArray vertexSequenceForDrawingRectangle;

    public void rotate(float degree ){

        Matrix.rotateM(rotationMatrix, 0, degree, 0, 1f,0);
    }

    public void move(float dx, float dy, float dz){
        Matrix.translateM(translationMatrix, 0, dx, dy, dz);
    }

    public Painting(Point centralPoint, float width, float height) {
        //Painting's owner will give modelMatrix to it
        translationMatrix = new float[16];
        Matrix.setIdentityM(translationMatrix, 0);
        rotationMatrix = new float[16];
        Matrix.setIdentityM(rotationMatrix, 0);

        this.centralPoint = centralPoint;
        this.width = width;
        this.height = height;

        float locateAtX = centralPoint.x;
        float locateAtY = centralPoint.y;
        float locateAtZ = centralPoint.z;
        float dx = width/2;
        float dy = height/2;

//        float[] rotationMatrix = createRotationMatrix(rotationDegree);
        float[] modelVertexArray = new float[]{
                0-dx,  0+dy, 0,        // (0) Top-left  X, Y
                0+dx,  0+dy, 0,       // (1) Top-right
                0-dx,  0-dy, 0,       // (2) Bottom-left
                0+dx,  0-dy, 0          // (3) Bottom-right
        };
        Matrix.translateM(translationMatrix,0, locateAtX, locateAtY, locateAtZ);
//        modelVertexArray = rotateModel(rotationMatrix, modelVertexArray);
        vertexArray = new VertexArray(modelVertexArray);

        texturePointsArray = new VertexArray(new float[]{
                0f,  0f,                                 // (0) Top-left  S, T
                1f,  0f,                                 // (1) Top-right
                0f,  1f,                                 // (2) Bottom-left
                1f,  1f,                                 // (3) Bottom-right
        });
        vertexSequenceForDrawingRectangle = new IndexArray(new byte[]{
                // Front - counter-clockwise (front-facing)
                1, 0, 3,
                0, 2, 3,
        });
    }

//    public float[] createRotationMatrix(float degree){
//        float[] rotationMatrix = new float[16];
//        Matrix.setIdentityM(rotationMatrix,0);
//        Matrix.rotateM(rotationMatrix, 0, degree, 0, 1f,0);
//        return  rotationMatrix;
//    }
//    public float[] rotateModel(float[] rotationMatrix, float[] modelVertexArray){
//        float[] tempModelVertexArray = new float[12];
//        Matrix.multiplyMV(modelVertexArray,0, rotationMatrix, 0, modelVertexArray, 0);
//        Matrix.multiplyMV(modelVertexArray,3, rotationMatrix, 0, modelVertexArray, 3);
//        Matrix.multiplyMV(modelVertexArray,6, rotationMatrix, 0, modelVertexArray, 6);
//        Matrix.multiplyMV(modelVertexArray,9, rotationMatrix, 0, modelVertexArray, 9);
//        return  tempModelVertexArray;
//    }

    public void setTextureID(int textureID){
        this.textureID = textureID;
    }
    public void draw(int aPositionLocation, int aTextureCoordinatesLocation,
                     int uTextureUnitLocation, int uMatrixLocation, float[] viewProjectionMatrix) {

        Matrix.multiplyMM(modelMatrix,0, translationMatrix,0, rotationMatrix,0);
        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);
        prepareDataSource_forTextureCoordinateAttribute(aTextureCoordinatesLocation);

        // recalculate vertices per matrices
        float[] modelViewProjectionMatrix = new float[16];
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0); //what happens if we change the order?
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelViewProjectionMatrix, 0);


//        // View Controls (UI elements) are drawn directly in OpenGL normalized coordinates
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelMatrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, this.textureID);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);

        int nbIndexes4triangles = 2*3;
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nbIndexes4triangles, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceForDrawingRectangle.indexesBuffer);
        Log.d("position of painting:", centralPoint.toString());

    }
//    public void drawColored(int aPositionLocation, int uColorLocation,
//                            int uMatrixLocation, float[] viewProjectionMatrix) {
//
//        // prepare vertices buffer (floats --> bytes)
//        prepareDataSource_forPositionAttribute(aPositionLocation);
//
//        // recalculate vertices per matrices
//        float[] modelViewProjectionMatrix = new float[16];
//        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0); //what happens if we change the order?
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelViewProjectionMatrix, 0);
//
//        // Opacity must be enabled and opacity-mode selected
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//
//        float[] faceColor = {235f/255f, 12f/255f, 49f/255f};
//        // another words: set color for drawing faces
//        GLES20.glUniform4f(uColorLocation, faceColor[0], faceColor[1], faceColor[2], 0.5f);
//
//        int nbIndexes4triangles = 2*3;
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nbIndexes4triangles, GLES20.GL_UNSIGNED_BYTE, vertexSequenceForDrawingRectangle.indexesBuffer);
//
//        GLES20.glDisable(GLES20.GL_BLEND);
//       // Log.d("position of painting:", centralPoint.toString());
//
//    }

    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        // bind data to shader variable
        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);
    }

    public void prepareDataSource_forTextureCoordinateAttribute(int aTextureCoordinatesLocation) {
        // bind data to shader variable
        texturePointsArray.setVertexAttribPointer(0, aTextureCoordinatesLocation,
                COORDS_PER_TEXTURE_COORDINATE, 0);
    }

//    public void startTransforming() {
//        Matrix.setIdentityM(modelMatrix, 0);
//    }

}
