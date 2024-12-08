package pl.wsei.mobilne.myapplication;
import android.content.Context;
import android.opengl.GLSurfaceView;

import pl.wsei.mobilne.myapplication.space3d.SceneRenderer;

public class Surface3DView extends GLSurfaceView {

    private final SceneRenderer sceneRenderer;
    public Surface3DView(Context context, SceneRenderer renderer) {
        super(context);

        //We are creating an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        sceneRenderer = renderer;

        //Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        //Render the view only when there is a change in the drawing data

        //I commented it out becouse rotation animation didn't work
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); //TODO: CHECK IF SHOULD BE USED
    }
}