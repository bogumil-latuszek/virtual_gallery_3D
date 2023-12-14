package pl.wsei.mobilne.myapplication;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class WorldCoords {
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORD_LINES_NB = 21;
    private static final int VERTEX_COUNT = (COORD_LINES_NB*2)*2;
    static float[] gridCoords = new float[VERTEX_COUNT*COORDS_PER_VERTEX];
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;  // <-- Vertices

    public WorldCoords() {
        // calculate coordinates for vertices
        initPoints();
        // prepare buffer for vertices
        vertexData = ByteBuffer
                .allocateDirect(gridCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(gridCoords);
        // set the buffer to read the first coordinate
        vertexData.position(0);
    }

    private void initPoints() {
        int valuesPerLine = COORDS_PER_VERTEX*2;
        int coordValuesIdx=0;
        for (float x = -10f; x <= 10; x+=1.0f) {
            float[] coordLine = {
                    // X,  Y,  Z,
                    x, -1f, -10f,
                    x, -1f,  10f
            };
            System.arraycopy(coordLine, 0, gridCoords, coordValuesIdx, valuesPerLine);
            coordValuesIdx = coordValuesIdx + valuesPerLine;
        }
        for (float z = -10f; z <= 10; z+=1.0f) {
            float[] coordLine = {
                    // X,  Y,  Z,
                    -10f, -1f, z,
                    10f, -1f, z
            };
            System.arraycopy(coordLine, 0, gridCoords, coordValuesIdx, valuesPerLine);
            coordValuesIdx = coordValuesIdx + valuesPerLine;
        }
        return;
    }
    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        // ensure Vertex buffer has "start pointer" in correct place
        vertexData.position(0);
        // tell OpenGL where to find data for our attribute a_Position
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, vertexData);
        // we’ve linked our data to the attribute, we need to enable the attribute
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glLineWidth(2.0f);
    }
    public void draw(int aPositionLocation, int uColorLocation, int useGlobalColorLocation,
                     int uMatrixLocation, float[] viewProjectionMatrix) {

        // tell OpenGL what are 4 values (uniform vec4 %u_Color;) to fill for global color
        // another words: set color for drawing
        //               R   G   B
        float[] black = {0f, 0f, 0f};
        GLES20.glUniform4f(uColorLocation, black[0], black[1], black[2], 1.0f);

        // force shader to use uniform color
        int trueInGPU = 1;
        GLES20.glUniform1i(useGlobalColorLocation, trueInGPU);

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);

        // recalculate vertices per matrices
        float[] modelViewProjectionMatrix = new float[16];
        // world coordinates do not transform - they just display at projection
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);

        // Draw the grid
        int offsetToStart_inVertexData = 0;
        GLES20.glDrawArrays(GLES20.GL_LINES, offsetToStart_inVertexData, VERTEX_COUNT);
    }
}