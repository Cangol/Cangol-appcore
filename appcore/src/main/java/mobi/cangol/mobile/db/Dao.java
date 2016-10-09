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

public abstract interface Dao<T, ID> {
    /**
     * 条件查询
     *
     * @param queryBuilder
     * @return
     */
    public abstract List<T> query(QueryBuilder queryBuilder) throws SQLException;

    /**
     * 根据K查询
     *
     * @param paramK
     * @return
     */
    public abstract T queryForId(ID paramK) throws SQLException;

    /**
     * 查询所有
     *
     * @return
     */
    public abstract List<T> queryForAll() throws SQLException;

    /**
     * 新建一个数据对象
     *
     * @param paramT
     * @return
     */
    public abstract int create(T paramT) throws SQLException;

    /**
     * 新建一组数据对象
     *
     * @param paramT
     * @return
     */
    public abstract int create(Collection<T> paramT) throws SQLException;

    /**
     * 刷新数据对象
     *
     * @param paramT
     * @return
     */
    public abstract T refresh(T paramT) throws SQLException;

    /**
     * 更新数据对象
     *
     * @param paramT
     * @return
     */
    public abstract int update(T paramT) throws SQLException;

    /**
     * 更新数据对象
     *
     * @param paramT
     * @return
     */
    public abstract int update(Collection<T> paramT) throws SQLException;

    /**
     * 更新数据对象根据
     *
     * @param paramT
     * @param paramID
     * @return
     */
    public abstract int updateById(T paramT, ID paramID) throws SQLException;

    /**
     * 根据条件删除
     *
     * @param deleteBuilder
     * @return
     */
    public abstract int delete(DeleteBuilder deleteBuilder) throws SQLException;

    /**
     * 删除数据对象
     *
     * @param paramT
     * @return
     */
    public abstract int delete(T paramT) throws SQLException;

    /**
     * 删除数据对象集合
     *
     * @param paramCollection
     * @return
     */
    public abstract int delete(Collection<T> paramCollection) throws SQLException;

    /**
     * 更具K删除
     *
     * @param paramID
     * @return
     */
    public abstract int deleteById(ID paramID) throws SQLException;

    /**
     * 根据K列表执行删除
     *
     * @param paramCollection
     * @return
     */
    public abstract int deleteByIds(Collection<ID> paramCollection) throws SQLException;

    /**
     * 清空表删
     *
     */
    public abstract int deleteAll() throws SQLException;

    /**
     * 获取实体类
     *
     * @return
     */
    public abstract Class<T> getEntityClass();

}
