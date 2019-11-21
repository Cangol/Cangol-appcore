/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.crash

import android.app.Application
import android.os.StrictMode
import android.text.TextUtils
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.Task
import mobi.cangol.mobile.http.AsyncHttpClient
import mobi.cangol.mobile.http.AsyncHttpResponseHandler
import mobi.cangol.mobile.http.RequestParams
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.PoolManager
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.service.conf.ConfigService
import mobi.cangol.mobile.service.session.SessionService
import mobi.cangol.mobile.utils.DeviceInfo
import mobi.cangol.mobile.utils.FileUtils
import mobi.cangol.mobile.utils.Object2FileUtils
import mobi.cangol.mobile.utils.TimeUtils
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.util.*
import kotlin.system.exitProcess

/**
 * @author Cangol
 */
@Service("CrashService")
internal class CrashServiceImpl : CrashService, UncaughtExceptionHandler {
    private var debug = true
    private var mDefaultExceptionHandler: UncaughtExceptionHandler? = null
    private var mApplication: CoreApplication? = null
    private var mSessionService: SessionService? = null
    private var mServiceProperty = ServiceProperty(TAG)
    private var asyncHttpClient: AsyncHttpClient? = null
    private var mUrl: String? = null
    private var mParams: Map<String, String>? = null
    private var mCrashDir: String? = null

    override fun onCreate(context: Application) {
        mApplication = context as CoreApplication
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        mSessionService = mApplication?.getAppService(AppService.SESSION_SERVICE) as SessionService?
        val configService = mApplication!!.getAppService(AppService.CONFIG_SERVICE) as ConfigService?
        mCrashDir = configService?.getTempDir()?.absolutePath + File.separator + "crash"

        val oldPolicy = StrictMode.allowThreadDiskReads()
        FileUtils.newFolder(mCrashDir!!)
        StrictMode.setThreadPolicy(oldPolicy)
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
        PoolManager.buildPool(mServiceProperty.getString(CrashService.CRASHSERVICE_THREADPOOL_NAME)!!, mServiceProperty.getInt(CrashService.CRASHSERVICE_THREAD_MAX))
        asyncHttpClient = AsyncHttpClient.build(mServiceProperty.getString(CrashService.CRASHSERVICE_THREADPOOL_NAME)!!)
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        asyncHttpClient?.cancelRequests(mApplication!!, true)
    }

    override fun setDebug(mDebug: Boolean) {
        this.debug = mDebug
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        val sp = ServiceProperty(TAG)
        sp.putString(CrashService.CRASHSERVICE_THREADPOOL_NAME, TAG)
        sp.putInt(CrashService.CRASHSERVICE_THREAD_MAX, 1)
        sp.putString(CrashService.CRASHSERVICE_REPORT_URL, "")
        sp.putString(CrashService.CRASHSERVICE_REPORT_ERROR, "error")
        sp.putString(CrashService.CRASHSERVICE_REPORT_POSITION, "position")
        sp.putString(CrashService.CRASHSERVICE_REPORT_TIMESTAMP, "timestamp")
        sp.putString(CrashService.CRASHSERVICE_REPORT_CONTEXT, "content")
        sp.putString(CrashService.CRASHSERVICE_REPORT_FATAL, "fatal")
        return sp
    }

    override fun setReport(url: String, params: Map<String, String>?) {
        this.mUrl = url
        this.mParams = params
    }

    private fun report(report: ReportError) {
        if (debug) {
            Log.d(TAG, "report .crash " + report.path)
        }

        val params = if (this.mParams == null) RequestParams() else RequestParams(this.mParams!!)
        params.put(mServiceProperty.getString(CrashService.CRASHSERVICE_REPORT_ERROR), report.error)
        params.put(mServiceProperty.getString(CrashService.CRASHSERVICE_REPORT_POSITION), report.position)
        params.put(mServiceProperty.getString(CrashService.CRASHSERVICE_REPORT_CONTEXT), report.context)
        params.put(mServiceProperty.getString(CrashService.CRASHSERVICE_REPORT_TIMESTAMP), report.timestamp)
        params.put(mServiceProperty.getString(CrashService.CRASHSERVICE_REPORT_FATAL), report.fatal)
        asyncHttpClient?.post(mApplication!!, mUrl!!, params, object : AsyncHttpResponseHandler() {

            override fun onStart() {
                //do nothings
            }

            override fun onSuccess(content: String) {
                super.onSuccess(content)
                mApplication?.post(Runnable { FileUtils.delFile(report.path!!) })
            }

            override fun onFailure(error: Throwable, content: String) {
                //do nothings
            }

        })
    }

    private fun throwableToString(ex: Throwable): String {
        val writer = StringWriter()
        val pw = PrintWriter(writer)
        ex.printStackTrace(pw)
        pw.close()
        return writer.toString()
    }

    private fun makeReportError(ex: Throwable): ReportError {
        val timestamp = TimeUtils.getCurrentTime()
        val filename = timestamp.replace(" ".toRegex(), "").replace("-".toRegex(), "").replace(":".toRegex(), "")
        val error = ReportError()
        error.error = ex.toString()
        error.position = ex.stackTrace[0].toString()
        error.context = throwableToString(ex)
        error.timestamp = timestamp
        error.fatal = "0"
        error.path = mCrashDir + File.separator + filename + CRASH
        return error
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        Thread.setDefaultUncaughtExceptionHandler(mDefaultExceptionHandler)
        val error = makeReportError(ex)
        Log.e("AndroidRuntime", error.context)
        if (debug) {
            Log.d(TAG, "save .crash " + error.path!!)
        }
        Object2FileUtils.writeObject(error, error.path!!)

        mSessionService?.saveString("exitCode", "1")
        mSessionService?.saveString("exitVersion", DeviceInfo.getAppVersion(mApplication!!))
        //0 正常推退出  1异常退出
        exitProcess(0)
    }

    override fun report(crashReportListener: CrashReportListener) {
        mApplication?.post(object : Task<List<ReportError>>() {
            override fun call(): List<ReportError> {
                val files = FileUtils.searchBySuffix(File(mCrashDir), null, CRASH)

                val reports = ArrayList<ReportError>()
                var obj: Any?
                for (file in files) {
                    obj = FileUtils.readObject(file)
                    if (obj != null) {
                        reports.add(obj as ReportError)
                    } else {
                        FileUtils.delFile(file.absolutePath)
                    }
                }
                return reports
            }

            override fun result(result: List<ReportError>) {
                for (errorReport in result) {
                    if (!TextUtils.isEmpty(mUrl)) {
                        report(errorReport)
                    }
                    crashReportListener.report(errorReport.path,
                            errorReport.error,
                            errorReport.position,
                            errorReport.context,
                            errorReport.timestamp,
                            errorReport.fatal)
                }
            }
        })
    }


    internal class ReportError : java.io.Serializable {
        var error: String? = null
        var position: String? = null
        var context: String? = null
        var timestamp: String? = null
        var fatal: String? = null
        var path: String? = null

        companion object {
            private const val serialVersionUID = 0L
        }
    }

    companion object {
        private const val TAG = "CrashService"
        private const val CRASH = ".crash"
    }
}
