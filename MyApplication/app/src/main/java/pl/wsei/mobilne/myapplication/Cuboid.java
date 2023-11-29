package pl.wsei.mobilne.myapplication;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cuboid {

    // Use to access and set the view transformation
    private int vPMatrixHandle;
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int mProgram;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawInstructionBuffer;

    //****************************************
    private FloatBuffer colorFloatBuffer;
    //****************************************
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    /*static float[] cuboidCoords = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f,  // bottom right
            0.5f, 0.622008459f, 0.0f, // top right
            -0.5f, 0.622008459f, 0.0f // top left
    };*/

    /*static float[] cuboidCoords = {   // in counterclockwise order:
            -0,5f,  0.5f, -0.5f, // left top front
            -0,5f,  -0.5f, -0.5f, // left bottom front
            0,5f,  -0.5f, -0.5f, // right bottom front
            0,5f,  0.5f, -0.5f, // right top front
            -0,7f,  0.3f, 0.5f, // left top back
            -0,7f,  -0.7f, 0.5f, // left bottom back
            0,3f,  -0.7f, 0.5f, // right bottom back
            0,3f,  0.3f, 0.5f, // right top back
    };*/
    static float[] cuboidCoords = {
            -1.0f,  1.0f, -1.0f, /* Back. */
            1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f, /* Front. */
            1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f, /* Left. */
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f, /* Right. */
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f, /* Top. */
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f, /* Bottom. */
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f
    };

    /*static short[] instruction = {
            0, 1, 2, 0, 2, 3, //front side
            4, 5, 1, 4, 1, 0, // left side
            1, 5, 6, 1, 6, 2, // bottom side
            3, 2, 6, 3, 6, 7, // right side
            0, 3, 7, 0, 7, 4, // top side
            7, 6, 5, 7, 5, 4 // back side

    };*/
    static short[] instruction = {
            0, 2, 3, 0, 1, 3,
            4, 6, 7, 4, 5, 7,
            8, 9, 10, 11, 8, 10,
            12, 13, 14, 15, 12, 14,
            16, 17, 18, 16, 19, 18,
            20, 21, 22, 20, 23, 22

    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    float color2[] = { 0.9f, 0.1f, 0.1f, 1.0f };
    float color3[] = {1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f
    };

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = cuboidCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // I made this function to test drawing of cuboid on the screen depending on params
    // It could be repurposed later for creation of different cuboids from base cube
    float[] transformCuboidCoords(float[] cuboidCoords, float xScalar, float yScalar, float zScalar,
                                  float xMove, float yMove, float zMove){
        float[] newCoords = new float[cuboidCoords.length];
        for (int i = 0; i < cuboidCoords.length-3; i+=3) {
            float xValue = cuboidCoords[i]*xScalar + xMove;
            float yValue = cuboidCoords[i+1]*yScalar +yMove;
            float zValue = cuboidCoords[i+2]*zScalar + zMove;

            newCoords[i] = xValue;
            newCoords[i+1] = yValue;
            newCoords[i+2] = zValue;
        }
        return  newCoords;
    }

    public Cuboid(MyGLRenderer myGLRenderer) {

        cuboidCoords = transformCuboidCoords(cuboidCoords, 0.5f, 0.5f, 0.5f, 0,0f,0);

        // both of these are just ints - memory pointers to real things??
        //int vertexShader = myGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        //poniższy kod to to samo co zakomentowana linijka wyżej
        int vertexShader = myGLRenderer.loadShader(35633,
                vertexShaderCode);
        int fragmentShader = myGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // in both cases below we "attach" ints (pointers?) to int (program pointer?)
        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);

        //initialize byte buffer for drawing order instruction
        ByteBuffer instructionBuffer = ByteBuffer.allocateDirect(
                instruction.length * 2);
        instructionBuffer.order(ByteOrder.nativeOrder());


        drawInstructionBuffer = instructionBuffer.asShortBuffer();
        drawInstructionBuffer.put(instruction);
        drawInstructionBuffer.position(0);


        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                cuboidCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(cuboidCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //***************************************************
        // initialize color byte buffer
        ByteBuffer colorBuffer = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                cuboidCoords.length * 4);
        // use the device hardware's native byte order
        colorBuffer.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        colorFloatBuffer = colorBuffer.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        colorFloatBuffer.put(color3);
        // set the buffer to read the first coordinate
        colorFloatBuffer.position(0);
        //**************************************************
    }

    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the cuboid vertices
        GLES20.glEnableVertexAttribArray(positionHandle); //positionHandle = 0

        // Prepare the cuboid coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the cuboid
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        //GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT, GLES20.GL_FALSE, 0, color3);

        //GLES20.glUniform4fv(colorHandle, 3, color3, 0);

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        //******************************************
        //int vertexColourLocation = GLES20.glGetAttribLocation(mProgram, "vColor");// but we already have this? colorHandle few lines up?
        GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT, false, 0, colorFloatBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        //******************************************

        // Draw cuboid
        // 1 mode- specifies what kind of primitive to draw
        // 2 count - number of elements to be rendered
        // 3 type - the type of the values in indices. Must be GL_UNSIGNED_BYTE or GL_UNSIGNED_SHORT
        // 4 indices - a pointer to the location where the indices are stored
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, instruction.length,
                GLES20.GL_UNSIGNED_SHORT, drawInstructionBuffer);


        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);

    }

}