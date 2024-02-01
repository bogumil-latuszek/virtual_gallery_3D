package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

import android.content.Context;

import pl.wsei.mobilne.myapplication.R;

public class ColorShaderProgram extends ShaderProgram{
    // Uniform locations
    private final int uMatrixLocation;
    // Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;
    public ColorShaderProgram(Context context) {
        super(context, R.raw.color_vertex_shader,
                R.raw.color_fragment_shader);
    // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
    // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
    }
    public void setUniforms(float[] matrix) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    //getters
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}
