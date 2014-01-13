package mobi.cangol.mobile.db;

public class QueryBuilder {
	private boolean distinct;
	private String table;
	private String orderBy;
	private String groupBy;
	private String having;
	
	private Long limit = null;
	private Long offset = null;
	
	private DatabaseTable mDbtable;
	
	public QueryBuilder(Class<?> clazz){
		
		mDbtable = clazz.getAnnotation(DatabaseTable.class);
		table=mDbtable.value();
	}
	
	
	public boolean isDistinct() {
		return distinct;
	}
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getHaving() {
		return having;
	}
	public void setHaving(String having) {
		this.having = having;
	}
	public Long getLimit() {
		return limit;
	}
	public void setLimit(Long limit) {
		this.limit = limit;
	}
	public Long getOffset() {
		return offset;
	}
	public void setOffset(Long offset) {
		this.offset = offset;
	}

}
