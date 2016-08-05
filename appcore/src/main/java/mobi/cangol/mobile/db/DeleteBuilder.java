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


public class DeleteBuilder {
    private String table;

    private List<String> paraKey;
    private List<Object> paraValue;
    private List<String> condList;


    private DatabaseTable mDbtable;

    public DeleteBuilder(Class<?> clazz) {

        mDbtable = clazz.getAnnotation(DatabaseTable.class);
        table = mDbtable.value();

        paraKey = new ArrayList<String>();
        paraValue = new ArrayList<Object>();
        condList = new ArrayList<String>();
    }

    /**
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
        if (pName != null && !"".equals(pName) && pValue != null && !"".equals(String.valueOf(pValue))) {
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
     * 添加查询条件
     *
     * @param pName   字段名称
     * @param pValue1 字段值
     * @param pValue2 字段值
     * @param pType   查询类型{}
     * @param isOr
     */
    public void addQuery(String pName, Object pValue1, Object pValue2, String pType, boolean isOr) {
        if (pName != null && !"".equals(pName) && pValue1 != null && pValue2 != null && !"".equals(String.valueOf(pValue1)) && !"".equals(String.valueOf(pValue2))) {
            if (pType.equals("between")) {
                paraKey.add(pName + " between " + pValue1 + " and " + pValue2);
            }
            condList.add(isOr ? " or " : " and ");
        }
    }

    protected String getWhere() {
        StringBuffer sql = new StringBuffer();
        if (paraKey != null) {
            for (int i = 0; i < paraKey.size(); i++) {
                if (i == 0) {
                    //sql.append(" where ");
                    ;
                } else {
                    sql.append(condList.get(i));
                }

                sql.append(paraKey.get(i));
            }
            return sql.toString();
        } else {
            return null;
        }
    }

    protected String[] getWhereArgs() {
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

    protected String getTable() {
        return table;
    }


}
