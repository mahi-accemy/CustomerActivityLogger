package com.accemy.mahindraloggerapp.injection.component;

import dagger.Subcomponent;
import com.accemy.mahindraloggerapp.injection.PerFragment;
import com.accemy.mahindraloggerapp.injection.module.FragmentModule;

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {
}
