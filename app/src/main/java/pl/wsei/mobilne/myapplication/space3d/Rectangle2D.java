package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;

import pl.wsei.mobilne.myapplication.space3d.geometry.Point;

public class Rectangle2D {
    private static final int COORDS_PER_VERTEX = 3;
    private final float[] modelMatrix = new float[16]; //a 4x4 matrix

    private static final int BYTES_PER_FLOAT = 4;
   // private final VertexArray vertexArray;  // <-- Vertices

    //private final IndexArray vertexSequenceForDrawingOuterCircle;

   // private final ByteBuffer vertexSequenceForDrawingEdges;
    private  final  VertexArray vertexArray;
    private final IndexArray indexArray;
    private Point centralPoint;
    private float width;
    private float height;


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
//        vertexSequenceForDrawingEdges = ByteBuffer.allocateDirect(3 * 2 * 6)//num. of vertexes per triangle * triangles per face * faces per cuboid
//                .put(new byte[]{
//                        0, 1,
//                        1, 3,
//                        3, 2,
//                        2, 0
//                });
//
//        vertexSequenceForDrawingEdges.position(0);
        byte[] byteArray = new byte[]{
                0, 1,
                1, 3,
                3, 2,
                2, 0
        };
        indexArray = new IndexArray(byteArray);

    }



    public void draw(int aPositionLocation, int uColorLocation, int useGlobalColorLocation,
                     int uMatrixLocation, float[] aspectAdjustmentMatrix,
                     float[] edgeColor) {
        // force shader to use uniform color
        int trueInGPU = 1;
        GLES20.glUniform1i(useGlobalColorLocation, trueInGPU);

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);

        // we do not recalculate vertices per View-Projection matrices
        // View Controls (UI elements) are drawn directly in OpenGL normalized coordinates
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelMatrix, 0);

        // tell OpenGL what are 4 values (uniform vec4 %u_Color;) to fill for global color
        // set color for drawing edges
        GLES20.glUniform4f(uColorLocation, edgeColor[0], edgeColor[1], edgeColor[2], 1.0f);

        int nbIndexes4lines = indexArray.length();
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
                indexArray.indexesBuffer);
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

}
