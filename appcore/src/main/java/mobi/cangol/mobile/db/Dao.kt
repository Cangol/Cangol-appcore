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
package mobi.cangol.mobile.db


import android.database.SQLException

interface Dao<T, I> {
    /**
     * 设置是否显示执行的sql语句
     *
     * @param showSql
     */
    @Throws(SQLException::class)
    fun showSql(showSql: Boolean)

    /**
     * 新建一个数据对象
     *
     * @param paramT
     * @return
     */
    @Throws(SQLException::class)
    fun create(paramT: T): Int

    /**
     * 新建一组数据对象
     *
     * @param paramT
     * @return
     */
    @Throws(SQLException::class)
    fun create(paramT: Collection<T>): Int

    /**
     * 条件查询
     *
     * @param queryBuilder
     * @return
     */
    @Throws(SQLException::class)
    fun query(queryBuilder: QueryBuilder, vararg columns: String): List<T>

    /**
     * 根据K查询
     *
     * @param paramK
     * @return
     */
    @Throws(SQLException::class)
    fun queryForId(paramK: I, vararg columns: String): T?

    /**
     * 查询所有
     *
     * @return
     */
    @Throws(SQLException::class)
    fun queryForAll(vararg columns: String): List<T>

    /**
     * 刷新数据对象
     *
     * @param paramT
     * @return
     */
    @Throws(SQLException::class)
    fun refresh(paramT: T, vararg columns: String): T?

    /**
     * 更新数据
     *
     * @param updateBuilder
     * @return
     */
    @Throws(SQLException::class)
    fun update(updateBuilder: UpdateBuilder): Int

    /**
     * 更新数据对象
     *
     * @param paramT
     * @return
     */
    @Throws(SQLException::class)
    fun update(paramT: T, vararg column: String): Int

    /**
     * 更新数据对象
     *
     * @param paramT
     * @param columns
     * @return
     */
    @Throws(SQLException::class)
    fun update(paramT: Collection<T>, vararg columns: String): Int

    /**
     * 更新数据对象根据
     *
     * @param paramT
     * @param paramID
     * @param columns
     * @return
     */
    @Throws(SQLException::class)
    fun updateById(paramT: T, paramID: I, vararg columns: String): Int

    /**
     * 根据条件删除
     *
     * @param deleteBuilder
     * @return
     */
    @Throws(SQLException::class)
    fun delete(deleteBuilder: DeleteBuilder): Int

    /**
     * 删除数据对象
     *
     * @param paramT
     * @return
     */
    @Throws(SQLException::class)
    fun delete(paramT: T): Int

    /**
     * 删除数据对象集合
     *
     * @param paramCollection
     * @return
     */
    @Throws(SQLException::class)
    fun delete(paramCollection: Collection<T>): Int

    /**
     * 更具ID删除
     *
     * @param paramID
     * @return
     */
    @Throws(SQLException::class)
    fun deleteById(paramID: I): Int

    /**
     * 根据ID列表执行删除
     *
     * @param paramCollection
     * @return
     */
    @Throws(SQLException::class)
    fun deleteByIds(paramCollection: Collection<I>): Int

    /**
     * 清空表删
     */
    @Throws(SQLException::class)
    fun deleteAll(): Int

    /**
     * 获取实体类
     *
     * @return
     */
    fun getEntityClass(): Class<T>
}
