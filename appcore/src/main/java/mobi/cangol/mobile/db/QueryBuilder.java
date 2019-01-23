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

import java.util.ArrayList;
import java.util.List;


public class QueryBuilder {
    private boolean distinctValue;
    private String table;
    private String orderByValue;
    private String groupByValue;
    private String havingValue;

    private Long limitValue = null;
    private Long offsetValue = null;


    private List<String> paraKey;
    private List<Object> paraValue;
    private List<String> condList;


    private DatabaseTable mDbtable;

    public QueryBuilder(Class<?> clazz) {

        mDbtable = clazz.getAnnotation(DatabaseTable.class);
        table = mDbtable.value();

        paraKey = new ArrayList<>();
        paraValue = new ArrayList<>();
        condList = new ArrayList<>();
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
        if (pName != null && !"".equals(pName) && pValue != null
            //&& !"".equals(String.valueOf(pValue))
                ) {
            if (pType.equals("is")) {
                paraKey.add(pName + " is ?");
                paraValue.add(pValue);
            } else if (pType.equals("isnot")) {
                paraKey.add(pName + " is not ?");
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
            } else if (pType.equals(">=")) {
                paraKey.add(pName + ">=?");
                paraValue.add(pValue);
            } else if (pType.equals("<=")) {
                paraKey.add(pName + "<=?");
                paraValue.add(pValue);
            } else {
                if (pType.indexOf('?') == -1) {
                    pType += "?";
                }
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
        if (pName != null && !"".equals(pName) && !"".equals(String.valueOf(pValue1)) && !"".equals(String.valueOf(pValue2))) {
            if (pType.equals("between")) {
                paraKey.add(pName + " between " + pValue1 + " and " + pValue2);
            }
            condList.add(isOr ? " or " : " and ");
        }
    }

    protected String getSelection() {
        StringBuilder sql = new StringBuilder();
        if (paraKey != null) {
            for (int i = 0; i < paraKey.size(); i++) {
                if (i > 0) {
                    sql.append(condList.get(i));
                }
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
            return new String[0];
        }

    }

    protected String getWhere() {
        StringBuilder sql = new StringBuilder();
        String str = null;
        if (paraKey != null) {
            for (int i = 0; i < paraKey.size(); i++) {
                if (i > 0) {
                    sql.append(condList.get(i));
                }
                str = paraKey.get(i);
                sql.append(str.replace("?", String.valueOf(paraValue.get(i))));
            }
            return sql.toString();
        } else {
            return null;
        }
    }


    protected boolean isDistinctValue() {
        return distinctValue;
    }

    /**
     * 设置distinct
     *
     * @param distinct
     */
    public void distinct(boolean distinct) {
        this.distinctValue = distinct;
    }

    protected String getTable() {
        return table;
    }

    protected String getOrderByValue() {
        return orderByValue;
    }

    /**
     * 设置 orderByValue
     *
     * @param orderBy
     */
    public void orderBy(String orderBy) {
        this.orderByValue = orderBy;
    }

    protected String getGroupByValue() {
        return groupByValue;
    }

    /**
     * 设置groupBy
     *
     * @param groupBy
     */
    public void groupBy(String groupBy) {
        this.groupByValue = groupBy;
    }

    protected String getHavingValue() {
        return havingValue;
    }

    /**
     * 设置having
     *
     * @param having
     */
    public void having(String having) {
        this.havingValue = having;
    }

    protected String getLimitValue() {
        if (limitValue != null) {
            if (offsetValue != null) {
                return offsetValue + "," + limitValue;
            }
            return "" + limitValue;
        } else {
            return null;
        }
    }

    /**
     * 设置limit
     *
     * @param limit
     */
    public void limit(long limit) {
        this.limitValue = limit;
    }

    /**
     * 设置offset
     *
     * @param offset
     */
    public void offset(long offset) {
        this.offsetValue = offset;
    }

}
