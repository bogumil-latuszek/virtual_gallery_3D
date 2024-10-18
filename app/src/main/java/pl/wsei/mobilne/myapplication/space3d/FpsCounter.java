package pl.wsei.mobilne.myapplication.space3d;

import android.content.Context;
import android.os.SystemClock;

import pl.wsei.mobilne.myapplication.R;

public class FpsCounter {

    private float spacing;
    private CharacterDisplayingRectangle[] characterDisplayingRectangles;
    private float positionX;
    private float positionY;
    float charWidth = 0.1f;
    float charHeight = 0.1f;
    int charTableTextureID;

    private int fpsCount;
    long timeSinceLastUpdate = 0;
    int framesCounted = 0;


    public FpsCounter(Context context, float positionX, float positionY, float spacing) {
        this.spacing = spacing;
        this.positionX = positionX;
        this.positionY = positionY;
        charTableTextureID = TextureHelper.loadTexture(context, R.drawable.char_table);
        characterDisplayingRectangles = new CharacterDisplayingRectangle[7];
        characterDisplayingRectangles[0] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX,positionY, 'f');
        characterDisplayingRectangles[1] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX + spacing,positionY, 'p');
        characterDisplayingRectangles[2] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX + 2*spacing,positionY, 's');
        characterDisplayingRectangles[3] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX + 3*spacing,positionY, ':');
        characterDisplayingRectangles[4] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX + 4*spacing,positionY, '0');
        characterDisplayingRectangles[5] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX + 5*spacing,positionY, '5');
        characterDisplayingRectangles[6] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX + 6*spacing,positionY, '6');
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


    private void updateCharacters(){
        int ones = fpsCount%10;
        int tens = (fpsCount/10)%10;
        int hundreds = (fpsCount/100)%10;
        if(hundreds==0){
            if(tens==0){
                char onesConverted = (char)(ones +48);
                updateChar(4, onesConverted);
                updateChar(5, 'x' );
                updateChar(6, 'x' );
            }
            else{
                char onesConverted = (char)(ones +48);
                char tensConverted = (char)(tens +48);
                updateChar(4, tensConverted );
                updateChar(5, onesConverted );
                updateChar(6, 'x' );
            }
        }
        else {
            char onesConverted = (char)(ones +48);
            char tensConverted = (char)(tens +48);
            char hundredsConverted = (char)(hundreds +48);
            updateChar(4, (char)hundredsConverted );
            updateChar(5, (char)tensConverted );
            updateChar(6, (char)onesConverted );
        }

    }

    private void updateChar(int charNum, char newChar){
        characterDisplayingRectangles[charNum] = new CharacterDisplayingRectangle(charTableTextureID, charWidth, charHeight,positionX + charNum*spacing,positionY, newChar );
    }



    public void draw(int aPositionLocation, int aTextureCoordinatesLocation,
                     int uTextureUnitLocation,int uMatrixLocation, float[] viewProjectionMatrix){
        UpdateFpsCounter();
        updateCharacters();
        for(int i = 0; i<this.characterDisplayingRectangles.length; i++){
            characterDisplayingRectangles[i].draw(aPositionLocation, aTextureCoordinatesLocation, uTextureUnitLocation, uMatrixLocation, viewProjectionMatrix);
        }
    }

}
