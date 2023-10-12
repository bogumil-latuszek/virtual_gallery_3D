package pl.wsei.mobilne.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Cell_RecyclerViewAdapter extends RecyclerView.Adapter<Cell_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<CellModel> cellModels;

    private final RecyclerViewInterface recyclerViewInterface;


    /*public void ChangeImg(int modelID, int imageID){
        AminoAcidModel aminoAcidModel = aminoAcidModels.get(modelID);
        aminoAcidModel.setImage(imageID);
    }*/
    public Cell_RecyclerViewAdapter(Context context, ArrayList<CellModel> cellModels, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.cellModels = cellModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public Cell_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new Cell_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull Cell_RecyclerViewAdapter.MyViewHolder holder, int position) {
        //holder.tvName.setText(cellModels.get(position).getCellName());
        holder.imageView.setImageResource(cellModels.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return cellModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
       // TextView tvName;

        public MyViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            //tvName = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
