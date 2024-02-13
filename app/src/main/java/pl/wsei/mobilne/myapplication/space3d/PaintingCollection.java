package pl.wsei.mobilne.myapplication.space3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import pl.wsei.mobilne.myapplication.R;
import pl.wsei.mobilne.myapplication.utility.FileManager;

public class PaintingCollection {
    private Context appContext;
    private ArrayList<Integer> textureIDs;
    private Map<String, Integer> texturesCache; // filename --> textureId
    public PaintingCollection(Context context) {
        this.appContext = context;
        textureIDs = new ArrayList<>();
        texturesCache = new HashMap<>();
        loadAllTextures();
    }

    public void loadAllTextures(){

        int imageNotFoundID = TextureHelper.loadTexture(appContext, R.drawable.image_not_found );
        this.texturesCache.put("image_not_found.jpg", imageNotFoundID);

        ArrayList<File> imageFiles = FileManager.getAllImageFiles();
        for (File imageFile: imageFiles) {
            String textureName = imageFile.getName();
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            int textureId = TextureHelper.loadTexture(bitmap);
            this.texturesCache.put(textureName, Integer.valueOf(textureId));
            this.textureIDs.add(textureId);
        }
    }

    public int getRandomTextureID(){
        int randomIndex = ThreadLocalRandom.current().nextInt(0, textureIDs.size());
        return textureIDs.get(randomIndex);
    }
    public int getTextureID(String textureName){
        if (this.texturesCache.containsKey(textureName)) {
            Integer textureId = this.texturesCache.get(textureName);
            return textureId.intValue();
        }
        Integer textureId = this.texturesCache.get("image_not_found.jpg");
        return textureId.intValue();
    }
}
