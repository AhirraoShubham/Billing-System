package com.example.invoicebilling.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoicebilling.R;
import com.example.invoicebilling.pojo.Product;

import java.util.ArrayList;

public class ViewCategoryAdapter extends RecyclerView.Adapter<ViewCategoryAdapter.ProductViewHolder> {
    private Context mContext;
    private ArrayList<Product>mListProduct;
    Product product;

    public ViewCategoryAdapter(Context mContext, ArrayList<Product> mListProduct) {
        this.mContext = mContext;
        this.mListProduct = mListProduct;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.adapter_category,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        holder.mBtnCategory.setText(mListProduct.get(position).getProdCategory());

    }

    @Override
    public int getItemCount() {
        return mListProduct.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private Button mBtnCategory;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mBtnCategory=itemView.findViewById(R.id.btnCategory);

            mBtnCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    product=mListProduct.get(getAdapterPosition());
                    mOnItemClickListener.onItemClicked(product);
                }
            });

            }
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;

    }

    public interface OnItemClickListener {

        void onItemClicked(Product product);

    }
}
