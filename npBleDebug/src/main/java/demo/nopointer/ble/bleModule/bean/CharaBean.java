package demo.nopointer.ble.bleModule.bean;

import java.util.UUID;

public class CharaBean
{
  private UUID charaUUid;
  private UUID serviceUUId;
  
  public CharaBean(String paramString1, String paramString2)
  {
    this.serviceUUId = UUID.fromString(paramString1);
    this.charaUUid = UUID.fromString(paramString2);
  }
  
  public CharaBean(UUID paramUUID1, UUID paramUUID2)
  {
    this.serviceUUId = paramUUID1;
    this.charaUUid = paramUUID2;
  }
  
  public UUID getCharaUUid()
  {
    return this.charaUUid;
  }
  
  public UUID getServiceUUId()
  {
    return this.serviceUUId;
  }
  
  public void setCharaUUid(UUID paramUUID)
  {
    this.charaUUid = paramUUID;
  }
  
  public void setServiceUUId(UUID paramUUID)
  {
    this.serviceUUId = paramUUID;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CharaBean{serviceUUId=");
    localStringBuilder.append(this.serviceUUId);
    localStringBuilder.append(", charaUUid=");
    localStringBuilder.append(this.charaUUid);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}