package th.co.toyota.rvhd.batch.util;

import java.util.Hashtable;

import th.co.toyota.rvhd.batch.vo.CAA9601AItem;

public class CAA96019Message {
  public CAA96019Message() {}
  
  public CAA96019Message(String name) {
    this.name = name;
  }
  
  public CAA9601AItem[] items = new CAA9601AItem[0];
  
  public String name;
  
  public int length;
  
  public int limitLength;
  
  public void addItem(String name, int size, int start) {
    synchronized (this.items) {
      CAA9601AItem[] results = new CAA9601AItem[this.items.length + 1];
      for (int i = 0; i < this.items.length; i++)
        results[i] = this.items[i]; 
      CAA9601AItem item = new CAA9601AItem(name, size, start);
      this.positions.put(name, (new StringBuffer(String.valueOf(this.items.length))).toString());
      results[this.items.length] = item;
      this.items = results;
    } 
  }
  
  public void addItem(CAA9601AItem item) {
    synchronized (this.items) {
      CAA9601AItem[] results = new CAA9601AItem[this.items.length + 1];
      for (int i = 0; i < this.items.length; i++)
        results[i] = this.items[i]; 
      this.positions.put(item.name, (new StringBuffer(String.valueOf(this.items.length))).toString());
      results[this.items.length] = item;
      this.items = results;
    } 
  }
  
  private Hashtable positions = new Hashtable();
  
  public CAA9601AItem getItem(String name) {
    String indexStr = (String)this.positions.get(name);
    int index = Integer.parseInt(indexStr);
    return this.items[index];
  }
  
  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append(String.valueOf(this.name) + "\r\n");
    for (int i = 0; i < this.items.length; i++) {
      if (i > 0)
        buf.append("\r\n"); 
      buf.append((this.items[i]).name);
      buf.append("=");
      buf.append((this.items[i]).value);
    } 
    String str = buf.toString();
    return str;
  }
  
  public void normalize() {
    this.name = "DestinationLogicalName";
    CAA9601AItem item = getItem(this.name);
    item.value = item.value.trim();
    this.name = "TrackPoint";
    item = getItem(this.name);
    item.value = item.value.trim();
  }
  
  public int from(String fromName) {
    String fromIndexStr = (String)this.positions.get(fromName);
    int fromIndex = Integer.parseInt(fromIndexStr);
    return fromTo(fromIndex, this.items.length - 1);
  }
  
  public int fromTo(String fromName, String toName) {
    String fromIndexStr = (String)this.positions.get(fromName);
    int fromIndex = Integer.parseInt(fromIndexStr);
    String toIndexStr = (String)this.positions.get(toName);
    int toIndex = Integer.parseInt(toIndexStr);
    return fromTo(fromIndex, toIndex);
  }
  
  private int fromTo(int fromIndex, int toIndex) {
    int count = 0;
    for (int i = fromIndex; i <= toIndex; i++) {
      CAA9601AItem item = this.items[i];
      int length = item.value.length();
      count += length;
    } 
    return count;
  }
  
  public Object clone() {
    CAA96019Message _message = new CAA96019Message();
    _message.items = (CAA9601AItem[])this.items.clone();
    for (int i = 0; i < this.items.length; i++)
      this.items[i] = new CAA9601AItem((this.items[i]).name, (this.items[i]).size, (this.items[i]).start); 
    _message.positions = (Hashtable)this.positions.clone();
    _message.name = this.name;
    _message.length = this.length;
    _message.limitLength = this.limitLength;
    return _message;
  }
  
  public String toStringMesg() {
    StringBuffer stb = new StringBuffer(this.limitLength);
    for (int i = 0; i < this.items.length; i++) {
      CAA9601AItem item = this.items[i];
      int begin = item.start;
      int end = begin + item.size;
      String strItem = item.toString();
      String value = strItem.trim();
      stb.append(value);
    } 
    return stb.toString();
  }
}
