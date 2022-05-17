package com.foto.foto;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder  {
    TextView title, price, mfg_date, expiry_date, ingredients, barcodeId;
    View mView;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

        title = itemView.findViewById(R.id.item_title);
        price = itemView.findViewById(R.id.item_price);
        mfg_date = itemView.findViewById(R.id.item_mfgDate);
        expiry_date = itemView.findViewById(R.id.item_expiryDate);
        ingredients = itemView.findViewById(R.id.item_ingredients);
        barcodeId = itemView.findViewById(R.id.barcodeId);
    }
    private ViewHolder.ClickListener mClickListener;
    public interface ClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
