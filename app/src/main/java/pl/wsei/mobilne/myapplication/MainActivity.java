package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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

    private static final int PERMISSION_REQ_CODE = 100;
    private String whyNeedPermission = "Application needs access to your Pictures to let you place them on walls of Virtual Gallery";

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

        requestImagesPermission();
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
        //najpierw zdobądź listę walls z view
        List<CellModel> wallsInCells = new ArrayList<>();
        for (CellModel cell: cellModels) {
            if(!cell.isEmpty()){
                wallsInCells.add(cell);
            }
        }
        //zdobądz liste walls z database
        List<DbmWall> wallsLoadedFromDb = DbmWall.getAll(database);

        //lista toBeAdded = suma walls z view których nie ma w database(manualnie dodane przez usera)
        // oraz tych z database które są z view (niezmienione przez usera)
        List<DbmWall> wallsToBeAdded = new ArrayList<>();

        for(int i = 0; i<wallsInCells.size(); i++) {
            CellModel wallInCell = wallsInCells.get(i);
            boolean wallInCellIsNew = true;
            float wallInCellX = (float)wallInCell.columnPosition;
            float wallInCellZ = (float)wallInCell.rowPosition;
            for (int j = 0; j < wallsLoadedFromDb.size(); j++) {
                DbmWall wallLoaded = wallsLoadedFromDb.get(j);
                float wallLoadedX = wallLoaded.getX();
                float wallLoadedZ = wallLoaded.getZ();
                if (wallLoadedX == wallInCellX && wallLoadedZ == wallInCellZ) {
                    //if a wall in db already exists in this position, we want to keep the one in db
                    wallsToBeAdded.add(wallLoaded);
                    wallInCellIsNew = false;
                }
            }
            if(wallInCellIsNew){
                wallsToBeAdded.add( new DbmWall(0,wallInCellX, wallInCellZ,null,null,null,null) );
            }
        }
        //wyczyść stary table
        DbmWall.emptyTable(database);

        //dodaj każdą pozycje z toBeAdded
        for (DbmWall wallToAdd: wallsToBeAdded){
            wallToAdd.add(database);
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

    private void requestImagesPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(whyNeedPermission)
                        .setPositiveButton("I understand", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // nothing to do
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQ_CODE);
            }
        }
        else {
            // nothing to do - PERMISSION_GRANTED
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_CODE:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Now, reading from Pictures storage will work
//                }
                break;
            default:
                break;
        }
    }

}