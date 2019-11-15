package mobi.cangol.mobile.service.route

import android.content.Intent
import android.support.v4.app.Fragment

/**
 * Created by xuewu.wei on 2018/11/2.
 */
interface OnNavigation {

    fun toActivity(intent: Intent)

    fun toFragment(fragment: Fragment)
}
