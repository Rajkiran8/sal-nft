package th.co.toyota.rvhd.batch.vo;

import java.io.Serializable;

public class CAA9601CResponseObject implements Serializable {
  public static final int SUCCESS = 0;
  
  public static final int FAIL = 1;
  
  private int status = 1;
  
  private Object resultObj;
  
  private Object errorObject;
  
  private boolean needRollback = false;
  
  public CAA9601CResponseObject() {}
  
  public CAA9601CResponseObject(int defaultStatus) {
    this.status = defaultStatus;
  }
  
  public CAA9601CResponseObject(boolean status) {
    if (status) {
      this.status = 0;
    } else {
      this.status = 1;
    } 
  }
  
  public int getStatus() {
    return this.status;
  }
  
  public void setStatus(int i) {
    this.status = i;
  }
  
  public Object getResultObj() {
    return this.resultObj;
  }
  
  public void setResultObj(Object object) {
    this.resultObj = object;
  }
  
  public Object getErrorObject() {
    return this.errorObject;
  }
  
  public void setErrorObject(Object object) {
    this.errorObject = object;
  }
  
  public void setSuccess() {
    this.status = 0;
  }
  
  public void setFail() {
    this.status = 1;
  }
  
  public boolean isSuccess() {
    return (this.status == 0);
  }
  
  public boolean isFail() {
    return (this.status == 1);
  }
  
  public boolean isNeedRollback() {
    return this.needRollback;
  }
  
  public void setNeedRollback(boolean needRollback) {
    this.needRollback = needRollback;
  }
}
