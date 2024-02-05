package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;

import pl.wsei.mobilne.myapplication.Constants;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;

public class Rectangle2D {
    ///////////////////////////////////////////////////////
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    ////////////////////////////////////////////////////
    private static final int COORDS_PER_VERTEX = 3;
    private final float[] modelMatrix = new float[16]; //a 4x4 matrix
    private final VertexArray vertexArray;  // <-- Vertices
    private final IndexArray vertexSequenceForDrawingRectangle;

    private Point centralPoint;
    private float width;
    private float height;

    private  int numberOfIndexesPerFace = 3*2;//num. of vertexes per triange * triangles per face


    public Rectangle2D(Point centralPoint, float width, float height) {
        this.centralPoint = centralPoint;
        this.width = width;
        this.height = height;

        //float [] vertices = new float[4 * COORDS_PER_VERTEX];

        float locateAtX = centralPoint.x;
        float locateAtY = centralPoint.z;
        float dx = width/2;
        float dy = height/2;

        vertexArray = new VertexArray(new float[]{
                locateAtX-dx,  locateAtY+dy, 0,            // (0) Top-left
                locateAtX+dx,  locateAtY+dy, 0,            // (1) Top-right
                locateAtX-dx,  locateAtY-dy, 0,            // (2) Bottom-left
                locateAtX+dx,  locateAtY-dy, 0,            // (3) Bottom-right
        });

        byte[] byteArray = new byte[]{
                // Front - counter-clockwise (front-facing)
                1, 0, 3,
                0, 2, 3,
        };
        vertexSequenceForDrawingRectangle = new IndexArray(byteArray);
    }

    public void draw(int aPositionLocation, int uColorLocation,
                     int uTextureUnitLocation, int uMatrixTextureLocation,
                     int uMatrixLocation, float[] aspectAdjustmentMatrix,
                     float[] edgeColor) {

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);

        float[] red = {1f, 0f, 0f};
        GLES20.glUniform4f(uColorLocation, red[0], red[1], red[2], 1.0f);

        // we do not recalculate vertices per View-Projection matrices
        // View Controls (UI elements) are drawn directly in OpenGL normalized coordinates
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelMatrix, 0);

//        int nbIndexes4triangles = 2*3;
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nbIndexes4triangles, GLES20.GL_UNSIGNED_BYTE,
//                              vertexSequenceForDrawingRectangle.indexesBuffer);

    }

    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        // bind data to shader variable
        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);
    }

    public void startTransforming() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void move(float[] aspectAdjustmentMatrix, float dx, float dy) {
        // first correct aspect ratio while shape is still at (0.0, 0.0)
        float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, aspectAdjustmentMatrix, 0, modelMatrix, 0);
        System.arraycopy(tempMatrix, 0, modelMatrix, 0, tempMatrix.length);
        // and then move shape
        Matrix.translateM(modelMatrix, 0, dx, dy, 0f);
    }
    ////////////////////////////////////////////////
//    public void bindData(TextureShaderProgram textureProgram) { //to tutaj zdaje się określane są lokacje pozycji werteksów, i koordyn. tekstury. Być może moglibyśmy je rozdzielić?
//        vertexArray.setVertexAttribPointer(
//                0,
//                textureProgram.getPositionAttributeLocation(),
//                POSITION_COMPONENT_COUNT,
//                STRIDE);
//        vertexArray.setVertexAttribPointer(
//                POSITION_COMPONENT_COUNT,
//                textureProgram.getTextureCoordinatesAttributeLocation(),
//                TEXTURE_COORDINATES_COMPONENT_COUNT,
//                STRIDE);
//    }

}
