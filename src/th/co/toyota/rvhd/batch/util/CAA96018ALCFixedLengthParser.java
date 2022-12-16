package th.co.toyota.rvhd.batch.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import th.co.toyota.rvhd.batch.vo.CAA9601AItem;
import th.co.toyota.rvhd.batch.vo.CAA9601CResponseObject;

public class CAA96018ALCFixedLengthParser {
  private final CAA96019Message message = new CAA96019Message();
  
  private Object lock = new Object();
  
  private String responseText;
  
  private String responsewithResult;
  
  public int limitLen = 0;
  
  public CAA96019Message getCloneMetadata() {
    synchronized (this.lock) {
      return (CAA96019Message)this.message.clone();
    } 
  }
  
  public String createRecord(CAA96019Message dataMessage) {
    StringBuffer stb = new StringBuffer(dataMessage.limitLength);
    for (int i = 0; i < dataMessage.items.length; i++) {
      CAA9601AItem item = dataMessage.items[i];
      int begin = item.start;
      int end = begin + item.size;
      String value = CAA96017ALCUtil.prefixSpace(item.value, item.size);
      stb.append(value);
    } 
    return stb.toString();
  }
  
  public CAA96019Message parseRecord(String record) {
    CAA96019Message _message = null;
    synchronized (this.lock) {
      _message = (CAA96019Message)this.message.clone();
    } 
    for (int i = 0; i < _message.items.length; i++) {
      CAA9601AItem item = _message.items[i];
      String val = extract(record, item.name);
      if (CAA96017ALCUtil.isEmpty(val)) {
        item.value = CAA96017ALCUtil.prefixSpace("", item.size);
      } else {
        item.value = val;
      } 
    } 
    _message.length = record.length();
    _message.normalize();
    return _message;
  }
  
  public String extract(String record, String name) {
    CAA9601AItem item = this.message.getItem(name);
    if (item == null)
      throw new RuntimeException("Field name not found:" + name); 
    int begin = item.start;
    int end = begin + item.size;
    if (begin >= record.length())
      return null; 
    if (end > record.length())
      end = record.length(); 
    String obj = record.substring(begin, end);
    return obj;
  }
  
  public String getSchema() {
    return this.message.name;
  }
  
  public void setSchema(String name, String stBCData) {
    this.message.name = name;
    File f = new File(name);
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      Document doc = parser.parse(f);
      doc.getDocumentElement().normalize();
      NodeList nl = doc.getElementsByTagName("CAA9601AItem");
      int i;
      for (i = 0; i < nl.getLength(); i++) {
        Element elem = (Element)nl.item(i);
        String itemname = elem.getAttribute("name");
        int start = Integer.parseInt(elem.getAttribute("start"));
        int size = Integer.parseInt(elem.getAttribute("size"));
        this.message.addItem(itemname, size, start);
        String finish = elem.getAttribute("finish");
        if ("true".equals(finish))
          this.message.limitLength = start + size; 
        if (i == nl.getLength() - 1 && CAA96017ALCUtil.isEmpty(finish))
          this.message.limitLength = start + size; 
        if (stBCData.length() >= start + size)
          (this.message.items[i]).value = stBCData.substring(start, start + size); 
      } 
      System.out.println("--------ALCFixedLengthParser CAA96019Message--------");
      for (i = 0; i < nl.getLength(); i++);
      CAA9601CResponseObject response = new CAA9601CResponseObject();
      CAA96019Message responseMesg = (CAA96019Message)this.message.clone();
      setResponseText(createRecord(responseMesg));
      setLimitLen(this.message.limitLength);
      System.out.println("message.limitLength=" + this.message.limitLength);
    } catch (Exception exception) {}
  }
  
  public void setResponseText(String string) {
    this.responseText = string;
  }
  
  public String getResponseText() {
    return this.responseText;
  }
  
  public String getResponsewithResult() {
    return this.responsewithResult;
  }
  
  public void setResponsewithResult(String string) {
    this.responsewithResult = string;
  }
  
  public int getLimitLen() {
    return this.limitLen;
  }
  
  public void setLimitLen(int i) {
    this.limitLen = i;
  }
}
