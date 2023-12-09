package pl.wsei.mobilne.myapplication;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MultiTriangleRenderer implements GLSurfaceView.Renderer {

    // access to "drawing color variable" inside OpenGL
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    // access to "vertex position" inside OpenGL
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private final String setPosition_vertexShaderCode =
            String.format("attribute vec4 %s;", A_POSITION) +
                    "void main() {" +
                    String.format("  gl_Position = %s;", A_POSITION) +
                    "}";
    private final String setUnicolor_fragmentShaderCode =
            "precision mediump float;" +
                    String.format("uniform vec4 %s;", U_COLOR) +
                    "void main() {" +
                    String.format("  gl_FragColor = %s;", U_COLOR) +
                    "}";
    // remember identifiers of entities created "inside" OpenGL
    private int vertexShaderId;
    private int fragmentShaderId;
    private int programObjectId; // vertex and fragment renderers combined

    // shape we will render
    private MultiTriangle mTriangle;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // prepare shaders
        vertexShaderId = loadShader(GLES20.GL_VERTEX_SHADER, setPosition_vertexShaderCode);
        fragmentShaderId = loadShader(GLES20.GL_FRAGMENT_SHADER, setUnicolor_fragmentShaderCode);
        programObjectId = loadProgram(vertexShaderId, fragmentShaderId);
        // tell OpenGL to use the program defined here when
        // drawing anything to the screen
        GLES20.glUseProgram(programObjectId);
        // retrieve "location" of "shaders variables" inside OpenGL
        uColorLocation = GLES20.glGetUniformLocation(programObjectId, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(programObjectId, A_POSITION);

        //Set the background frame color (light mint)
        //another words: define color to be used as we call glClear()
        GLES20.glClearColor(0.8f, 1.0f, 0.8f, 0.0f);

        // Prepare our shape
        mTriangle = new MultiTriangle();
        mTriangle.prepareDataSource_forPositionAttribute(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // draw our shape
        mTriangle.draw(uColorLocation);
    }

    public int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        final int shaderObjectId = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        return shaderObjectId;
    }

    public int loadProgram(int vertexShaderId, int fragmentShaderId) {
        // create empty OpenGL ES Program
        final int programObjectId  = GLES20.glCreateProgram();
        // add the shaders to program
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        // create OpenGL ES program executables
        GLES20.glLinkProgram(programObjectId);
        return programObjectId;
    }

}