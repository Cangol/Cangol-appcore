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
package mobi.cangol.mobile.db;


import android.database.SQLException;

import java.util.Collection;
import java.util.List;

public interface Dao<T, I> {

    /**
     * 设置是否显示执行的sql语句
     *
     * @param showSql
     */
    void showSql(boolean showSql) throws SQLException;

    /**
     * 新建一个数据对象
     *
     * @param paramT
     * @return
     */
    int create(T paramT) throws SQLException;

    /**
     * 新建一组数据对象
     *
     * @param paramT
     * @return
     */
    int create(Collection<T> paramT) throws SQLException;

    /**
     * 条件查询
     *
     * @param queryBuilder
     * @return
     */
    List<T> query(QueryBuilder queryBuilder, String... columns) throws SQLException;

    /**
     * 根据K查询
     *
     * @param paramK
     * @return
     */
    T queryForId(I paramK, String... columns) throws SQLException;

    /**
     * 查询所有
     *
     * @return
     */
    List<T> queryForAll(String... columns) throws SQLException;

    /**
     * 刷新数据对象
     *
     * @param paramT
     * @return
     */
    T refresh(T paramT, String... columns) throws SQLException;

    /**
     * 更新数据
     *
     * @param updateBuilder
     * @return
     */
    int update(UpdateBuilder updateBuilder) throws SQLException;

    /**
     * 更新数据对象
     *
     * @param paramT
     * @return
     */
    int update(T paramT, String... column) throws SQLException;

    /**
     * 更新数据对象
     *
     * @param paramT
     * @param columns
     * @return
     */
    int update(Collection<T> paramT, String... columns) throws SQLException;

    /**
     * 更新数据对象根据
     *
     * @param paramT
     * @param paramID
     * @param columns
     * @return
     */
    int updateById(T paramT, I paramID, String... columns) throws SQLException;

    /**
     * 根据条件删除
     *
     * @param deleteBuilder
     * @return
     */
    int delete(DeleteBuilder deleteBuilder) throws SQLException;

    /**
     * 删除数据对象
     *
     * @param paramT
     * @return
     */
    int delete(T paramT) throws SQLException;

    /**
     * 删除数据对象集合
     *
     * @param paramCollection
     * @return
     */
    int delete(Collection<T> paramCollection) throws SQLException;

    /**
     * 更具ID删除
     *
     * @param paramID
     * @return
     */
    int deleteById(I paramID) throws SQLException;

    /**
     * 根据ID列表执行删除
     *
     * @param paramCollection
     * @return
     */
    int deleteByIds(Collection<I> paramCollection) throws SQLException;

    /**
     * 清空表删
     */
    int deleteAll() throws SQLException;

    /**
     * 获取实体类
     *
     * @return
     */
    Class<T> getEntityClass();

}
