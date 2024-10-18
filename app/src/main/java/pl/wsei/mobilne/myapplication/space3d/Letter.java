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

    public char letter ='X';
    private int alphabetTextureID;
    private int alphabetTextureSize;
    private VertexArray texturePointsArray;
    private IndexArray vertexSequenceArray;
    private VertexArray vertexArray;
    private static final int COORDS_PER_VERTEX = 3;  // X, Y, Z
    private static final int COORDS_PER_TEXTURE_COORDINATE = 2;  // S, T

    public Letter(int textureID, float width, float height, float positionX, float positionY, char letter) {

        this.letter = letter;
        alphabetTextureID = textureID ; // rename to charTableTextureID?

        float[] modelVertexArray = new float[]{
                0-width+positionX,  0+height+positionY, 0,        // (0) Top-left  X, Y
                0+width+positionX,  0+height+positionY, 0,       // (1) Top-right
                0-width+positionX,  0-height+positionY, 0,       // (2) Bottom-left
                0+width+positionX,  0-height+positionY, 0          // (3) Bottom-right
        };
        vertexArray = new VertexArray(modelVertexArray);

        alphabetTextureSize = 1;
        int charID = 18;
        // this should be replaced with dictionary
        switch (letter){
            case 'f':
                charID = 70;
                break;
            case 'p':
                charID = 80;
                break;
            case 's':
                charID = 83;
                break;
            case ':':
                charID = 26;
                break;
            case '0':
                charID = 16;
                break;
            case '1':
                charID = 17;
                break;
            case '2':
                charID = 18;
                break;
            case '3':
                charID = 19;
                break;
            case '4':
                charID = 20;
                break;
            case '5':
                charID = 21;
                break;
            case '6':
                charID = 22;
                break;
            case '7':
                charID = 23;
                break;
            case '8':
                charID = 24;
                break;
            case '9':
                charID = 25;
                break;
            case 'x':
                charID = 130;
                break;

        }



        int columnCount = 15;
        int rowCount = 11;
        float columnWidth = (float) alphabetTextureSize /15;
        float rowHeight = (float) alphabetTextureSize /11;
        int numberInRow = charID%columnCount;
        int numberInColumn = charID/columnCount;
        float positionInRow = numberInRow*columnWidth;
        float positionInColumn = numberInColumn*rowHeight;

        texturePointsArray = new VertexArray(new float[]{
                positionInRow,  positionInColumn,                                 // (0) Top-left  S, T
                positionInRow+columnWidth,  positionInColumn,                                  // (1) Top-right
                positionInRow,  positionInColumn+rowHeight,                                 // (2) Bottom-left
                positionInRow+columnWidth,  positionInColumn+rowHeight                                  // (3) Bottom-right
        });

/*
        texturePointsArray = new VertexArray(new float[]{
                0f,  0f,                                 // (0) Top-left  S, T
                0.1f,  0f,                                 // (1) Top-right
                0f,  0.1f,                                 // (2) Bottom-left
                0.1f,  0.1f,                                 // (3) Bottom-right
        });
*/
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
        glBindTexture(GL_TEXTURE_2D, this.alphabetTextureID);
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
