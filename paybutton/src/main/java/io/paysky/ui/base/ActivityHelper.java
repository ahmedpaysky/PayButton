package io.paysky.ui.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Paysky-202 on 5/13/2018.
 */

public interface ActivityHelper {

    void setHeaderIcon(@DrawableRes int icon);

    void setHeaderIconClickListener(View.OnClickListener clickListener);

    void replaceFragmentAndRemoveOldFragment(Class<? extends Fragment> fragmentClass);

    void replaceFragmentAndAddOldToBackStack(Class<? extends Fragment> fragmentClass);

    void replaceFragmentAndRemoveOldFragment(Class<? extends Fragment> fragmentClass, Bundle bundle);

    void replaceFragmentAndAddOldToBackStack(Class<? extends Fragment> fragmentClass, Bundle bundle);
}
