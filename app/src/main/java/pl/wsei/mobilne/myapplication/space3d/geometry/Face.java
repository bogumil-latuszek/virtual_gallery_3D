package pl.wsei.mobilne.myapplication.space3d.geometry;

//import android.graphics.Point;

import java.util.Optional;

import pl.wsei.mobilne.myapplication.space3d.Painting;

public class Face {

    public Painting painting;
    private float paintingPositionOffset = 0.01f;
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private float minZ;
    private float maxZ;

    private Vector3D normal;

    private Plane plane;
    public  String faceID;
    public Face(Point pointOnFace, Vector3D normal,
                float minX, float maxX,
                float minY, float maxY,
                float minZ, float maxZ,
                String FaceID){
        plane = new Plane(pointOnFace, normal);
        this.normal = normal;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        faceID = FaceID;
        //addPainting();
    }

    public void removePainting(){
        painting = null;
    }
    public void addPainting(int textureID){
        painting = new Painting(plane.point,0.5f, 0.5f);
        int normalz = Math.round(normal.z);
        int normalx = Math.round(normal.x);
        switch(normalz) {
            case 1:
                painting.rotate(-90);
                painting.move(0,0, paintingPositionOffset);
                break;
            case -1:
                painting.rotate(90);
                painting.move(0,0, -paintingPositionOffset);
                break;
            default:
        }
        switch(normalx) {
            case 1:
                painting.move( paintingPositionOffset,0,0);
                break;
            case -1:
                painting.rotate(-180);
                painting.move( -paintingPositionOffset,0,0);
                break;
            default:
        }
        painting.rotate(90);
        painting.setTextureID(textureID);

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
                                     int uTextureUnitLocation, int uMatrixLocation, float[] viewProjectionMatrix){
        if(this.painting != null){
            painting.draw(aPositionLocation, aTextureCoordinatesLocation,
                          uTextureUnitLocation, uMatrixLocation, viewProjectionMatrix);
        }
    }
}