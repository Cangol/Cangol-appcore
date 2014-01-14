package mobi.cangol.mobile.db;

import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.utils.StringUtils;

public class QueryBuilder {
	private boolean distinct;
	private String table;
	private String orderBy;
	private String groupBy;
	private String having;
	
	private Long limit = null;
	private Long offset = null;
	
	
	private List<String> paraKey;
	private List<Object> paraValue; 
	private List<String> condList;
	
	
	private DatabaseTable mDbtable;
	
	public QueryBuilder(Class<?> clazz){
		
		mDbtable = clazz.getAnnotation(DatabaseTable.class);
		table=mDbtable.value();
		
		paraKey = new ArrayList<String>();
		paraValue = new ArrayList<Object>();
		condList = new ArrayList<String>();
	}
	public void addQuery(String pName, Object pValue, String pType) {
		addQuery(pName, pValue, pType, false);
	}
	
	/**
	 * 添加查询条件
	 * @param pName 字段名称
	 * @param pValue 字段值
	 * @param pType 查询类型{}
	 * @param isOr
	 */
	public void addQuery(String pName, Object pValue, String pType, boolean isOr) {
		if (StringUtils.isNotBlank(pName) && pValue != null && !pValue.toString().equals("")) {
			if (StringUtils.isBlank(pType)) {
				paraKey.add(pName + "=?");
				paraValue.add(pValue);
			} else if (pType.equals("like")) {
				paraKey.add(pName + " like ?");
				paraValue.add("%" + pValue + "%");
			} else if (pType.equals("blike")) {
				paraKey.add(pName + " like ?");
				paraValue.add("%" + pValue);
			} else if (pType.equals("elike")) {
				paraKey.add(pName + " like ?");
				paraValue.add(pValue + "%");
			} else if (pType.equals("in")) {
				//in查询无法用占位符，只能直接拼成sql
				paraKey.add(pName + " in(" + pValue + ")");
			} else if (pType.equals("=")) {
				paraKey.add(pName + "=?");
				paraValue.add(pValue);
			} else if (pType.equals(">")) {
				paraKey.add(pName + ">?");
				paraValue.add(pValue);
			} else if (pType.equals("<")) {
				paraKey.add(pName + "<?");
				paraValue.add(pValue);
			} else if (pType.equals("<>")) {
				paraKey.add(pName + "<>?");
				paraValue.add(pValue);
			} else if (pType.equals("!=")) {
				paraKey.add(pName + "!=?");
				paraValue.add(pValue);
			} else {
				if (pType.indexOf("?") == -1)
					pType += "?";
				paraKey.add(pName + pType);
				paraValue.add(pValue);
			}
			condList.add(isOr ? " or " : " and ");
		}
	}
	
	public void addQuery(String pName, Object pValue1,Object pValue2 ,String pType,boolean isOr) {
		if (StringUtils.isNotBlank(pName) && pValue1 != null && pValue2!= null && !pValue1.toString().equals("") && !pValue2.toString().equals("")) {
			if (pType.equals("between")) {
				paraKey.add(pName + " between " + pValue1 + " and " +pValue2);
			}
			condList.add(isOr ? " or " : " and ");
		}
	}
	public String getSelection() {
		StringBuffer sql = new StringBuffer();
    	if (paraKey != null) {
	    	for (int i = 0; i < paraKey.size(); i++) {
	    		if (i == 0)
	    			//sql.append(" where ");
	    			;
	    		else
	    			sql.append(" and ");
	    		sql.append(paraKey.get(i));
	    	}
	    	return sql.toString();
    	}else{
    		return null;
    	}
	}
	
	public String[] getSelectionArgs() {
		if(paraValue!=null){
			String[] args=new String[paraValue.size()];
			for (int i = 0; i < paraValue.size(); i++) {
				paraKey.get(i);
				args[i]=String.valueOf(paraValue.get(i));
			}
			return args;
		}else{
			return null;
		}
		
	}
	
	public boolean isDistinct() {
		return distinct;
	}
	public void distinct(boolean distinct) {
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
	public void orderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void groupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getHaving() {
		return having;
	}
	public void having(String having) {
		this.having = having;
	}
	public String getLimit() {
		if(limit!=null&&offset!=null){
			return "LIMIT "+limit+" OFFSET "+offset;
		}else
			return null;
	}
	public void limit(Long limit) {
		this.limit = limit;
	}

	public void offset(Long offset) {
		this.offset = offset;
	}

}
