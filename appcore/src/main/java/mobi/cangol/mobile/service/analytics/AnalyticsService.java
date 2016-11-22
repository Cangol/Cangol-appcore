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
package mobi.cangol.mobile.service.analytics;

import mobi.cangol.mobile.service.AppService;

public interface AnalyticsService extends AppService {
    /**
     * 并发线程数
     */
    String ANALYTICSSERVICE_THREAD_MAX = "thread_max";
    /**
     * 线程池名称
     */
    String ANALYTICSSERVICE_THREADPOOL_NAME = "threadpool_name";

    /**
     * 获取一个统计跟踪器
     *
     * @param trackingId
     * @return
     */
    ITracker getTracker(String trackingId);

    /**
     * 关闭统计跟踪器
     */
    void closeTracker(String trackingId);
}
