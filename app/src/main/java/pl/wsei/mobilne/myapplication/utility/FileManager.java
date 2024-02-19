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
    public static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/Camera";
    public static boolean cameraDirExists = false;

    private static void setPathToCamera(){
        File cameraFolder = new File(path);
        if(!cameraFolder.exists()){
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString();
        }
        cameraDirExists = true;
    }
    public static Bitmap loadImageFromStorage(String fileName)
    {
        if(!cameraDirExists){
            setPathToCamera();
        }
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
    }

    public static ArrayList<File> getAllImageFiles(){
        if(!cameraDirExists){
            setPathToCamera();
        }
        File directory = new File(path);
        ArrayList<File> imageFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        int maxFilesToLoad = (files.length < 10) ? files.length : 10;
        for (int i = 0; i < maxFilesToLoad; i++)
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
        final String[] imageFileExtensions = new String[] {
                "jpg",
                "png",
                "gif",
                "jpeg"
        };

        for (String extension: imageFileExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static void saveImageToStorage(Bitmap bitmapImage, Context context, String fileName){
        if(!cameraDirExists){
            setPathToCamera();
        }
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
    }
}
