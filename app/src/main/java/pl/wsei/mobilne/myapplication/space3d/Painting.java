package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import pl.wsei.mobilne.myapplication.space3d.geometry.Point;

public class Painting {
    //model matrix defines position, rotation etc
    private float[] modelMatrix= new float[16];;
    private Point centralPoint;
    private float width;
    private float height;
    private static final int COORDS_PER_VERTEX = 3;  // X, Y, Z
    private static final int COORDS_PER_TEXTURE_COORDINATE = 2;  // S, T
    private final VertexArray vertexArray;  // <-- Vertices
    private final VertexArray texturePointsArray;  // <-- texture coordinates
    private final IndexArray vertexSequenceForDrawingRectangle;


    public Painting(Point centralPoint, float width, float height) {
        //Painting's owner will give modelMatrix to it

        this.centralPoint = centralPoint;
        this.width = width;
        this.height = height;

        float locateAtX = centralPoint.x;
        float locateAtY = centralPoint.z;
        float locateAtZ = centralPoint.z;
        float dx = width/2;
        float dy = height/2;

        vertexArray = new VertexArray(new float[]{
                locateAtX-dx,  locateAtY+dy, locateAtZ,        // (0) Top-left  X, Y
                locateAtX+dx,  locateAtY+dy, locateAtZ,       // (1) Top-right
                locateAtX-dx,  locateAtY-dy, locateAtZ,       // (2) Bottom-left
                locateAtX+dx,  locateAtY-dy, locateAtZ          // (3) Bottom-right
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
                     int textureId,  float[] viewProjectionMatrix) {

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);
        prepareDataSource_forTextureCoordinateAttribute(aTextureCoordinatesLocation);

        // recalculate vertices per matrices
        float[] modelViewProjectionMatrix = new float[16];
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0); //what happens if we change the order?
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelViewProjectionMatrix, 0);


//        // View Controls (UI elements) are drawn directly in OpenGL normalized coordinates
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelMatrix, 0);

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
        Log.d("position of painting:", centralPoint.toString());

    }
//    public void drawColored(int aPositionLocation, int uColorLocation,
//                            int uMatrixLocation, float[] viewProjectionMatrix) {
//
//        // prepare vertices buffer (floats --> bytes)
//        prepareDataSource_forPositionAttribute(aPositionLocation);
//
//        // recalculate vertices per matrices
//        float[] modelViewProjectionMatrix = new float[16];
//        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0); //what happens if we change the order?
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelViewProjectionMatrix, 0);
//
//        // Opacity must be enabled and opacity-mode selected
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//
//        float[] faceColor = {235f/255f, 12f/255f, 49f/255f};
//        // another words: set color for drawing faces
//        GLES20.glUniform4f(uColorLocation, faceColor[0], faceColor[1], faceColor[2], 0.5f);
//
//        int nbIndexes4triangles = 2*3;
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nbIndexes4triangles, GLES20.GL_UNSIGNED_BYTE, vertexSequenceForDrawingRectangle.indexesBuffer);
//
//        GLES20.glDisable(GLES20.GL_BLEND);
//       // Log.d("position of painting:", centralPoint.toString());
//
//    }

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

}
