package com.accemy.mahindraloggerapp.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import com.accemy.mahindraloggerapp.common.injection.module.ApplicationTestModule;
import com.accemy.mahindraloggerapp.injection.component.AppComponent;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends AppComponent {
}
