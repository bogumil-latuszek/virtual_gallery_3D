package pl.wsei.mobilne.myapplication;

public class CellModel {


    int rowPosition;
    int columnPosition;
    int image;


    public CellModel(int rowPosition, int columnPosition, int image) {
        this.rowPosition = rowPosition;
        this.columnPosition = columnPosition;
        this.image = image;
    }

    public int getRowPosition() {
        return rowPosition;
    }

    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }

    public int getColumnPosition() {
        return columnPosition;
    }

    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
    public boolean isEmpty() {
        return this.image == R.drawable.empty_image;
    }
}
