package pl.wsei.mobilne.myapplication.space3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IndexArray {
    public final ByteBuffer indexesBuffer;
    private final byte [] indexes;

    public IndexArray(byte[] indexes) {
        this.indexes = new byte[indexes.length];
        System.arraycopy(indexes, 0, this.indexes, 0, indexes.length);
        indexesBuffer = ByteBuffer
                .allocateDirect(indexes.length)
                .order(ByteOrder.nativeOrder())
                .put(indexes);
        // ensure Indexes buffer has "start pointer" in correct place
        indexesBuffer.position(0);
    }
    public int length() {
        return this.indexes.length;
    }
}