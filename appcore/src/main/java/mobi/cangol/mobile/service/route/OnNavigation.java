package mobi.cangol.mobile.service.route;

import android.content.Intent;
import androidx.fragment.app.Fragment;

/**
 * Created by xuewu.wei on 2018/11/2.
 */
public interface OnNavigation {

    void toActivity(Intent intent);

    void toFragment(Fragment fragment);
}
