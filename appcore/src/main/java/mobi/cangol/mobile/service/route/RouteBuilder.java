package mobi.cangol.mobile.service.route;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xuewu.wei on 2018/10/16.
 */
public class RouteBuilder {
    private String path = null;
    private Bundle bundle = null;
    private boolean standalone = false;
    private Context context = null;
    private RouteServiceImpl routeService;

    protected RouteBuilder(RouteServiceImpl routeService, String path) {
        this.routeService = routeService;
        this.bundle = new Bundle();
        this.path = path;
    }

    protected String getPath() {
        return path;
    }

    protected Bundle getBundle() {
        return bundle;
    }

    protected Context getContext() {
        return context;
    }

    public void navigation(Context context) {
        this.context = context;
        Class clazz = routeService.getClassByPath(getPath());
        if (clazz != null) {
            if (clazz.isAnnotationPresent(Route.class)) {
                Route route = (Route) clazz.getAnnotation(Route.class);
                routeService.handleNavigation(clazz, getBundle(), getContext(), route.standalone());
            } else {
                routeService.handleNavigation(clazz, getBundle(), getContext(), this.standalone);
            }
        }
    }

    public RouteBuilder putString(String key, String value) {
        this.bundle.putString(key, value);
        return this;
    }

    public RouteBuilder putParcelable(String key, Parcelable value) {
        this.bundle.putParcelable(key, value);
        return this;
    }

    public RouteBuilder putChar(String key, char value) {
        this.bundle.putChar(key, value);
        return this;
    }

    public RouteBuilder putFloat(String key, float value) {
        this.bundle.putFloat(key, value);
        return this;
    }

    public RouteBuilder putShort(String key, short value) {
        this.bundle.putShort(key, value);
        return this;
    }

    public RouteBuilder putDouble(String key, Double value) {
        this.bundle.putDouble(key, value);
        return this;
    }

    public RouteBuilder putInt(String key, int value) {
        this.bundle.putInt(key, value);
        return this;
    }

    public RouteBuilder putSerializable(String key, Serializable value) {
        this.bundle.putSerializable(key, value);
        return this;
    }

    public RouteBuilder putLong(String key, long value) {
        this.bundle.putLong(key, value);
        return this;
    }

    public RouteBuilder putAll(Bundle bundle) {
        this.bundle.putAll(bundle);
        return this;
    }

    public RouteBuilder putByte(String key, byte value) {
        this.bundle.putByte(key, value);
        return this;
    }

    public RouteBuilder putBoolean(String key, boolean value) {
        this.bundle.putBoolean(key, value);
        return this;
    }

    public RouteBuilder putBundle(String key, Bundle value) {
        this.bundle.putBundle(key, value);
        return this;
    }


    public RouteBuilder putIntArray(String key, int[] value) {
        this.bundle.putIntArray(key, value);
        return this;
    }


    public RouteBuilder putFloatArray(String key, float[] value) {
        this.bundle.putFloatArray(key, value);
        return this;
    }

    public RouteBuilder putCharArray(String key, char[] value) {
        this.bundle.putCharArray(key, value);
        return this;
    }


    public RouteBuilder putLongArray(String key, long[] value) {
        this.bundle.putLongArray(key, value);
        return this;
    }


    public RouteBuilder putDoubleArray(String key, double[] value) {
        this.bundle.putDoubleArray(key, value);
        return this;
    }

    public RouteBuilder putByteArray(String key, byte[] value) {
        this.bundle.putByteArray(key, value);
        return this;
    }

    public RouteBuilder putBooleanArray(String key, boolean[] value) {
        this.bundle.putBooleanArray(key, value);
        return this;
    }

    public RouteBuilder putStringArray(String key, String[] value) {
        this.bundle.putStringArray(key, value);
        return this;
    }

    public RouteBuilder putCharSequence(String key, CharSequence value) {
        this.bundle.putCharSequence(key, value);
        return this;
    }

    public RouteBuilder putCharSequenceArray(String key, CharSequence[] value) {
        this.bundle.putCharSequenceArray(key, value);
        return this;
    }

    public RouteBuilder putStringArrayList(String key, ArrayList<String> value) {
        this.bundle.putStringArrayList(key, value);
        return this;
    }


    public RouteBuilder putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
        this.bundle.putParcelableArrayList(key, value);
        return this;
    }

    public RouteBuilder putIntegerArrayList(String key, ArrayList<Integer> value) {
        this.bundle.putIntegerArrayList(key, value);
        return this;
    }

    public RouteBuilder putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
        this.bundle.putSparseParcelableArray(key, value);
        return this;
    }
}
