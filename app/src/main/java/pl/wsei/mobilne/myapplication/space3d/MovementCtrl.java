package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.Matrix;

import java.util.Optional;

import pl.wsei.mobilne.myapplication.space3d.geometry.Circle;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

public class MovementCtrl {
    private Circle circleCollider;

    //private Rectangle Image?
    private  Rectangle2D visualRepresentation;

    private float[] aspectAdjustmentMatrix;
    public MovementCtrl(Point center, float radius) {
        this.circleCollider = new Circle(center, radius);
        this.visualRepresentation = new Rectangle2D(center,radius*2,radius*2);
    }

    //add a function that changes pointPressed?;

    public Optional<Vector3D> getMovementVector(Point pointPressed){ //change is so that point pressed is given as param?
        Optional<Vector3D> movementVector3D = Optional.empty();

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
            float forwardOrBackward = vector2D.y * -1;
            float leftOrRight = vector2D.x * -1;
            movementVector3D = Optional.of(new Vector3D(leftOrRight, 0, forwardOrBackward));
        }
        return movementVector3D;
    }

    public void startTransforming(){
        visualRepresentation.startTransforming();
    }

    public void move(float[] aspectAdjustmentMatrix, float dx, float dy) {
        this.aspectAdjustmentMatrix = aspectAdjustmentMatrix;
        this.circleCollider.move(dx, dy);
        this.visualRepresentation.move(aspectAdjustmentMatrix, dx, dy);
    }

    public void draw(int aPositionLocation,
                     int aTextureCoordinatesLocation, int uTextureUnitLocation,
                     int uMatrixLocation,
                     int textureId,
                     float[] aspectAdjustmentMatrix){

        this.visualRepresentation.draw(aPositionLocation,
                aTextureCoordinatesLocation, uTextureUnitLocation,
                uMatrixLocation,
                textureId, aspectAdjustmentMatrix);
    }
}
