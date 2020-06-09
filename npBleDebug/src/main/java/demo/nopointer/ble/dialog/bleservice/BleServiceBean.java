package demo.nopointer.ble.dialog.bleservice;

import java.util.List;

public class BleServiceBean
{
  private List<BleServiceBean> charaBeanList = null;
  private boolean isExpanded;
  private int type;
  private String uuid;
  
  public BleServiceBean() {}
  
  public BleServiceBean(String paramString)
  {
    this.uuid = paramString;
  }
  
  public List<BleServiceBean> getCharaBeanList()
  {
    return this.charaBeanList;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public String getUuid()
  {
    return this.uuid;
  }
  
  public boolean isExpanded()
  {
    return this.isExpanded;
  }
  
  public void setCharaBeanList(List<BleServiceBean> paramList)
  {
    this.charaBeanList = paramList;
  }
  
  public void setExpanded(boolean paramBoolean)
  {
    this.isExpanded = paramBoolean;
  }
  
  public void setType(int paramInt)
  {
    this.type = paramInt;
  }
  
  public void setUuid(String paramString)
  {
    this.uuid = paramString;
  }
}