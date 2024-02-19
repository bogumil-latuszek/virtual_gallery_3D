package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.content.Intent;
import java.util.ArrayList;

import pl.wsei.mobilne.myapplication.space3d.MyGLRenderer;

public class Mode3DActivity extends AppCompatActivity {
    private GLSurfaceView gLView;
    private MyGLRenderer glRenderer;
    private static final int PERMISSION_REQ_CODE = 100;
    private String whyNeedPermission = "Application needs access to your Pictures to let you place them on walls of Virtual Gallery";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        requestImagesPermission();  // <---- TO !!!

        // get intent
        Intent intent = getIntent();
        // retrieve walls data
        ArrayList<String> walls = intent.getStringArrayListExtra("walls");

        glRenderer = new MyGLRenderer(getApplicationContext(), walls);
        gLView = new MyGLSurfaceView(this, glRenderer);

        //according to the book, we can add touch events here
        gLView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    // Convert touch coordinates into normalized device
                    // coordinates, keeping in mind that Android's Y
                    // coordinates are inverted.
                    //first we divide raw x coordinate by view width, obtained number will be in range 0-1
                    //next we multiply this number x2 so its now in range 0-2
                    //finally we substract 1 so now its in range (-1) to 1
                    //normalised space: topleft:(-1,1), bottomright:(1,-1) standard xy space coordinates )
                    final float normalizedX =
                            (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY =
                            -((event.getY() / (float) v.getHeight()) * 2 - 1);

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        gLView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                //Log.d("event:", "action down");

                                glRenderer.handleTouchPress(
                                        normalizedX, normalizedY);
                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        gLView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("event:", "action move");
                                glRenderer.handleTouchDrag(
                                        normalizedX, normalizedY);
                            }
                        });
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        gLView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("event:", "action move");
                                glRenderer.handleTouchRelease(
                                        normalizedX, normalizedY);
                            }
                        });
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        setContentView(gLView);
    }

    private void requestImagesPermission() {
        if (ContextCompat.checkSelfPermission(Mode3DActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mode3DActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(whyNeedPermission)
                        .setPositiveButton("I understand", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // nothing to do
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(Mode3DActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQ_CODE);
            }
        }
        else {
            // nothing to do - PERMISSION_GRANTED
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_CODE:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Now, reading from Pictures storage will work
//                }
                break;
            default:
                break;
        }
    }

}