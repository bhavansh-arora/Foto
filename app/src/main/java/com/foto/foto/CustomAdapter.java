package com.foto.foto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    ViewData listData;
    List<Model> modelList;
    Context context;

    public CustomAdapter(ViewData listData, List<Model> modelList) {
        this.listData = listData;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String title = modelList.get(position).getTitle();
                String price = modelList.get(position).getPrice();
                String mfgdate = modelList.get(position).getMfgDate();
                String expirydate = modelList.get(position).getExpiryDate();
                String barcodeId = modelList.get(position).getBarcodeId();
                List<String> ingredients = modelList.get(position).getIngredients();
                String concatenatedIngredientNames = "";
                for (int i = 0; i < ingredients.size(); i++) {
                    concatenatedIngredientNames += ingredients.get(i);
                    if (i < ingredients.size() - 1) concatenatedIngredientNames += ", ";
                }
                Toast.makeText(listData, "Title: "+title+"\n"+"Price: "+price+"\n"+"Barcode Id: "+barcodeId+"\n"
                        +"Mfg. Date: "+mfgdate+"\n"+"Expiry Date: "+expirydate+"\n"+"Ingredients: "+concatenatedIngredientNames, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(modelList.get(position).getTitle());
        holder.price.setText(" Php " + modelList.get(position).getPrice());
        holder.mfg_date.setText(modelList.get(position).getMfgDate());
        holder.expiry_date.setText(modelList.get(position).getExpiryDate());
        holder.barcodeId.setText(modelList.get(position).getBarcodeId());
        //  holder.ingredients.setText(modelList.get(position)[);

        String concatenatedIngredientNames = "";
        List<String> ingredients = modelList.get(position).getIngredients(); // I assume the return value is a list of type "Star"!
        for (int i = 0; i < ingredients.size(); i++) {
            concatenatedIngredientNames += ingredients.get(i);
            if (i < ingredients.size() - 1) concatenatedIngredientNames += ", ";
        }
        holder.ingredients.setText(concatenatedIngredientNames);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
