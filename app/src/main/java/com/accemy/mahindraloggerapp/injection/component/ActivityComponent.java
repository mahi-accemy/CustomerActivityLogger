package com.accemy.mahindraloggerapp.injection.component;

import dagger.Subcomponent;
import com.accemy.mahindraloggerapp.features.detail.DetailActivity;
import com.accemy.mahindraloggerapp.features.main.MainActivity;
import com.accemy.mahindraloggerapp.injection.PerActivity;
import com.accemy.mahindraloggerapp.injection.module.ActivityModule;

@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(DetailActivity detailActivity);
}
