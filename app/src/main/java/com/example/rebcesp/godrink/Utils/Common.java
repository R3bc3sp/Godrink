package com.example.rebcesp.godrink.Utils;

import com.example.rebcesp.godrink.Retrofit.IGoDrinkAPI;
import com.example.rebcesp.godrink.Retrofit.RetrofitClient;

public class Common {


    //In Emulator, localhost = 10.0.2.2

    private static final String BASE_URL = "http://10.0.2.2:80/godrink/";


    public static IGoDrinkAPI getAPI(){

        return RetrofitClient.getClient(BASE_URL).create(IGoDrinkAPI.class);
    }


}
