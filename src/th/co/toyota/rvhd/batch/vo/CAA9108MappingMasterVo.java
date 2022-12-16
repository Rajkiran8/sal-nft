package th.co.toyota.rvhd.batch.vo;

import th.co.toyota.rvhd.common.web.CAA90010BaseVO;
import th.co.toyota.standard.util.CSTD0063StandardHash;

public class CAA9108MappingMasterVo extends CAA90010BaseVO {
  private static final long serialVersionUID = -7801769615765180678L;
  
  private String dtoColumnName;
  
  private String dtoColumnNameOriginal;
  
  private String dtoFieldName;
  
  private String dtoFieldCode;
  
  private String dtoFieldType;
  
  private String dtoPosition;
  
  private String dtoLength;
  
  private String dtoUpdatePermission;
  
  private String dtoFlag;
  
  private String dtoGridLabel;
  
  private String dtoDetailLabel;
  
  private String dtoTableName;
  
  private int dtoIndex;
  
  private String dtoNewValue;
  
  private String dtoOldValue;
  
  private String dtoAlignment;
  
  private String dtoDataLength;
  
  private String dtoItemCode;
  
  public String getDtoColumnName() {
    return this.dtoColumnName;
  }
  
  public void setDtoColumnName(String dtoColumnName) {
    this.dtoColumnName = dtoColumnName;
  }
  
  public String getDtoFieldCode() {
    return this.dtoFieldCode;
  }
  
  public void setDtoFieldCode(String dtoFieldCode) {
    this.dtoFieldCode = dtoFieldCode;
  }
  
  public String getDtoFieldName() {
    return this.dtoFieldName;
  }
  
  public void setDtoFieldName(String dtoFieldName) {
    this.dtoFieldName = dtoFieldName;
  }
  
  public String getDtoFieldType() {
    return this.dtoFieldType;
  }
  
  public void setDtoFieldType(String dtoFieldType) {
    this.dtoFieldType = dtoFieldType;
  }
  
  public String getDtoLength() {
    return this.dtoLength;
  }
  
  public void setDtoLength(String dtoLength) {
    this.dtoLength = dtoLength;
  }
  
  public String getDtoPosition() {
    return this.dtoPosition;
  }
  
  public void setDtoPosition(String dtoPosition) {
    this.dtoPosition = dtoPosition;
  }
  
  public String getDtoUpdatePermission() {
    return this.dtoUpdatePermission;
  }
  
  public void setDtoUpdatePermission(String dtoUpdatePermission) {
    this.dtoUpdatePermission = dtoUpdatePermission;
  }
  
  public String getDtoFlag() {
    return this.dtoFlag;
  }
  
  public void setDtoFlag(String dtoFlag) {
    this.dtoFlag = dtoFlag;
  }
  
  public String getDtoDetailLabel() {
    return this.dtoDetailLabel;
  }
  
  public void setDtoDetailLabel(String dtoDetailLabel) {
    this.dtoDetailLabel = dtoDetailLabel;
  }
  
  public String getDtoGridLabel() {
    return this.dtoGridLabel;
  }
  
  public void setDtoGridLabel(String dtoGridLabel) {
    this.dtoGridLabel = dtoGridLabel;
  }
  
  public int getDtoIndex() {
    return this.dtoIndex;
  }
  
  public void setDtoIndex(int dtoIndex) {
    this.dtoIndex = dtoIndex;
  }
  
  public String getDtoTableName() {
    return this.dtoTableName;
  }
  
  public void setDtoTableName(String dtoTableName) {
    this.dtoTableName = dtoTableName;
  }
  
  public String getDtoNewValue() {
    return this.dtoNewValue;
  }
  
  public void setDtoNewValue(String dtoNewValue) {
    this.dtoNewValue = dtoNewValue;
  }
  
  public String getDtoOldValue() {
    return this.dtoOldValue;
  }
  
  public void setDtoOldValue(String dtoOldValue) {
    this.dtoOldValue = dtoOldValue;
  }
  
  public String getDtoAlignment() {
    return this.dtoAlignment;
  }
  
  public void setDtoAlignment(String dtoAlignment) {
    this.dtoAlignment = dtoAlignment;
  }
  
  public String getDtoDataLength() {
    return this.dtoDataLength;
  }
  
  public void setDtoDataLength(String dtoDataLength) {
    this.dtoDataLength = dtoDataLength;
  }
  
  public void setAll(CSTD0063StandardHash hValues) {
    super.setAll(hValues);
    setDtoColumnName(hValues.getString("PK_COLUMN_NM"));
    setDtoColumnNameOriginal(hValues.getString("PK_COLUMN_NM"));
    setDtoFieldName(hValues.getString("FIELD_NM"));
    setDtoFieldCode(hValues.getString("FIELD_CD"));
    setDtoFieldType(hValues.getString("FIELD_TYPE"));
    setDtoPosition(hValues.getStringFromDouble("POSI_FRM"));
    setDtoLength(hValues.getStringFromDouble("POSI_LEN"));
    setDtoUpdatePermission(hValues.getString("FLAG_SINGLE"));
    setDtoFlag(hValues.getString("FLAG"));
    setDtoTableName(hValues.getString("TABLE_NAME"));
    setDtoDataLength(hValues.getString("DATA_LENGTH"));
    setDtoItemCode(hValues.getString("ITEM_CODE"));
  }
  
  public CAA9108MappingMasterVo getClone() {
    CAA9108MappingMasterVo vo = new CAA9108MappingMasterVo();
    vo.setDtoColumnName(this.dtoColumnName);
    vo.setDtoColumnNameOriginal(this.dtoColumnNameOriginal);
    vo.setDtoFieldName(this.dtoFieldName);
    vo.setDtoFieldCode(this.dtoFieldCode);
    vo.setDtoFieldType(this.dtoFieldType);
    vo.setDtoPosition(this.dtoPosition);
    vo.setDtoLength(this.dtoLength);
    vo.setDtoUpdatePermission(this.dtoUpdatePermission);
    vo.setDtoFlag(this.dtoFlag);
    vo.setDtoGridLabel(this.dtoGridLabel);
    vo.setDtoDetailLabel(this.dtoDetailLabel);
    vo.setDtoTableName(this.dtoTableName);
    vo.setDtoIndex(this.dtoIndex);
    vo.setDtoNewValue(this.dtoNewValue);
    vo.setDtoOldValue(this.dtoOldValue);
    vo.setDtoAlignment(this.dtoAlignment);
    vo.setDtoDataLength(this.dtoDataLength);
    vo.setDtoItemCode(this.dtoItemCode);
    return vo;
  }
  
  public String getDtoItemCode() {
    return this.dtoItemCode;
  }
  
  public void setDtoItemCode(String dtoItemCode) {
    this.dtoItemCode = dtoItemCode;
  }
  
  public String getDtoColumnNameOriginal() {
    return this.dtoColumnNameOriginal;
  }
  
  public void setDtoColumnNameOriginal(String dtoColumnNameOriginal) {
    this.dtoColumnNameOriginal = dtoColumnNameOriginal;
  }
}
