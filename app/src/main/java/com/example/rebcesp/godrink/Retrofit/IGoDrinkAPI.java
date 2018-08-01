package com.example.rebcesp.godrink.Retrofit;

import com.example.rebcesp.godrink.Model.CheckUserResponse;
import com.example.rebcesp.godrink.Model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IGoDrinkAPI {

    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse>checkUserExists(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("register.php")
    Call<User>registerNewUser(@Field("phone") String phone,
                              @Field("name") String name,
                              @Field("address") String address,
                              @Field("birthdate") String birthdate);
}
