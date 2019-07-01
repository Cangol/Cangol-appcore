package mobi.cangol.mobile.appcore.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import mobi.cangol.mobile.db.CoreSQLiteOpenHelper;
import mobi.cangol.mobile.db.Dao;
import mobi.cangol.mobile.db.DatabaseField;
import mobi.cangol.mobile.db.DatabaseTable;
import mobi.cangol.mobile.db.DatabaseUtils;
import mobi.cangol.mobile.db.QueryBuilder;
import mobi.cangol.mobile.logging.Log;

/**
 * Created by weixuewu on 16/4/30.
 */
public class DatabaseFragment extends Fragment {
    private static final String TAG = "DatabaseFragment";
    private ListView listView;
    private Button button0,button1, button2,button3;
    private DataService dataService;
    private AtomicInteger atomicInteger;
    private ArrayAdapter simpleAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.createDataBaseHelper(getActivity());
        dataService = new DataService(getActivity());
        atomicInteger = new AtomicInteger();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        listView = this.getView().findViewById(R.id.listview);
        button0 = this.getView().findViewById(R.id.button0);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        button1 = this.getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });
        button2 = this.getView().findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        button3 = this.getView().findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delData();
            }
        });
        simpleAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,new ArrayList<Data>());
        listView.setAdapter(simpleAdapter);

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadData() {
        simpleAdapter.clear();
        List<Data> list=dataService.findListByName("1");
        simpleAdapter.addAll(list);
    }
    private void addData() {
//        Data data=new Data("name_" + atomicInteger.addAndGet(1));
//        data.setNickname("nickname_"+new Random().nextInt(100));
//        dataService.save(data);
//        dataService.refresh(data);
//        simpleAdapter.add(data);
        List<Data> list=new ArrayList<>();
        int size=10;
        Data data=null;
        for (int i = 0; i < size; i++) {
            data=new Data("name_" + i);
            data.setNickname("nickname_"+i);
            list.add(data);
        }
        Log.e("idle start");
        long start=System.currentTimeMillis();
        dataService.createAll(list);
        Log.e("idle end "+(System.currentTimeMillis()-start)/size+"ms");
    }
    private void updateData() {
        List<Data> list=dataService.findListByName("1");
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setName(list.get(i).getName()+"a");
            list.get(i).setNickname(list.get(i).getNickname()+"a");
        }
        Log.e("idle start");
        long start=System.currentTimeMillis();
        dataService.updateAll(list,"name");
        Log.e("idle end "+(System.currentTimeMillis()-start)+"ms");
    }
    private void delData() {
        if(simpleAdapter.getCount()>0){
            Data data= (Data) simpleAdapter.getItem(simpleAdapter.getCount()-1);
            dataService.delete(data.getId());
            simpleAdapter.remove(data);
        }
    }
}

class DataService implements BaseService<Data> {
    private static final String TAG = "DataService";
    private Dao<Data, Integer> dao;

    public DataService(Context context) {
        try {
            DatabaseHelper dbHelper = DatabaseHelper
                    .createDataBaseHelper(context);
            dao = dbHelper.getDao(Data.class);
            dao.showSql(true);
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService init fail!");
        }
    }

    /**
     * @return the row ID of the newly inserted row or updated row, or -1 if an error occurred
     */
    @Override
    public int save(Data obj) {
        int result = -1;
        try {
            if (obj.getId() > 0 && obj.getId() != -1) {
                result = dao.update(obj);
                if (result > 0) {
                    result = obj.getId();
                }
            } else {
                result = dao.create(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService save fail!");
        }
        return result;
    }

    @Override
    public void refresh(Data obj) {
        try {
            dao.refresh(obj);
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService refresh fail!");
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService delete fail!");
        }

    }

    @Override
    public Data find(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService find fail!");
        }
        return null;
    }

    @Override
    public int getCount() {
        try {
            return dao.queryForAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService getCount fail!");
        }
        return -1;
    }

    public void updateAll(List<Data> list,String... columns) {
        try {
            dao.update(list,columns);
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService delete fail!");
        }

    }

    public List<Data> getAllList() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "DataService getAllList fail!");
        }
        return null;
    }
    public void createAll(List<Data> list) {
        try {
            dao.create(list);
        } catch (Exception e) {
            Log.e(TAG, "DataService createAll fail!" + e.getMessage());
        }
    }
    @Override
    public List<Data> findList(QueryBuilder queryBuilder) {
        return dao.query(queryBuilder);
    }

    public List<Data> findListByName(String name) {
        QueryBuilder queryBuilder = new QueryBuilder(Data.class);
        queryBuilder.addQuery("name", name, "like",true);
        queryBuilder.addQuery("nickname", name, "like",true);
        return dao.query(queryBuilder,"name");
    }

}

class DatabaseHelper extends CoreSQLiteOpenHelper {
    private static final String TAG = "DataBaseHelper";
    public static final String DATABASE_NAME = "demo";
    public static final int DATABASE_VERSION = 1;
    private static DatabaseHelper dataBaseHelper;

    public static DatabaseHelper createDataBaseHelper(Context context) {
        if (null == dataBaseHelper) {
            dataBaseHelper = new DatabaseHelper();
            dataBaseHelper.open(context);
        }
        return dataBaseHelper;
    }

    @Override
    public String getDataBaseName() {
        return DATABASE_NAME;
    }

    @Override
    public int getDataBaseVersion() {
        return DATABASE_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        DatabaseUtils.createTable(db, Data.class);
        DatabaseUtils.createIndex(db,Data.class,"teIndex","name","nickname");
        DatabaseUtils.addColumn(db,Data.class,"nickname1");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade " + oldVersion + "->" + newVersion);
    }

}

@DatabaseTable("TEST_DATA")
class Data {
    @DatabaseField(primaryKey = true, notNull = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String nickname;
    @DatabaseField
    private String nickname1;
    @DatabaseField
    private String nickname2;
    @DatabaseField
    private String nickname3;
    @DatabaseField
    private String nickname4;
    @DatabaseField
    private String nickname5;
    @DatabaseField
    private String nickname6;
    @DatabaseField
    private String nickname7;
    @DatabaseField
    private String nickname8;
    @DatabaseField
    private String nickname9;
    @DatabaseField
    private String nickname10;
    @DatabaseField
    private String nickname11;
    @DatabaseField
    private String nickname12;
    public Data() {
    }

    public Data(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname1() {
        return nickname1;
    }

    public void setNickname1(String nickname1) {
        this.nickname1 = nickname1;
    }

    public String getNickname2() {
        return nickname2;
    }

    public void setNickname2(String nickname2) {
        this.nickname2 = nickname2;
    }

    public String getNickname3() {
        return nickname3;
    }

    public void setNickname3(String nickname3) {
        this.nickname3 = nickname3;
    }

    public String getNickname4() {
        return nickname4;
    }

    public void setNickname4(String nickname4) {
        this.nickname4 = nickname4;
    }

    public String getNickname5() {
        return nickname5;
    }

    public void setNickname5(String nickname5) {
        this.nickname5 = nickname5;
    }

    public String getNickname6() {
        return nickname6;
    }

    public void setNickname6(String nickname6) {
        this.nickname6 = nickname6;
    }

    public String getNickname7() {
        return nickname7;
    }

    public void setNickname7(String nickname7) {
        this.nickname7 = nickname7;
    }

    public String getNickname8() {
        return nickname8;
    }

    public void setNickname8(String nickname8) {
        this.nickname8 = nickname8;
    }

    public String getNickname9() {
        return nickname9;
    }

    public void setNickname9(String nickname9) {
        this.nickname9 = nickname9;
    }

    public String getNickname10() {
        return nickname10;
    }

    public void setNickname10(String nickname10) {
        this.nickname10 = nickname10;
    }

    public String getNickname11() {
        return nickname11;
    }

    public void setNickname11(String nickname11) {
        this.nickname11 = nickname11;
    }

    public String getNickname12() {
        return nickname12;
    }

    public void setNickname12(String nickname12) {
        this.nickname12 = nickname12;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Data{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

interface BaseService<T> {
    /**
     * @param obj
     * @return
     */
    void refresh(T obj) throws SQLException;

    /**
     * @param id
     */
    void delete(Integer id) throws SQLException;

    /**
     * @param id
     * @return
     */
    T find(Integer id) throws SQLException;

    /**
     * @return
     */
    int getCount() throws SQLException;

    /**
     * @return
     */
    List<T> getAllList() throws SQLException;

    /**
     * @param obj
     */
    int save(T obj);

    /**
     * @param queryBuilder
     * @return
     */
    List<T> findList(QueryBuilder queryBuilder);
}