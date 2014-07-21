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
	public final static String CRASHSERVICE_THREAD_MAX="thread_max";
	public final static String CRASHSERVICE_THREADPOOL_NAME="threadpool_name";
	public final static String CRASHSERVICE_REPORT_URL="report_url";
	public final static String CRASHSERVICE_REPORT_ERROR="report_param_error";
	public final static String CRASHSERVICE_REPORT_POSITION="report_param_position";
	public final static String CRASHSERVICE_REPORT_CONTEXT="report_param_context";
	public final static String CRASHSERVICE_REPORT_TIMESTAMP="report_param_timestamp";
	public final static String CRASHSERVICE_REPORT_FATAL="report_param_fatal";
	
	void setReport(String url,Map<String,String> params);
	
	void report(CrashReportListener crashReportListener);
}
