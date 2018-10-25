package mobi.cangol.mobile.service.route;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by xuewu.wei on 2018/10/16.
 */
public class RouteAction {
    private Context context = null;
    private String path = null;
    private Bundle bundle = null;

    public RouteAction(Context context, Builder builder) {
        this.context =context;
        this.path = builder.path;
        this.bundle = builder.bundle;
    }

    public Context getContext() {
        return context;
    }

    public String getPath() {
        return path;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public class Builder {

        private String path = null;
        private Bundle bundle = null;

        public Builder() {
            this.bundle = new Bundle();
        }

        public Builder navigation(String path) {
            this.path = path;
            return this;
        }

        public Builder putString(String key, String value) {
            this.bundle.putString(key, value);
            return this;
        }

        public RouteAction build(Context context) {
            return new RouteAction(context, this);
        }
    }

}
