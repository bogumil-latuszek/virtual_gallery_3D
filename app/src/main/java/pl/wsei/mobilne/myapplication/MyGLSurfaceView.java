package pl.wsei.mobilne.myapplication;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    private final GLSurfaceView.Renderer myRenderer;
    public MyGLSurfaceView(Context context, GLSurfaceView.Renderer renderer) {
        super(context);

        //We are creating an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        myRenderer = renderer;

        //Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        //Render the view only when there is a change in the drawing data

        //I commented it out becouse rotation animation didn't work
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
