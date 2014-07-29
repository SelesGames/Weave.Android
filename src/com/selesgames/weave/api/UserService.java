package com.selesgames.weave.api;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

import com.selesgames.weave.model.CategoryNews;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.User;

public interface UserService {

    @POST("/user/create")
    Observable<User> create();

    @GET("/user/info")
    Observable<User> getInfo(@Query("userId") String userId, @Query("refresh") boolean refresh);

    @POST("/user/add_feed")
    Observable<Feed> addFeed(@Query("userId") String userId, @Body Feed feed);

    @GET("/user/remove_feed")
    Observable<Void> removeFeed(@Query("userId") String userId, @Query("feedId") String feedId);

    @GET("/user/news")
    Observable<CategoryNews> getFeedsForCategory(@Query("userId") String userId, @Query("category") String category,
            @Query("entry") String entry, @Query("skip") int skip, @Query("take") int take);

    @GET("/user/news")
    Observable<List<Feed>> getNewsForFeed(@Query("userId") String userId, @Query("feedId") String feedId,
            @Query("entry") String entry, @Query("skip") int skip, @Query("take") int take);

}
