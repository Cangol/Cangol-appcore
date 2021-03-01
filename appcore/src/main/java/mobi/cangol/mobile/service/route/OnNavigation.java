package mobi.cangol.mobile.service.route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

/**
 * Created by xuewu.wei on 2018/11/2.
 */
public interface OnNavigation {

    void toActivity(Intent intent,boolean newStack);

    void toFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean newStack);
}
