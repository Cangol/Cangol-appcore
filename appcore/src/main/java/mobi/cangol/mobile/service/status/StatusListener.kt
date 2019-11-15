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
package mobi.cangol.mobile.service.status

import android.content.Context

interface StatusListener {
    /**
     * 网络已连接
     */
    fun networkConnect(context: Context)

    /**
     * 网络连接中断
     */
    fun networkDisconnect(context: Context)

    /**
     * 连接到手机网络
     */
    fun networkTo3G(context: Context)

    /**
     * 外置存储移除
     */
    fun storageRemove(context: Context)

    /**
     * 外置存储挂载
     */
    fun storageMount(context: Context)

    /**
     * 呼叫状态
     */
    fun callStateIdle()

    /**
     * 挂起状态
     */
    fun callStateOffhook()

    /**
     * 响铃状态
     */
    fun callStateRinging()
}
