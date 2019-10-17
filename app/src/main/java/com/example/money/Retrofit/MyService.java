package com.example.money.Retrofit;

import com.example.money.models.Transaction;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyService {

    @POST("/users/login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                    @Field("password") String password);

    @POST("/users/create")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("username") String username,
                                    @Field("password") String password);

    @POST("/transaction/create/")
    @FormUrlEncoded
    Call<Transaction> addTransaction(
                        @Field("email") String email,
                        @Field("amount") String amount,
                        @Field("note") String note,
                        @Field("category") String category,
                        @Field("type") String type,
                        @Field("photo") String photo);

    @GET("/transaction/")
    Call<List<Transaction>> getAllTransactions();

    @GET("/transaction/plan")
    Call<List<Transaction>> getAllPlanTransactions();

//    @GET("/transaction/{id}")
//    Call<Transaction> getTransaction(@Query("id") String id);

    @GET("/transaction/{category}")
    Call<Transaction> getTransactionByCategory(@Path("category") String category);

    @GET("/transaction/{id}")
    Call<Transaction> getTransactionById(@Path("id") String id);
}
