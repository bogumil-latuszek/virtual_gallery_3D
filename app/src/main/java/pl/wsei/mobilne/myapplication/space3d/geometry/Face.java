package pl.wsei.mobilne.myapplication.space3d.geometry;

//import android.graphics.Point;

import java.util.Optional;

import pl.wsei.mobilne.myapplication.space3d.Painting;

public class Face {

    private Painting painting;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private float minZ;
    private float maxZ;

    private Plane plane;
    public  String faceID;
    public Face(Point pointOnFace, Vector3D normal,
                float minX, float maxX,
                float minY, float maxY,
                float minZ, float maxZ,
                String FaceID){
        plane = new Plane(pointOnFace, normal);
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        faceID = FaceID;
        painting = new Painting(pointOnFace,0.5f, 0.5f);
        painting.startTransforming();
    }
    @Override
    public String toString() {
        return this.faceID + "[x:" + this.minX + ".." + this.maxX + ", y:"
                                   + this.minY + ".." + this.maxY + ", z:"
                                   + this.minZ + ".." + this.maxZ + "]";
    }
    public Optional<PointOnFace> checkRayCollision(Ray ray) {
        Optional<PointOnFace> noCollision = Optional.empty();
        Point intersectionPoint = Geometry.rayToPlaneIntersectionPoint(ray, plane);
        float intersectionPointX = intersectionPoint.x;
        float intersectionPointY = intersectionPoint.y;
        float intersectionPointZ = intersectionPoint.z;
        if ((intersectionPointX<(minX-0.01)) || (intersectionPointX>(maxX+0.01))) {
            return noCollision;
        }
        if ((intersectionPointY<(minY-0.01)) || (intersectionPointY>(maxY+0.01))) {
            return noCollision;
        }
        if ((intersectionPointZ<(minZ-0.01)) || (intersectionPointZ>(maxZ+0.01))) {
            return noCollision;
        }
        Optional<PointOnFace> collisionWithFace = Optional.of(new PointOnFace(this, intersectionPoint));
        return collisionWithFace;
    }
    public void drawPaintingIfExists(int aPositionLocation, int aTextureCoordinatesLocation,
                                     int uTextureUnitLocation, int uMatrixLocation,
                                     int textureId,  float[] viewProjectionMatrix){
        if(this.painting != null){
            painting.draw(aPositionLocation, aTextureCoordinatesLocation,
                          uTextureUnitLocation, uMatrixLocation,
                          textureId, viewProjectionMatrix);
        }
    }
}