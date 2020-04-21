package demo.nopointer.ble.database.deviceuuid;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

public class DeviceUuidTable {
  @PrimaryKey(AssignType.BY_MYSELF)
  private String name;
  private String readOrNotifyCharaUUid;
  private String readOrNotifyServiceUUid;
  private String writeCharaUUid;
  private String writeServiceUUid;

  public DeviceUuidTable(String paramString) {
    this.name = paramString;
  }

  public String getName() {
    return this.name;
  }

  public String getReadOrNotifyCharaUUid() {
    return this.readOrNotifyCharaUUid;
  }

  public String getReadOrNotifyServiceUUid() {
    return this.readOrNotifyServiceUUid;
  }

  public String getWriteCharaUUid() {
    return this.writeCharaUUid;
  }

  public String getWriteServiceUUid() {
    return this.writeServiceUUid;
  }

  public void setName(String paramString) {
    this.name = paramString;
  }

  public void setReadOrNotifyCharaUUid(String paramString) {
    this.readOrNotifyCharaUUid = paramString;
  }

  public void setReadOrNotifyServiceUUid(String paramString) {
    this.readOrNotifyServiceUUid = paramString;
  }

  public void setWriteCharaUUid(String paramString) {
    this.writeCharaUUid = paramString;
  }

  public void setWriteServiceUUid(String paramString) {
    this.writeServiceUUid = paramString;
  }

  @Override
  public String toString() {
    return "DeviceUuidTable{" +
            "name='" + name + '\'' +
            ", readOrNotifyCharaUUid='" + readOrNotifyCharaUUid + '\'' +
            ", readOrNotifyServiceUUid='" + readOrNotifyServiceUUid + '\'' +
            ", writeCharaUUid='" + writeCharaUUid + '\'' +
            ", writeServiceUUid='" + writeServiceUUid + '\'' +
            '}';
  }
}