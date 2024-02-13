package pl.wsei.mobilne.myapplication.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {
    public static String path = Environment.getExternalStorageDirectory().toString()+"/Pictures";

    public static Bitmap loadImageFromStorage(String fileName)
    {
        try {
            File f=new File(path, fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            return bitmap;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return  null;
        }

        //path = Environment.getExternalStorageDirectory().toString()+"/Pictures";

    }

    public static void  listFiles(){
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

    public static ArrayList<File> getAllImageFiles(){
        File directory = new File(path);
        ArrayList<File> imageFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            String fileName = files[i].getName();
            if(isImageType(fileName)){
                File imageFile = files[i];
                imageFiles.add(imageFile);
            }
        }
        return imageFiles;
    }
    public static boolean isImageType(String fileName){
        int fileNameLength = fileName.length();

        if(fileName.charAt(fileNameLength-3)=='p'&&fileName.charAt(fileNameLength-2)=='n'&&fileName.charAt(fileNameLength-1)=='g'){
            return  true;
        }
        else if (fileName.charAt(fileNameLength-3)=='j'&&fileName.charAt(fileNameLength-2)=='p'&&fileName.charAt(fileNameLength-1)=='g') {
            return  true;
        }
        return false;
    }

    public static void saveImageToStorage(Bitmap bitmapImage, Context context, String fileName){
        File directory = new File(path);
        File mypath=new File(directory,fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        ContextWrapper cw = new ContextWrapper(context);
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory,"profile.jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return directory.getAbsolutePath();
    }
}
