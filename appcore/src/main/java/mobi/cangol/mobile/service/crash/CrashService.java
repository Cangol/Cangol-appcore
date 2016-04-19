/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.crash;

import java.util.Map;

import mobi.cangol.mobile.service.AppService;

public interface CrashService  extends AppService{
	/**
	 * 并发线程数
	 */
	public final static String CRASHSERVICE_THREAD_MAX="thread_max";
	/**
	 * 线程池名称
	 */
	public final static String CRASHSERVICE_THREADPOOL_NAME="threadpool_name";
	/**
	 * 报告url
	 */
	public final static String CRASHSERVICE_REPORT_URL="report_url";
	/**
	 * 报告错误参数
	 */
	public final static String CRASHSERVICE_REPORT_ERROR="report_param_error";
	/**
	 * 报告错误位置参数
	 */
	public final static String CRASHSERVICE_REPORT_POSITION="report_param_position";
	/**
	 * 报告错误内容参数
	 */
	public final static String CRASHSERVICE_REPORT_CONTEXT="report_param_context";
	/**
	 * 报告错误时间参数
	 */
	public final static String CRASHSERVICE_REPORT_TIMESTAMP="report_param_timestamp";
	/**
	 * 报告错误是否致命参数
	 */
	public final static String CRASHSERVICE_REPORT_FATAL="report_param_fatal";
	/**
	 * 追加异常报告的参数
	 * @param url
	 * @param params
	 */
	void setReport(String url,Map<String,String> params);
	/**
	 * 注册异常报告监听
	 * @param crashReportListener
	 */
	void report(CrashReportListener crashReportListener);
}
