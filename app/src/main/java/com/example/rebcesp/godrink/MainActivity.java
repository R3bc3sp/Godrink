package com.example.rebcesp.godrink;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rebcesp.godrink.Model.CheckUserResponse;
import com.example.rebcesp.godrink.Model.User;
import com.example.rebcesp.godrink.Retrofit.IGoDrinkAPI;
import com.example.rebcesp.godrink.Utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    Button btn_continue;

    IGoDrinkAPI mService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = Common.getAPI();



        btn_continue = (Button)findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Loguearemos con el telefono de celular / movil. > Abrira a la pagina de facebook
                startLoginPage(LoginType.PHONE);

                //Check session

                if(AccountKit.getCurrentAccessToken() != null){

                    final android.app.AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please waiting");

                    // Auto Login
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {

                            mService.checkUserExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                            CheckUserResponse userResponse = response.body();
                                            if(userResponse.isExists()){

                                                //Fetch Information

                                                mService.getUserInformation(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {

                                                                //If User already exists, just strat new Activity


                                                                alertDialog.dismiss();

                                                                Common.currentUser = response.body(); //Fix here

                                                                startActivity(new Intent (MainActivity.this, HomeActivity.class ));
                                                                finish(); //Close MainActivity

                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_SHORT);

                                                            }
                                                        });


                                            }
                                            else{
                                                //Else, need register
                                                alertDialog.dismiss();

                                                showRegisterDialog(account.getPhoneNumber().toString());

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                            Log.d("ERROR",accountKitError.getErrorType().getMessage());

                        }
                    });
                }

            }
        });

        

    }

    private void startLoginPage(LoginType loginType) {

        //Estamos pasando con un intent a la activity donde esta para registrarse
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,AccountKitActivity.ResponseType.TOKEN);

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,builder.build());
        startActivityForResult(intent,REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null)
            {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }
            else if (result.wasCancelled()) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();

            }
            else
            {


                if(result.getAccessToken() != null)
                {
                    final android.app.AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please waiting");


                    //Get User phone and Check exists on server
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {

                            mService.checkUserExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {

                                            CheckUserResponse userResponse = response.body();
                                            if(userResponse.isExists()){

                                                //Fetch Information

                                                mService.getUserInformation(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {

                                                                //If User already exists, just strat new Activity


                                                                alertDialog.dismiss();

                                                                //Fixed first time crash

                                                                Common.currentUser = response.body();

                                                                startActivity(new Intent (MainActivity.this, HomeActivity.class ));
                                                                finish(); //Close MainActivity

                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_SHORT);

                                                            }
                                                        });


                                            }
                                            else{
                                                //Else, need register
                                                alertDialog.dismiss();

                                                showRegisterDialog(account.getPhoneNumber().toString());

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                            Log.d("ERROR",accountKitError.getErrorType().getMessage());

                        }
                    });
                }
            }
        }
    }

    private void showRegisterDialog(final String phone) {

       final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
       builder.setTitle("REGISTER");


        LayoutInflater inflater = this.getLayoutInflater();
        View register_layout= inflater.inflate(R.layout.register_layout,null);

        final MaterialEditText edt_name = (MaterialEditText)register_layout.findViewById(R.id.edt_name);
        final MaterialEditText edt_address = (MaterialEditText)register_layout.findViewById(R.id.edt_address);
        final MaterialEditText edt_birthdate = (MaterialEditText)register_layout.findViewById(R.id.edt_birthdate);

        Button btn_register = (Button)register_layout.findViewById(R.id.btn_register);

        edt_birthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(register_layout);

        final android.app.AlertDialog dialog= builder.create();

        //Event

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();


                if (TextUtils.isEmpty(edt_address.getText().toString())) {

                    Toast.makeText(MainActivity.this, "Please Enter your Address", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(edt_birthdate.getText().toString())) {

                    Toast.makeText(MainActivity.this, "Please Enter your Birthdate", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(edt_name.getText().toString())) {

                    Toast.makeText(MainActivity.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
                    return;

                }

                final android.app.AlertDialog watingDialog = new SpotsDialog(MainActivity.this);
                watingDialog.show();
                watingDialog.setMessage("Cargando, espera...");

                mService.registerNewUser(phone,
                        edt_name.getText().toString(),
                        edt_address.getText().toString(),
                        edt_birthdate.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {

                                watingDialog.dismiss();
                                User user = response.body();

                                if (TextUtils.isEmpty(user.getError_msg())) {
                                    Toast.makeText(MainActivity.this, "User register Successfully", Toast.LENGTH_SHORT).show();


                                    Common.currentUser = response.body();
                                    //Start New Activity
                                    startActivity(new Intent (MainActivity.this, HomeActivity.class ));
                                    finish();



                                }

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                                watingDialog.dismiss();

                            }
                        });


            }


        });



        dialog.show();

    }

    //Generaremos una KEY para nuestra Aplicacion
    private void printKeyHash() {

        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.rebcesp.godrink",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature:info.signatures){

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
