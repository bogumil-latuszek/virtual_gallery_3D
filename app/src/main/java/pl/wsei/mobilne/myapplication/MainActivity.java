package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import pl.wsei.mobilne.myapplication.database.DatabaseHelper;
import pl.wsei.mobilne.myapplication.database.DbmWall;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    ArrayList<CellModel> cellModels = new ArrayList<>();

    //int[] images = {R.drawable.ic_alanine};

    Cell_RecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);

        dbHelper = new DatabaseHelper(getApplicationContext());
        this.database = dbHelper.getWritableDatabase();

        int columnCount = 10;
        SetUpCellModels(10,columnCount);

        adapter = new Cell_RecyclerViewAdapter(this, cellModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
    }

    public void ChangeModeTo3D(View v){

        Intent i = new Intent(MainActivity.this, Mode3DActivity.class);
        startActivity(i);
    }

    private ArrayList<String> encodeWallPositions() {
        ArrayList<String> wallCoordinates = new ArrayList<>();
        for (CellModel wall: cellModels) {
            if (! wall.isEmpty()) {
                String value = String.format("%s,%s", wall.columnPosition, wall.rowPosition);
                wallCoordinates.add(value);
            }
        }
        return wallCoordinates;
    }

    private void SetUpCellModels(int rowCount, int columnCount){
        // build 2D grid
        for(int i = 0; i < rowCount; i++){
            for (int j = 0; j< columnCount; j++){
                cellModels.add(new CellModel(i, j, R.drawable.empty_image));
            }
        }
        // restore 2D walls from DB
        List<DbmWall> wallsLoaded = DbmWall.getAll(database);
        for (int i = 0; i < wallsLoaded.size(); i++) {
            DbmWall dbmWallNext = wallsLoaded.get(i);
            int xPosition = (int) dbmWallNext.getX();
            int zPosition = (int) dbmWallNext.getZ();
            int indexAtArray = zPosition * columnCount + xPosition;
            CellModel cellModel = cellModels.get(indexAtArray);
            cellModel.setImage(R.drawable.wall);
        }
    }

    public void SaveWalls(View v) {
        DbmWall.emptyTable(database);
        for (CellModel wall2D: cellModels) {
            if (! wall2D.isEmpty()) {
                DbmWall dbmWall = new DbmWall(0, (float) wall2D.columnPosition, (float)wall2D.rowPosition,null,null,null,null);
                dbmWall.add(database);
            }
        }
    }


    @Override
    public void onItemClick(int position) {
        CellModel cellModel = cellModels.get(position);
        int foundImage = cellModel.getImage();
        int wall = R.drawable.wall;
        int empty_image = R.drawable.empty_image;
        if(foundImage == empty_image ){
            cellModel.setImage(R.drawable.wall);
        }
        else{
            cellModel.setImage(R.drawable.empty_image);
        }
        adapter.notifyItemChanged(position);
    }

    /*public void ChangeImage(View w){
        int modelID = w.getId();
        Log.d("myTag", "modelID: "+modelID);
        int imageID = R.drawable.ic_awesome;
        Log.d("myTag", "imageID: "+imageID);
        adapter.ChangeImg(modelID, imageID);
    }*/

}