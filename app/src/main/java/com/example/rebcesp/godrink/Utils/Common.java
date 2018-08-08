package com.example.rebcesp.godrink.Utils;

import com.example.rebcesp.godrink.Model.User;
import com.example.rebcesp.godrink.Retrofit.IGoDrinkAPI;
import com.example.rebcesp.godrink.Retrofit.RetrofitClient;

public class Common {


    //In Emulator, localhost = 10.0.2.2

    private static final String BASE_URL = "http://192.168.1.137:80/godrink/";

    public static User currentUser = null;

    public static IGoDrinkAPI getAPI(){

        return RetrofitClient.getClient(BASE_URL).create(IGoDrinkAPI.class);
    }


}
