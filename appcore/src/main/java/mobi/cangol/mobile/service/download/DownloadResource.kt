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

import java.io.File
import java.io.Serializable
import java.util.*


class DownloadResource : Serializable {
    //下载的URL
    var url: String?=null
    //下载的文件名
    var fileName: String?=null
    //文件长度
    var fileLength: Long = 0
    //已完成长度
    var completeSize: Long = 0
    //进度
    var progress: Int = 0
    //速度 b/s
    var speed: Long = 0
    //开始,暂停,安装,运行
    var status: Int = 0
    //本地文件更目录
    var localPath: String?=null
    //异常说明
    var exception: String?=null
    //源文件地址
    var sourceFile: String?=null
    //配置地址
    var confFile: String?=null
    //下载对象 json 字符串
    var `object`: String?=null
    //唯一标示
    var key: String?=null

    @Transient
    var downloadTask: DownloadTask?=null

    @Transient
    protected var viewHolders: HashMap<Any, BaseViewHolder>? = HashMap()

    constructor() {}

    constructor(localPath: String, url: String, fileName: String) {
        this.url = url
        this.fileName = fileName
        this.key = url
        this.localPath = localPath
        this.sourceFile = localPath + File.separator + fileName + Download.SUFFIX_SOURCE
        this.confFile = localPath + File.separator + fileName + Download.SUFFIX_CONFIG
    }

    fun getViewHolder(obj: Any): BaseViewHolder? {
        return viewHolders!![obj]
    }

    fun setViewHolder(obj: Any, viewHolder: BaseViewHolder) {
        if (viewHolders == null) {
            viewHolders = HashMap()
        }
        this.viewHolders!![obj] = viewHolder
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as DownloadResource?

        return key == that!!.key

    }

    fun reset() {
        completeSize = 0
        progress = 0
        speed = 0
        status = 0
    }

    companion object {
        private const val serialVersionUID = 0L
    }
}
