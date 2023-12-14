package pl.wsei.mobilne.myapplication;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.Matrix;

public class MultiTriangle {
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;

    static float[] shapeCoords = {
            // in counterclockwise order:

            // each 3 vertices construct triangle
            // X,     Y,   Z,       R,     G,     B,
            // first triangle fan of qube
            -1.0f,  1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  // (v1) center of triangle fan
            -1.0f,  1.0f, -1.0f,  0.6f,  0.0f,  0.0f,  // (v2)
            -1.0f, -1.0f,  1.0f,  0.0f,  0.6f,  0.0f,  // (v3)
            // second triangle is (v1) (v3) (v4)
            1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  0.6f,  // (v4)
            // third triangle is (v1) (v4) (v5=v2)
            -1.0f,  1.0f, -1.0f,  0.6f,  0.0f,  0.0f,  // (v5=v2)

            // second triangle fan of qube
            1.0f, -1.0f,  1.0f,  1.0f,  0.0f,  1.0f,  // (v6) center of triangle fan
            -1.0f, -1.0f, 1.0f,  0.0f,  0.6f,  0.0f,
            1.0f,  -1.0f,  -1.0f,  0.0f,  0.6f,  0.6f,  // (v7)
            // second triangle is (v6) (v8) (v9)
            1.0f, 1.0f,  1.0f,  0.0f,  0.0f,  0.6f,  // (v8)
            // third triangle is (v6) (v9) (v10=v7)
            -1.0f, -1.0f, 1.0f,  0.0f,  0.6f,  0.0f, // (v10=v7)

            // third triangle fan of qube
            1.0f,  1.0f,  -1.0f,  1.0f,  0.0f,  1.0f,  // (v11) center of triangle fan
            1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  0.6f,  // (v12)
            1.0f, -1.0f,  -1.0f,  0.0f,  0.6f,  0.0f,  // (v13)
            // second triangle is (v6) (v8) (v9)
            -1.0f,  1.0f, -1.0f,  0.6f,  0.0f,  0.0f,  // (v14)
            // third triangle is (v6) (v9) (v10=v7)
            1.0f,  1.0f,  1.0f,  0.0f,  0.0f,  0.6f,  // (v15=v12)

            // fourth triangle fan of qube
            -1.0f,  -1.0f,  -1.0f,  1.0f,  0.0f,  1.0f,  // (v16) center of triangle fan
            -1.0f,  1.0f,  -1.0f,  0.0f,  0.0f,  0.6f,  // (v17)
            1.0f,  -1.0f,  -1.0f,  0.0f,  0.6f,  0.0f,  // (v18)
            // second triangle is (v6) (v8) (v9)
            -1.0f,  -1.0f, 1.0f,  0.6f,  0.0f,  0.0f,  // (v19)
            // third triangle is (v6) (v9) (v10=v7)
            -1.0f,  1.0f,  -1.0f,  0.0f,  0.0f,  0.6f,  // (v20=v17)
    };

    private final float[] modelMatrix = new float[16];

    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE =
            (COORDS_PER_VERTEX + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private final FloatBuffer vertexData;  // <-- Vertices

    private final int vertexCount = shapeCoords.length / COORDS_PER_VERTEX;

    public MultiTriangle() {
        // prepare buffer for vertices
        vertexData = ByteBuffer
                .allocateDirect(shapeCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(shapeCoords);
        // set the buffer to read the first coordinate
        vertexData.position(0);
    }

    public void prepareDataSource_forVertexShaderAttributes(int aPositionLocation, int aColorLocation) {
        // ensure Vertex buffer has "start pointer" at "position" of vertex 1
        vertexData.position(0);
        // tell OpenGL where to find data for our attribute a_Position
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, STRIDE, vertexData);
        // we’ve linked our data to the attribute, we need to enable the attribute
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        // ensure Vertex buffer has "start pointer" at "color" of vertex 1
        vertexData.position(COORDS_PER_VERTEX);
        // tell OpenGL where to find data for our attribute a_Color
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, STRIDE, vertexData);
        // we’ve linked our data to the attribute, we need to enable the attribute
        GLES20.glEnableVertexAttribArray(aColorLocation);
    }
    public void startTransforming() {
        Matrix.setIdentityM(modelMatrix, 0);
    }
    public void move(float dx, float dy, float dz) {
        Matrix.translateM(modelMatrix, 0, dx, dy, dz);
    }
    public void rotateAroundX(float angleInDegrees) {
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 1f, 0f, 0f);
    }
    public void rotateAroundY(float angleInDegrees) {
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0f);
    }
    public void rotateAroundZ(float angleInDegrees) {
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0f, 0f, 1f);
    }
    public void combineWithModelMatrix(float[] projectionMatrix, float[] modelViewProjectionMatrix) {
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    }
    public void draw(int useGlobalColorLocation) {
        // force shader to use uniform color
        int trueInGPU = 0;
        GLES20.glUniform1i(useGlobalColorLocation, trueInGPU);

        // no need to fill uColorLocation with "current color"
        // via glUniform4fv() or glUniform4f()
        // since aColorLocation is already defined to pull color values from vertices buffer

        // Draw the triangle fan 4
        int offsetToStart_inVertexData = 15;
        int nbVerticesToDraw = 5;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, offsetToStart_inVertexData, nbVerticesToDraw);
        // Draw the triangle fan 3
        offsetToStart_inVertexData = 10;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, offsetToStart_inVertexData, nbVerticesToDraw);
        // Draw the triangle fan 2
        offsetToStart_inVertexData = 5;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, offsetToStart_inVertexData, nbVerticesToDraw);
        // Draw the triangle fan 1
        offsetToStart_inVertexData = 0;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, offsetToStart_inVertexData, nbVerticesToDraw);

    }
}