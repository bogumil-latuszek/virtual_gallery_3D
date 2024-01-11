package pl.wsei.mobilne.myapplication.space3d;

import java.util.ArrayList;
import java.util.List;

import pl.wsei.mobilne.myapplication.space3d.geometry.Face;
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

    public List<Face> GetCollidedFaces(Ray ray) {
        // Ray may collide with multiple faces of given wall
        ArrayList<Face> collidedFaces = new ArrayList<>();
        boolean collisionDetected = false;
        collisionDetected = frontFace.checkRayCollision(ray);
        if (collisionDetected) {
            collidedFaces.add(frontFace);
        }
        collisionDetected = leftFace.checkRayCollision(ray);
        if (collisionDetected) {
            collidedFaces.add(leftFace);
        }
        collisionDetected = rightFace.checkRayCollision(ray);
        if (collisionDetected) {
            collidedFaces.add(rightFace);
        }
        collisionDetected = backFace.checkRayCollision(ray);
        if (collisionDetected) {
            collidedFaces.add(backFace);
        }
        return collidedFaces;
    }

    public Wall(float dx, float dy, float dz, float moveX, float moveZ, String WallID) {
        //super(0.5f, 1f, 0.5f);
        super(dx, dy, dz, moveX, moveZ);

        //will this line of code execute? TODO:CHECK IT
        float minX = -dx + moveX;
        float maxX = dx + moveX;
        float minY = -dy;
        float maxY = dy;
        float minZ = -dz + moveZ;
        float maxZ = dz + moveZ;

        wallID = WallID;

        frontFace = new Face(
            new Point(X_position, 0, maxZ), new Vector(0,0,1),
            minX, maxX, minY, maxY, maxZ, maxZ,
            "frontFace"
        );
        leftFace = new Face(
            new Point(minX, 0, Z_position), new Vector(-1,0,0),
            minX, minX, minY, maxY, minZ, maxZ,
            "leftFace"
        );
        rightFace = new Face(
            new Point(maxX, 0, Z_position), new Vector(1,0,0),
            maxX, maxX, minY, maxY, minZ, maxZ,
            "rightFace"
        );
        backFace = new Face(
            new Point(X_position, 0, minZ), new Vector(0,0,-1),
            minX, maxX, minY, maxY, minZ, minZ,
            "backFace"
        );
    }

}