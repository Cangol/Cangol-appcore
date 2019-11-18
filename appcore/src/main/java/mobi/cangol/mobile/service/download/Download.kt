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
package mobi.cangol.mobile.service.download

object Download {

    //下载文件后缀
    const val SUFFIX_SOURCE = ".tmp"
    //配置文件后缀
    const val SUFFIX_CONFIG = ".conf"
    //正在等待
    const val STATUS_WAIT = 0
    //正在下载
    const val STATUS_START = 1
    //暂停
    const val STATUS_STOP = 2
    //重新下载
    const val STATUS_RERUN = 3
    //下载完成
    const val STATUS_FINISH = 4
    //出错
    const val STATUS_FAILURE = 5


    const val ACTION_DOWNLOAD_START = 0

    const val ACTION_DOWNLOAD_FINISH = 1

    const val ACTION_DOWNLOAD_FAILED = 2

    const val ACTION_DOWNLOAD_UPDATE = 3

    const val ACTION_DOWNLOAD_DELETE = 5

    /**public static final int ACTION_DOWNLOAD_UPDATEFILE = 6; */
    const val ACTION_DOWNLOAD_STOP = 7

    /**public static final int ACTION_DOWNLOAD_INSTALL = 8; */

    const val ACTION_DOWNLOAD_CONTINUE = 9

}
