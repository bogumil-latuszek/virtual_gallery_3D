package pl.wsei.mobilne.myapplication.space3d;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import pl.wsei.mobilne.myapplication.R;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class FpsCounter {

    private float spacing;
    private Letter[] letters;
    private float positionX;
    private float positionY;
    float letterWidth = 0.1f;
    float letterHeight = 0.1f;
    int charTableTextureID;

    private int fpsCount;
    long timeSinceLastUpdate = 0;
    int framesCounted = 0;


    public FpsCounter(Context context, float positionX, float positionY, float spacing) {
        this.spacing = spacing;
        this.positionX = positionX;
        this.positionY = positionY;
        charTableTextureID = TextureHelper.loadTexture(context, R.drawable.char_table);
        letters = new Letter[7];
        letters[0] = new Letter(charTableTextureID, letterWidth,letterHeight,positionX,positionY, 'f');
        letters[1] = new Letter(charTableTextureID, letterWidth,letterHeight,positionX + spacing,positionY, 'p');
        letters[2] = new Letter(charTableTextureID, letterWidth,letterHeight,positionX + 2*spacing,positionY, 's');
        letters[3] = new Letter(charTableTextureID, letterWidth,letterHeight,positionX + 3*spacing,positionY, ':');
        letters[4] = new Letter(charTableTextureID, letterWidth,letterHeight,positionX + 4*spacing,positionY, '0');
        letters[5] = new Letter(charTableTextureID, letterWidth,letterHeight,positionX + 5*spacing,positionY, '5');
        letters[6] = new Letter(charTableTextureID, letterWidth,letterHeight,positionX + 6*spacing,positionY, '6');
    }


    private void UpdateFpsCounter(){
        long currentTime = SystemClock.uptimeMillis();
        long timePassed = currentTime-timeSinceLastUpdate;
        if(timePassed>1000){

            fpsCount = (int)((1000*framesCounted)/(timePassed));
            timeSinceLastUpdate = currentTime;
            framesCounted = 0;
        }
        else {
            framesCounted += 1;
        }
    }


    private void updateLetters(){
        int ones = fpsCount%10;
        int tens = (fpsCount/10)%10;
        int hundreds = (fpsCount/100)%10;
        if(hundreds==0){
            if(tens==0){
                char onesConverted = (char)(ones +48);
                updateLetter(4, onesConverted);
                updateLetter(5, 'x' );
                updateLetter(6, 'x' );
            }
            else{
                char onesConverted = (char)(ones +48);
                char tensConverted = (char)(tens +48);
                updateLetter(4, tensConverted );
                updateLetter(5, onesConverted );
                updateLetter(6, 'x' );
            }
        }
        else {
            char onesConverted = (char)(ones +48);
            char tensConverted = (char)(tens +48);
            char hundredsConverted = (char)(hundreds +48);
            updateLetter(4, (char)hundredsConverted );
            updateLetter(5, (char)tensConverted );
            updateLetter(6, (char)onesConverted );
        }

    }

    private void updateLetter(int letterNum, char newLetter){
        letters[letterNum] = new Letter(charTableTextureID, letterWidth, letterHeight,positionX + letterNum*spacing,positionY, newLetter );
    }



    public void draw(int aPositionLocation, int aTextureCoordinatesLocation,
                     int uTextureUnitLocation,int uMatrixLocation, float[] viewProjectionMatrix){
        UpdateFpsCounter();
        updateLetters();
        for(int i=0; i<this.letters.length; i++){
            letters[i].draw(aPositionLocation, aTextureCoordinatesLocation, uTextureUnitLocation, uMatrixLocation, viewProjectionMatrix);
        }
    }

}
