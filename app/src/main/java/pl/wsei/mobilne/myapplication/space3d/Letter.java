package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;

import android.content.Context;
import android.opengl.GLES20;

import pl.wsei.mobilne.myapplication.R;

public class Letter {

    private char letter ='X';
    private int AlphabetTextureID;
    private VertexArray texturePointsArray;
    private IndexArray vertexSequenceArray;
    private VertexArray vertexArray;
    private static final int COORDS_PER_VERTEX = 3;  // X, Y, Z
    private static final int COORDS_PER_TEXTURE_COORDINATE = 2;  // S, T

    public Letter(Context context, float width, float height, float positionX, float positionY, char letter) {

        this.letter = letter;
        AlphabetTextureID = TextureHelper.loadTexture(context, R.drawable.char_table);

        float[] modelVertexArray = new float[]{
                0-width+positionX,  0+height+positionY, 0,        // (0) Top-left  X, Y
                0+width+positionX,  0+height+positionY, 0,       // (1) Top-right
                0-width+positionX,  0-height+positionY, 0,       // (2) Bottom-left
                0+width+positionX,  0-height+positionY, 0          // (3) Bottom-right
        };
        vertexArray = new VertexArray(modelVertexArray);
        texturePointsArray = new VertexArray(new float[]{
                0f,  0f,                                 // (0) Top-left  S, T
                1f,  0f,                                 // (1) Top-right
                0f,  1f,                                 // (2) Bottom-left
                1f,  1f,                                 // (3) Bottom-right
        });
        vertexSequenceArray = new IndexArray(new byte[]{
                // Front - counter-clockwise (front-facing)
                1, 0, 3,
                0, 2, 3,
        });
    }
    public void draw(int aPositionLocation, int aTextureCoordinatesLocation,
                     int uTextureUnitLocation,int uMatrixLocation, float[] viewProjectionMatrix){

        prepareDataSource_forPositionAttribute(aPositionLocation);
        prepareDataSource_forTextureCoordinateAttribute(aTextureCoordinatesLocation);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, this.AlphabetTextureID);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);

        int nbIndexes4triangles = 2*3;
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nbIndexes4triangles, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceArray.indexesBuffer);
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


}
