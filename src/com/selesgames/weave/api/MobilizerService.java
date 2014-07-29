package com.selesgames.weave.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

import com.selesgames.weave.model.NewsItem;

public interface MobilizerService {

    @GET("/ipf")
    Observable<NewsItem> getPage(@Query("url") String url);

}
