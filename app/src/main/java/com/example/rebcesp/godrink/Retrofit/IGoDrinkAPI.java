package com.example.rebcesp.godrink.Retrofit;

import android.view.Menu;

import com.example.rebcesp.godrink.Model.Banner;
import com.example.rebcesp.godrink.Model.Category;
import com.example.rebcesp.godrink.Model.CheckUserResponse;
import com.example.rebcesp.godrink.Model.Drink;
import com.example.rebcesp.godrink.Model.User;

import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @FormUrlEncoded
    @POST("getdrink.php")
    Call<Drink>getDrink(@Field("menuid") String menuID);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<User>getUserInformation(@Field("phone") String phone);

    @GET("getbanner.php")
    io.reactivex.Observable<List<Banner>> getBanners();

    @GET("getmenu.php")
    io.reactivex.Observable<List<Category>> getMenu();





}
