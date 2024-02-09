package pl.wsei.mobilne.myapplication.space3d;

import static pl.wsei.mobilne.myapplication.space3d.geometry.Geometry.vector3DBetweenTwoPoints;
import static pl.wsei.mobilne.myapplication.space3d.geometry.Geometry.vector3DBetweenTwoPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.wsei.mobilne.myapplication.space3d.geometry.Face;
import pl.wsei.mobilne.myapplication.space3d.geometry.Point;
import pl.wsei.mobilne.myapplication.space3d.geometry.PointOnFace;
import pl.wsei.mobilne.myapplication.space3d.geometry.Ray;
import pl.wsei.mobilne.myapplication.space3d.geometry.Vector3D;

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

    public List<PointOnFace> GetCollidedFaces(Ray ray) {
        // Ray may collide with multiple faces of given wall
        ArrayList<PointOnFace> collidedFaces = new ArrayList<>();

        Optional<PointOnFace> collision;
        collision = frontFace.checkRayCollision(ray);
        if (collision.isPresent()) {
            PointOnFace faceRayIntersection = collision.get();
            collidedFaces.add(faceRayIntersection);
        }
        collision = leftFace.checkRayCollision(ray);
        if (collision.isPresent()) {
            PointOnFace faceRayIntersection = collision.get();
            collidedFaces.add(faceRayIntersection);
        }
        collision = rightFace.checkRayCollision(ray);
        if (collision.isPresent()) {
            PointOnFace faceRayIntersection = collision.get();
            collidedFaces.add(faceRayIntersection);
        }
        collision = backFace.checkRayCollision(ray);
        if (collision.isPresent()) {
            PointOnFace faceRayIntersection = collision.get();
            collidedFaces.add(faceRayIntersection);
        }
        return collidedFaces;
    }

    public static Optional<PointOnFace> getPointedFace(Ray ray, List<Wall> walls) {
        ArrayList<PointOnFace> allFacesHitByRay = new ArrayList<>();
        for (Wall wall : walls) {
            List<PointOnFace> wallFacesHitByRay = wall.GetCollidedFaces(ray);
            allFacesHitByRay.addAll(wallFacesHitByRay);
        }
        // To detect real hit we are searching Face that is nearest to Ray starting point.
        // Precisely, having shortest distance between Ray starting point
        // and Ray-Face intersection point.
        Optional<PointOnFace> noCollision = Optional.empty();
        if (allFacesHitByRay.isEmpty()) {
            return noCollision;
        }
        PointOnFace nearestHitFace = allFacesHitByRay.get(0);
        Point rayStartPoint = ray.point;
        for (PointOnFace hitFace: allFacesHitByRay) {
            Point rayFaceIntersection = hitFace.point;
            Point rayNearestFaceIntersection = nearestHitFace.point;
            Vector3D vector3DToFace = vector3DBetweenTwoPoints(rayStartPoint, rayFaceIntersection);
            Vector3D vector3DToNearestFace = vector3DBetweenTwoPoints(rayStartPoint, rayNearestFaceIntersection);
            float distanceToFace = vector3DToFace.length();
            float distanceToNearestFace = vector3DToNearestFace.length();
            if (distanceToFace < distanceToNearestFace) {
                // remember which face is nearest to ray starting point
                nearestHitFace = hitFace;
            }
        }
        Optional<PointOnFace> collisionWithNearestFace = Optional.of(nearestHitFace);
        return collisionWithNearestFace;
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
            new Point(X_position, 0, maxZ), new Vector3D(0,0,1),
            minX, maxX, minY, maxY, maxZ, maxZ,
            "frontFace"
        );
        leftFace = new Face(
            new Point(minX, 0, Z_position), new Vector3D(-1,0,0),
            minX, minX, minY, maxY, minZ, maxZ,
            "leftFace"
        );
        rightFace = new Face(
            new Point(maxX, 0, Z_position), new Vector3D(1,0,0),
            maxX, maxX, minY, maxY, minZ, maxZ,
            "rightFace"
        );
        backFace = new Face(
            new Point(X_position, 0, minZ), new Vector3D(0,0,-1),
            minX, maxX, minY, maxY, minZ, minZ,
            "backFace"
        );
    }
    public void drawPaintings(int aPositionLocation, int aTextureCoordinatesLocation,
                              int uTextureUnitLocation, int uMatrixLocation,
                              int textureId,  float[] viewProjectionMatrix){
        List<Face> facesWithPaintings = new ArrayList<>();
        facesWithPaintings.add(this.backFace);
        facesWithPaintings.add(this.frontFace);
        facesWithPaintings.add(this.leftFace);
        facesWithPaintings.add(this.rightFace);
        for (Face faceWithPainting : facesWithPaintings)
        {
            faceWithPainting.drawPaintingIfExists(aPositionLocation, aTextureCoordinatesLocation,
                                                  uTextureUnitLocation, uMatrixLocation,
                                                  textureId, viewProjectionMatrix);
        }
    }

}