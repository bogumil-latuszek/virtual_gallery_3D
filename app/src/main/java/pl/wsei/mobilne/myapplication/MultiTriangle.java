package pl.wsei.mobilne.myapplication;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MultiTriangle {
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float[] shapeCoords = {
            // in counterclockwise order:

            // each 3 vertices construct triangle
            -0.9f, -0.9f, 0.0f, // (v1) bottom left
            0.9f, -0.9f, 0.0f,  // (v2) bottom right
            -0.9f,  0.9f, 0.0f, // (v3) top

            // second triangle is (v2) (v3) (v4)
            0.6f, 0.3f, 0.0f,   // (v4) top right

            // third triangle is (v3) (v4) (v5)
            0.1f, 0.7f, 0.0f  // (v5) middle top
    };

    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;  // <-- Vertices


    // Set color with red, green, blue and alpha (opacity) values
    float colors[] = {
            1.0f, 0.0f, 0.0f, 1.0f,  // red
            0.0f, 1.0f, 0.0f, 1.0f,  // green
            0.0f, 0.0f, 1.0f, 1.0f,  // blue
    };

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

    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        // ensure Vertex buffer has "start pointer" in correct place
        vertexData.position(0);
        // tell OpenGL where to find data for our attribute a_Position
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, vertexData);
        // we’ve linked our data to the attribute, we need to enable the attribute
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }
    public void draw(int uColorLocation) {

        // tell OpenGL what are 4 values (uniform vec4 %u_Color;) to fill for global color
        // another words: set color for drawing
        int count = 1;  // of vec4 variables (each take 4 floats) to take
        int offset = 0;
        GLES20.glUniform4fv(uColorLocation, count, colors, offset);
//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);

        // Draw the triangle
        int offsetToStart_inVertexData = 0;
        int nbVerticesToDraw = 3;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, offsetToStart_inVertexData, nbVerticesToDraw);

        // second triangle
//        GLES20.glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
        offset = 4;
        GLES20.glUniform4fv(uColorLocation, count, colors, offset);

        // Draw the triangle
        offsetToStart_inVertexData = 1;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, offsetToStart_inVertexData, nbVerticesToDraw);

        // third triangle
        offset = 8;
        GLES20.glUniform4fv(uColorLocation, count, colors, offset);

        // Draw the triangle
        offsetToStart_inVertexData = 2;
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, offsetToStart_inVertexData, nbVerticesToDraw);
    }
}