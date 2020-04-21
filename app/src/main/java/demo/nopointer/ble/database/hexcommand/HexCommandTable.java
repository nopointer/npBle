package demo.nopointer.ble.database.hexcommand;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

public class HexCommandTable
{
  @PrimaryKey(AssignType.BY_MYSELF)
  private long dataId;
  private String hexString;
  private boolean isSelect;
  @NotNull
  private String name;
  
  public HexCommandTable(long paramLong)
  {
    this.dataId = paramLong;
  }
  
  public long getDataId()
  {
    return this.dataId;
  }
  
  public String getHexString()
  {
    return this.hexString;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public boolean isSelect()
  {
    return this.isSelect;
  }
  
  public void setDataId(long paramLong)
  {
    this.dataId = paramLong;
  }
  
  public void setHexString(String paramString)
  {
    this.hexString = paramString;
  }
  
  public void setName(String paramString)
  {
    this.name = paramString;
  }
  
  public void setSelect(boolean paramBoolean)
  {
    this.isSelect = paramBoolean;
  }

  @Override
  public String toString() {
    return "HexCommandTable{" +
            "dataId=" + dataId +
            ", hexString='" + hexString + '\'' +
            ", isSelect=" + isSelect +
            ", name='" + name + '\'' +
            '}';
  }
}