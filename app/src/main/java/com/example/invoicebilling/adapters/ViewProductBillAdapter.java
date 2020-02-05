package com.example.invoicebilling.adapters;

import android.content.Context;
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
import com.example.invoicebilling.databse.SqliteDatabase;
import com.example.invoicebilling.pojo.Product;

import java.util.ArrayList;

public class ViewProductBillAdapter extends RecyclerView.Adapter<ViewProductBillAdapter.ProductViewHolder> {
    private Context mContext;
    private ArrayList<Product>mListProduct;
    Product product;

    public ViewProductBillAdapter(Context mContext, ArrayList<Product> mListProduct) {
        this.mContext = mContext;
        this.mListProduct = mListProduct;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.adapter_bill_product,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        holder.mTxtProdName.setText(mListProduct.get(position).getProdName());
        holder.mTxtProdPrice.setText(mListProduct.get(position).getProdPrice());
        holder.mBtnProdQty.setText(mListProduct.get(position).getProdQty());
        String t2 = String.valueOf(Float.valueOf(holder.mBtnProdQty.getText().toString()) * Float.valueOf(holder.mTxtProdPrice.getText().toString()));
        holder.mTxtProdTotal.setText(String.format(" = %s",t2));

    }

    @Override
    public int getItemCount() {
        return mListProduct.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtProdName,mTxtProdPrice,mTxtProdTotal;
        private ImageView mImgDelete;
        private TextView mBtnProdQty;
        ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxtProdName=itemView.findViewById(R.id.tvBillProdName);
            mTxtProdPrice=itemView.findViewById(R.id.tvBillProdPrice);
            mTxtProdTotal=itemView.findViewById(R.id.tvBillProdTotal);
            mBtnProdQty=itemView.findViewById(R.id.btnBillProdQty);
            mImgDelete=itemView.findViewById(R.id.ivDeleteBillProd);

            mImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    product = mListProduct.get(getAdapterPosition());
                    SqliteDatabase.getInstance(mContext).deleteProductBill(product.getProdId());
                    mOnDeleteItemClickListener.onDeleteItemClicked(product);
                }
            });

            mBtnProdQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    product = mListProduct.get(getAdapterPosition());
                    mOnItemClickListener.onItemClicked(getAdapterPosition());
                }
            });

        }
    }


    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
    private OnDeleteItemClickListener mOnDeleteItemClickListener;

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener onDeleteItemClickListener) {
        mOnDeleteItemClickListener = onDeleteItemClickListener;
    }

    public interface OnDeleteItemClickListener {
        void onDeleteItemClicked(Product product);
    }
}
