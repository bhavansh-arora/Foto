package com.foto.foto;

import java.util.List;
import java.util.Map;

public class Model {
    String id, title, price, mfgDate, expiryDate, barcodeId;
    List<String> ingredients;

    public Model(String id, String title, String price, String mfgDate, String expiryDate, String barcodeId, List<String> ingredients) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.mfgDate = mfgDate;
        this.expiryDate = expiryDate;
        this.ingredients = ingredients;
        this.barcodeId = barcodeId;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(String mfgDate) {
        this.mfgDate = mfgDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
