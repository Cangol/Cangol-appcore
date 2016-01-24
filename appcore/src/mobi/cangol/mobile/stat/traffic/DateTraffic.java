package mobi.cangol.mobile.stat.traffic;

import mobi.cangol.mobile.db.DatabaseField;
import mobi.cangol.mobile.db.DatabaseTable;

/**
 * Created by weixuewu on 16/1/23.
 */

@DatabaseTable("DATE_TRAFFIC")
public class DateTraffic{
    @DatabaseField(primaryKey=true,notNull=true)
    public int _id;
    @DatabaseField(value="status",notNull=true)
    public int status;
    @DatabaseField(value="date",notNull=true)
    public String date;
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
        return "DateTraffic{" +
                "_id=" + _id +
                ", status=" + status +
                ", date='" + date + '\'' +
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