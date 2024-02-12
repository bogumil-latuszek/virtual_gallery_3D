package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

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
        //setContentView(R.layout.activity_main);

        //gLView = new MyGLSurfaceView(this);
        //setContentView(gLView);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);

        int columnCount = 10;
        SetUpCellModels(10,columnCount);

        adapter = new Cell_RecyclerViewAdapter(this, cellModels, this);
        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));

        dbHelper = new DatabaseHelper(getApplicationContext());
        this.database = dbHelper.getWritableDatabase();
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
        for(int i = 0; i < rowCount; i++){
            for (int j = 0; j< columnCount; j++){
                cellModels.add(new CellModel(i, j, R.drawable.empty_image));
            }
        }
    }

    public void SaveWalls(View v) {
        DbmWall.emptyTable(database);
        for (CellModel wall2D: cellModels) {
            if (! wall2D.isEmpty()) {
                DbmWall dbmWall = new DbmWall((float) wall2D.columnPosition, (float)wall2D.rowPosition,null,null,null,null);
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
        Toast.makeText(getApplicationContext(), "działa"+position, Toast.LENGTH_SHORT).show();
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