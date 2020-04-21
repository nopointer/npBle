package demo.nopointer.ble.database.hexcommand;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;

import demo.nopointer.ble.database.DbCfgUtil;

public class HexCommandServiceImpl
{
  private static final HexCommandServiceImpl ourInstance = new HexCommandServiceImpl();
  protected LiteOrm liteOrm = DbCfgUtil.getDbCfgUtil().getLiteOrm();
  
  public static HexCommandServiceImpl getInstance()
  {
    return ourInstance;
  }
  
  public void delete(long paramLong)
  {
    this.liteOrm.delete(WhereBuilder.create(HexCommandTable.class).where("dataId =?", new Object[] { Long.valueOf(paramLong) }));
  }
  
  public List<HexCommandTable> findById(long paramLong)
  {
    return this.liteOrm.query(QueryBuilder.create(HexCommandTable.class).where("dataId =?", new Object[] { Long.valueOf(paramLong) }));
  }
  
  public List<HexCommandTable> findByName(String paramString)
  {
    return this.liteOrm.query(QueryBuilder.create(HexCommandTable.class).where("name =?", new Object[] { paramString }));
  }
  
  public void save(HexCommandTable paramHexCommandTable)
  {
    List localList = findById(paramHexCommandTable.getDataId());
    if ((localList != null) && (localList.size() > 0))
    {
      this.liteOrm.update(paramHexCommandTable);
      return;
    }
    this.liteOrm.save(paramHexCommandTable);
  }
}