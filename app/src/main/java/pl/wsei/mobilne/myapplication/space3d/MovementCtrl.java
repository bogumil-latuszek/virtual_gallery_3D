package pl.wsei.mobilne.myapplication.space3d;

import pl.wsei.mobilne.myapplication.space3d.geometry.Circle;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector;

public class MovementCtrl {
    private Circle circleCollider;
    private Point pointPressed;
    //private Rectangle Image?

    public MovementCtrl(Point center, float radius) {
        this.circleCollider = new Circle(center, radius);
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

}
