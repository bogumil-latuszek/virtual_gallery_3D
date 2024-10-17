package pl.wsei.mobilne.myapplication.space3d;

import static android.opengl.Matrix.multiplyMV;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.wsei.mobilne.myapplication.R;
import pl.wsei.mobilne.myapplication.database.DatabaseHelper;
import pl.wsei.mobilne.myapplication.database.DbmWall;

import pl.wsei.mobilne.myapplication.space3d.geometry.Geometry;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.PointOnFace;
import pl.wsei.mobilne.myapplication.space3d.geometry.Ray;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private SQLiteDatabase database;

    public MyGLRenderer(Context context, ArrayList<String> wallsCoordinates){
        this.appContext = context;
        dbHelper = new DatabaseHelper(context);
        this.database = dbHelper.getReadableDatabase();

        this.fragmentShaderCode = TextResourceReader.readTextFileFromResource(
                context, R.raw.color_fragment_shader);
        this.vertexShaderCode = TextResourceReader.readTextFileFromResource(
                context, R.raw.color_vertex_shader);
        this.textureFragmentShaderCode = TextResourceReader.readTextFileFromResource(
                context, R.raw.texture_fragment_shader);
        this.textureVertexShaderCode = TextResourceReader.readTextFileFromResource(
                context, R.raw.texture_vertex_shader);

        // get walls from intent passed data and not from DB (temporary solution)
        float[] red_e = {235f/255f, 12f/255f, 49f/255f};
        float[] red_f = {242f/255f, 66f/255f, 95f/255f};


//        //***********************************************************
//        Resources res = context.getResources();
//        int lionId = R.drawable.lion;
//        Bitmap lionBitmap = BitmapFactory.decodeResource(res, lionId);
//
//        String pathToLion = FileManager.saveImageToStorage(lionBitmap, context);
//
//        Bitmap lionBitmapLoaded = FileManager.loadImageFromStorage(pathToLion);
//        int textureID = TextureHelper.loadTexture(lionBitmapLoaded);
//        walls.get(0).SetPainting(textureID);
//        //***********************************************************

    }


    // access to "drawing color variable" inside OpenGL
    // naming convention: A_ - shader attributes, U_ - shader uniforms
//    private DatabaseManager dbManager;
    private Context appContext;

    private Vector3D cameraMovementVector = new Vector3D(0f,0f,0f);
    private float cameraMoveSpeed = 0.2f;
    private String rotationCtrlPressedLocation = null;
    private float rotationSpeed = 0.2f;

    private PaintingCollection paintingCollection;
    private final float[] invertedViewProjectionMatrix = new float[16];
    private DatabaseHelper dbHelper;

    //define strings
//    private static final String U_USE_GLOBAL_COLOR = "u_useGlobalColor";
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";
    private static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    private static final String U_TEXTURE_UNIT = "u_TextureUnit";

//    private int bUseGlobalColorLocation;
    private int uColorLocation;

    private int aPositionLocation;

    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    //TODO:wyciąć tworzenie programu z shadera do osobnych klas, wyciąć kod shaderów w string do tych klas albo do res
    private final String fragmentShaderCode;
    private final String vertexShaderCode;

    private final String textureFragmentShaderCode;
    private final String textureVertexShaderCode;

    // remember identifiers of entities created "inside" OpenGL
    private int programObjectId; // vertex and fragment renderers combined
    private int vertexShaderId;
    private int fragmentShaderId;

    private int textureProgramObjectId; // vertex and fragment renderers combined
    private int textureVertexShaderId;
    private int textureFragmentShaderId;


    private final float[] aspectAdjustmentMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    // shapes to render
    private FloorGrid floorGrid;
    private RayLine touchRay;

    private Cuboid blueCuboid;
    private RotationCtrl rotationCtrl;
    private MovementCtrl movementCtrl;


    private  ArrayList<Wall> walls;

////////////////////////////////////////////////////////
    private int texture;

    private int uMatrixTextureLocation;

    private int aPositionTextureLocation;

    private int aTextureCoordinatesLocation;

    private int uTextureUnitLocation;

    private Point CameraPosition;

    private float[] viewRotationMatrix;
    private float[] viewTranslationMatrix;

//////////////////////////////////////////////////////////

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        //PaintingCollection.addSomePicturesFromResourcesToFiles(appContext);

        viewRotationMatrix = new float[16];
        Matrix.setIdentityM(viewRotationMatrix, 0);
        viewTranslationMatrix = new float[16];
        Matrix.setIdentityM(viewTranslationMatrix, 0);
        CameraPosition = new Point(0,0,0);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // prepare shaders
        programObjectId = ShaderHelper.buildProgram(vertexShaderCode, fragmentShaderCode);
        textureProgramObjectId = ShaderHelper.buildProgram(textureVertexShaderCode, textureFragmentShaderCode);

        // retrieve "location" of "shaders variables" inside OpenGL // co znaczą te literki_nazwa?
        uColorLocation = GLES20.glGetUniformLocation(programObjectId, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(programObjectId, A_POSITION);
        uMatrixLocation = GLES20.glGetUniformLocation(programObjectId, U_MATRIX);

        // retrieve attributes and uniforms from texture program
        uMatrixTextureLocation = GLES20.glGetUniformLocation(textureProgramObjectId, U_MATRIX);
        aPositionTextureLocation = GLES20.glGetAttribLocation(textureProgramObjectId, A_POSITION);
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(textureProgramObjectId, A_TEXTURE_COORDINATES);
        uTextureUnitLocation = GLES20.glGetUniformLocation(textureProgramObjectId, U_TEXTURE_UNIT);
//        bUseGlobalColorLocation = GLES20.glGetUniformLocation(textureProgramObjectId, U_USE_GLOBAL_COLOR);


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
        float[] black = {0f,0f,0f};
        float[] red_f = {242f/255f, 66f/255f, 95f/255f};
        float[] purple_e = {190f/255f, 12f/255f, 235f/255f};
        float[] purple_f = {218f/255f, 79f/255f, 253f/255f};
        floorGrid = new FloorGrid();

        blueCuboid = new Cuboid(1f, 1f, 1f, 2f, -6f);

        rotationCtrl = new RotationCtrl();
        rotationCtrl.setEdgeColor(black);

        movementCtrl = new MovementCtrl(new Point(0,0,0),0.3f);

        Point nearPointRay = new Point(-0.2f, -0.3f, 2.5f);
        Point farPointRay = new Point(-0.2f, -0.3f, -9.5f);
        Ray fixedRay = new Ray(nearPointRay,
                Geometry.vector3DBetweenTwoPoints(nearPointRay, farPointRay));
        touchRay = new RayLine(fixedRay);
        texture = TextureHelper.loadTexture(appContext, R.drawable.move_icon_transparent);

        paintingCollection = new PaintingCollection(appContext);

        walls = new ArrayList<Wall>();
        List<DbmWall> wallsLoaded = DbmWall.getAll(database);
        for (int i = 0; i < wallsLoaded.size(); i++) {
            DbmWall dbmWallNext = wallsLoaded.get(i);
            int wallID = dbmWallNext.getId();
            float xPosition = dbmWallNext.getX();
            float zPosition = dbmWallNext.getZ();
            String backWallPaintingName = dbmWallNext.back_painting;
            String frontWallPaintingName  = dbmWallNext.front_painting;
            String leftWallPaintingName   = dbmWallNext.left_painting;
            String rightWallPaintingName  = dbmWallNext.right_painting;
            Wall newWall = new Wall(0.5f, 1.0f, 0.5f, xPosition+0.5f, zPosition-8.5f, wallID, database);

            if(backWallPaintingName != null){
                int backWallTextureID  = paintingCollection.getTextureID(backWallPaintingName);
                newWall.setBackFacePaintingTexture(backWallTextureID);
            }
            if(frontWallPaintingName != null){
                int frontWallTextureID  = paintingCollection.getTextureID(frontWallPaintingName);
                newWall.setFrontFacePaintingTexture(frontWallTextureID);
            }
            if(leftWallPaintingName != null){
                int leftWallTextureID  = paintingCollection.getTextureID(leftWallPaintingName);
                newWall.setLeftFacePaintingTexture(leftWallTextureID);
            }
            if(rightWallPaintingName != null){
                int rightWallTextureID  = paintingCollection.getTextureID(rightWallPaintingName);
                newWall.setRightFacePaintingTexture(rightWallTextureID);
            }
            newWall.setEdgeColor(red_e);
            newWall.setFaceColor(red_f);
            newWall.setFaceOpacity(0.8f);
            walls.add(newWall);
        }
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
                -ratio, ratio, -1, 1, 3, 20);


        //projectionMatrix = CreateOrthographicProjectionMatrix(10, 10,3, 20);

        // why did I use this if it gets overwritten anyway?
        /*
        Matrix.setLookAtM(viewMatrix, 0,
                0f, 1.5f, 2f,
                0f, 0f, -10f,
                0f, 1f, 0f);
        */
        // TODO: loop to initialize wall internal modelMatrix


        for (Wall wall : walls) {
            wall.startTransforming();
        }


        blueCuboid.startTransforming();
        //blueCuboid.scale(0.1f, 1f, 1f);

        touchRay.startTransforming();

        rotationCtrl.startTransforming();
        rotationCtrl.move(aspectAdjustmentMatrix, -1.32f, -0.67f);

        movementCtrl.startTransforming();
        movementCtrl.move(aspectAdjustmentMatrix, 1.32f, -0.67f);

    }

    @Override
    public void onDrawFrame(GL10 unused) {
        rotationCtrl.rotateCamera(rotationCtrlPressedLocation, viewRotationMatrix, rotationSpeed);
        //move camera(viewTranslationMatrix)
        Matrix.translateM(viewTranslationMatrix,0, this.cameraMovementVector.x, this.cameraMovementVector.y, this.cameraMovementVector.z);
        //construct viewMatrix from viewTranslationMatrix and viewRotationMatrix
        Matrix.multiplyMM(viewMatrix, 0, viewRotationMatrix,0, viewTranslationMatrix,0);

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //animateCameraView();

        // TODO: move to code point where we change viewMatrix (f.ex into handleTouchPress(), handleTouchDrag()) ???
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        //this matrix will be useful for converting from normalised to world space
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        GLES20.glUseProgram(textureProgramObjectId);

        //***************** animation *************************
        Wall firstwall = walls.get(0);// only crushes with no walls

        long timePassedInMiliseconds = SystemClock.uptimeMillis();
        float animationPeriodInMilliseconds = 10000f;
        float currentFractionOfAnimation = (timePassedInMiliseconds % animationPeriodInMilliseconds)/animationPeriodInMilliseconds;
        float angle = currentFractionOfAnimation*360; //czas od ostatnich 10 sekund
        firstwall.set_Y_rotation(angle);

        //***************** animation *************************

        for(Wall wall : walls){
            wall.drawPaintings(aPositionTextureLocation,
                    aTextureCoordinatesLocation, uTextureUnitLocation,
                    uMatrixTextureLocation, viewProjectionMatrix);
        }

        // instruct OpenGL to use given program when drawing anything to the screen
        GLES20.glUseProgram(programObjectId);

        // draw our shapes
        floorGrid.draw(aPositionLocation, uColorLocation,   uMatrixLocation, viewProjectionMatrix);

        // if we pass projectionMatrix instead of viewProjectionMatrix then shape doesn't move with other shapes
        // (in that case the only transformation the shape is subjected to is perspective correction)

        for(Wall wall : walls){
            wall.draw(aPositionLocation, uColorLocation,   uMatrixLocation, viewProjectionMatrix);
        }
//        touchRay.draw(aPositionLocation, uColorLocation,   uMatrixLocation, viewProjectionMatrix);

        // to draw UI controls without depth test - just using draw order (drawn last is at front)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        rotationCtrl.draw(aPositionLocation, uColorLocation,   uMatrixLocation, aspectAdjustmentMatrix);



        // instruct OpenGL to use another (texture) program when drawing anything to the screen
        GLES20.glUseProgram(textureProgramObjectId);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
         //Draw movement Ctrl

        movementCtrl.draw(aPositionTextureLocation,
                          aTextureCoordinatesLocation, uTextureUnitLocation,
                          uMatrixTextureLocation, texture,  aspectAdjustmentMatrix);


        GLES20.glDisable(GLES20.GL_BLEND);
    }
/*
// I will come back to this, but for now, it doesn't work and I don't understand why
    private float[] CreateOrthographicProjectionMatrix (float right, float top,  float near, float far){
        float a11 = 1/right;
        float a12 = 0;
        float a13 = 0;
        float a14 = 0;
        float a21 = 0;
        float a22 = 1/top;
        float a23 = 0;
        float a24 = 0;
        float a31 = 0;
        float a32 = 0;
        float a33 = 1/(far-near);
        float a34 = -near/(far-near);
        float a41 = 0f;
        float a42 = 0f;
        float a43 = 0f;
        float a44 = 1f;
        float[] orthographicProjectionMatrix = {a11, a21, a31, a41, a12, a22,a32,a42, a13,a23,a33,a43, a14, a24, a34, a44};
        return orthographicProjectionMatrix;
    }
*/
//    private void animateCameraView() {
//        long uptimeMs = SystemClock.uptimeMillis();
//        long angle = uptimeMs % 3600L;
//        long timeSpace = uptimeMs % 70000L;
//        double radianAngle = Math.toRadians(angle / 10f);
//        double sinVal = Math.sin(radianAngle);
//        float delta = (float) (2 * sinVal);
//        float dx = 0f;
//        float dy = 0f;
//        float dz = 0f;
//        float cx = 0f;
//        float cy = 0f;
//        float upx = 0f;
//        float upy = 1f;
//        float lookAroundAngle = 0f;
//        float rotateAroundCameraLensAngle = 0f;
//        if (timeSpace < 10000L) {
//            dx = delta * 2;
//        }
//        else if ((timeSpace > 10000L) && (timeSpace < 20000L)) {
//            dy = delta;
//        }
//        else if ((timeSpace > 20000L) && (timeSpace < 30000L)) {
//            dz = delta;
//        }
//        else if ((timeSpace > 30000L) && (timeSpace < 40000L)) {
//            cx = delta * 3f;
//        }
//        else if ((timeSpace > 40000L) && (timeSpace < 50000L)) {
//            cy = delta * 3f;
//        }
//        else if ((timeSpace > 50000L) && (timeSpace < 60000L)) {
//            lookAroundAngle = angle / 15f;
//        }
//        else {
//            rotateAroundCameraLensAngle = angle / 20f;
//        }
//        if (lookAroundAngle > 0f) {
//            Matrix.setRotateM(viewMatrix, 0, lookAroundAngle, 0f, 1f, 0f);
//        }
//        else {
//            if (rotateAroundCameraLensAngle > 0f) {
//                double rotateAroundCameraLensRadian = Math.toRadians(rotateAroundCameraLensAngle);
//                upx = (float) Math.cos(rotateAroundCameraLensRadian);
//                upy = (float) Math.sin(rotateAroundCameraLensRadian);
//            }
//            Matrix.setLookAtM(viewMatrix, 0,
//                    dx, dy, dz,
//                    cx, cy, -10f,
//                    upx, upy, 0f);
//        }
//
//
//    }

    public int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        final int shaderObjectId = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        return shaderObjectId;
    }



    //te dwie funkcje handleTouchDrag i handleTouchPress powinny być zdefiniowane w interfejsie, możemy zrobić do nich dekorator?
    // TODO: change type of renderer parameter inside constructor of Surface View
    public void handleTouchDrag(float normalizedX, float normalizedY) {
//        String whereInsideRotationCtrl = this.rotationCtrl.where(normalizedX, normalizedY);
//        //Log.d("drag:", String.format("x = %s, y = %s [inside = %s]",
//        //        normalizedX, normalizedY, whereInsideRotationCtrl));
//        //there's a few problems with this function
//        //1) if you start dragging from a different point than you last touched the screen
//        //the view "jumps" around
//        //2) the span of rotation is limited
//        //3) we need a function that rotates camera on its own axis, not this
//
//        float dx = normalizedX * 4;
//        float dy = normalizedY * 4;
//        float dz = 0f;
//        float cx = 0f;
//        float cy = 0f;
//        float upx = 0f;
//        float upy = 1f;
//        if (whereInsideRotationCtrl.equals("outside")) {
//            Matrix.setLookAtM(viewMatrix, 0,
//                    dx, dy, dz,
//                    cx, cy, -10f,
//                    upx, upy, 0f);
//        }
//        else if (whereInsideRotationCtrl.equals("ring")) {
//            float lookAroundAngle = this.rotationCtrl.getRotationAngle(normalizedX, normalizedY);
//            //Log.d("touch:", String.format("x = %s, y = %s, angle = %s degrees", normalizedX, normalizedY, lookAroundAngle));
////            Matrix.setRotateM(viewMatrix, 0, lookAroundAngle, 0f, 1f, 0f);
//
//            Matrix.rotateM(viewMatrix, 0, lookAroundAngle, 0f, -1f, 0f);
//        }
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Point pointPressed = new Point(normalizedX, normalizedY, 0f);
        // this isn't checking for collision with objects in scene
        // this is just detection of user interacting with movement control
        // since there's actual collision detection being used in the form of rays, we should avoid
        // confusion, and change the name for the variable below to something more intuitive
        // for example: "movement_control_triggered"
        Optional<Vector3D> collision = this.movementCtrl.getMovementVector(pointPressed);
        if(collision.isPresent()){
            Vector3D moveVector3D = collision.get();
            float[] moveVector4D = new float[]{moveVector3D.x, moveVector3D.y, moveVector3D.z, 0};
            float[] targetVector4D = new  float[4];
            Matrix.multiplyMV(targetVector4D, 0, viewProjectionMatrix, 0, moveVector4D, 0 );
            targetVector4D[1] = 0; // y coord must remain 0  so we won't fly
            this.cameraMovementVector = new Vector3D(targetVector4D[0], 0, targetVector4D[2]);
            this.cameraMovementVector.scale(this.cameraMoveSpeed);
//            Matrix.translateM(viewTranslationMatrix,0, targetVector4D[0], targetVector4D[1], targetVector4D[2]);

//            this.CameraPosition.move(targetVector4D[0], targetVector4D[1], targetVector4D[2]);
//            Log.d("camera position:", CameraPosition.toString());
            return;
        }
        boolean pointInsideRotationCtrl = this.rotationCtrl.isInsideRotationCtrl(normalizedX, normalizedY);
        if(pointInsideRotationCtrl){
            this.rotationCtrlPressedLocation =  this.rotationCtrl.where(normalizedX, normalizedY);
            return;
        }
        // "outside" - calculate Ray and check which wall/face it hits
        // there's a problem here, that I may didn't realize before.
        // this function works only becouse we assume frustum has width and height 2
        // so we don't need to convert normalised coordinates in range of(-1 <-> 1)
        // what we should do instead is resize them to frustum width/height:
        // normalisedX * cameraFrustum.width/2
        // but there remains one problem - what does invertedViewProjectionMatrix do?
        // it's probably elongating and bending the further point and that's a problem since it's unpredictable
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        touchRay = new RayLine(ray);
        Optional<PointOnFace> collisionWithNearestFace = Wall.getPointedFace(ray, walls);
        if (collisionWithNearestFace.isPresent()) {
            PointOnFace pointedFace = collisionWithNearestFace.get();
            if(pointedFace.face.painting == null){
                if (! paintingCollection.isEmpty()) {
                    Texture randomTexture = paintingCollection.getNextTexture();
                    pointedFace.face.addPainting(randomTexture.textureName, randomTexture.textureID);
                }
            }
            else{
                pointedFace.face.removePainting();
            }
        }
    }

    public void handleTouchRelease(float normalizedX, float normalizedY) {
        this.cameraMovementVector = new Vector3D(0f,0f,0f);
        this.rotationCtrlPressedLocation = null;
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
                Geometry.vector3DBetweenTwoPoints(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

}