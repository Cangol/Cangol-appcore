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
package mobi.cangol.mobile.service

import mobi.cangol.mobile.logging.Log
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * TheadPool manager by name
 *
 * @author Cangol
 */
object PoolManager {

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = CPU_COUNT + 1
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
    private const val KEEP_ALIVE = 60
    private var poolMap: ConcurrentHashMap<String, Pool> = ConcurrentHashMap()

    /**
     * 获取一个线程池
     *
     * @param name
     * @return
     */
    fun getPool(name: String): Pool {
        if (!poolMap.containsKey(name)) {
            poolMap[name] = Pool(name, MAXIMUM_POOL_SIZE)
        }
        return poolMap[name]!!
    }

    /**
     * 创建一个线程池
     */
    fun buildPool(name: String, core: Int): Pool {
        if (!poolMap.containsKey(name)) {
            poolMap[name] = Pool(name, core)
        }
        var pool = poolMap[name]

        if (pool!!.isShutdown || pool.isTerminated || pool.isThreadPoolClose) {
            pool = Pool(name, core)
            poolMap[name] = pool
        }
        return pool
    }

    /**
     * 清除线程池
     */
    fun clear() {
        poolMap.clear()
    }

    /**
     * 停止所有线程池
     */
    fun closeAll() {
        for ((_, value) in poolMap) {
            value?.close(false)
        }
        poolMap.clear()
    }

    fun clear(name: String) {
        poolMap.remove(name)
    }

    class Pool {
        var executorService: ExecutorService? = null
        var isThreadPoolClose = false
        var name: String? = null

        val isTerminated: Boolean
            get() = this.executorService!!.isTerminated

        val isShutdown: Boolean
            get() = this.executorService!!.isShutdown

        internal constructor(name: String, core: Int) {
            this.name = name
            this.executorService = generateExecutorService(name, core)
            this.isThreadPoolClose = false
        }

        internal constructor(name: String) {
            this.name = name
            this.executorService = generateExecutorService(name)
            this.isThreadPoolClose = false
        }

        fun close(shutDownNow: Boolean) {
            if (shutDownNow)
                this.executorService!!.shutdownNow()
            else
                this.executorService!!.shutdown()

            this.isThreadPoolClose = true
            this.executorService = null
        }

        private fun generateExecutorService(name: String, core: Int): ExecutorService {

            return ThreadPoolExecutor(core, core * 2 + 1, KEEP_ALIVE.toLong(),
                    TimeUnit.SECONDS, LinkedBlockingQueue(), object : ThreadFactory {
                private val mCount = AtomicInteger(1)

                override fun newThread(r: Runnable): Thread {
                    return Thread(r, name + "\$WorkThread #" + mCount.getAndIncrement())
                }
            })
        }

        private fun generateExecutorService(name: String): ExecutorService {
            return ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE.toLong(),
                    TimeUnit.SECONDS, LinkedBlockingQueue(), object : ThreadFactory {
                private val mCount = AtomicInteger(1)

                override fun newThread(r: Runnable): Thread {
                    return Thread(r, name + "\$WorkThread #" + mCount.getAndIncrement())
                }
            })
        }

        fun submit(task: Runnable): Future<*> {
            Log.d(name!!, "submit Runnable")
            return this.executorService!!.submit(task)
        }

        fun <T> submit(task: Callable<T>): Future<T> {
            Log.d(name!!, "submit Callable")
            return this.executorService!!.submit(task)
        }

        fun <T> submit(task: Runnable, result: T): Future<T> {
            Log.d(name!!, "submit Runnable result")
            return this.executorService!!.submit(task, result)
        }

    }
}
