package com.accemy.mahindraloggerapp.features.detail;

import com.accemy.mahindraloggerapp.data.model.response.Pokemon;
import com.accemy.mahindraloggerapp.data.model.response.Statistic;
import com.accemy.mahindraloggerapp.features.base.MvpView;

public interface DetailMvpView extends MvpView {

    void showPokemon(Pokemon pokemon);

    void showStat(Statistic statistic);

    void showProgress(boolean show);

    void showError(Throwable error);
}
