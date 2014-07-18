package com.selesgames.weave.modules;

import javax.inject.Singleton;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;
import retrofit.converter.SimpleXMLConverter;
import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.selesgames.weave.ForJSON;
import com.selesgames.weave.ForXML;
import com.selesgames.weave.api.CategoryService;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.Provides;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module(library = true)
public class NetworkModule {

    @Provides
    @Singleton
    @ForJSON
    Converter provideJSONConverter() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        mapper.registerModule(module);

        return new JacksonConverter(mapper);
    }

    @Provides
    @Singleton
    @ForXML
    Converter provideXMLConverter() {
        return new SimpleXMLConverter();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    OkClient provideOkClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides
    @Singleton
    CategoryService provideCategoryService(OkClient client, @ForXML Converter converter) {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://weave.blob.core.windows.net")
                .setClient(client).setConverter(converter).setLogLevel(LogLevel.FULL).build();
        return restAdapter.create(CategoryService.class);
    }

}