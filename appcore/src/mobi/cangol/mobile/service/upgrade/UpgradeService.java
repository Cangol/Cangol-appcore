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
package mobi.cangol.mobile.service.upgrade;

import mobi.cangol.mobile.service.AppService;

public interface UpgradeService extends AppService {

	/**
	 * 跟新类型枚举
	 * @author Cangol
	 *
	 */
	public enum UpgradeType {
		APK, // apk升级
		DEX, // dex升级
		RES, // res升级
		SO// so库升级
	}

	/**
	 * 更新资源
	 * 
	 * @param name
	 * @param url
	 * @param load
	 */
	void upgradeRes(String name, String url, boolean load);

	/**
	 * 更新dex
	 * 
	 * @param name
	 * @param url
	 * @param launch
	 */
	void upgradeDex(String name, String url, boolean launch);

	/**
	 * 更新so
	 * 
	 * @param name
	 * @param url
	 * @param load
	 */
	void upgradeSo(String name, String url, boolean load);

	/**
	 * 更新插件apk
	 * 
	 * @param name
	 * @param url
	 * @param install
	 */
	void upgradeApk(String name, String url, boolean install);

	/**
	 * 更新主程序
	 * 
	 * @param name
	 * @param url
	 * @param constraint
	 */
	void upgrade(String name, String url, boolean constraint);

	/**
	 * 设置更新监听接口
	 * 
	 * @param upgradeListener
	 */
	void setOnUpgradeListener(UpgradeListener upgradeListener);
}
