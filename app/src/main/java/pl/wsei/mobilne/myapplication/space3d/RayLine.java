package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import pl.wsei.mobilne.myapplication.space3d.geometry.Ray;

public class RayLine {
    private static final int COORDS_PER_VERTEX = 3;
    private float[] lineColor = {0f, 0f, 1f}; // opengl requires color as float in range 0-1
    private final VertexArray vertexArray;  // <-- Vertices
    private final float[] modelMatrix = new float[16]; //a 4x4 matrix

    public RayLine(Ray ray) {
        float [] vertices = new float[] {
                ray.point.x, ray.point.y, ray.point.z,
                ray.point.x + ray.vector.x, ray.point.y + ray.vector.y, ray.point.z + ray.vector.z
        };
        vertexArray = new VertexArray(vertices);
    }

    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);

        GLES20.glLineWidth(5.0f);
    }

    public void startTransforming() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void draw(int aPositionLocation, int uColorLocation, int useGlobalColorLocation,
                     int uMatrixLocation, float[] viewProjectionMatrix) {
        // force shader to use uniform color
        int trueInGPU = 1;
        GLES20.glUniform1i(useGlobalColorLocation, trueInGPU);

        // tell OpenGL what are 4 values (uniform vec4 %u_Color;) to fill for global color
        // another words: set color for drawing
        //                                     R             G             B
        GLES20.glUniform4f(uColorLocation, lineColor[0], lineColor[1], lineColor[2], 1.0f);

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);

        // ray does not transform (stays as it is at creation time) - it just display at projection
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);

        // Draw the grid
        int offsetToStart_inVertexData = 0;
        int vertex_count = 2;
        GLES20.glDrawArrays(GLES20.GL_LINES, offsetToStart_inVertexData, vertex_count);
    }
}