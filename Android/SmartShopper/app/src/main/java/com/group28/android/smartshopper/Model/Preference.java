package com.group28.android.smartshopper.Model;

/**
 * Created by deepika on 11/6/2016.
 */

public class Preference {

    private int userId;
    private String category;
    private String shoppingPreference;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getShoppingPreference() {
        return shoppingPreference;
    }

    public void setShoppingPreference(String shoppingPreference) {
        this.shoppingPreference = shoppingPreference;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
