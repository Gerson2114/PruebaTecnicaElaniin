package com.example.gerson.pruebatecnicaelaniin;

import android.app.Application;

/**
 * Created by gerson on 16/01/18.
 */

public class MyApplication extends Application {
    public String  accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
