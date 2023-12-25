package pl.wsei.mobilne.myapplication;
import android.content.Context;
import android.opengl.GLSurfaceView;

import pl.wsei.mobilne.myapplication.space3d.MyGLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer myRenderer;
    public MyGLSurfaceView(Context context, MyGLRenderer renderer) {
        super(context);

        //We are creating an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        myRenderer = renderer;

        //Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        //Render the view only when there is a change in the drawing data

        //I commented it out becouse rotation animation didn't work
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); //TODO: CHECK IF SHOULD BE USED
    }
}
