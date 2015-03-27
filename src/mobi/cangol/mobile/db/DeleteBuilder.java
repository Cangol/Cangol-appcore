package mobi.cangol.mobile.db;

import java.util.ArrayList;
import java.util.List;


public class DeleteBuilder {
	private String table;
	
	private List<String> paraKey;
	private List<Object> paraValue; 
	private List<String> condList;
	
	
	private DatabaseTable mDbtable;
	
	public DeleteBuilder(Class<?> clazz){
		
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
		if (pName!= null && !pName.toString().equals("") && pValue != null && !pValue.toString().equals("")) {
			if (pValue == null || pValue.toString().trim().equals("")) {
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
		if (pName!= null && !pName.toString().equals("") && pValue1 != null && pValue2!= null && !pValue1.toString().equals("") && !pValue2.toString().equals("")) {
			if (pType.equals("between")) {
				paraKey.add(pName + " between " + pValue1 + " and " +pValue2);
			}
			condList.add(isOr ? " or " : " and ");
		}
	}
	public String getWhere() {
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
	
	public String[] getWhereArgs() {
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
	public String getTable() {
		return table;
	}
	
	
}
