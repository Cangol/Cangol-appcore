package mobi.cangol.mobile.db;

import java.util.ArrayList;
import java.util.List;


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

    public QueryBuilder(Class<?> clazz) {

        mDbtable = clazz.getAnnotation(DatabaseTable.class);
        table = mDbtable.value();

        paraKey = new ArrayList<String>();
        paraValue = new ArrayList<Object>();
        condList = new ArrayList<String>();
    }

    /**
     * 添加查询条件
     *
     * @param pName  字段名称
     * @param pValue 字段值
     * @param pType  查询类型{}
     */
    public void addQuery(String pName, Object pValue, String pType) {
        addQuery(pName, pValue, pType, false);
    }

    /**
     * 添加查询条件
     *
     * @param pName  字段名称
     * @param pValue 字段值
     * @param pType  查询类型{}
     * @param isOr
     */
    public void addQuery(String pName, Object pValue, String pType, boolean isOr) {
        if (pName != null && !pName.toString().equals("") && pValue != null && !pValue.toString().equals("")) {
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

    /**
     * 添加between条件
     *
     * @param pName
     * @param pValue1
     * @param pValue2
     * @param pType
     * @param isOr
     */
    public void addQuery(String pName, Object pValue1, Object pValue2, String pType, boolean isOr) {
        if (pName != null && !pName.toString().equals("") && !pValue1.toString().equals("") && !pValue2.toString().equals("")) {
            if (pType.equals("between")) {
                paraKey.add(pName + " between " + pValue1 + " and " + pValue2);
            }
            condList.add(isOr ? " or " : " and ");
        }
    }

    protected String getSelection() {
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
        } else {
            return null;
        }
    }

    protected String[] getSelectionArgs() {
        if (paraValue != null) {
            String[] args = new String[paraValue.size()];
            for (int i = 0; i < paraValue.size(); i++) {
                paraKey.get(i);
                args[i] = String.valueOf(paraValue.get(i));
            }
            return args;
        } else {
            return null;
        }

    }

    protected boolean isDistinct() {
        return distinct;
    }

    /**
     * 设置distinct
     *
     * @param distinct
     */
    public void distinct(boolean distinct) {
        this.distinct = distinct;
    }

    protected String getTable() {
        return table;
    }

    protected String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置 orderBy
     *
     * @param orderBy
     */
    public void orderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    protected String getGroupBy() {
        return groupBy;
    }

    /**
     * 设置groupBy
     *
     * @param groupBy
     */
    public void groupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    protected String getHaving() {
        return having;
    }

    /**
     * 设置having
     *
     * @param having
     */
    public void having(String having) {
        this.having = having;
    }

    protected String getLimit() {
        if (limit != null) {
            if (offset != null) {
                return offset + "," + limit;
            }
            return "" + limit;
        } else
            return null;
    }

    /**
     * 设置limit
     *
     * @param limit
     */
    public void limit(long limit) {
        this.limit = limit;
    }

    /**
     * 设置offset
     *
     * @param offset
     */
    public void offset(long offset) {
        this.offset = offset;
    }

}
