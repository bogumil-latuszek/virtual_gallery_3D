package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.Matrix;

import pl.wsei.mobilne.myapplication.space3d.geometry.Circle;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class MovementCtrl {
    private Circle circleCollider;

    private Point pointPressed;
    //private Rectangle Image?
    private  Rectangle2D visualRepresentation;

    private float[] aspectAdjustmentMatrix;
    public MovementCtrl(Point center, float radius) {
        this.circleCollider = new Circle(center, radius);
        this.visualRepresentation = new Rectangle2D(center,radius*2,radius*2);
    }

    //add a function that changes pointPressed?;

    public Vector3D getMovementVector(){ //change is so that point pressed is given as param?
        Vector3D movementVector3D = new Vector3D(0,0,0);

        float[] invertedAspectMatrix = new float[16];
        Matrix.invertM(invertedAspectMatrix, 0, aspectAdjustmentMatrix, 0);
        float[] vector4DPressed = new float[]{pointPressed.x, pointPressed.y, pointPressed.z, 0}; //czy tak się tworzy float array?
        float[] target4DVector = new float[4];
        Matrix.multiplyMV(target4DVector, 0, invertedAspectMatrix, 0, vector4DPressed, 0);

        Point pointPressedAspectAdjusted = new Point(target4DVector[0],target4DVector[1],target4DVector[2]);
//        public static void multiplyMV (float[] resultVec,
//        int resultVecOffset,
//        float[] lhsMat,
//        int lhsMatOffset,
//        float[] rhsVec,
//        int rhsVecOffset)

        if(circleCollider.checkIfPointInside(pointPressedAspectAdjusted)){
            Vector3D vector2D = circleCollider.getVectorToPointFromCenter(pointPressedAspectAdjusted);
            float forwardOrBackward = vector2D.y;
            float leftOrRight = vector2D.x;
            movementVector3D = new Vector3D(leftOrRight, 0, forwardOrBackward);
        }
        return movementVector3D;
    }
    public void updatePointPressed(Point point){
        pointPressed = point;
    }

    public void startTransforming(){
        visualRepresentation.startTransforming();
    }

    public void move(float[] aspectAdjustmentMatrix, float dx, float dy) {
        this.aspectAdjustmentMatrix = aspectAdjustmentMatrix;
        this.circleCollider.move(dx, dy);
        this.visualRepresentation.move(aspectAdjustmentMatrix, dx, dy);
    }

    public void draw(int aPositionLocation, int uColorLocation,
                     int uTextureUnitLocation, int uMatrixTextureLocation,
                     int uMatrixLocation, float[] aspectAdjustmentMatrix){
        float[] red_e = {235f/255f, 12f/255f, 49f/255f};
        this.visualRepresentation.draw(aPositionLocation, uColorLocation,
                uTextureUnitLocation, uMatrixTextureLocation,
                uMatrixLocation, aspectAdjustmentMatrix,
                red_e);
    }

//    public void bindData(TextureShaderProgram textureProgram) {
//        visualRepresentation.bindData(textureProgram);
//    }
}
