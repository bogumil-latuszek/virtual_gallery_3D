package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;

import pl.wsei.mobilne.myapplication.space3d.VertexArray;

public class Cuboid {
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    //not sure what this code below was meant for
    /*
    private static final int CUBE_LINES_NB = 12;
    private static final int VERTEX_COUNT = CUBE_LINES_NB*2;
    static float[] cubeCoords = new float[VERTEX_COUNT*COORDS_PER_VERTEX];
    */
    private final float[] modelMatrix = new float[16]; //a 4x4 matrix

    private float[] edgeColor = {37f/256f, 58f/256f, 190f/256f}; // opengl requires color as float in range 0-1
    private float[] faceColor = {81f/256f, 97f/256f, 203f/256f}; // default is blue
    private float faceOpacity = 0.5f; // default is 50%
    private final VertexArray vertexArray;  // <-- Vertices
    private final ByteBuffer vertexSequenceForDrawingFaces;
    private final ByteBuffer vertexSequenceForDrawingEdges;


    public Cuboid(float dx, float dy, float dz) {
        // prepare buffer for vertices
        vertexArray = new VertexArray(new float[]{
                -dx, dy, dz,             // (0) Top-left near
                dx, dy, dz,              // (1) Top-right near
                -dx, -dy, dz,            // (2) Bottom-left near
                dx, -dy, dz,             // (3) Bottom-right near
                -dx, dy, -dz,            // (4) Top-left far
                dx, dy, -dz,             // (5) Top-right far
                -dx, -dy, -dz,           // (6) Bottom-left far
                dx, -dy, -dz             // (7) Bottom-right far
        });
        vertexSequenceForDrawingFaces = ByteBuffer.allocateDirect(3 * 2 * 6)//num. of vertexes per triange * triangles per face * faces per cuboid
                .put(new byte[]{
                        // Front - counter-clockwise (front-facing)
                        1, 0, 3,
                        0, 2, 3,
                        // Back - clockwise (back-facing)
                        5, 7, 4,
                        4, 7, 6,
                        // Left - clockwise (back-facing)
                        0, 4, 2,
                        2, 4, 6,
                        // Right - counter-clockwise (front-facing)
                        5, 1, 7,
                        7, 1, 3,
                        // Top - counter-clockwise (front-facing)
                        5, 4, 1,
                        1, 4, 0,
                        // Bottom - clockwise (back-facing)
                        7, 3, 6,
                        6, 3, 2
                });
        vertexSequenceForDrawingFaces.position(0); //set starting position
        vertexSequenceForDrawingEdges = ByteBuffer.allocateDirect(3 * 2 * 6)
                .put(new byte[]{
                        // vertical Y-axis edges
                        0, 2,
                        4, 6,
                        5, 7,
                        1, 3,
                        // horizontal X-axis edges
                        0, 1,
                        4, 5,
                        6, 7,
                        2, 3,
                        // horizontal Z-axis edges
                        0, 4,
                        1, 5,
                        3, 7,
                        2, 6,
                });
        vertexSequenceForDrawingEdges.position(0);
    }
    public void setEdgeColor(float[] color) {
        //  float with 3 values:   R   G   B
        System.arraycopy(color, 0, this.edgeColor, 0, 3);
    }
    public void setFaceColor(float[] color) {
        //  float with 3 values:   R   G   B
        System.arraycopy(color, 0, this.faceColor, 0, 3);
    }
    public void setFaceOpacity(float opacity) {
        this.faceOpacity = opacity;
    }
    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);

        GLES20.glLineWidth(5.0f);
    }
    public void startTransforming() {
        Matrix.setIdentityM(modelMatrix, 0);
    }
    public void move(float dx, float dy, float dz) {
        Matrix.translateM(modelMatrix, 0, dx, dy, dz);
    }
    public void scale(float x, float y, float z) {
        Matrix.scaleM(modelMatrix, 0, x, y, z);
    }

    public void draw(int aPositionLocation, int uColorLocation, int useGlobalColorLocation, //overloading draw  function
                     int uMatrixLocation, float[] viewProjectionMatrix) {
        draw(aPositionLocation, uColorLocation, useGlobalColorLocation,
                uMatrixLocation, viewProjectionMatrix, this.edgeColor, this.faceColor);
    }
    public void draw(int aPositionLocation, int uColorLocation, int useGlobalColorLocation,
                     int uMatrixLocation, float[] viewProjectionMatrix,
                     float[] edgeColor, float[] faceColor) {


        // force shader to use uniform color // todo: clarify naming
        int trueInGPU = 1;
        GLES20.glUniform1i(useGlobalColorLocation, trueInGPU);

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);

        // recalculate vertices per matrices
        float[] modelViewProjectionMatrix = new float[16];
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0); //what happens if we change the order?
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelViewProjectionMatrix, 0);

        // Opacity must be enabled and opacity-mode selected
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // tell OpenGL what are 4 values (uniform vec4 %u_Color;) to fill for global color
        // set color for drawing edges
        GLES20.glUniform4f(uColorLocation, edgeColor[0], edgeColor[1], edgeColor[2], 1.0f);

        int nbIndexes4lines = 2*4*3;
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE, vertexSequenceForDrawingEdges);

        // another words: set color for drawing faces
        GLES20.glUniform4f(uColorLocation, faceColor[0], faceColor[1], faceColor[2], this.faceOpacity);

        int nbIndexes4triangles = 2*3*6;
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nbIndexes4triangles, GLES20.GL_UNSIGNED_BYTE, vertexSequenceForDrawingFaces);

        GLES20.glDisable(GLES20.GL_BLEND);
    }
}