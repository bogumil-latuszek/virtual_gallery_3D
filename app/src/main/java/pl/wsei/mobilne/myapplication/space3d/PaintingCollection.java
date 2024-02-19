package pl.wsei.mobilne.myapplication.space3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pl.wsei.mobilne.myapplication.R;
import pl.wsei.mobilne.myapplication.utility.FileManager;

public class PaintingCollection {
    private Context appContext;
    private int nextTextureIdx = 0;
    private ArrayList<Integer> textureIDs;
    private Map<String, Integer> texturesCache; // filename --> textureId
    private int imageNotFoundID;

    private Pair<String, Integer> texture;
    public PaintingCollection(Context context) {
        this.appContext = context;
        textureIDs = new ArrayList<>();
        texturesCache = new HashMap<>();
        loadAllTextures();
        this.imageNotFoundID = TextureHelper.loadTexture(appContext, R.drawable.image_not_found );
    }

    public void loadAllTextures(){
        ArrayList<File> imageFiles = FileManager.getAllImageFiles();
        for (File imageFile: imageFiles) {
            String textureName = imageFile.getName();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            int textureId = TextureHelper.loadTexture(bitmap);
            this.texturesCache.put(textureName, Integer.valueOf(textureId));
            this.textureIDs.add(textureId);
        }
    }

    public boolean isEmpty(){
        return texturesCache.isEmpty();
    }

    public Texture getNextTexture(){
        Set<String> textureNamesSet = texturesCache.keySet();

        ArrayList<String> textureNames = new ArrayList<>(textureNamesSet);
        int textureCount = textureNames.size();
        String nextTextureName = textureNames.get(nextTextureIdx);
        nextTextureIdx = (nextTextureIdx + 1) % textureCount;

        Integer nextTextureID = texturesCache.get(nextTextureName);
        Texture randomTexture = new Texture(nextTextureName, nextTextureID);

        return randomTexture;
    }
    public int getTextureID(String textureName){
        if (this.texturesCache.containsKey(textureName)) {
            Integer textureId = this.texturesCache.get(textureName);
            return textureId.intValue();
        }
        return imageNotFoundID;
    }
}
