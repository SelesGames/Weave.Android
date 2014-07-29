package com.selesgames.weave.api;

import retrofit.http.GET;
import rx.Observable;

import com.selesgames.weave.model.CategoryList;

public interface CategoryService {

    @GET("/settings/masterfeeds.xml")
    Observable<CategoryList> getCategories();

}
