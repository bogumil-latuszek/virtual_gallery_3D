package pl.wsei.mobilne.myapplication.space3d;

import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import pl.wsei.mobilne.myapplication.R;

public class PaintingCollection {
    private Context appContext;
    private ArrayList<Integer> textureIDs;
    public PaintingCollection(Context context) {
        this.appContext = context;
        textureIDs = new ArrayList<>();
        loadAllTextures();
    }

    public void loadAllTextures(){
        int newTextureID1 = TextureHelper.loadTexture(appContext, R.drawable.leaf_texture);
        int newTextureID2 = TextureHelper.loadTexture(appContext, R.drawable.face1);

        textureIDs.add(newTextureID1);
        textureIDs.add(newTextureID2);
    }

    public int getRandomTextureID(){
        int randomIndex = ThreadLocalRandom.current().nextInt(0, textureIDs.size());
        return textureIDs.get(randomIndex);
    }
}
