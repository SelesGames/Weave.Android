package com.selesgames.weave.api;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

import com.selesgames.weave.model.Article;

public interface MobilizerService {

    @GET("/ipf")
    Observable<Article> getArticle(@Query("url") String url);

}
