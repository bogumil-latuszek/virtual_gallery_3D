package pl.wsei.mobilne.myapplication.space3d;

import android.content.Context;

import pl.wsei.mobilne.myapplication.R;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class FpsCounter {


    private int[] fpsCount;
    private float spacing = 1;
    private Letter[] letters;
    private float positionX;
    private float positionY;
    float letterWidth = 0.1f;
    float letterHeight = 0.1f;


    public FpsCounter(Context context, float positionX, float positionY, float spacing) {
        this.spacing = spacing;
        setCount(0);
        this.positionX = positionX;
        this.positionY = positionY;
        letters = new Letter[7];
        letters[0] = new Letter(context, letterWidth,letterHeight,positionX,positionY, 'f' );
        letters[1] = new Letter(context, letterWidth,letterHeight,positionX + spacing,positionY, 'p' );
        letters[2] = new Letter(context, letterWidth,letterHeight,positionX + 2*spacing,positionY, 's' );
        letters[3] = new Letter(context, letterWidth,letterHeight,positionX + 3*spacing,positionY, ':' );
        letters[4] = new Letter(context, letterWidth,letterHeight,positionX + 4*spacing,positionY, '0' );
        letters[5] = new Letter(context, letterWidth,letterHeight,positionX + 5*spacing,positionY, '0' );
        letters[6] = new Letter(context, letterWidth,letterHeight,positionX + 6*spacing,positionY, '0' );
    }

    public void setCount( int count){
        int ones = count%10;
        int tens = count%10;
        int hundreds = count%10;
        fpsCount = new int[]{ones, tens, hundreds};
    }
    public void draw(int aPositionLocation, int aTextureCoordinatesLocation,
                     int uTextureUnitLocation,int uMatrixLocation, float[] viewProjectionMatrix){
        for(int i=0; i<this.letters.length; i++){
            letters[i].draw(aPositionLocation, aTextureCoordinatesLocation, uTextureUnitLocation, uMatrixLocation, viewProjectionMatrix);
        }
    }

}
