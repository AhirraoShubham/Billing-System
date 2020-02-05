package com.example.invoicebilling.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.invoicebilling.Bluetooth.DeviceListActivity;
import com.example.invoicebilling.Bluetooth.UnicodeFormatter;
import com.example.invoicebilling.R;
import com.example.invoicebilling.adapters.ViewCategoryAdapter;
import com.example.invoicebilling.adapters.ViewProductAdapter;
import com.example.invoicebilling.adapters.ViewProductBillAdapter;
import com.example.invoicebilling.databse.SqliteDatabase;
import com.example.invoicebilling.fragments.AddProductFragment;
import com.example.invoicebilling.fragments.MakePaymentFragment;
import com.example.invoicebilling.pojo.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,Runnable {

    //Arraylist for product
    private ArrayList<Product>mListProduct;
    //Adapter for product
    ViewProductAdapter mAdapterProduct;
    //Arraylist for Product bill
    private ArrayList<Product>mListBill;
    //Adapter for product bill
    ViewProductBillAdapter mAdapterProductBill;
    //Arraylist for Product Category
    private ArrayList<Product>mListCategory;
    //Adapter for product bill
    ViewCategoryAdapter mAdapterProductCategory;

    RecyclerView mProductRecycler,mProductBillRecycler,mProductCategoryRecycler;
    private ImageView inputAddPhoto,inputImage;
    private Button mBtnPayment,mBtnAll ;

    private Bitmap bp;
    private SqliteDatabase db;
    double input1 = 0, input2 = 0;

    private TextView edt1,mTxtTotal;
    String prod_cate;

    // BT
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    public static BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    //date and bill number
    String current_date,bill_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Recyclerview
        mProductRecycler=findViewById(R.id.rv_product);
        mProductBillRecycler=findViewById(R.id.rv_bill);
        mProductCategoryRecycler=findViewById(R.id.rv_category);
        mTxtTotal=findViewById(R.id.tvTotal1);
        mBtnAll=findViewById(R.id.btnAll);

        //Arraylist
        mListProduct=new ArrayList<>();
        mListBill=new ArrayList<>();
        mListCategory=new ArrayList<>();
        mBtnPayment=findViewById(R.id.btnPayment);
        mBtnAll=findViewById(R.id.btnAll);
        //Instantiate database handler
        db=new SqliteDatabase(MainActivity.this);

        if (SqliteDatabase.getInstance(MainActivity.this).getProoductBill().getCount()!=0){
            reloadArrListProductBill();
        }
        if (SqliteDatabase.getInstance(MainActivity.this).getProodCategory().getCount()!=0){
            reloadArrListCategory();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //product
        mAdapterProduct=new ViewProductAdapter(MainActivity.this,mListProduct);
        mProductRecycler.setAdapter(mAdapterProduct);
        mAdapterProduct.setOnUpdateClickListener(new MyUpdateListener());
        mAdapterProduct.setOnItemClickListener(new MyItemListener());
        mProductRecycler.setLayoutManager(new GridLayoutManager(MainActivity.this,2));

        //bill
        mAdapterProductBill=new ViewProductBillAdapter(MainActivity.this,mListBill);
        mProductBillRecycler.setAdapter(mAdapterProductBill);
        mAdapterProductBill.setOnDeleteItemClickListener(new MyDeleteItemListener());
        mAdapterProductBill.setOnItemClickListener(new MyAddQtyBtnistener());
        mProductBillRecycler.setLayoutManager(new LinearLayoutManager(this));

        //product category
        mAdapterProductCategory=new ViewCategoryAdapter(MainActivity.this,mListCategory);
        mProductCategoryRecycler.setAdapter(mAdapterProductCategory);
        mProductCategoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapterProductCategory.setOnItemClickListener(new MyCategoryBtnListner());

        //BT


        BillNo();
        getCurrentDate();

    }

    //Insert data to the database
    private void addProductBill(String p_name,String p_price,String p_qty){

        db.addProductBill(new Product(p_name,p_price, "1"));
        Toast.makeText(MainActivity.this,String.format(p_name,"added successfully"), Toast.LENGTH_LONG).show();
        mAdapterProductBill.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.bt_connect) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(MainActivity.this, "Message1", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(MainActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_add_product) {
            AddProductFragment addProductFragment=new AddProductFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container,addProductFragment)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_show_report) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reloadArrListProduct() {

        mListProduct.clear();

        Cursor c = SqliteDatabase.getInstance(MainActivity.this).getProoducts();

        while (c.moveToNext()) {

            Product product = new Product();

            product.setProdId(c.getInt(0));
            product.setProdName(c.getString(1));
            product.setProdPrice(c.getString(2));
            product.setProdCategory(c.getString(3));
            product.setProdImage(c.getBlob(4));

            mListProduct.add(product);

        }

        c.close();
    }

    //get prod category wise
    private void reloadArrListProdCate(String prod_cat) {

        mListProduct.clear();

        Cursor c = SqliteDatabase.getInstance(MainActivity.this).getProdCate(prod_cat);

        while (c.moveToNext()) {

            Product product = new Product();

            product.setProdId(c.getInt(0));
            product.setProdName(c.getString(1));
            product.setProdPrice(c.getString(2));
            product.setProdCategory(c.getString(3));
            product.setProdImage(c.getBlob(4));

            mListProduct.add(product);

        }

        c.close();
    }

    private void reloadArrListProductBill() {

        mListBill.clear();

        Cursor c = SqliteDatabase.getInstance(MainActivity.this).getProoductBill();

        while (c.moveToNext()) {

            Product product = new Product();

            product.setProdId(c.getInt(0));
            product.setProdName(c.getString(1));
            product.setProdQty(c.getString(2));
            product.setProdPrice(c.getString(3));

            mListBill.add(product);

        }

        c.close();
    }

    public void getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        String CurrentDate = sdf.format(c.getTime());
        current_date=(CurrentDate);
    }

    public void BillNo() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:yy");
        String CurrentDate = sdf.format(c.getTime());
        bill_no=(String.format("ES/%s",CurrentDate));
    }

    public void makePayment(View view) {
        final Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = MainActivity.mBluetoothSocket
                            .getOutputStream();

                    StringBuilder msg= new StringBuilder(" ");
                    msg.append("     ");
                    msg.append("Baba Infotech P Ltd");
                    msg.append("     \n");
                    msg.append("149,East street,Pune");
                    msg.append("     \n");
                    msg.append("Contact:9370571388");
                    msg.append("     \n");
                    msg.append("GSTIN:27AABCB5731H1ZU");
                    msg.append("\n");
                    msg.append("Date :"+current_date);
                    msg.append("\n");
                    msg.append("Bill no. :"+bill_no);
                    msg.append("\n--------------------------------");
                    msg.append("Item ");
                    msg.append(" Qty  ");
                    msg.append(" RATE");
                    msg.append(" Total");
                    msg.append("\n");

                    for (Product product : mListBill) {
                        int q = Integer.parseInt(String.valueOf((product.getProdQty())));
                        final Double p = Double.parseDouble(product.getProdPrice());
                        Double total = (p * q);

                        msg.append(product.getProdName()).append("\n").append(product.getProdQty()).append("   ").append(product.getProdPrice()).append("   ").append(total).append("\n");
                    }
                    msg.append("\n--------------------------------");
                    if (mListBill.size()!=0) {
                        msg.append(mTxtTotal.getText().toString());

                    }
                    msg.append("\n--------------------------------");
                    msg.append("Thank you!");

                    os.write(msg.toString().getBytes());

                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
        calculateAmount(mListBill);
        //addReport1();
    }

    public void getAllProduct(View view) {
        if (SqliteDatabase.getInstance(MainActivity.this).getProoducts().getCount()!=0){
            reloadArrListProduct();
            mAdapterProduct.notifyDataSetChanged();
        }
    }

    class MyCategoryBtnListner implements ViewCategoryAdapter.OnItemClickListener{

        @Override
        public void onItemClicked(Product product) {
            if (SqliteDatabase.getInstance(MainActivity.this).getProoducts().getCount()!=0){
                reloadArrListProdCate(product.getProdCategory());
                mAdapterProduct.notifyDataSetChanged();
            }
        }
    }

    class MyUpdateListener implements ViewProductAdapter.OnUpdateClickListener{

        @Override
        public void onUpdateClicked(int position) {

            showItemDialog(true, mListProduct.get(position), position);
        }
    }

    class MyItemListener implements ViewProductAdapter.OnItemClickListener{
        @Override
        public void onItemClicked(Product product) {
            addProductBill(product.getProdName(),product.getProdPrice(),product.getProdQty());
            reloadArrListProductBill();
            getTotal(mListBill);
            mAdapterProductBill.notifyDataSetChanged();
        }
    }

    class MyDeleteItemListener implements ViewProductBillAdapter.OnDeleteItemClickListener{
        @Override
        public void onDeleteItemClicked(Product product) {
            reloadArrListProductBill();
            getTotal(mListBill);
            mAdapterProductBill.notifyDataSetChanged();
        }
    }

    private void showItemDialog(final boolean shouldUpdate, final Product product, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View view = layoutInflaterAndroid.inflate(R.layout.update_product_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputDes = view.findViewById(R.id.edtDialogName);
        final EditText inputPrice = view.findViewById(R.id.edtDialogPrice);
        inputImage = view.findViewById(R.id.ivDialogProd);
        inputAddPhoto = view.findViewById(R.id.ivAddPhoto);

        TextView dialogTitle = view.findViewById(R.id.dialog_staff_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_prod) : getString(R.string.lbl_edit_prod));

        inputAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               selectImage();
            }
        });

        if (shouldUpdate && product != null) {
            inputDes.setText(product.getProdName());
            inputPrice.setText(product.getProdPrice());
            inputImage.setImageBitmap(convertToBitmap(product.getProdImage()));

        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputDes.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && product != null && bp!=null) {

                    updateProduct(product.getProdId(),inputDes.getText().toString(), inputPrice.getText().toString(),profileImage(bp),position);
                }else {
                    updateProduct(product.getProdId(),inputDes.getText().toString(), inputPrice.getText().toString(),profileImage(bp),position);
                }
            }
        });
    }

    private void updateProduct(int id,String name,String price,
                             byte[] image,int position) {
        Product table = mListProduct.get(position);

        table.setProdId(id);
        table.setProdName(name);
        table.setProdPrice(price);
        table.setProdImage(image);

        SqliteDatabase.getInstance(MainActivity.this).updateProduct(id,
                name, price,image
        );

        mListProduct.set(position, table);
        mAdapterProduct.notifyItemChanged(position);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Uri choosenImage = Objects.requireNonNull(data).getData();

                if (choosenImage != null) {

                    bp = decodeUri(choosenImage, 400);
                    inputImage.setImageBitmap(bp);
                }
            }
        }
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle mExtra = data.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(MainActivity.this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(MainActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(MainActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Convert and resize our image to 400dp for faster uploading our images to DB
    private Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (width_tmp / 2 >= REQUIRED_SIZE
                    && height_tmp / 2 >= REQUIRED_SIZE) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
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

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    //open gallery
    private void selectImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 2);
    }

    class MyAddQtyBtnistener implements ViewProductBillAdapter.OnItemClickListener{

        @Override
        public void onItemClicked(int position) {
            showQtyDialog(true, mListProduct.get(position), position);
        }
    }

    //show qty dialog
    private void showQtyDialog(final boolean shouldUpdate, final Product product, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View view = layoutInflaterAndroid.inflate(R.layout.add_qty_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final  Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonDel, buttonDot;

        button0 = view. findViewById(R.id.button0);
        button1 = view. findViewById(R.id.button1);
        button2 = view. findViewById(R.id.button2);
        button3 =view. findViewById(R.id.button3);
        button4 = view. findViewById(R.id.button4);
        button5 =view. findViewById(R.id.button5);
        button6 = view. findViewById(R.id.button6);
        button7 = view. findViewById(R.id.button7);
        button8 = view. findViewById(R.id.button8);
        button9 = view. findViewById(R.id.button9);
        buttonDot = view. findViewById(R.id.buttonDot);
        buttonDel = view. findViewById(R.id.buttonDel);

        edt1 = view. findViewById(R.id.display);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "1");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "2");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "3");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "4");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "5");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "6");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "7");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "8");
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "9");
            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText(edt1.getText() + "0");
            }
        });



        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt1.setText("");
                input1 = 0.0;
                input2 = 0.0;
            }
        });

        buttonDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (decimal) {
                    //do nothing or you can show the error
                } else {
                    edt1.setText(edt1.getText() + ".");
                    decimal = true;
                }*/
                edt1.setText(edt1.getText() + ".");

            }
        });

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(edt1.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Quantity!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && product != null) {

                    updateProduct(product.getProdId(),edt1.getText().toString(),position);


                }
            }
        });
    }

    private void updateProduct(int id,String qty,int position) {
        Product table = mListProduct.get(position);

        table.setProdId(id);
        table.setProdQty(qty);

        SqliteDatabase.getInstance(MainActivity.this).updateProductQty(id,
                qty
        );

        mListBill.set(position, table);
        getTotal(mListBill);

        mAdapterProductBill.notifyItemChanged(position);

    }
    //category
    private void reloadArrListCategory() {
        mListCategory.clear();

        Cursor c = SqliteDatabase.getInstance(MainActivity.this).getProodCategory();

        while (c.moveToNext()) {

            Product product = new Product();

            product.setProdId(c.getInt(0));
            product.setProdCategory(c.getString(1));

            mListCategory.add(product);

        }

        c.close();

    }

    //get Total amount
    private void getTotal(ArrayList<Product> itemServiceInfoList) {
        //For Total amount
        double totalAmount=0;
        int i;
        for(i = 0; i< this.mListBill.size(); i++){
            Product product=itemServiceInfoList.get(i);

            totalAmount= totalAmount+Double.parseDouble(product.getProdPrice() )* Double.parseDouble(product.getProdQty());
        }
        mTxtTotal.setText(String.format("Total %s",String.valueOf(totalAmount)));

    }

    private void calculateAmount(ArrayList<Product> itemServiceInfoList) {
        //For Total amount
        double totalAmount=0;

        int i;
        for(i = 0; i< this.mListBill.size(); i++){
            Product product=itemServiceInfoList.get(i);

                totalAmount= totalAmount+Double.parseDouble(product.getProdPrice() )* Double.parseDouble(product.getProdQty());
        }

        MakePaymentFragment makePaymentFragment=new MakePaymentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("total", String.valueOf(totalAmount));
        makePaymentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container,makePaymentFragment)
                .addToBackStack(null)
                .commit();
        Toast.makeText(MainActivity.this, ""+totalAmount, Toast.LENGTH_SHORT).show();
    }

    public void addReport1(){
        Gson gson = new Gson();
        final String report = new Gson().toJson(mListBill);
        db.addProductReport(report);

        Toast.makeText(MainActivity.this,report, Toast.LENGTH_LONG).show();

    }

    // BT

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }



}
