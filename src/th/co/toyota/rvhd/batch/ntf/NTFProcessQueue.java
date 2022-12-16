package th.co.toyota.rvhd.batch.ntf;

import th.co.toyota.rvhd.common.web.CAA90010BaseVO;
import th.co.toyota.standard.util.CSTD0063StandardHash;

public class NTFProcessQueue extends CAA90010BaseVO {
  private static final long serialVersionUID = -4877464503484903247L;
  
  private String port;
  
  private String docNo;
  
  private String bcSeqNo;
  
  private String idNo;
  
  private String urn;
  
  private String vinNo;
  
  private String bodyNo;
  
  private String plant;
  
  private String trackingPoint;
  
  private String line;
  
  private String loDt;
  
  private String unique_key;
  
  public String getDocNo() {
    return this.docNo;
  }
  
  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }
  
  public String getPort() {
    return this.port;
  }
  
  public void setPort(String port) {
    this.port = port;
  }
  
  public String getBcSeqNo() {
    return this.bcSeqNo;
  }
  
  public void setBcSeqNo(String bcSeqNo) {
    this.bcSeqNo = bcSeqNo;
  }
  
  public String getIdNo() {
    return this.idNo;
  }
  
  public void setIdNo(String idNo) {
    this.idNo = idNo;
  }
  
  public String getUrn() {
    return this.urn;
  }
  
  public void setUrn(String urn) {
    this.urn = urn;
  }
  
  public String getVinNo() {
    return this.vinNo;
  }
  
  public void setVinNo(String vinNo) {
    this.vinNo = vinNo;
  }
  
  public String getBodyNo() {
    return this.bodyNo;
  }
  
  public void setBodyNo(String bodyNo) {
    this.bodyNo = bodyNo;
  }
  
  public String getPlant() {
    return this.plant;
  }
  
  public void setPlant(String plant) {
    this.plant = plant;
  }
  
  public String getTrackingPoint() {
    return this.trackingPoint;
  }
  
  public void setTrackingPoint(String trackingPoint) {
    this.trackingPoint = trackingPoint;
  }
  
  public String getLoDt() {
    return this.loDt;
  }
  
  public void setLoDt(String loDt) {
    this.loDt = loDt;
  }
  
  public String getLine() {
    return this.line;
  }
  
  public void setLine(String line) {
    this.line = line;
  }
  
  public String getUniqueKey() {
    return this.unique_key;
  }
  
  public void setUniqueKey(String unique_key) {
    this.unique_key = unique_key;
  }
  
  public void setAll(CSTD0063StandardHash hValues) {
    setDocNo(hValues.getString("DOC_NO"));
    setBcSeqNo(hValues.getString("BC_SEQ_NO"));
    setBodyNo(hValues.getString("BODY_NO"));
    setUniqueKey(hValues.getString("UNIQUE_KEY"));
    setIdNo(hValues.getString("ID_NO"));
    setLoDt(hValues.getString("LO_DT"));
    setPlant(hValues.getString("PLANT"));
    setPort(hValues.getString("PORT"));
    setTrackingPoint(hValues.getString("TP"));
    setLine(hValues.getString("LINE"));
    setUrn(hValues.getString("URN"));
    setVinNo(hValues.getString("VIN_NO"));
  }
}
