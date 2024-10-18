package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;

import android.opengl.GLES20;

import java.util.HashMap;
import java.util.Map;

public class CharacterDisplayingRectangle {

    public char character ='X';
    private int alphabetTextureID;
    private int alphabetTextureSize;
    private VertexArray texturePointsArray;
    private IndexArray vertexSequenceArray;
    private VertexArray vertexArray;
    private static final int COORDS_PER_VERTEX = 3;  // X, Y, Z
    private static final int COORDS_PER_TEXTURE_COORDINATE = 2;  // S, T

    public CharacterDisplayingRectangle(int textureID, float width, float height, float positionX, float positionY, char character) {

        this.character = character;
        alphabetTextureID = textureID ; // rename to charTableTextureID?

        float[] modelVertexArray = new float[]{
                0-width+positionX,  0+height+positionY, 0,        // (0) Top-left  X, Y
                0+width+positionX,  0+height+positionY, 0,       // (1) Top-right
                0-width+positionX,  0-height+positionY, 0,       // (2) Bottom-left
                0+width+positionX,  0-height+positionY, 0          // (3) Bottom-right
        };
        vertexArray = new VertexArray(modelVertexArray);

        alphabetTextureSize = 1;

        Map<Character,Integer> charPositionDictionary = new HashMap<Character, Integer>();
        charPositionDictionary.put('f', 70);
        charPositionDictionary.put('p', 80);
        charPositionDictionary.put('s', 83);
        charPositionDictionary.put(':', 26);
        charPositionDictionary.put('0', 16);
        charPositionDictionary.put('1', 17);
        charPositionDictionary.put('2', 18);
        charPositionDictionary.put('3', 19);
        charPositionDictionary.put('4', 20);
        charPositionDictionary.put('5', 21);
        charPositionDictionary.put('6', 22);
        charPositionDictionary.put('7', 23);
        charPositionDictionary.put('8', 24);
        charPositionDictionary.put('9', 25);

        int charID = 130;
        Integer charIDOrNull = charPositionDictionary.get(character);
        if (charIDOrNull != null){
            charID = charIDOrNull;
        }


        int columnCount = 15;
        int rowCount = 11;
        float columnWidth = (float) alphabetTextureSize /columnCount;
        float rowHeight = (float) alphabetTextureSize /rowCount;
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
