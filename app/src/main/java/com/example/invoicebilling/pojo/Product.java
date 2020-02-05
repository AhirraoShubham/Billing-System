package com.example.invoicebilling.pojo;

import java.io.Serializable;

public class Product implements Serializable {

    private int prodId;
    private String prodName,prodPrice,prodQty,prodCategory,prodAmt;
    private byte[] prodImage;

    public Product() {

    }
    public Product(String prodCategory) {
        this.prodCategory = prodCategory;
    }

    public Product(String prodName, String prodPrice, String prodCategory, byte[] prodImage) {
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodCategory = prodCategory;
        this.prodImage = prodImage;
    }

    public Product(int prodId, String prodName, String prodPrice, byte[] prodImage) {
        this.prodId = prodId;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodImage = prodImage;
    }

    public Product(String prodName,String prodQty, String prodPrice, String prodAmt) {
        this.prodName = prodName;
        this.prodQty = prodQty;
        this.prodPrice = prodPrice;
        this.prodAmt = prodAmt;

    }

    public Product(String prodName, String prodPrice, String prodQty) {
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodQty = prodQty;
    }

    public String getProdAmt() {
        return prodAmt;
    }

    public void setProdAmt(String prodAmt) {
        this.prodAmt = prodAmt;
    }

    public String getProdCategory() {
        return prodCategory;
    }

    public void setProdCategory(String prodCategory) {
        this.prodCategory = prodCategory;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdQty() {
        return prodQty;
    }

    public void setProdQty(String prodQty) {
        this.prodQty = prodQty;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }

    public byte[] getProdImage() {
        return prodImage;
    }

    public void setProdImage(byte[] prodImage) {
        this.prodImage = prodImage;
    }

    @Override
    public String toString() {
        return getProdCategory();
    }
}
