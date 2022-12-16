package th.co.toyota.rvhd.batch.ntf;

public class NTFHeader {
  private String Process_Result;
  
  private String List_Count;
  
  public String getProcessResult() {
    return this.Process_Result;
  }
  
  public void setProcessResult(String process_Result) {
    this.Process_Result = process_Result;
  }
  
  public String getListCount() {
    return this.List_Count;
  }
  
  public void setListCount(String list_Count) {
    this.List_Count = list_Count;
  }
}
