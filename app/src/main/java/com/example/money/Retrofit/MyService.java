package com.example.money.Retrofit;

import com.example.money.models.Chart;
import com.example.money.models.Transaction;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
                        @Field("category") String category,
                        @Field("type") String type,
                        @Field("note") String note,
                        @Field("date") String date,
                        @Field("photo") String photo);
    //this month
    @POST("/transaction/")
    Call<List<Transaction>> getAllTransactions(@Body Transaction transaction);


    @POST("/transaction/getAll")
    Call<List<Transaction>> getAll(@Body Transaction transaction);

    @GET("/transaction/plan")
    Call<List<Transaction>> getAllPlanTransactions();

    //last month
    @POST("/transaction/email/")
    Call<List<Transaction>> getAllTransactionsByEmail(@Body Transaction transaction);


//    @GET("/transaction/{id}")
//    Call<Transaction> getTransaction(@Query("id") String id);

    @GET("/transaction/{category}")
    Call<Transaction> getTransactionByCategory(@Path("category") String category);


    //get transaction by id
    @GET("/transaction/id/{id}")
    Call<Transaction> getTransactionById(@Path("id") String id );

    //delete transaction by id
    @DELETE("/transaction/id/{id}")
    Call<Transaction> deleteTransactionById(@Path("id") String id);

    @PUT("/transaction/id/update/{id}")
    @FormUrlEncoded
    Call<Transaction> updateTransaction(@Path("id") String id,
                                        @Field("amount") String amount,
                                        @Field("category") String category,
                                        @Field("type") String type,
                                        @Field("note") String note,
                                        @Field("date") String date);


//    @GET("/transaction/chart1/{month}/{year}")
//    Call<List<Chart>> getChartByMonth( @Body("month") String month,
//                                 @Body("year") String year);

    @POST("/transaction/chart")
    Call<List<Chart>> getChartByMonth(@Body Chart chart);
}
