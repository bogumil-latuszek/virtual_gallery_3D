package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.Matrix.multiplyMV;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.wsei.mobilne.myapplication.database.DatabaseHelper;
import pl.wsei.mobilne.myapplication.database.dbmWall;

import pl.wsei.mobilne.myapplication.space3d.geometry.Geometry;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.Ray;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    public MyGLRenderer(Context context){
        this.appContext = context;
        dbHelper = new DatabaseHelper(context);
//        DatabaseHelper dbHelper = new DatabaseHelper(context);
//        dbManager = new DatabaseManager(dbHelper);
    }
    // access to "drawing color variable" inside OpenGL
    // naming convention: A_ - shader attributes, U_ - shader uniforms
//    private DatabaseManager dbManager;
    private Context appContext;
    private final float[] invertedViewProjectionMatrix = new float[16];
    private DatabaseHelper dbHelper;
    private static final String A_COLOR = "a_Color";
    private static final String U_COLOR = "u_Color";
    private static final String U_USE_GLOBAL_COLOR = "u_useGlobalColor";
    private int aColorLocation;
    private int uColorLocation;
    private int bUseGlobalColorLocation;

    // access to "vertex position" inside OpenGL
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 u_Color;" +
                    "varying vec4 v_Color;" +
                    "uniform bool u_useGlobalColor;" +
                    "void main() {" +
                    "  gl_FragColor = v_Color;" +
                    "  if (u_useGlobalColor) {" +
                    "    gl_FragColor = u_Color;" +
                    "  }" +
                    "}";
    private final String vertexShaderCode =
            "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec4 a_Color;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  v_Color = a_Color;" +
                    "  gl_Position = u_Matrix * a_Position;" +
                    "}";

    // remember identifiers of entities created "inside" OpenGL
    private int programObjectId; // vertex and fragment renderers combined
    private int vertexShaderId;
    private int fragmentShaderId;

    private final float[] aspectAdjustmentMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    // shapes to render
    private FloorGrid floorGrid;
    private RayLine touchRay;

//    private Cuboid blueCuboid;
//    private Cuboid greenCuboid;
//    private Cuboid limeCuboid;
//    private Cuboid redCuboid;
//    private Cuboid orangeCuboid;
//    private Cuboid purpleCuboid;
//    private Cuboid purpleCuboid2;
//    private Cuboid purpleCuboid3;
    private RotationCtrl rotationCtrl;

    //    private ArrayList<WallCoordinates>  wallCoordinatesList;
    private  ArrayList<Wall> walls;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // prepare shaders
        vertexShaderId = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        fragmentShaderId = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        programObjectId = loadProgram(vertexShaderId, fragmentShaderId);

        // instruct OpenGL to use the program defined here when
        // drawing anything to the screen
        GLES20.glUseProgram(programObjectId);

        // retrieve "location" of "shaders variables" inside OpenGL // co znaczą te literki_nazwa?
        aColorLocation = GLES20.glGetAttribLocation(programObjectId, A_COLOR);
        uColorLocation = GLES20.glGetUniformLocation(programObjectId, U_COLOR);
        bUseGlobalColorLocation = GLES20.glGetUniformLocation(programObjectId, U_USE_GLOBAL_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(programObjectId, A_POSITION);

        uMatrixLocation = GLES20.glGetUniformLocation(programObjectId, U_MATRIX);

        // define color to be used as we call glClear()
        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 0.0f);
        // Prepare our shape
        //                    R         G         B
        float[] orange_f = {252f/255f, 186f/255f, 3f/255f};
        float[] orange_e = {223f/255f, 165f/255f, 3f/255f};
        float[] green_e = {15f/255f, 92f/255f, 35f/255f};
        float[] green_f = {27f/255f, 153f/255f, 60f/255f};
        float[] lime_e = {60f/255f, 235f/255f, 12f/255f};
        float[] lime_f = {140f/255f, 231f/255f, 115f/255f};
        float[] red_e = {235f/255f, 12f/255f, 49f/255f};
        float[] red_f = {242f/255f, 66f/255f, 95f/255f};
        float[] purple_e = {190f/255f, 12f/255f, 235f/255f};
        float[] purple_f = {218f/255f, 79f/255f, 253f/255f};
        floorGrid = new FloorGrid();

//        wallCoordinatesList = new ArrayList<WallCoordinates>();
//        wallCoordinatesList.add( new WallCoordinates(0f,0f));
//        wallCoordinatesList.add( new WallCoordinates(0f,1f));
//        wallCoordinatesList.add( new WallCoordinates(0f,2f));
//        wallCoordinatesList.add( new WallCoordinates(2f,1f));
//        wallCoordinatesList.add( new WallCoordinates(3f,1f));

        walls = new ArrayList<Wall>();
//        for (int i = 0; i < wallCoordinatesList.size(); i++) {
//            WallCoordinates currentWallCoord = wallCoordinatesList.get(i);
//            Wall newWall = new Wall();
//            newWall.X_position = currentWallCoord.getX();
//            newWall.Z_position = currentWallCoord.getZ();
//            newWall.setEdgeColor(red_e);
//            newWall.setFaceColor(red_f);
//            newWall.setFaceOpacity(0.8f);
//            walls.add(newWall);
//        }


        List<dbmWall> wallsLoaded = dbmWall.getAll(dbHelper);
        for (int i = 0; i < wallsLoaded.size(); i++) {
            float xPosition = wallsLoaded.get(i).getX();
            float zPosition = wallsLoaded.get(i).getZ();
            Wall newWall = new Wall(0.5f, 1.0f, 0.5f, xPosition+0.5f, zPosition-8.5f, "Wall nr."+i);
            newWall.setEdgeColor(red_e);
            newWall.setFaceColor(red_f);
            newWall.setFaceOpacity(0.8f);
            walls.add(newWall);
        }


        /*
        greenCuboid = new Cuboid(0.5f, 1f, 0.5f, 0f, 0f);
        greenCuboid.setEdgeColor(green_e);
        greenCuboid.setFaceColor(green_f);

        blueCuboid = new Cuboid(1f, 1f, 1f);

        limeCuboid = new Cuboid(0.1f, 2f, 1f);
        limeCuboid.setEdgeColor(lime_e);
        limeCuboid.setFaceColor(lime_f);

        redCuboid = new Cuboid(1f, 1f, 1f);
        redCuboid.setEdgeColor(red_e);
        redCuboid.setFaceColor(red_f);

        orangeCuboid = new Cuboid(1f, 1f, 1f);
        orangeCuboid.setEdgeColor(orange_e);
        orangeCuboid.setFaceColor(orange_f);
        orangeCuboid.setFaceOpacity(0.8f);

        purpleCuboid = new Cuboid(1f, 0.2f, 3f);
        purpleCuboid.setEdgeColor(purple_e);
        purpleCuboid.setFaceColor(purple_f);
        purpleCuboid2 = new Cuboid(0.1f, 1f, 0.1f);
        purpleCuboid2.setEdgeColor(purple_e);
        purpleCuboid2.setFaceColor(purple_f);
        purpleCuboid2.setFaceOpacity(0.2f);
        purpleCuboid3 = new Cuboid(0.1f, 1f, 0.1f);
        purpleCuboid3.setEdgeColor(purple_e);
        purpleCuboid3.setFaceColor(purple_f);
        purpleCuboid3.setFaceOpacity(0.8f);
        */

        rotationCtrl = new RotationCtrl();
        rotationCtrl.setEdgeColor(red_e);

        Point nearPointRay = new Point(-0.2f, -0.3f, 2.5f);
        Point farPointRay = new Point(-0.2f, -0.3f, -9.5f);
        Ray fixedRay = new Ray(nearPointRay,
                Geometry.vectorBetweenTwoPoints(nearPointRay, farPointRay));
        touchRay = new RayLine(fixedRay);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0,0,width,height);

        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method

        // calculate aspect adjustment matrix
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            // Landscape
            Matrix.orthoM(aspectAdjustmentMatrix, 0,
                    -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            Matrix.orthoM(aspectAdjustmentMatrix, 0,
                    -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }

        int offsetToStart_in_projectionMatrix = 0;
        Matrix.frustumM(projectionMatrix, offsetToStart_in_projectionMatrix,
                -ratio, ratio, -1, 1, 3, 10);

        Matrix.setLookAtM(viewMatrix, 0,
                0f, 1.5f, 0f,
                0f, 0f, -10f,
                0f, 1f, 0f);

        // TODO: loop to initialize wall internal modelMatrix

        for (Wall wall : walls) {
            wall.startTransforming();
        }

        /*
        greenCuboid.startTransforming();
        greenCuboid.scale(1f, 1f, 1f);
        greenCuboid.move(0f, 0f, -5f);

        limeCuboid.startTransforming();
        limeCuboid.move(2f, 0f, -6.5f);

        blueCuboid.startTransforming();
        blueCuboid.move(2f, 0f, -6f);
        blueCuboid.scale(0.1f, 1f, 1f);

        redCuboid.startTransforming();
        redCuboid.move(-1.5f, -0f, -5f);
        redCuboid.scale(0.5f, 1f, 1f);

        orangeCuboid.startTransforming();
        orangeCuboid.move(-2.0f, 0f, -7f);
        orangeCuboid.scale(2f, 1f, 1f);

        purpleCuboid.startTransforming();
        purpleCuboid.move(5.0f, -0.9f, 4f);

        purpleCuboid2.startTransforming();
        purpleCuboid2.move(-2.0f, 0f, 4f);

        purpleCuboid3.startTransforming();
        purpleCuboid3.move(-1.5f, 0f, 5f);
         */
        touchRay.startTransforming();

        rotationCtrl.startTransforming();
        rotationCtrl.move(aspectAdjustmentMatrix, -1.32f, -0.67f);
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //animateCameraView();

        // TODO: move to code point where we change viewMatrix (f.ex into handleTouchPress(), handleTouchDrag()) ???
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        //this matrix will be useful for converting from normalised to world space
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        // draw our shapes
        floorGrid.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);

        // if we pass projectionMatrix instead of viewProjectionMatrix then shape doesn't move with other shapes
        // (in that case the only transformation the shape is subjected to is perspective correction)

        for(Wall wall : walls){
            wall.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        }

        /*
        greenCuboid.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        blueCuboid.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        limeCuboid.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        redCuboid.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        orangeCuboid.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        purpleCuboid.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        purpleCuboid2.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
        purpleCuboid3.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);
         */
        touchRay.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, viewProjectionMatrix);

        // to draw UI controls without depth test - just using draw order (drawn last is at front)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        rotationCtrl.draw(aPositionLocation, uColorLocation, bUseGlobalColorLocation, uMatrixLocation, aspectAdjustmentMatrix);
    }

    private void animateCameraView() {
        long uptimeMs = SystemClock.uptimeMillis();
        long angle = uptimeMs % 3600L;
        long timeSpace = uptimeMs % 70000L;
        double radianAngle = Math.toRadians(angle / 10f);
        double sinVal = Math.sin(radianAngle);
        float delta = (float) (2 * sinVal);
        float dx = 0f;
        float dy = 0f;
        float dz = 0f;
        float cx = 0f;
        float cy = 0f;
        float upx = 0f;
        float upy = 1f;
        float lookAroundAngle = 0f;
        float rotateAroundCameraLensAngle = 0f;
        if (timeSpace < 10000L) {
            dx = delta * 2;
        }
        else if ((timeSpace > 10000L) && (timeSpace < 20000L)) {
            dy = delta;
        }
        else if ((timeSpace > 20000L) && (timeSpace < 30000L)) {
            dz = delta;
        }
        else if ((timeSpace > 30000L) && (timeSpace < 40000L)) {
            cx = delta * 3f;
        }
        else if ((timeSpace > 40000L) && (timeSpace < 50000L)) {
            cy = delta * 3f;
        }
        else if ((timeSpace > 50000L) && (timeSpace < 60000L)) {
            lookAroundAngle = angle / 15f;
        }
        else {
            rotateAroundCameraLensAngle = angle / 20f;
        }
        if (lookAroundAngle > 0f) {
            Matrix.setRotateM(viewMatrix, 0, lookAroundAngle, 0f, 1f, 0f);
        }
        else {
            if (rotateAroundCameraLensAngle > 0f) {
                double rotateAroundCameraLensRadian = Math.toRadians(rotateAroundCameraLensAngle);
                upx = (float) Math.cos(rotateAroundCameraLensRadian);
                upy = (float) Math.sin(rotateAroundCameraLensRadian);
            }
            Matrix.setLookAtM(viewMatrix, 0,
                    dx, dy, dz,
                    cx, cy, -10f,
                    upx, upy, 0f);
        }


    }

    public int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        final int shaderObjectId = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        return shaderObjectId;
    }

    public int loadProgram(int vertexShaderId, int fragmentShaderId) {
        // create empty OpenGL ES Program
        final int programObjectId  = GLES20.glCreateProgram();
        // add the shaders to program
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        // create OpenGL ES program executables
        GLES20.glLinkProgram(programObjectId);
        return programObjectId;
    }



    //te dwie funkcje handleTouchDrag i handleTouchPress powinny być zdefiniowane w interfejsie, możemy zrobić do nich dekorator?
    // TODO: change type of renderer parameter inside constructor of Surface View
    public void handleTouchDrag(float normalizedX, float normalizedY) {
        String whereInsideRotationCtrl = this.rotationCtrl.where(normalizedX, normalizedY);
        Log.d("drag:", String.format("x = %s, y = %s [inside = %s]",
                normalizedX, normalizedY, whereInsideRotationCtrl));
        //there's a few problems with this function
        //1) if you start dragging from a different point than you last touched the screen
        //the view "jumps" around
        //2) the span of rotation is limited
        //3) we need a function that rotates camera on its own axis, not this

        float dx = normalizedX * 4;
        float dy = normalizedY * 4;
        float dz = 0f;
        float cx = 0f;
        float cy = 0f;
        float upx = 0f;
        float upy = 1f;
        if (whereInsideRotationCtrl.equals("outside")) {
            Matrix.setLookAtM(viewMatrix, 0,
                    dx, dy, dz,
                    cx, cy, -10f,
                    upx, upy, 0f);
        }
        else if (whereInsideRotationCtrl.equals("ring")) {
            float lookAroundAngle = this.rotationCtrl.getRotationAngle(normalizedX, normalizedY);
            Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, lookAroundAngle));
            Matrix.setRotateM(viewMatrix, 0, lookAroundAngle, 0f, 1f, 0f);
        }
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        String whereInsideRotationCtrl = this.rotationCtrl.where(normalizedX, normalizedY);
        Log.d("touch:", String.format("x = %s, y = %s", normalizedX, normalizedY));
        float dx = 0;
        float dy = 1.5f;
        float dz = 0f;
        float cx = normalizedX;
        float cy = normalizedY;
        float upx = 0f;
        float upy = 1f;

        if (whereInsideRotationCtrl.equals("center")) {
            float lookAroundAngle = 0.0f;
            Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, lookAroundAngle));
            Matrix.setRotateM(viewMatrix, 0, lookAroundAngle, 0f, 1f, 0f);
        }
        else if (whereInsideRotationCtrl.equals("ring")) {
            float lookAroundAngle = this.rotationCtrl.getRotationAngle(normalizedX, normalizedY);
            Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, lookAroundAngle));
            Matrix.setRotateM(viewMatrix, 0, lookAroundAngle, 0f, 1f, 0f);
        }
        else if (whereInsideRotationCtrl.equals("up")) {
            this.rotationCtrl.up();
            float lookUpAngle = this.rotationCtrl.getUpAngle();
            Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, lookUpAngle));
            Matrix.setRotateM(viewMatrix, 0, lookUpAngle, -1f, 0f, 0f);
        }
        else if (whereInsideRotationCtrl.equals("down")) {
            this.rotationCtrl.down();
            float lookUpAngle = this.rotationCtrl.getUpAngle();
            Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, lookUpAngle));
            Matrix.setRotateM(viewMatrix, 0, lookUpAngle, -1f, 0f, 0f);
        }
        else if (whereInsideRotationCtrl.equals("right")) {
            float addAngle = 5f;
            Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, addAngle));
            Matrix.rotateM(viewMatrix, 0, addAngle, 0f, 1f, 0f);
        }
        else if (whereInsideRotationCtrl.equals("left")) {
            float addAngle = 5f;
            Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, addAngle));
            Matrix.rotateM(viewMatrix, 0, addAngle, 0f, -1f, 0f);
        }
        else { // "outside" - calculate Ray and check which wall/face it hits
            Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

            touchRay = new RayLine(ray);

            for (int i = 0; i < walls.size(); i++) {
                Wall wall = walls.get(i);
//            Boolean wallHitByRay = wall.CheckSimpleRayCollision(ray);
//            if (wallHitByRay){
//                //Toast.makeText(appContext, "WALL HIT", Toast.LENGTH_SHORT).show(); this gives error
//                Log.d("WallCollision","ray hit wall nr."+i);
//                break;
//            }
                String faceHitByRayID = wall.GetCollidedFaceID(ray);
                if(faceHitByRayID != null){
                    Log.d("FaceCollision",faceHitByRayID);
                    break; //but does it break the loop? TODO:Log iterations and check if they stop after break
                }
            }
        }
    }

    private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        multiplyMV(
                nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);
        //theres an error somewhere here, far point Y should be further away from 0(expanding)
        //than near point Y, but instead its closer to 0 (shrinking)
        //maybe there's a problem with invertedviewprojectionmatrix?
        Point nearPointRay =
                new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPointRay =
                new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Ray(nearPointRay,
                Geometry.vectorBetweenTwoPoints(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

}