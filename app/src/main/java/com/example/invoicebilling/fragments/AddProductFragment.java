package com.example.invoicebilling.fragments;


import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.invoicebilling.R;
import com.example.invoicebilling.activity.MainActivity;
import com.example.invoicebilling.adapters.ViewProductAdapter;
import com.example.invoicebilling.databse.SqliteDatabase;
import com.example.invoicebilling.pojo.Product;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AddProductFragment extends Fragment implements View.OnClickListener {

    private EditText mEdtProdName,mEdtProdPrice;
    private Button mBtnSaveProduct;
    private ImageView mImgProduct;
    private SqliteDatabase db;
    private String p_name,p_price,spinnerText;

    private Bitmap bp;
    private byte[] photo;
    private Spinner spinner;
    private ArrayList<Product> categories=new ArrayList<>();
    private ArrayAdapter<Product> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_add_product, container, false);
        //Instantiate database handler
        db=new SqliteDatabase(getContext());
        //categoryList = new ArrayList<>();

        mImgProduct= view.findViewById(R.id.ivAddImage);
        mEdtProdName=view.findViewById(R.id.etProductName);
        mEdtProdPrice=view.findViewById(R.id.etProductPrice);
        mBtnSaveProduct=view.findViewById(R.id.btnSaveProduct);
        spinner=view.findViewById(R.id.spinnerCategory);
        mBtnSaveProduct.setOnClickListener(this);
        mImgProduct.setOnClickListener(this);
        Button mBtnNewCategory=view.findViewById(R.id.btnNewCategory);
        mBtnNewCategory.setOnClickListener(this);

        if (SqliteDatabase.getInstance(getContext()).getProodCategory().getCount()!=0){
            reloadArrListCategory();
        }

        adapter=new ArrayAdapter<Product>(getContext(),android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,categories);
        //attach adapter to spinner
        spinner.setAdapter(adapter);

        //handle click of spinner item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // clicked item will be shown as spinner
                Toast.makeText(getContext(),""+parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private void selectImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 2:
                if(resultCode == RESULT_OK){
                    Uri choosenImage = data.getData();

                    if(choosenImage !=null){

                        bp=decodeUri(choosenImage, 400);
                        mImgProduct.setImageBitmap(bp);
                    }
                }
        }
    }

    //Convert and resize our image to 400dp for faster uploading our images to DB
    private Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o);
            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //Convert bitmap to bytes
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private byte[] profileImage(Bitmap b){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();

    }

    // function to get values from the Edittext and image
    private void getValues(){

        p_name = mEdtProdName.getText().toString();
        p_price = mEdtProdPrice.getText().toString();
        spinnerText = spinner.getSelectedItem().toString();
        if(bp == null) {
            Toast.makeText(getContext(),"not found", Toast.LENGTH_LONG).show();

            Bitmap bitmap = ((BitmapDrawable)mImgProduct.getDrawable()).getBitmap();
            photo = profileImage(bitmap);

        }else{
            photo = profileImage(bp);
        }


    }

    //Insert data to the database
    private void addProduct(){
        getValues();
        db.addProduct(new Product(p_name,p_price,spinnerText,photo));
        startActivity(new Intent(getContext(), MainActivity.class));
        Objects.requireNonNull(getActivity()).finish();
        Toast.makeText(getContext(),"Saved successfully", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSaveProduct:
                if(mEdtProdName.getText().toString().trim().equals("") || mEdtProdPrice.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(),"Field's can't be empty", Toast.LENGTH_LONG).show();
                } else{
                    addProduct();
                }
                break;
            case R.id.ivAddImage:
                selectImage();
                break;
            case R.id.btnNewCategory:
                AddNewCategory cdd=new AddNewCategory(getContext());
                cdd.show();
                break;
                default:
        }
    }

    //category
    private void reloadArrListCategory() {

        categories.clear();

        Cursor c = SqliteDatabase.getInstance(getContext()).getProodCategory();

        while (c.moveToNext()) {

            Product product = new Product();

            product.setProdId(c.getInt(0));
            product.setProdCategory(c.getString(1));

            categories.add(product);

        }

        c.close();

    }


}
