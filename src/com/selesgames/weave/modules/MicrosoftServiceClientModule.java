package com.selesgames.weave.modules;

import java.net.MalformedURLException;

import javax.inject.Singleton;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.selesgames.weave.ForActivity;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class MicrosoftServiceClientModule {

    @Provides
    @Singleton
    MobileServiceClient provideMobileServiceClient(@ForActivity Context context) {
        MobileServiceClient client = null;
        try {
            client = new MobileServiceClient("https://weaveuser.azure-mobile.net/", "AItWGBDhTNmoHYvcCvixuYgxSvcljU97",
                    context);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return client;
    }
}
