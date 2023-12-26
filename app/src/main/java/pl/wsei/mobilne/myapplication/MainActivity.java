package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.wsei.mobilne.myapplication.database.DatabaseHelper;
import pl.wsei.mobilne.myapplication.database.DatabaseManager;
import pl.wsei.mobilne.myapplication.database.Wall;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{

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
    }

    public void ChangeModeTo3D(View v){

//        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
//        DatabaseManager dbManager = new DatabaseManager(dbHelper);
//
//        Wall myWall = new Wall(0.7f, 0.9f);
//        dbManager.AddWall(myWall);
//        List<Wall> wallList = dbManager.GetAll();
//        Toast.makeText(this, wallList.toString(), Toast.LENGTH_SHORT).show();
//

        Intent i = new Intent(MainActivity.this, Mode3DActivity.class);
        startActivity(i);
    }


    private void SetUpCellModels(int rowCount, int columnCount){
        for(int i = 0; i < rowCount; i++){
            for (int j = 0; j< columnCount; j++){
                cellModels.add(new CellModel(i, j, R.drawable.empty_image));
            }
        }
    }

    public void SaveWalls(View v) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        List<Wall> wallsLoaded1 = Wall.getAll(dbHelper);
        Wall.emptyTable(dbHelper);
        List<Wall> wallsLoaded = Wall.getAll(dbHelper);
//        DatabaseManager dbManager = new DatabaseManager(dbHelper);
//        dbManager.emptyTable();
//        dbManager.createWallsTable();
        for (CellModel wall2D: cellModels) {
            if (! wall2D.isEmpty()) {
                Wall wall = new Wall((float) wall2D.columnPosition, (float)wall2D.rowPosition);
                wall.add(dbHelper);
//                dbManager.AddWall(new Wall((float) wall.columnPosition, (float)wall.rowPosition));
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
