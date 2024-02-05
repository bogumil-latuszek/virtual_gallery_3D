package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.GLES20;
import android.opengl.Matrix;

import pl.wsei.mobilne.myapplication.space3d.geometry.Circle;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector;

public class MovementCtrl {
    private Circle circleCollider;

    private Point pointPressed;
    //private Rectangle Image?
    private  Rectangle2D visualRepresentation;
    public MovementCtrl(Point center, float radius) {
        this.circleCollider = new Circle(center, radius);
        this.visualRepresentation = new Rectangle2D(center,radius*2,radius*2);
    }

    //add a function that changes pointPressed?;

    public Vector getMovementVector(){
        Vector movementVector = new Vector(0,0,0);
        if(circleCollider.checkIfPointInside(pointPressed)){
            Vector vector2D = circleCollider.getVectorToPointFromCenter(pointPressed);
            float forwardOrBackward = vector2D.y;
            float leftOrRight = vector2D.x;
            movementVector = new Vector(leftOrRight, 0, forwardOrBackward);
        }
        return movementVector;
    }
    public void updatePointPressed(Point point){
        pointPressed = point;
    }

    public void startTransforming(){
        visualRepresentation.startTransforming();
    }

    public void move(float[] aspectAdjustmentMatrix, float dx, float dy) {
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
