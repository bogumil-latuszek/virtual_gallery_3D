package pl.wsei.mobilne.myapplication.space3d;

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

    public Vector3D getMovementVector(){
        Vector3D movementVector3D = new Vector3D(0,0,0);
//        Point pointAdjusted = Matrix.multiplyMV (,
//        int resultVecOffset,
//        float[] lhsMat,
//        int lhsMatOffset,
//        float[] rhsVec,
//        int rhsVecOffset)
        if(circleCollider.checkIfPointInside(pointPressed)){
            Vector3D vector2D = circleCollider.getVectorToPointFromCenter(pointPressed);
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
        this.circleCollider.translate(dx, dy);
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
