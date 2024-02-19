package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;

import android.opengl.GLES20;
import android.opengl.Matrix;

import pl.wsei.mobilne.myapplication.space3d.geometry.Point;

public class Rectangle2D {

    private static final int COORDS_PER_VERTEX = 2;  // X, Y
    private static final int COORDS_PER_TEXTURE_COORDINATE = 2;  // S, T
    private final float[] modelMatrix = new float[16]; //a 4x4 matrix
    private final VertexArray vertexArray;  // <-- Vertices
    private final VertexArray texturePointsArray;  // <-- texture coordinates
    private final IndexArray vertexSequenceForDrawingRectangle;

    private Point centralPoint;
    private float width;
    private float height;

    public Rectangle2D(Point centralPoint, float width, float height) {
        this.centralPoint = centralPoint;
        this.width = width;
        this.height = height;

        float locateAtX = centralPoint.x;
        float locateAtY = centralPoint.z;
        float dx = width/2;
        float dy = height/2;

        vertexArray = new VertexArray(new float[]{
                locateAtX-dx,  locateAtY+dy,         // (0) Top-left  X, Y
                locateAtX+dx,  locateAtY+dy,         // (1) Top-right
                locateAtX-dx,  locateAtY-dy,         // (2) Bottom-left
                locateAtX+dx,  locateAtY-dy          // (3) Bottom-right
        });
        texturePointsArray = new VertexArray(new float[]{
                0f,  1f,                                 // (0) Top-left  S, T
                1f,  1f,                                 // (1) Top-right
                0f,  0f,                                 // (2) Bottom-left
                1f,  0f,                                 // (3) Bottom-right
        });
        vertexSequenceForDrawingRectangle = new IndexArray(new byte[]{
                // Front - counter-clockwise (front-facing)
                1, 0, 3,
                0, 2, 3,
        });
    }

    public void draw(int aPositionLocation, int aTextureCoordinatesLocation,
                     int uTextureUnitLocation, int uMatrixLocation,
                     int textureId,  float[] aspectAdjustmentMatrix) {

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);
        prepareDataSource_forTextureCoordinateAttribute(aTextureCoordinatesLocation);

        // we do not recalculate vertices per View-Projection matrices
        // View Controls (UI elements) are drawn directly in OpenGL normalized coordinates
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelMatrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);

        int nbIndexes4triangles = 2*3;
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nbIndexes4triangles, GLES20.GL_UNSIGNED_BYTE,
                              vertexSequenceForDrawingRectangle.indexesBuffer);

    }

    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        // bind data to shader variable
        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);
    }

    public void prepareDataSource_forTextureCoordinateAttribute(int aTextureCoordinatesLocation) {
        // bind data to shader variable
        texturePointsArray.setVertexAttribPointer(0, aTextureCoordinatesLocation,
                COORDS_PER_TEXTURE_COORDINATE, 0);
    }

    public void startTransforming() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void move(float[] aspectAdjustmentMatrix, float dx, float dy) {
        // first correct aspect ratio while shape is still at (0.0, 0.0)
        float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, aspectAdjustmentMatrix, 0, modelMatrix, 0);
        System.arraycopy(tempMatrix, 0, modelMatrix, 0, tempMatrix.length);
        // and then move shape
        Matrix.translateM(modelMatrix, 0, dx, dy, 0f);
    }
}
