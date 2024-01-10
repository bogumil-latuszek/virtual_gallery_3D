package pl.wsei.mobilne.myapplication.space3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class VertexArray {

    public class Constants {
        public static final int BYTES_PER_FLOAT = 4;
    }
    private final FloatBuffer floatBuffer;
    private final float [] vertexData;
    private final int _length;
    public VertexArray(float[] vertexData) {
        this._length = vertexData.length;
        this.vertexData = new float[this._length];
        System.arraycopy(vertexData, 0, this.vertexData, 0, this._length);
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }
    public int length() {
        return this._length;
    }
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        // ensure Vertex buffer has "start pointer" in correct place
        floatBuffer.position(dataOffset);
        // tell OpenGL where to find data for our attribute pointed via attributeLocation
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT,
                false, stride, floatBuffer);
        // we’ve linked our data to the attribute, we need to enable the attribute
        GLES20.glEnableVertexAttribArray(attributeLocation);
    }
}