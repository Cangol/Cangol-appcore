package mobi.cangol.mobile.stat.traffic;

import mobi.cangol.mobile.db.DatabaseField;
import mobi.cangol.mobile.db.DatabaseTable;

/**
 * Created by weixuewu on 16/1/23.
 */
@DatabaseTable("APP_TRAFFIC")
public class AppTraffic{
    @DatabaseField(primaryKey=true,notNull=true)
    public int _id;
    @DatabaseField(value="package_name",notNull=true)
    public String packageName;
    @DatabaseField(value="uid",notNull=true)
    public int uid;
    @DatabaseField(value="total_rx",notNull=true)
    public long totalRx;
    @DatabaseField(value="total_tx",notNull=true)
    public long totalTx;
    @DatabaseField(value="mobile_rx",notNull=true)
    public long mobileRx;
    @DatabaseField(value="mobile_tx",notNull=true)
    public long mobileTx;
    @DatabaseField(value="wifi_rx",notNull=true)
    public long wifiRx;
    @DatabaseField(value="wifi_tx",notNull=true)
    public long wifiTx;

    @Override
    public String toString() {
        return "AppTraffic{" +
                "_id=" + _id +
                ", packageName='" + packageName + '\'' +
                ", uid=" + uid +
                ", totalRx=" + totalRx +
                ", totalTx=" + totalTx +
                ", mobileRx=" + mobileRx +
                ", mobileTx=" + mobileTx +
                ", wifiRx=" + wifiRx +
                ", wifiTx=" + wifiTx +
                '}';
    }
}