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
    
    //*************************************************************
    //EXPERIMENT
    private ShortBuffer drawInstruction1Buffer;
    private ShortBuffer drawInstruction2Buffer;
    //*************************************************************

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float[] cuboidCoords = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f,  // bottom right
            0.5f, 0.622008459f, 0.0f, // top right
            -0.5f, 0.622008459f, 0.0f // top left
    };

    static short[] instruction = {
            0, 1, 2, 0, 2, 3, 4, 1, 0
    };

    //*************************************************************
    //EXPERIMENT
    static short[] instruction1 = {//tego nie widzi?
            4,1,3
    };
    static short[] instruction2 = {
            1,2,3
    };
    //*************************************************************

    /*
    static float[] CuboidCoordsINSTRUCTED = {   // in counterclockwise order:
            0.0f,  0.622008459f, -0.01f, // top
            -0.5f, -0.311004243f, -0.01f, // bottom left
            0.5f, -0.311004243f, -0.01f,  // bottom right
            0.0f,  0.622008459f, -0.01f, // top
            0.5f, -0.311004243f, -0.01f,  // bottom right
            0.5f, 0.622008459f, -0.01f // top right
    };*/

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    //*************************************************************
    //EXPERIMENT
    float color2[] = { 0.9f, 0.1f, 0.1f, 1.0f };
    //*************************************************************

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = cuboidCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Cuboid(MyGLRenderer myGLRenderer) {

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

        //initialize byte buffer for instruction
        ByteBuffer instructionBuffer = ByteBuffer.allocateDirect(
                instruction.length * 2);
        instructionBuffer.order(ByteOrder.nativeOrder());


        drawInstructionBuffer = instructionBuffer.asShortBuffer();
        drawInstructionBuffer.put(instruction);
        drawInstructionBuffer.position(0);


        //*************************************************************
        //EXPERIMENT
        //initialize byte buffer for instruction1
        ByteBuffer instruction1Buffer = ByteBuffer.allocateDirect(
                instruction1.length * 2);
        instruction1Buffer.order(ByteOrder.nativeOrder());
        drawInstruction1Buffer = instruction1Buffer.asShortBuffer();
        drawInstruction1Buffer.put(instruction1);
        drawInstruction1Buffer.position(0);

        //initialize byte buffer for instruction2
        ByteBuffer instruction2Buffer = ByteBuffer.allocateDirect(
                instruction2.length * 2);
        instruction2Buffer.order(ByteOrder.nativeOrder());
        drawInstruction2Buffer = instruction2Buffer.asShortBuffer();
        drawInstruction2Buffer.put(instruction2);
        drawInstruction2Buffer.position(0);
        //*************************************************************

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

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the cuboid
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount); //mode, first, count
        
        /*GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, instruction.length,
                GLES20.GL_UNSIGNED_SHORT, drawInstructionBuffer);*/
        //*****************************************************
        //EXPERIMENT
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, instruction1.length,
                GLES20.GL_UNSIGNED_SHORT, drawInstruction1Buffer);

        GLES20.glUniform4fv(colorHandle, 1, color2, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, instruction2.length,
                GLES20.GL_UNSIGNED_SHORT, drawInstruction2Buffer);
        //*****************************************************

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);

    }

}