package com.example.invoicebilling.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.invoicebilling.R;
import com.example.invoicebilling.activity.MainActivity;
import com.example.invoicebilling.pojo.Product;

import java.io.OutputStream;


public class MakePaymentFragment extends Fragment {

    private Button mBtnStartNew,mBtnPrintReceipt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_make_payment, container, false);
        TextView tvTotal=view.findViewById(R.id.tvPriceTotal);
        mBtnStartNew=view.findViewById(R.id.btnStartNewSale);
        mBtnPrintReceipt=view.findViewById(R.id.btnPrintReceipt);

        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        if (mBundle !=null) {
            String total=  mBundle.getString("total");
            tvTotal.setText(total);
        }
        mBtnStartNew.setOnClickListener(new startNewSale());
        mBtnPrintReceipt.setOnClickListener(new printReceipt());
        return view;
    }
    class startNewSale implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }
    }

    class printReceipt implements View.OnClickListener{
        @Override
        public void onClick(View view) {

        }
    }

}
