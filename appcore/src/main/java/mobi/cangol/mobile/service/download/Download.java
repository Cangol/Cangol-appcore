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
package mobi.cangol.mobile.service.download;

public interface Download {
	enum DownloadType{
		APK,//下载完会提示安装
		OTHER,
	}
	//下载文件后缀
	public static final String SUFFIX_SOURCE=".tmp";
	//配置文件后缀
	public static final String SUFFIX_CONFIG=".conf";
	//正在等待
	public static final int STATUS_WAIT	    = 0;
	//正在下载
	public static final int STATUS_START	= 1;
	//暂停
	public static final int STATUS_STOP 	= 2;
	//重新下载
	public static final int STATUS_RERUN 	= 3;
	//下载完成
	public static final int STATUS_FINISH	= 4;
	//出错
	public static final int STATUS_FAILURE 	= 5;
	
	
	public final static int TYPE_DOWNLOAD_START= 0;
	
	public final static int TYPE_DOWNLOAD_FINISH= 1;
	
	public final static int TYPE_DOWNLOAD_FAILED = 2;
	
	public final static int TYPE_DOWNLOAD_UPDATE = 3;
	
	public final static int TYPE_DOWNLOAD_DELETE = 5;
	
	//public final static int TYPE_DOWNLOAD_UPDATEFILE = 6;
	
	public final static int TYPE_DOWNLOAD_STOP = 7;
	
	//public final static int TYPE_DOWNLOAD_INSTALL = 8;
	
	public final static int TYPE_DOWNLOAD_CONTINUE = 9;
	
}
