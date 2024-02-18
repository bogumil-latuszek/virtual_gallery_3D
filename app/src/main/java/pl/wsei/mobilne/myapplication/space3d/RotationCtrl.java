package pl.wsei.mobilne.myapplication.space3d;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;

public class RotationCtrl {
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private final float[] modelMatrix = new float[16]; //a 4x4 matrix
    private float[] edgeColor = {37f/256f, 58f/256f, 190f/256f}; // opengl requires color as float in range 0-1
    private final VertexArray vertexArray;  // <-- Vertices
    private final IndexArray vertexSequenceForDrawingOuterCircle;
    private final IndexArray vertexSequenceForDrawingInnerCircle;
    private final IndexArray vertexSequenceForDrawingUpTriangle;
    private final IndexArray vertexSequenceForDrawingDownTriangle;
    private final IndexArray vertexSequenceForDrawingRightTriangle;
    private final IndexArray vertexSequenceForDrawingLeftTriangle;
    private final float outerRadius = 0.2f;
    private final float innerRadius = 0.08f;
    private final int circleSegmentsNb = 50;
    private final int outerCircleFirstVertexIdx = 0;
    private final int innerCircleFirstVertexIdx = circleSegmentsNb;
    private final int upTriangleFirstVertexIdx = circleSegmentsNb*2;
    private final int downTriangleFirstVertexIdx = upTriangleFirstVertexIdx+3;
    private final int rightTriangleFirstVertexIdx = downTriangleFirstVertexIdx+3;
    private final int leftTriangleFirstVertexIdx = rightTriangleFirstVertexIdx+3;
    private final Region2D [] triangleRegions = new Region2D[4];
    private float upAngle = 0f;


    public RotationCtrl() {
        // prepare buffer for vertices
        int nbVertices = circleSegmentsNb * 2 + 4 * 3; // 2 circles + 4 triangles
        float [] vertices = new float[nbVertices * COORDS_PER_VERTEX];
        byte [] circleLines_indexesOfVertices = new byte[circleSegmentsNb * 2];
        byte [] triangleLines_indexesOfVertices = new byte[3 * 2];
        // outer circle
        calculateAndFillCirclePoints(outerRadius,
                outerCircleFirstVertexIdx * COORDS_PER_VERTEX,
                circleSegmentsNb, vertices);
        // inner circle
        calculateAndFillCirclePoints(innerRadius,
                innerCircleFirstVertexIdx * COORDS_PER_VERTEX,
                circleSegmentsNb, vertices);
        // triangles
        calculateAndFillTrianglesPoints(innerRadius, outerRadius,
                upTriangleFirstVertexIdx * COORDS_PER_VERTEX, vertices, triangleRegions);
        // both circles and all triangles have vertices in same array
        vertexArray = new VertexArray(vertices);
        // but they use separate indexes
        fillIndexesOfLineVertices(outerCircleFirstVertexIdx, circleLines_indexesOfVertices);
        vertexSequenceForDrawingOuterCircle = new IndexArray(circleLines_indexesOfVertices);
        fillIndexesOfLineVertices(innerCircleFirstVertexIdx, circleLines_indexesOfVertices);
        vertexSequenceForDrawingInnerCircle = new IndexArray(circleLines_indexesOfVertices);

        fillIndexesOfLineVertices(upTriangleFirstVertexIdx, triangleLines_indexesOfVertices);
        vertexSequenceForDrawingUpTriangle = new IndexArray(triangleLines_indexesOfVertices);
        fillIndexesOfLineVertices(downTriangleFirstVertexIdx, triangleLines_indexesOfVertices);
        vertexSequenceForDrawingDownTriangle = new IndexArray(triangleLines_indexesOfVertices);
        fillIndexesOfLineVertices(rightTriangleFirstVertexIdx, triangleLines_indexesOfVertices);
        vertexSequenceForDrawingRightTriangle = new IndexArray(triangleLines_indexesOfVertices);
        fillIndexesOfLineVertices(leftTriangleFirstVertexIdx, triangleLines_indexesOfVertices);
        vertexSequenceForDrawingLeftTriangle = new IndexArray(triangleLines_indexesOfVertices);
    }
    private void calculateAndFillCirclePoints(float radius, int startingIdx, int nbPointsToFill, float [] circlePoints) {
        float fullAngle = 360f;
        float stepAngle = fullAngle / nbPointsToFill;
        for (int step = 0; step < nbPointsToFill; step++) {
            float currentAngle = stepAngle * step;
            double radianAngle = Math.toRadians(currentAngle);
            float x = (float) (radius * Math.cos(radianAngle));
            float y = (float) (radius * Math.sin(radianAngle));
            float z = 0.0f;
            int bufferIdx = step * 3;
            circlePoints[startingIdx+bufferIdx] = x;
            circlePoints[startingIdx+bufferIdx+1] = y;
            circlePoints[startingIdx+bufferIdx+2] = z;
        }
    }
    private void calculateAndFillTrianglesPoints(float innerRadius, float outerRadius,
                                                 int startingIdx, float [] circlePoints,
                                                 Region2D [] clickRegions) {
        float width = 1.5f * outerRadius;
        float height = 0.9f * innerRadius;
        float delta = 0.3f * innerRadius;
        // up triangle
        int triangleStartIdx = startingIdx;
        circlePoints[triangleStartIdx+0] = 0f;
        circlePoints[triangleStartIdx+1] = outerRadius + delta + height;
        circlePoints[triangleStartIdx+2] = 0f;
        circlePoints[triangleStartIdx+3] = -0.5f * width;
        circlePoints[triangleStartIdx+4] = outerRadius + delta;
        circlePoints[triangleStartIdx+5] = 0f;
        circlePoints[triangleStartIdx+6] = 0.5f * width;
        circlePoints[triangleStartIdx+7] = outerRadius + delta;
        circlePoints[triangleStartIdx+8] = 0f;
        clickRegions[0] = new Region2D(-0.5f * width, outerRadius + delta, width, height);
        // down triangle
        triangleStartIdx += 9;
        circlePoints[triangleStartIdx+0] = 0f;
        circlePoints[triangleStartIdx+1] = -1f * outerRadius - delta - height;
        circlePoints[triangleStartIdx+2] = 0f;
        circlePoints[triangleStartIdx+3] = 0.5f * width;
        circlePoints[triangleStartIdx+4] = -1f * outerRadius - delta;
        circlePoints[triangleStartIdx+5] = 0f;
        circlePoints[triangleStartIdx+6] = -0.5f * width;
        circlePoints[triangleStartIdx+7] = -1f * outerRadius - delta;
        circlePoints[triangleStartIdx+8] = 0f;
        clickRegions[1] = new Region2D(-0.5f * width, -1f * outerRadius - delta - height,
                width, height);
        // right triangle
        triangleStartIdx += 9;
        circlePoints[triangleStartIdx+0] = outerRadius + delta + height;
        circlePoints[triangleStartIdx+1] = 0f;
        circlePoints[triangleStartIdx+2] = 0f;
        circlePoints[triangleStartIdx+3] = outerRadius + delta;
        circlePoints[triangleStartIdx+4] = 0.5f * width;
        circlePoints[triangleStartIdx+5] = 0f;
        circlePoints[triangleStartIdx+6] = outerRadius + delta;
        circlePoints[triangleStartIdx+7] = -0.5f * width;
        circlePoints[triangleStartIdx+8] = 0f;
        clickRegions[2] = new Region2D(outerRadius + delta, -0.5f * width,
                height, width);
        // right triangle
        triangleStartIdx += 9;
        circlePoints[triangleStartIdx+0] = -1f * outerRadius - delta - height;
        circlePoints[triangleStartIdx+1] = 0f;
        circlePoints[triangleStartIdx+2] = 0f;
        circlePoints[triangleStartIdx+3] = -1f * outerRadius - delta;
        circlePoints[triangleStartIdx+4] = -0.5f * width;
        circlePoints[triangleStartIdx+5] = 0f;
        circlePoints[triangleStartIdx+6] = -1f * outerRadius - delta;
        circlePoints[triangleStartIdx+7] = 0.5f * width;
        circlePoints[triangleStartIdx+8] = 0f;
        clickRegions[3] = new Region2D(-1f * outerRadius - delta - height, -0.5f * width,
                height, width);
    }
    private void fillIndexesOfLineVertices(int startingIdx, byte [] verticesIndexes) {
        int lineStartVertexIdx = startingIdx;
        int lineEndVertexIdx = startingIdx + 1;
        for (int idx = 1; idx < verticesIndexes.length; idx+=2) {
            verticesIndexes[idx-1] = (byte) lineStartVertexIdx;
            verticesIndexes[idx] = (byte) lineEndVertexIdx;
            lineStartVertexIdx = lineEndVertexIdx;
            lineEndVertexIdx++;
        }
        verticesIndexes[verticesIndexes.length - 1] = (byte) startingIdx;
    }
    public void setEdgeColor(float[] color) {
        //  float with 3 values:   R   G   B
        System.arraycopy(color, 0, this.edgeColor, 0, 3);
    }
    public void up() {
        this.upAngle += 1f;
    }
    public void down() {
        this.upAngle -= 1f;
    }
    public float getUpAngle() {
        return this.upAngle;
    }

    public void prepareDataSource_forPositionAttribute(int aPositionLocation) {
        vertexArray.setVertexAttribPointer(0, aPositionLocation, COORDS_PER_VERTEX, 0);

        GLES20.glLineWidth(5.0f);
    }
    public void startTransforming() {
        Matrix.setIdentityM(modelMatrix, 0);
    }
    public void move(float[] aspectAdjustmentMatrix, float dx, float dy) {
        // first correct aspect ratio while shape is still at (0.0, 0.0)
        float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, aspectAdjustmentMatrix, 0, modelMatrix, 0);
        System.arraycopy(tempMatrix, 0, modelMatrix, 0, tempMatrix.length);
        // and then move shape
        Matrix.translateM(modelMatrix, 0, dx, dy, 0f);
    }
    public boolean isInsideRotationCtrl(float xCoordinate, float yCoordinate){
        // shape may have been translated from (0.0, 0.0) origin
        // we start test by translating point by inverted translationMatrix
        float[] translatateBackToCenterMatrix = new float[16];
        Matrix.invertM(translatateBackToCenterMatrix, 0, modelMatrix, 0);
        float[] point = {xCoordinate, yCoordinate, 0f, 1f};
        float[] pointTranslated = new float[4];
        Matrix.multiplyMV(pointTranslated, 0, translatateBackToCenterMatrix, 0, point, 0);
        float x = pointTranslated[0];
        float y = pointTranslated[1];
        float r = (float) Math.sqrt(x*x + y*y);
        if (r <= this.outerRadius + 0.1f){
            return true;
        }
        return false;
    }
    public String where(float xCoordinate, float yCoordinate) {
        // shape may have been translated from (0.0, 0.0) origin
        // we start test by translating point by inverted translationMatrix
        float[] translatateBackToCenterMatrix = new float[16];
        Matrix.invertM(translatateBackToCenterMatrix, 0, modelMatrix, 0);
        float[] point = {xCoordinate, yCoordinate, 0f, 1f};
        float[] pointTranslated = new float[4];
        Matrix.multiplyMV(pointTranslated, 0, translatateBackToCenterMatrix, 0, point, 0);
        float x = pointTranslated[0];
        float y = pointTranslated[1];
        float r = (float) Math.sqrt(x*x + y*y);
//        Log.d("inside:", String.format("(x=%s, y=%s) --> (x=%s, y=%s) [r=%s : r1=%s, r2=%s]",
//                xCoordinate, yCoordinate, x, y, r, this.innerRadius, this.outerRadius));

        if (r < this.innerRadius) {
//            Log.d("inside:", "center");
            return "center";
        }
        else if (r <= this.outerRadius) {
//            Log.d("inside:", "ring");
            return "ring";
        }
        else if (triangleRegions[0].inside(x, y)) {
//            Log.d("inside:", "up");
            return "up";
        }
        else if (triangleRegions[1].inside(x, y)) {
//            Log.d("inside:", "down");
            return "down";
        }
        else if (triangleRegions[2].inside(x, y)) {
//            Log.d("inside:", "right");
            return "right";
        }
        else if (triangleRegions[3].inside(x, y)) {
//            Log.d("inside:", "left");
            return "left";
        }
        else {
//            Log.d("inside:", "outside");
            return "outside";
        }
    }
    public float getRotationAngle(float xCoordinate, float yCoordinate) {
        // shape may have been translated from (0.0, 0.0) origin
        // we start test by translating point by inverted translationMatrix
        float[] translatateBackToCenterMatrix = new float[16];
        Matrix.invertM(translatateBackToCenterMatrix, 0, modelMatrix, 0);
        float[] point = {xCoordinate, yCoordinate, 0f, 1f};
        float[] pointTranslated = new float[4];
        Matrix.multiplyMV(pointTranslated, 0, translatateBackToCenterMatrix, 0, point, 0);
        float x = pointTranslated[0];
        float y = pointTranslated[1];
        float r = (float) Math.sqrt(x*x + y*y);

        double angleSinus = (double) (y / r);
        double angleCosinus = (double) (x / r);
        double angleRadians = Math.asin(Math.abs(angleSinus));
        float angleQ1 = (float) Math.toDegrees(angleRadians);
        float angle = angleQ1; // I Q

        if ((angleSinus >= 0) && (angleCosinus < 0)) { // II Q
            angle = 180.0f - angleQ1;
        }
        else if ((angleSinus < 0) && (angleCosinus < 0)) { // III Q
            angle = 180.0f + angleQ1;
        }
        else if ((angleSinus < 0) && (angleCosinus >= 0)) { // IV Q
            angle = 360.0f - angleQ1;
        }
        //Log.d("touch:", String.format("angle = %s --> %s degrees", angleQ1, angle));
        float angleFromNorth = angle - 90.0f;
        return angleFromNorth;
    }

    public void rotateCamera(String rotationCtrlPressedLocation, float[] viewRotationMatrix, float rotationSpeed){
        if(rotationCtrlPressedLocation == null){
            return;
        }
        if (rotationCtrlPressedLocation.equals("center")) {
            float lookAroundAngle = 0.0f;
            Matrix.setRotateM(viewRotationMatrix, 0, lookAroundAngle, 0f, 1f, 0f);
        }
        else if (rotationCtrlPressedLocation.equals("up")) {
            float lookUpAngle = this.getUpAngle();
            if(lookUpAngle <= 20f){
                up();
            }
            Matrix.setRotateM(viewRotationMatrix, 0, lookUpAngle, -1f, 0f, 0f);
        }
        else if (rotationCtrlPressedLocation.equals("down")) {
            float lookUpAngle = this.getUpAngle();
            if(lookUpAngle >= -20f){
                this.down();
            }
           Matrix.setRotateM(viewRotationMatrix, 0, lookUpAngle, -1f, 0f, 0f);
        }
        else if (rotationCtrlPressedLocation.equals("right")) {
            float addAngle = -5f * rotationSpeed;
            Matrix.rotateM(viewRotationMatrix, 0, addAngle, 0f, -1f, 0f);
        }
        else if (rotationCtrlPressedLocation.equals("left")) {
            float addAngle = 5f* rotationSpeed;
            Matrix.rotateM(viewRotationMatrix, 0, addAngle, 0f, -1f, 0f);
        }
    }

    public void draw(int aPositionLocation, int uColorLocation, //overloading draw  function
                     int uMatrixLocation, float[] aspectAdjustmentMatrix) {
        draw(aPositionLocation, uColorLocation,
                uMatrixLocation, aspectAdjustmentMatrix, this.edgeColor);
    }
    public void draw(int aPositionLocation, int uColorLocation,
                     int uMatrixLocation, float[] aspectAdjustmentMatrix,
                     float[] edgeColor) {

        // prepare vertices buffer (floats --> bytes)
        prepareDataSource_forPositionAttribute(aPositionLocation);

        // we do not recalculate vertices per View-Projection matrices
        // View Controls (UI elements) are drawn directly in OpenGL normalized coordinates
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, modelMatrix, 0);

        // tell OpenGL what are 4 values (uniform vec4 %u_Color;) to fill for global color
        // set color for drawing edges
        GLES20.glUniform4f(uColorLocation, edgeColor[0], edgeColor[1], edgeColor[2], 1.0f);

        int nbIndexes4lines = vertexSequenceForDrawingOuterCircle.length();
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceForDrawingOuterCircle.indexesBuffer);
        nbIndexes4lines = vertexSequenceForDrawingInnerCircle.length();
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceForDrawingInnerCircle.indexesBuffer);

        nbIndexes4lines = vertexSequenceForDrawingUpTriangle.length();
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceForDrawingUpTriangle.indexesBuffer);
        nbIndexes4lines = vertexSequenceForDrawingDownTriangle.length();
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceForDrawingDownTriangle.indexesBuffer);
        nbIndexes4lines = vertexSequenceForDrawingRightTriangle.length();
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceForDrawingRightTriangle.indexesBuffer);
        nbIndexes4lines = vertexSequenceForDrawingLeftTriangle.length();
        GLES20.glDrawElements(GLES20.GL_LINES, nbIndexes4lines, GLES20.GL_UNSIGNED_BYTE,
                vertexSequenceForDrawingLeftTriangle.indexesBuffer);
    }
}