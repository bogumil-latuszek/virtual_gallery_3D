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

public class AA_RecyclerViewAdapter extends RecyclerView.Adapter<AA_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<AminoAcidModel> aminoAcidModels;

    private final RecyclerViewInterface recyclerViewInterface;


    /*public void ChangeImg(int modelID, int imageID){
        AminoAcidModel aminoAcidModel = aminoAcidModels.get(modelID);
        aminoAcidModel.setImage(imageID);
    }*/
    public AA_RecyclerViewAdapter(Context context, ArrayList<AminoAcidModel> aminoAcidModels, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.aminoAcidModels = aminoAcidModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public AA_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new AA_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AA_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(aminoAcidModels.get(position).getAminoAcidName());
        holder.imageView.setImageResource(aminoAcidModels.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return aminoAcidModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvName;

        public MyViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.textView);

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
