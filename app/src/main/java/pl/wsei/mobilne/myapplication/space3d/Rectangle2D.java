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

    private static final float[] VERTEX_DATA = {
    // Order of coordinates: X, Y, S, T
    // Triangle Fan
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f,  1f, 0.9f,
            0.5f, 0.8f,  1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f
    };
    ////////////////////////////////////////////////////
    private static final int COORDS_PER_VERTEX = 3;
    private final float[] modelMatrix = new float[16]; //a 4x4 matrix
   // private final VertexArray vertexArray;  // <-- Vertices

    //private final IndexArray vertexSequenceForDrawingOuterCircle;

   // private final ByteBuffer vertexSequenceForDrawingEdges;
    private  final  VertexArray vertexArray;

    //private final IndexArray indexArray;
    private Point centralPoint;
    private float width;
    private float height;

    private final ByteBuffer vertexSequenceForDrawingFaces;

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
                locateAtX-dx,  locateAtY-dy, 0,               // (2) Bottom-left
                locateAtX+dx,  locateAtY-dy, 0,                // (3) Bottom-right
        });

        //vertexArray = new VertexArray(VERTEX_DATA);

//        vertexSequenceForDrawingEdges = ByteBuffer.allocateDirect(3 * 2 * 6)//num. of vertexes per triangle * triangles per face * faces per cuboid
//                .put(new byte[]{
//                        0, 1,
//                        1, 3,
//                        3, 2,
//                        2, 0
//                });
//
//        vertexSequenceForDrawingEdges.position(0);
//        byte[] byteArray = new byte[]{
//                0, 1,
//                1, 3,
//                3, 2,
//                2, 0
//        };
//        indexArray = new IndexArray(byteArray);

        vertexSequenceForDrawingFaces = ByteBuffer.allocateDirect(3 * 2)
                .put(new byte[]{
                        // Front - counter-clockwise (front-facing)
                        1, 0, 3,
                        0, 2, 3,
                });
        vertexSequenceForDrawingFaces.position(0); //set starting position
    }

    public void draw(int aPositionLocation, int uTextureUnitLocation, int uMatrixTextureLocation,
                     int uMatrixLocation, float[] aspectAdjustmentMatrix,
                     float[] edgeColor) {

//        // prepare vertices buffer (floats --> bytes)
//        prepareDataSource_forPositionAttribute(aPositionLocation);
//
//        // we do not recalculate vertices per View-Projection matrices
//        // View Controls (UI elements) are drawn directly in OpenGL normalized coordinates
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelMatrix, 0);
//
//        int nbIndexes4lines = indexArray.length();
//        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
//                indexArray.indexesBuffer);


//        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numberOfIndexesPerFace, GLES20.GL_UNSIGNED_BYTE, vertexSequenceForDrawingFaces);

    }

    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);

        GLES20.glLineWidth(5.0f);
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
    public void bindData(TextureShaderProgram textureProgram) { //to tutaj zdaje się określane są lokacje pozycji werteksów, i koordyn. tekstury. Być może moglibyśmy je rozdzielić?
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

}
