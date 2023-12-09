package pl.wsei.mobilne.myapplication;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    //private final MyGLRenderer renderer;
    private final MultiTriangleRenderer renderer;
    public MyGLSurfaceView(Context context) {
        super(context);

        //We are creating an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        //renderer= new MyGLRenderer();
        renderer = new MultiTriangleRenderer();

        //Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        //Render the view only when there is a change in the drawing data

        //I commented it out becouse rotation animation didn't work
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
