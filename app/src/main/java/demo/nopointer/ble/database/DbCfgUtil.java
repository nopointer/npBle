package demo.nopointer.ble.database;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;

import demo.nopointer.ble.database.deviceuuid.DeviceUuidTable;
import demo.nopointer.ble.database.hexcommand.HexCommandTable;

public class DbCfgUtil
{
  private static DbCfgUtil dbCfgUtil = new DbCfgUtil();
  private LiteOrm liteOrm = null;
  
  public static DbCfgUtil getDbCfgUtil()
  {
    return dbCfgUtil;
  }
  
  public LiteOrm getLiteOrm()
  {
    return this.liteOrm;
  }
  
  public void init(Context context)
  {
    DataBaseConfig config = new DataBaseConfig(context);
    config.dbName = "appDB";
    config.debugged = true;
    config.dbVersion = 1;
    config.onUpdateListener = null;
    if (this.liteOrm == null) {
      this.liteOrm = LiteOrm.newSingleInstance(config);
    }
    this.liteOrm.openOrCreateDatabase();
    this.liteOrm.getTableManager().checkOrCreateTable(this.liteOrm.getReadableDatabase(), HexCommandTable.class);
    this.liteOrm.getTableManager().checkOrCreateTable(this.liteOrm.getReadableDatabase(), DeviceUuidTable.class);
  }
}