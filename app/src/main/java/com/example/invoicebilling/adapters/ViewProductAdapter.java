package com.example.invoicebilling.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invoicebilling.R;
import com.example.invoicebilling.pojo.Product;

import java.util.ArrayList;

public class ViewProductAdapter extends RecyclerView.Adapter<ViewProductAdapter.ProductViewHolder> {
    private Context mContext;
    private ArrayList<Product>mListProduct;
    Product product;

    public ViewProductAdapter(Context mContext, ArrayList<Product> mListProduct) {
        this.mContext = mContext;
        this.mListProduct = mListProduct;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.adapter_product,parent,false);
        ProductViewHolder viewHolder=new ProductViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        holder.mTxtProdName.setText(mListProduct.get(position).getProdName());
        holder.mTxtProdPrice.setText(String.format("INR %s",mListProduct.get(position).getProdPrice()));
        holder.mImgProd.setImageBitmap(convertToBitmap(mListProduct.get(position).getProdImage()));

    }

    @Override
    public int getItemCount() {
        return mListProduct.size();
    }
    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b){

    return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgProd,mImgMore;
        private TextView mTxtProdName,mTxtProdPrice;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mImgProd=itemView.findViewById(R.id.ivProdImg);
            mImgMore=itemView.findViewById(R.id.ivMore);
            mTxtProdName=itemView.findViewById(R.id.tvProdName);
            mTxtProdPrice=itemView.findViewById(R.id.tvProdPrice);

            mImgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    product=mListProduct.get(getAdapterPosition());
                    mOnUpdateClickListener.onUpdateClicked(getAdapterPosition());

                }
            });
            mImgProd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    product=mListProduct.get(getAdapterPosition());
                    mOnItemClickListener.onItemClicked(product);

                }
            });
        }
    }

    private OnUpdateClickListener mOnUpdateClickListener;

    public void setOnUpdateClickListener(OnUpdateClickListener onUpdateClickListener) {

        mOnUpdateClickListener = onUpdateClickListener;

    }

    public interface OnUpdateClickListener {

        void onUpdateClicked(int position);

    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;

    }

    public interface OnItemClickListener {

        void onItemClicked(Product product);

    }

}
