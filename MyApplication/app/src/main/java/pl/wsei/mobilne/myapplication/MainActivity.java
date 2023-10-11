package pl.wsei.mobilne.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<AminoAcidModel> aminoAcidModels = new ArrayList<>();

    int[] images = {R.drawable.ic_alanine};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //gLView = new MyGLSurfaceView(this);
        //setContentView(gLView);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);

        SetUpAminoAcidModels();

        AA_RecyclerViewAdapter adapter = new AA_RecyclerViewAdapter(this, aminoAcidModels);
        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 6));
    }

    public void ChangeModeTo3D(View v){
        Intent i = new Intent(MainActivity.this, Mode3DActivity.class);
        startActivity(i);
    }

    private void SetUpAminoAcidModels(){
        String[] aminoAcidNames = getResources().getStringArray(R.array.amino_acids_one_letter_txt);

        for(int i = 0; i < aminoAcidNames.length; i++){
            aminoAcidModels.add(new AminoAcidModel(
                    aminoAcidNames[i],
                    images[0]

            ));
        }

    }

}