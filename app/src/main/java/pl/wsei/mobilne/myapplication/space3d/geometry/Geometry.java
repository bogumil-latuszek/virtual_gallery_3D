package pl.wsei.mobilne.myapplication.space3d.geometry;

//import android.graphics.Point;

public class Geometry {


    public static Point rayToPlaneIntersectionPoint(Ray ray, Plane plane) {
        Vector3D rayToPlaneVector3D = vectorBetweenTwoPoints(ray.point, plane.point);
        float scaleFactor = rayToPlaneVector3D.dotProduct(plane.normal)
                / ray.vector3D.dotProduct(plane.normal);
        Point intersectionPoint = ray.point.translate(ray.vector3D.scale(scaleFactor));
        return intersectionPoint;
    }

    public static float distanceBetweenRayAndPoint(Point point, Ray ray) {
        Vector3D p1ToPoint = vectorBetweenTwoPoints(ray.point, point);
        Vector3D p2ToPoint = vectorBetweenTwoPoints(ray.point.translate(ray.vector3D), point);

        // The length of the cross product gives the area of an imaginary
        // parallelogram having the two vectors as sides. A parallelogram can be
        // thought of as consisting of two triangles, so this is the same as
        // twice the area of the triangle defined by the two vectors.
        // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector3D.length();

        // The area of a triangle is also equal to (base * height) / 2. In
        // other words, the height is equal to (area * 2) / base. The height
        // of this triangle is the distance from the point to the ray.
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }



    public static Vector3D vectorBetweenTwoPoints(Point from, Point to) {
        return new Vector3D(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }



}