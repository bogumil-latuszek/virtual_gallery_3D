package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
                    return true;
                } else {
                    return false;
                }
            }
        });
        setContentView(gLView);
    }
}