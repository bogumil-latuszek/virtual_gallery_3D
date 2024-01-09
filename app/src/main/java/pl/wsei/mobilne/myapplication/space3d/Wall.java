package pl.wsei.mobilne.myapplication.space3d;

import pl.wsei.mobilne.myapplication.space3d.geometry.Geometry;
import pl.wsei.mobilne.myapplication.space3d.geometry.Plane;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.Ray;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector;

public class Wall extends Cuboid {

//    private Point upperLeftFront;
//    private Point lowerLeftFront;
//    private Point lowerRightFront;
//    private Point upperRightFront;
//    private Point upperLeftBack;
//    private Point lowerLeftBack;
//    private Point lowerRightBack;
//    private Point upperRightBack;
    private String wallID;
    private Face frontFace;
    private Face leftFace;
    private Face rightFace;
    private Face backFace;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private float minZ;
    private float maxZ;
    private class Face{
        private Plane plane;
        public  String faceID;
       public Face(Point pointOnFace, Vector normal, String FaceID){
           plane = new Plane(pointOnFace, normal);
           faceID = FaceID;
       }
       public boolean checkRayCollision(Ray ray){
           Point intersectionPoint = Geometry.rayToPlaneIntersectionPoint(ray, plane);
           float intersectionPointX = intersectionPoint.x;
           float intersectionPointY = intersectionPoint.y;
           float intersectionPointZ = intersectionPoint.z;
           if (intersectionPointX<(minX-0.01) | intersectionPointX>(maxX+0.01)){
               return false;
           }
           if (intersectionPointY<(minY-0.01) | intersectionPointY>(maxY+0.01)){
               return false;
           }
           if (intersectionPointZ<(minZ-0.01) | intersectionPointZ>(maxZ+0.01)){
               return false;
           }
           return true;
       }
    }
    public String GetCollidedFaceID(Ray ray){
        boolean collisionDetected = false;
        collisionDetected = frontFace.checkRayCollision(ray);
        if(collisionDetected){
            return frontFace.faceID;
        }
        collisionDetected = leftFace.checkRayCollision(ray);
        if(collisionDetected){
            return leftFace.faceID;
        }
        collisionDetected = rightFace.checkRayCollision(ray);
        if(collisionDetected){
            return rightFace.faceID;
        }
        collisionDetected = backFace.checkRayCollision(ray);
        if(collisionDetected){
            return backFace.faceID;
        }
        return null;
    }
    float X_position;
    float Z_position;
    public Wall(float dx, float dy, float dz, float moveX, float moveZ, String WallID) {
        //super(0.5f, 1f, 0.5f);
        super(dx, dy, dz);
        //will this line of code execute? TODO:CHECK IT
        minX = -dx + moveX;
        maxX = dx + moveX;
        minY = -dy;
        maxY = dy;
        minZ = -dz + moveZ;
        maxZ = dz + moveZ;

        wallID = WallID;

        frontFace = new Face(new Point(0, 0, maxZ), new Vector(0,0,1), "frontFace");
        leftFace = new Face(new Point(minX, 0, 0), new Vector(-1,0,0), "leftFace");
        rightFace = new Face(new Point(maxX, 0, 0), new Vector(1,0,0), "rightFace");
        backFace = new Face(new Point(0, 0, minZ), new Vector(0,0,-1), "backFace");
    }

}
