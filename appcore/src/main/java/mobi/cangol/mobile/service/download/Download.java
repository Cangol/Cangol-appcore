/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.download;

public interface Download {
    //下载文件后缀
    String SUFFIX_SOURCE = ".tmp";
    //配置文件后缀
    String SUFFIX_CONFIG = ".conf";
    //正在等待
    int STATUS_WAIT = 0;
    //正在下载
    int STATUS_START = 1;
    //暂停
    int STATUS_STOP = 2;
    //重新下载
    int STATUS_RERUN = 3;
    //下载完成
    int STATUS_FINISH = 4;
    //出错
    int STATUS_FAILURE = 5;


    int ACTION_DOWNLOAD_START = 0;

    int ACTION_DOWNLOAD_FINISH = 1;

    int ACTION_DOWNLOAD_FAILED = 2;

    int ACTION_DOWNLOAD_UPDATE = 3;

    int ACTION_DOWNLOAD_DELETE = 5;

    //int ACTION_DOWNLOAD_UPDATEFILE = 6;

    int ACTION_DOWNLOAD_STOP = 7;

    //int ACTION_DOWNLOAD_INSTALL = 8;

    int ACTION_DOWNLOAD_CONTINUE = 9;

}
