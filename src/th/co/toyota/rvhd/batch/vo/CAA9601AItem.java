package th.co.toyota.rvhd.batch.vo;

import java.io.Serializable;

public class CAA9601AItem implements Serializable {
  public String name;
  
  public int size;
  
  public int start;
  
  public String value;
  
  public CAA9601AItem() {}
  
  public CAA9601AItem(String name, int size, int start) {
    this.name = name;
    this.size = size;
    this.start = start;
  }
}
