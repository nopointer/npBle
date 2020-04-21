package demo.nopointer.ble.database.deviceuuid;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

import demo.nopointer.ble.database.DbCfgUtil;

public class DeviceUuidServiceImpl
{
  private static final DeviceUuidServiceImpl ourInstance = new DeviceUuidServiceImpl();
  protected LiteOrm liteOrm = DbCfgUtil.getDbCfgUtil().getLiteOrm();
  
  public static DeviceUuidServiceImpl getInstance()
  {
    return ourInstance;
  }
  
  public DeviceUuidTable find(String name)
  {
    List<DeviceUuidTable> deviceUuidTables =liteOrm.query(QueryBuilder.create(DeviceUuidTable.class).where("name =?",name));
    if ((deviceUuidTables != null) && (deviceUuidTables.size() >= 1)) {
      return deviceUuidTables.get(0);
    }
    return null;
  }
  
  public void save(DeviceUuidTable paramDeviceUuidTable)
  {
    this.liteOrm.save(paramDeviceUuidTable);
  }
}