package com.selesgames.weave.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

import com.selesgames.weave.model.UserInfo;

public interface IdentityService {

    @GET("/")
    Observable<UserInfo> saveFacebookIdentity(@Query("userId") String userId, @Query("facebookToken") String token);

    @GET("/")
    Observable<UserInfo> saveTwitterIdentity(@Query("userId") String userId, @Query("twitterToken") String token);

    @GET("/")
    Observable<UserInfo> saveGoogleIdentity(@Query("userId") String userId, @Query("googleToken") String token);

    @GET("/")
    Observable<UserInfo> saveMicrosoftIdentity(@Query("userId") String userId, @Query("microsoftToken") String token);

}
