package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class Mode3DActivity extends AppCompatActivity {
    private GLSurfaceView gLView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gLView = new MyGLSurfaceView(this);
        //according to the book, we can add touch events here
        gLView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    // Convert touch coordinates into normalized device
                    // coordinates, keeping in mind that Android's Y
                    // coordinates are inverted.
                    Log.d("x obtained from touch event:", String.valueOf(event.getX()));
                    //first we divide raw x coordinate by view width, obtained number will be in range 0-1
                    //next we multiply this number x2 so its now in range 0-2
                    //finally we substract 1 so now its in range (-1) to 1
                    //normalised space: topleft:(-1,1), bottomright:(1,-1) standard xy space coordinates )
                    final float normalizedX =
                            (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY =
                            -((event.getY() / (float) v.getHeight()) * 2 - 1);

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Toast.makeText(getApplicationContext(), "TOUCH x:" + String.valueOf((float)((int)(normalizedX*100))/100) +" y:" + String.valueOf((float)((int)(normalizedX*1000))/100), Toast.LENGTH_SHORT).show();
                        gLView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("event:", "action down");
                                //Log.d("x obtained from touch event:", String.valueOf(event.getX()));
                                //Toast.makeText(getApplicationContext(), "TOUCH", Toast.LENGTH_SHORT).show();
                                /*
                                gLView.handleTouchPress(
                                        normalizedX, normalizedY);*/
                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        gLView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("event:", "action move");
                                //Toast.makeText(getApplicationContext(), "Drag", Toast.LENGTH_SHORT).show();
                                /*airHockeyRenderer.handleTouchDrag(
                                        normalizedX, normalizedY);*/
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