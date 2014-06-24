package mobi.cangol.mobile.db;

import java.util.Collection;
import java.util.List;

import android.database.SQLException;

public abstract interface Dao<T, ID> {
	
	public abstract List<T> query(QueryBuilder queryBuilder)throws SQLException;
	
	public abstract T queryForId(ID paramID) throws SQLException;

	public abstract List<T> queryForAll() throws SQLException;

	public abstract int create(T paramT) throws SQLException;
	
	public abstract int refresh(T paramT) throws SQLException;

	public abstract int update(T paramT) throws SQLException;

	public abstract int updateId(T paramT, ID paramID) throws SQLException;
	
	public abstract int delete(DeleteBuilder deleteBuilder) throws SQLException;

	public abstract int delete(T paramT) throws SQLException;
	
	public abstract int delete(Collection<T> paramCollection)throws SQLException;

	public abstract int deleteById(ID paramID) throws SQLException;

	public abstract int deleteByIds(Collection<ID> paramCollection)throws SQLException;

	public abstract Class<T> getEntityClass();
	
}
