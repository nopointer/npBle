package demo.nopointer.ble.sharedpreferences;

import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;

public class SharedPrefereceCycle
{
  public static void clear()
  {
    save(null);
  }
  
  public static String read()
  {
    return (String)SaveObjectUtils.getObject("cfg_cycle_time", String.class);
  }
  
  public static void save(String paramString)
  {
    SaveObjectUtils.setObject("cfg_cycle_time", paramString);
  }
}