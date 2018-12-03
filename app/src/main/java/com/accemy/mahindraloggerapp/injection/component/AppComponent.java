package com.accemy.mahindraloggerapp.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import com.accemy.mahindraloggerapp.data.DataManager;
import com.accemy.mahindraloggerapp.injection.ApplicationContext;
import com.accemy.mahindraloggerapp.injection.module.AppModule;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    @ApplicationContext
    Context context();

    Application application();

    DataManager apiManager();
}
