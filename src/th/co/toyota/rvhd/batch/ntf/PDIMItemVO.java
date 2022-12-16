package th.co.toyota.rvhd.batch.ntf;

import java.io.Serializable;

public class PDIMItemVO implements Serializable {
  private static final long serialVersionUID = 5838297917385361396L;
  
  private String docNo;
  
  private String unique_key;
  
  private String bc_seq_no;
  
  private String urn;
  
  private String vin_no;
  
  private String body_no;
  
  private String id_no;
  
  private String plant;
  
  private String tp;
  
  private String lo_dt;
  
  private String tracking_time;
  
  private String item_group;
  
  private String item_code;
  
  private String serial_no;
  
  private String source_db;
  
  public String getDocNo() {
    return this.docNo;
  }
  
  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }
  
  public String getUnique_key() {
    return this.unique_key;
  }
  
  public void setUnique_key(String unique_key) {
    this.unique_key = unique_key;
  }
  
  public String getBc_seq_no() {
    return this.bc_seq_no;
  }
  
  public void setBc_seq_no(String bc_seq_no) {
    this.bc_seq_no = bc_seq_no;
  }
  
  public String getUrn() {
    return this.urn;
  }
  
  public void setUrn(String urn) {
    this.urn = urn;
  }
  
  public String getVin_no() {
    return this.vin_no;
  }
  
  public void setVin_no(String vin_no) {
    this.vin_no = vin_no;
  }
  
  public String getBody_no() {
    return this.body_no;
  }
  
  public void setBody_no(String body_no) {
    this.body_no = body_no;
  }
  
  public String getId_no() {
    return this.id_no;
  }
  
  public void setId_no(String id_no) {
    this.id_no = id_no;
  }
  
  public String getPlant() {
    return this.plant;
  }
  
  public void setPlant(String plant) {
    this.plant = plant;
  }
  
  public String getTp() {
    return this.tp;
  }
  
  public void setTp(String tp) {
    this.tp = tp;
  }
  
  public String getLo_dt() {
    return this.lo_dt;
  }
  
  public void setLo_dt(String lo_dt) {
    this.lo_dt = lo_dt;
  }
  
  public String getTracking_time() {
    return this.tracking_time;
  }
  
  public void setTracking_time(String tracking_time) {
    this.tracking_time = tracking_time;
  }
  
  public String getItem_group() {
    return this.item_group;
  }
  
  public void setItem_group(String item_group) {
    this.item_group = item_group;
  }
  
  public String getItem_code() {
    return this.item_code;
  }
  
  public void setItem_code(String item_code) {
    this.item_code = item_code;
  }
  
  public String getSerial_no() {
    return this.serial_no;
  }
  
  public void setSerial_no(String serial_no) {
    this.serial_no = serial_no;
  }
  
  public String getSource_db() {
    return this.source_db;
  }
  
  public void setSource_db(String source_db) {
    this.source_db = source_db;
  }
}
