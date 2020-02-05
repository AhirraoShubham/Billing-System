package com.example.invoicebilling.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.invoicebilling.R;
import com.example.invoicebilling.activity.MainActivity;
import com.example.invoicebilling.databse.SqliteDatabase;
import com.example.invoicebilling.pojo.Product;

import java.util.Objects;

public class AddNewCategory extends Dialog implements
        View.OnClickListener{
    private Activity c1;
    private Context c;
    public Dialog d;
    private EditText mEdtAddCategory;
    private SqliteDatabase db;
    private String p_cate;
    public AddNewCategory(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_new_category_dialog);
        mEdtAddCategory=findViewById(R.id.edtAddNewCategory);
        Button mBtnCancel = findViewById(R.id.btn_cancel);
        Button mBtnOk = findViewById(R.id.btn_ok);
        mBtnCancel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        db=new SqliteDatabase(getContext());

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                if(mEdtAddCategory.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(),"Field's can't be empty", Toast.LENGTH_LONG).show();
                }  else{
                    addNewCategory();
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    //Insert data to the database
    private void addNewCategory(){

        p_cate = mEdtAddCategory.getText().toString();
        db.CheckingCategoryAlreadyExistsOrNot(p_cate);
       // db.addProductCategory(new Product(p_cate));
      //  c1.finish();
       Toast.makeText(getContext(),p_cate+" added new category!!!", Toast.LENGTH_LONG).show();

    }
}
