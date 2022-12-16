package th.co.toyota.rvhd.batch.ntf;

import java.util.ArrayList;

public class NTFData {
  private NTFHeader Header;
  
  private ArrayList Data = new ArrayList();
  
  public NTFHeader getHeader() {
    return this.Header;
  }
  
  public void setHeader(NTFHeader header) {
    this.Header = header;
  }
  
  public ArrayList getData() {
    return this.Data;
  }
  
  public void setData(ArrayList data) {
    this.Data = data;
  }
}
