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
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.leaf_texture));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.face1));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.autumn_forest));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.fog_forest));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.garden));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.lion));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.mountain_lake));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.painting));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.ray_forest));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.sunrise));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.sunrise_dark));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.sunset));
        textureIDs.add(TextureHelper.loadTexture(appContext, R.drawable.underwater));
    }

    public int getRandomTextureID(){
        int randomIndex = ThreadLocalRandom.current().nextInt(0, textureIDs.size());
        return textureIDs.get(randomIndex);
    }
}
