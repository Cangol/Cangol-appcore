package mobi.cangol.mobile.service.route

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import java.io.Serializable
import java.util.*


/**
 * Created by xuewu.wei on 2018/10/16.
 */
open class RouteBuilder(private val routeService: RouteServiceImpl, path: String) {
    private var path: String? = null
    private val bundle = Bundle()
    private var context: Context? = null

    init {
        this.path = path
    }

    fun getPath(): String {
        return this.path!!
    }

    fun getBundle(): Bundle {
        return bundle
    }

    fun getContext(): Context {
        return this.context!!
    }

    fun navigation(context: Context) {
        this.context = context
        val clazz = routeService.getClassByPath(path!!)
        if (clazz != null) {
            routeService.handleNavigation(clazz, bundle, context)
        }
    }

    fun putString(key: String, value: String): RouteBuilder {
        this.bundle.putString(key, value)
        return this
    }

    fun putParcelable(key: String, value: Parcelable): RouteBuilder {
        this.bundle.putParcelable(key, value)
        return this
    }

    fun putChar(key: String, value: Char): RouteBuilder {
        this.bundle.putChar(key, value)
        return this
    }

    fun putFloat(key: String, value: Float): RouteBuilder {
        this.bundle.putFloat(key, value)
        return this
    }

    fun putShort(key: String, value: Short): RouteBuilder {
        this.bundle.putShort(key, value)
        return this
    }

    fun putDouble(key: String, value: Double): RouteBuilder {
        this.bundle.putDouble(key, value)
        return this
    }

    fun putInt(key: String, value: Int): RouteBuilder {
        this.bundle.putInt(key, value)
        return this
    }

    fun putSerializable(key: String, value: Serializable): RouteBuilder {
        this.bundle.putSerializable(key, value)
        return this
    }

    fun putLong(key: String, value: Long): RouteBuilder {
        this.bundle.putLong(key, value)
        return this
    }

    fun putAll(bundle: Bundle): RouteBuilder {
        this.bundle.putAll(bundle)
        return this
    }

    fun putByte(key: String, value: Byte): RouteBuilder {
        this.bundle.putByte(key, value)
        return this
    }

    fun putBoolean(key: String, value: Boolean): RouteBuilder {
        this.bundle.putBoolean(key, value)
        return this
    }

    fun putBundle(key: String, value: Bundle): RouteBuilder {
        this.bundle.putBundle(key, value)
        return this
    }


    fun putIntArray(key: String, value: IntArray): RouteBuilder {
        this.bundle.putIntArray(key, value)
        return this
    }


    fun putFloatArray(key: String, value: FloatArray): RouteBuilder {
        this.bundle.putFloatArray(key, value)
        return this
    }

    fun putCharArray(key: String, value: CharArray): RouteBuilder {
        this.bundle.putCharArray(key, value)
        return this
    }


    fun putLongArray(key: String, value: LongArray): RouteBuilder {
        this.bundle.putLongArray(key, value)
        return this
    }


    fun putDoubleArray(key: String, value: DoubleArray): RouteBuilder {
        this.bundle.putDoubleArray(key, value)
        return this
    }

    fun putByteArray(key: String, value: ByteArray): RouteBuilder {
        this.bundle.putByteArray(key, value)
        return this
    }

    fun putBooleanArray(key: String, value: BooleanArray): RouteBuilder {
        this.bundle.putBooleanArray(key, value)
        return this
    }

    fun putStringArray(key: String, value: Array<String>): RouteBuilder {
        this.bundle.putStringArray(key, value)
        return this
    }

    fun putCharSequence(key: String, value: CharSequence): RouteBuilder {
        this.bundle.putCharSequence(key, value)
        return this
    }

    fun putCharSequenceArray(key: String, value: Array<CharSequence>): RouteBuilder {
        this.bundle.putCharSequenceArray(key, value)
        return this
    }

    fun putStringArrayList(key: String, value: ArrayList<String>): RouteBuilder {
        this.bundle.putStringArrayList(key, value)
        return this
    }


    fun putParcelableArrayList(key: String, value: ArrayList<out Parcelable>): RouteBuilder {
        this.bundle.putParcelableArrayList(key, value)
        return this
    }

    fun putIntegerArrayList(key: String, value: ArrayList<Int>): RouteBuilder {
        this.bundle.putIntegerArrayList(key, value)
        return this
    }

    fun putSparseParcelableArray(key: String, value: SparseArray<out Parcelable>): RouteBuilder {
        this.bundle.putSparseParcelableArray(key, value)
        return this
    }
}
