package com.selesgames.weave.modules;

import javax.inject.Singleton;

import retrofit.RequestInterceptor;
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
import com.selesgames.weave.api.IdentityService;
import com.selesgames.weave.api.UserService;
import com.squareup.okhttp.OkHttpClient;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class NetworkModule {

    private Context mContext;

    public NetworkModule(Context context) {
        mContext = context;
    }

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
    UserService provideUserService(OkClient client, @ForJSON Converter converter) {
        RequestInterceptor interceptor = new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade r) {
                r.addHeader("Accept", "application/json");
            }

        };
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://weave-user.cloudapp.net/api")
                .setClient(client).setConverter(converter).setLogLevel(LogLevel.FULL)
                .setRequestInterceptor(interceptor).build();
        return restAdapter.create(UserService.class);
    }

    @Provides
    @Singleton
    CategoryService provideCategoryService(OkClient client, @ForXML Converter converter) {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://weave.blob.core.windows.net")
                .setClient(client).setConverter(converter).setLogLevel(LogLevel.FULL).build();
        return restAdapter.create(CategoryService.class);
    }

    @Provides
    @Singleton
    IdentityService provideIdentityService(OkClient client, @ForJSON Converter converter) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://weave-identity.cloudapp.net/api/identity/sync").setClient(client)
                .setConverter(converter).setLogLevel(LogLevel.FULL).build();
        return restAdapter.create(IdentityService.class);
    }

}