package th.co.toyota.rvhd.batch.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import th.co.toyota.rvhd.batch.ntf.PDIMItemVO;
import th.co.toyota.rvhd.batch.vo.CAA96015BCNoticeVO;
import th.co.toyota.rvhd.common.dao.CAA90011BaseBatchDAO;
import th.co.toyota.rvhd.common.db.CAA90050CommonLogger;
import th.co.toyota.rvhd.shared.util.CAA90007FormatUtil;
import th.co.toyota.standard.batch.queue.CSTD0080BatchInfoVO;
import th.co.toyota.standard.batch.queue.CSTD0080BatchQueueRequest;
import th.co.toyota.standard.exception.CSTD0056CommonException;
import th.co.toyota.standard.util.CSTD0054ErrorUtil;
import th.co.toyota.standard.util.CSTD0068ConfigurationResolver;

public class CAA96013BCFromGALCDAO extends CAA90011BaseBatchDAO {
  public CAA96013BCFromGALCDAO() throws SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

public List getInitialData(String sLocation, String sPlant, String sTP) throws Exception {
    List aTempList = new ArrayList();
    StringBuilder sbTemp = new StringBuilder();
    List lParam = new ArrayList();
    sbTemp.append(" SELECT X.* FROM ( ");
    sbTemp.append(" SELECT M.CD, ");
    sbTemp.append("       M.VALUE, ");
    sbTemp.append("       M.REMARK, ");
    sbTemp.append("       (SELECT H.LAST_BC_SEQ  ");
    sbTemp.append("        FROM TB_M_PDIM_SET_INFO_H H ");
    sbTemp.append("        WHERE H.PLANT = ? ");
    sbTemp.append("              AND H.TP = ? ");
    sbTemp.append("              AND H.LINE = ?  ");
    sbTemp.append("              AND H.SYSTEM = ? ");
    sbTemp.append("              AND ROWNUM = 1) SEQ ");
    sbTemp.append(" FROM TB_M_SYSTEM M ");
    sbTemp.append(" WHERE M.CATEGORY = ? ");
    sbTemp.append("      AND M.SUB_CATEGORY = ? ");
    sbTemp.append("      AND M.STATUS = ? ");
    sbTemp.append("      AND EXISTS (SELECT 0 ");
    sbTemp.append("                 FROM TB_M_BC_TP_CNTRL C ");
    sbTemp.append("                 WHERE C.PLANT = ? ");
    sbTemp.append("                       AND C.TP = ? ");
    sbTemp.append("                       AND C.ITEM_CODE = M.VALUE ");
    sbTemp.append("      \t\t\tUNION ALL  ");
    sbTemp.append("      \t\t\tSELECT 0 ");
    sbTemp.append("                 FROM TB_M_BC_TP_CNTRL_NTF C ");
    sbTemp.append("                 WHERE C.PLANT = ? ");
    sbTemp.append("                       AND C.TP = ? ");
    sbTemp.append("                       AND C.ITEM_CODE = M.VALUE ");
    sbTemp.append("\t\t\t\t\t) ");
    sbTemp.append(" ) X ");
    sbTemp.append(" WHERE X.SEQ IS NOT NULL ");
    lParam.add(sPlant);
    lParam.add(sTP.substring(1));
    lParam.add(sTP.substring(0, 1));
    lParam.add("SYS02");
    lParam.add(sLocation);
    lParam.add("ITEM_CODE");
    lParam.add("Y");
    lParam.add(sPlant);
    lParam.add(sTP);
    lParam.add(sPlant);
    lParam.add(sTP);
    aTempList = this.adapter.executeQuery(this.con, sbTemp.toString(), lParam.toArray());
    return aTempList;
  }
  
  public List getVHDMapItemCode(String sLocation) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append(" SELECT T.ITEM_CODE,  ");
    sql.append(" \t\tT.COLUMN_NAME,   ");
    sql.append(" \t\tT.SET_METHOD  ");
    sql.append(" FROM TB_M_VHD_MAP_ITEMCODE T  ");
    sql.append(" WHERE UPPER(T.LOCATION) = UPPER(?)  ");
    sql.append("   AND T.FUNCTION_ID = 'AA9601'  ");
    sql.append(" ORDER BY T.ITEM_CODE  ");
    lParam.add(sLocation);
    return this.adapter.executeQuery(this.con, sql.toString(), lParam.toArray());
  }
  
  public List getDuplicateBCData(String strTableName, String strSerialNo, String itemGroup) throws Exception {
    List alRet = new ArrayList();
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append("SELECT R.URN, ");
    sql.append("       R.ID_NO, ");
    sql.append("       R.VIN_NO, ");
    sql.append("       R.BODY_NO, ");
    sql.append("       R.PLANT, ");
    sql.append("       R.TP, ");
    sql.append("       TO_CHAR(R.CREATE_DATE, 'YYYYMMDDHH24MISS') TRACK_TIME, ");
    sql.append("       R.ITEM_CODE ");
    sql.append(" FROM " + strTableName + " R ");
    sql.append(" WHERE R.SERIAL_NO = ? ");
    if ("TB_R_BC_ITEMCHK_NTF".equalsIgnoreCase(strTableName))
      sql.append(" AND NVL(ITEM_GROUP,' ') = NVL(?,' ') "); 
    sql.append(" ORDER BY R.URN, ");
    sql.append("       R.ID_NO, ");
    sql.append("       R.ITEM_CODE,  ");
    sql.append("       R.PLANT, ");
    sql.append("       R.TP, ");
    sql.append("       R.VIN_NO, ");
    sql.append("       R.BODY_NO ");
    lParam.add(strSerialNo);
    if ("TB_R_BC_ITEMCHK_NTF".equalsIgnoreCase(strTableName))
      lParam.add(itemGroup); 
    alRet = this.adapter.executeQuery(this.con, sql.toString(), lParam.toArray());
    return alRet;
  }
  
  public List getCheckForDuplicate(String strPlant, String strTP, String strLine, String location) throws Exception {
    List alRet = new ArrayList();
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append("SELECT D.CHKRECORD, ");
    sql.append("       D.ITEM_CODE, M.REMARK AS TABLENAME ");
    sql.append("FROM TB_M_PDIM_SET_INFO_D D, TB_M_SYSTEM M ");
    sql.append("WHERE D.MACHINE = (SELECT H.MACHINE ");
    sql.append("                   FROM TB_M_PDIM_SET_INFO_H H ");
    sql.append("                   WHERE H.PLANT = ? ");
    sql.append("                         AND H.TP = ? ");
    sql.append("                         AND H.LINE = ? ");
    sql.append("                         AND H.SYSTEM = ? ");
    sql.append("                         AND ROWNUM = 1) ");
    sql.append("      AND D.SYSTEM = ? ");
    sql.append("      AND M.CATEGORY = ? ");
    sql.append("      AND M.SUB_CATEGORY = 'ITEM_CODE' ");
    sql.append("      AND M.STATUS = 'Y' ");
    sql.append("      AND M.VALUE = D.ITEM_CODE ");
    sql.append("      AND EXISTS(SELECT 0 ");
    sql.append("                 FROM TB_M_BC_TP_CNTRL C ");
    sql.append("                 WHERE C.PLANT = ? ");
    sql.append("                       AND C.TP = ? ");
    sql.append("                       AND C.ITEM_CODE = D.ITEM_CODE) ");
    sql.append("      AND SUBSTR(D.ITEM_CODE, 1, 1) <> 'N' ");
    sql.append("  ORDER BY D.ITEM_CODE ");
    lParam.add(strPlant);
    lParam.add(strTP);
    lParam.add(strLine);
    lParam.add("SYS02");
    lParam.add("SYS02");
    lParam.add(location);
    lParam.add(strPlant);
    lParam.add(String.valueOf(strLine) + strTP);
    alRet = this.adapter.executeQuery(this.con, sql.toString(), lParam.toArray());
    return alRet;
  }
  
  public void mergeBCData(String strTableName, String strItemCode, String strSerialNo, CAA96015BCNoticeVO obVO, Timestamp sysdate, String itemGroup) throws Exception {
    if (strSerialNo == null)
      return; 
    if (strSerialNo.trim().length() == 0)
      return; 
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append(" MERGE INTO " + strTableName + " T ");
    sql.append(" USING (SELECT ? ITEM_CODE,  ");
    boolean NASection = false;
    if ("TB_R_BC_ITEMCHK_NTF".equalsIgnoreCase(strTableName)) {
      sql.append("              ? ITEM_GROUP,  ");
      NASection = true;
    } 
    sql.append("              ? SERIAL_NO,  ");
    sql.append("              ? URN,  ");
    sql.append("              ? ID_NO,  ");
    sql.append("              ? VIN_NO,  ");
    sql.append("              ? BODY_NO,  ");
    sql.append("              ? PLANT,  ");
    sql.append("              ? TP, ");
    sql.append("              ? BC_SEQ_NO, ");
    sql.append("              ? CREATE_BY, ");
    sql.append("              ? CREATE_DATE, ");
    sql.append("              ? UPDATE_BY, ");
    sql.append("              ? UPDATE_DATE ");
    sql.append("       FROM DUAL) SRC ");
    sql.append(" ON (T.URN = SRC.URN ");
    sql.append("    AND T.ID_NO = SRC.ID_NO ");
    sql.append("    AND T.ITEM_CODE = SRC.ITEM_CODE) ");
    sql.append(" WHEN MATCHED THEN ");
    sql.append("     UPDATE SET T.SERIAL_NO = SRC.SERIAL_NO, ");
    sql.append("                T.VIN_NO = SRC.VIN_NO, ");
    sql.append("                T.BODY_NO = SRC.BODY_NO, ");
    sql.append("                T.PLANT = SRC.PLANT, ");
    sql.append("                T.TP = SRC.TP, ");
    sql.append("                T.BC_SEQ_NO = SRC.BC_SEQ_NO, ");
    sql.append("                T.UPDATE_BY = SRC.UPDATE_BY, ");
    sql.append("                T.UPDATE_DATE = SRC.UPDATE_DATE ");
    sql.append("WHEN NOT MATCHED THEN ");
    sql.append("     INSERT (ITEM_CODE,  ");
    if (NASection)
      sql.append("         ITEM_GROUP, "); 
    sql.append("             SERIAL_NO, URN, ID_NO, VIN_NO, ");
    sql.append("             BODY_NO, PLANT, TP, BC_SEQ_NO, CREATE_BY, ");
    sql.append("             CREATE_DATE, UPDATE_BY, UPDATE_DATE) ");
    sql.append("     VALUES (SRC.ITEM_CODE,  ");
    if (NASection)
      sql.append("         SRC.ITEM_GROUP, "); 
    sql.append("             SRC.SERIAL_NO, SRC.URN, SRC.ID_NO, SRC.VIN_NO, ");
    sql.append("             SRC.BODY_NO, SRC.PLANT, SRC.TP, SRC.BC_SEQ_NO, SRC.CREATE_BY, ");
    sql.append("             SRC.CREATE_DATE, SRC.UPDATE_BY, SRC.UPDATE_DATE)");
    lParam.add(strItemCode);
    if (NASection)
      lParam.add(itemGroup); 
    lParam.add(strSerialNo);
    lParam.add(obVO.getStrURN());
    lParam.add(obVO.getStrIDNO());
    lParam.add(obVO.getStrVinNo());
    lParam.add(obVO.getStrBodyNo());
    lParam.add(obVO.getStrPlant());
    lParam.add(obVO.getStrTP());
    lParam.add(obVO.getStrBCSeqNo());
    lParam.add("SYSTEM");
    lParam.add(sysdate);
    lParam.add("SYSTEM");
    lParam.add(sysdate);
    this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
  }
  
  public void insertBCHistory(String strItemCode, String strSerialNo, CAA96015BCNoticeVO obVO, Timestamp sysdate, String itemGroup) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append("INSERT INTO TB_R_BC_HISTORY T ");
    sql.append("       (T.UNIQUE_KEY, ");
    sql.append("        T.ITEM_CODE, ");
    sql.append("        T.ITEM_GROUP, ");
    sql.append("        T.SERIAL_NO, ");
    sql.append("        T.URN, ");
    sql.append("        T.ID_NO, ");
    sql.append("        T.VIN_NO, ");
    sql.append("        T.BODY_NO, ");
    sql.append("        T.PLANT, ");
    sql.append("        T.TP, ");
    sql.append("        T.BC_SEQ_NO, ");
    sql.append("        T.SEQ_ID) ");
    sql.append("VALUES (?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        ?, ");
    sql.append("        SEQ_BC_HISTORY.NEXTVAL)");
    lParam.add(sysdate);
    lParam.add(strItemCode);
    lParam.add(itemGroup);
    lParam.add(strSerialNo);
    lParam.add(obVO.getStrURN());
    lParam.add(obVO.getStrIDNO());
    lParam.add(obVO.getStrVinNo());
    lParam.add(obVO.getStrBodyNo());
    lParam.add(obVO.getStrPlant());
    lParam.add(obVO.getStrTP());
    lParam.add(Integer.parseInt(obVO.getStrBCSeqNo()));
    this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
  }
  
  public void updateBCSeq(String strPlant, String strTP, String strBCSeq) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append("UPDATE TB_M_PDIM_SET_INFO_H H ");
    sql.append("SET H.LAST_BC_SEQ = ? ");
    sql.append("WHERE H.PLANT = ? ");
    sql.append("      AND H.TP = ? ");
    sql.append("      AND H.LINE = ?  ");
    sql.append("      AND H.SYSTEM = ? ");
    lParam.add(strBCSeq);
    lParam.add(strPlant);
    lParam.add(strTP.substring(1));
    lParam.add(strTP.substring(0, 1));
    lParam.add("SYS02");
    this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
  }
  
  public void updateBCResponse(String strPlant, String strTP, int port, String strBCResp) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append("UPDATE TB_M_PDIM_SET_INFO_H H ");
    sql.append("SET H.BC_RESPONSE = ? ");
    sql.append("WHERE H.PORT = ? ");
    lParam.add(strBCResp);
    lParam.add(new BigDecimal(port));
    this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
  }
  
  public List getNext_BCSeqNo() throws SQLException {
    String sqlSelect = "SELECT SEQ_PDIM_BCSEQ.NEXTVAL FROM DUAL";
    return this.adapter.executeQuery(this.con, sqlSelect, null);
  }
  
  public void insertNTFWebServiceProcess(int port, CAA96015BCNoticeVO obVO, Timestamp sysdate, String stLine, String docNo) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append(" insert into TB_R_NTF_WS_PROCESS   ");
    sql.append(" \t    (PORT,                     ");
    sql.append(" \t     BC_SEQ_NO,                ");
    sql.append(" \t     ID_NO,                    ");
    sql.append(" \t     URN,                      ");
    sql.append(" \t     VIN_NO,                   ");
    sql.append(" \t     BODY_NO,                  ");
    sql.append(" \t     PLANT,                    ");
    sql.append(" \t     TP,                       ");
    sql.append(" \t     LINE,                     ");
    sql.append(" \t     LO_DT,                    ");
    sql.append(" \t     DOC_NO,                ");
    sql.append(" \t     UNIQUE_KEY)              ");
    sql.append(" values                            ");
    sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYYMMDDHH24MI'),    ");
    sql.append("  ?, ?)                ");
    lParam.add(port);
    lParam.add(obVO.getStrBCSeqNo());
    lParam.add(obVO.getStrIDNO());
    lParam.add(obVO.getStrURN());
    lParam.add(obVO.getStrVinNo());
    lParam.add(obVO.getStrBodyNo());
    lParam.add(obVO.getStrPlant());
    lParam.add(obVO.getStrTP());
    lParam.add(stLine);
    String loDtTemp = null;
    if (obVO.getLoDate() != null && obVO.getLoDate().trim().length() > 0)
      loDtTemp = String.valueOf(obVO.getLoDate().substring(0, 8)) + obVO.getLoDate().substring(9); 
    lParam.add(loDtTemp);
    lParam.add(docNo);
    lParam.add(sysdate);
    this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
  }
  
  private boolean checkAlreadyInsertHeader(String tableName, String docNo) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT (CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END) INSERTED ");
    sql.append(" FROM ").append(tableName);
    sql.append(" WHERE DOC_NO = ? ");
    List ls = this.adapter.executeQuery(this.con, sql.toString(), new String[] { docNo });
    if (ls != null && ls.size() > 0)
      return "Y".equals(ls.get(0)); 
    return false;
  }
  
  public BigDecimal insertPDIM_DUPLICATE_H(PDIMItemVO obVO, String docNo) throws SQLException {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append(" SELECT REF_ID FROM TB_R_PDIM_DUPLICATE_H ");
    sql.append(" WHERE DOC_NO = ?   ");
    sql.append("   AND UNIQUE_KEY = TO_DATE(?, 'YYYYMMDDHH24MISS')   ");
    sql.append("   AND BC_SEQ_NO  = ?   ");
    sql.append("   AND URN = ?      ");
    sql.append("   AND NVL(VIN_NO,' ') = NVL(?,' ')   ");
    sql.append("   AND NVL(BODY_NO,' ') = NVL(?,' ')   ");
    sql.append("   AND ID_NO = ?   ");
    sql.append("   AND PLANT = ?   ");
    sql.append("   AND TP = ?      ");
    sql.append("   AND NVL(LO_DT,SYSDATE) = NVL(TO_DATE(?, 'YYYYMMDDHH24MISS'),SYSDATE) ");
    lParam.add(docNo);
    lParam.add(obVO.getUnique_key());
    lParam.add(obVO.getBc_seq_no());
    lParam.add(obVO.getUrn());
    lParam.add(obVO.getVin_no());
    lParam.add(obVO.getBody_no());
    lParam.add(obVO.getId_no());
    lParam.add(obVO.getPlant());
    lParam.add(obVO.getTp());
    lParam.add(obVO.getLo_dt());
    List list = this.adapter.executeQuery(this.con, sql.toString(), lParam.toArray());
    BigDecimal refID_H = new BigDecimal(0);
    if (list != null && list.size() > 0) {
      refID_H = (BigDecimal) list.get(0);
    } else {
      sql = new StringBuilder();
      sql.append(" SELECT MAX(REF_ID) FROM TB_R_PDIM_DUPLICATE_H WHERE DOC_NO = '" + docNo + "'  ");
      list = this.adapter.executeQuery(this.con, sql.toString(), null);
      BigDecimal maxRefId = new BigDecimal(0);
      if (list != null && list.size() > 0) {
        BigDecimal tmp = (BigDecimal) list.get(0);
        if (tmp != null)
          maxRefId = tmp; 
      } 
      refID_H = new BigDecimal(maxRefId.intValue() + 1);
      sql = new StringBuilder();
      sql.append(" INSERT INTO TB_R_PDIM_DUPLICATE_H ");
      sql.append(" (  ");
      sql.append("   DOC_NO     ,  ");
      sql.append("   REF_ID     ,  ");
      sql.append("   UNIQUE_KEY     ,  ");
      sql.append("   BC_SEQ_NO      ,  ");
      sql.append("   URN      \t\t ,  ");
      sql.append("   VIN_NO \t\t ,  ");
      sql.append("   BODY_NO \t\t ,  ");
      sql.append("   ID_NO  \t\t ,  ");
      sql.append("   PLANT  \t\t ,  ");
      sql.append("   TP  \t\t     ,  ");
      sql.append("   LO_DT  \t\t   ");
      sql.append(" )  ");
      sql.append(" VALUES(  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   TO_DATE(?, 'YYYYMMDDHH24MISS') ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   TO_DATE(?, 'YYYYMMDDHH24MISS')  ");
      sql.append(" ) ");
      lParam = new ArrayList();
      lParam.add(docNo);
      lParam.add(refID_H);
      lParam.add(obVO.getUnique_key());
      lParam.add(obVO.getBc_seq_no());
      lParam.add(obVO.getUrn());
      lParam.add(obVO.getVin_no());
      lParam.add(obVO.getBody_no());
      lParam.add(obVO.getId_no());
      lParam.add(obVO.getPlant());
      lParam.add(obVO.getTp());
      lParam.add(obVO.getLo_dt());
      this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
    } 
    return refID_H;
  }
  
  public void insertPDIM_DUPLICATE_D(List list, String sysdate, String docNo, String itemCodePdim) throws Exception {
    if (list != null) {
      StringBuilder sql = new StringBuilder();
      sql.append(" INSERT INTO TB_R_PDIM_DUPLICATE_D ");
      sql.append(" (  ");
      sql.append("   DOC_NO         ,  ");
      sql.append("   REF_ID     \t ,  ");
      sql.append("   TRACKING_TIME  ,  ");
      sql.append("   ITEM_GROUP     ,  ");
      sql.append("   ITEM_CODE      ,  ");
      sql.append("   SERIAL_NO      ,  ");
      sql.append("   SOURCE_DB      ,  ");
      sql.append("   ITEM_CODE_PDIM    ");
      sql.append(" )  ");
      sql.append(" VALUES(  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   TO_DATE(?, 'YYYYMMDDHH24MISS') ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ? ,  ");
      sql.append("   ?   ");
      sql.append(" ) ");
      for (int ix = 0; ix < list.size(); ix++) {
        PDIMItemVO obVO = (PDIMItemVO) list.get(ix);
        BigDecimal refID_H = insertPDIM_DUPLICATE_H(obVO, docNo);
        List lParam = new ArrayList();
        lParam.add(docNo);
        lParam.add(refID_H);
        lParam.add(sysdate);
        lParam.add(obVO.getItem_group());
        lParam.add(obVO.getItem_code());
        lParam.add(obVO.getSerial_no());
        lParam.add(obVO.getSource_db());
        lParam.add(itemCodePdim);
        this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
      } 
    } 
  }
  
  public void insertPDIM_HISTORY_H(PDIMItemVO obVO, String docNo) {
    StringBuilder sql = new StringBuilder();
    sql.append(" INSERT INTO TB_R_PDIM_HISTORY_H ");
    sql.append(" (  ");
    sql.append("   DOC_NO     ,  ");
    sql.append("   UNIQUE_KEY     ,  ");
    sql.append("   BC_SEQ_NO      ,  ");
    sql.append("   URN      \t\t ,  ");
    sql.append("   VIN_NO \t\t ,  ");
    sql.append("   BODY_NO \t\t ,  ");
    sql.append("   ID_NO  \t\t ,  ");
    sql.append("   PLANT  \t\t ,  ");
    sql.append("   TP  \t\t     ,  ");
    sql.append("   LO_DT  \t\t ,  ");
    sql.append("   SOURCE_DB         ");
    sql.append(" )  ");
    sql.append(" VALUES(  ");
    sql.append("   ? ,  ");
    sql.append("   TO_DATE(?, 'YYYYMMDDHH24MISS') ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   TO_DATE(?, 'YYYYMMDDHH24MISS'),  ");
    sql.append("   ?   ");
    sql.append(" ) ");
    List lParam = new ArrayList();
    lParam.add(docNo);
    lParam.add(obVO.getUnique_key());
    lParam.add(obVO.getBc_seq_no());
    lParam.add(obVO.getUrn());
    lParam.add(obVO.getVin_no());
    lParam.add(obVO.getBody_no());
    lParam.add(obVO.getId_no());
    lParam.add(obVO.getPlant());
    lParam.add(obVO.getTp());
    lParam.add(obVO.getLo_dt());
    lParam.add(obVO.getSource_db());
    try {
      this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
    } catch (Exception exception) {}
  }
  
  public void insertPDIM_HISTORY_D(PDIMItemVO obVO, Timestamp sysdate, String docNo) throws Exception {
    StringBuilder sql = new StringBuilder();
    sql.append(" INSERT INTO TB_R_PDIM_HISTORY_D ");
    sql.append(" (  ");
    sql.append("   DOC_NO         ,  ");
    sql.append("   TRACKING_TIME  ,  ");
    sql.append("   ITEM_GROUP     ,  ");
    sql.append("   ITEM_CODE      ,  ");
    sql.append("   SERIAL_NO         ");
    sql.append(" )  ");
    sql.append(" VALUES(  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ? ,  ");
    sql.append("   ?    ");
    sql.append(" ) ");
    boolean flag = checkAlreadyInsertHeader("TB_R_PDIM_HISTORY_H", docNo);
    if (!flag)
      insertPDIM_HISTORY_H(obVO, docNo); 
    List lParam = new ArrayList();
    lParam.add(docNo);
    lParam.add(sysdate);
    lParam.add(obVO.getItem_group());
    lParam.add(obVO.getItem_code());
    lParam.add(obVO.getSerial_no());
    this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
  }
  
  public void duplicateCheckingOfNASection(List lsNApartOfR0, Timestamp sysdate, String location, int port) throws Exception {
    String sAplID = "";
    String fileName = "";
    String SUBSYSTEM = "AA95";
    String FUNCTION_ID = "AA9601";
    String sUserID = "SYSTEM";
    String processLogDesc = "Batch program to receive vehicle data from Plant ALC for NA Section duplicate check";
    String moreDesc = " by Port=" + port;
    boolean alreadyLogBegin = false;
    boolean alreadyInsertVhdHeader = false;
    List alRet = new ArrayList();
    StringBuilder sqlCnt = new StringBuilder();
    List lParam = new ArrayList();
    sqlCnt.append(" SELECT (CASE WHEN COUNT(*) > 0 THEN 'YES' ELSE 'NO' END) DUPFLAG ");
    sqlCnt.append("    FROM TB_VH_NTF_D D, TB_M_VHD_MAP_ITEMCODE T2 ");
    sqlCnt.append("   WHERE D.SERIAL_NO = ? ");
    sqlCnt.append("     AND SUBSTR(D.ITEM_CODE, 1, 1) = 'A' ");
    sqlCnt.append("     AND D.URN <> ? ");
    sqlCnt.append("     AND T2.LOCATION = ? ");
    sqlCnt.append("     AND T2.FUNCTION_ID = ? ");
    sqlCnt.append("     AND NVL(T2.ITEM_GROUP,' ') = NVL(?,' ') ");
    sqlCnt.append("     AND D.ITEM_CODE = T2.ITEM_CODE ");
    sqlCnt.append("     AND EXISTS (SELECT 'X' ");
    sqlCnt.append("            FROM TB_VH_NTF_H H ");
    sqlCnt.append("           WHERE H.URN = D.URN ");
    sqlCnt.append("             AND H.IDENT_NO = D.IDENT_NO) ");
    StringBuilder sqlPartInfo = new StringBuilder();
    sqlPartInfo.append(" SELECT T2.ITEM_DESC, ");
    sqlPartInfo.append("    (SELECT EMAIL_ADDY ");
    sqlPartInfo.append("       FROM TB_M_EMAIL_MASTER ");
    sqlPartInfo.append("      WHERE GROUP_ID = 'NA_SECTION_DUP' ");
    sqlPartInfo.append("       AND PLANT_CD = ? ");
    sqlPartInfo.append("       AND ROWNUM = 1) V_EMAIL_TO_DUP ");
    sqlPartInfo.append("  FROM TB_M_VHD_MAP_ITEMCODE T2 ");
    sqlPartInfo.append(" WHERE ITEM_CODE = ? ");
    sqlPartInfo.append("   AND T2.LOCATION = ? ");
    sqlPartInfo.append("   AND T2.FUNCTION_ID = ? ");
    int status = 2;
    ArrayList msgArgs = new ArrayList();
    CAA90011BaseBatchDAO daoLog = new CAA90011BaseBatchDAO();
    try {
      for (int n = 0; n < lsNApartOfR0.size(); n++) {
        PDIMItemVO obPDIM = (PDIMItemVO) lsNApartOfR0.get(n);
        lParam = new ArrayList();
        lParam.add(obPDIM.getSerial_no());
        lParam.add(obPDIM.getUrn());
        lParam.add(location);
        lParam.add(FUNCTION_ID);
        lParam.add(obPDIM.getItem_group());
        alRet = new ArrayList();
        alRet = this.adapter.executeQuery(this.con, sqlCnt.toString(), lParam.toArray());
        if (alRet != null && alRet.size() > 0) {
          String dupFlag = (String) alRet.get(0);
          if ("YES".equalsIgnoreCase(dupFlag)) {
            status = 3;
            if (!alreadyLogBegin) {
              sAplID = getAppID();
              fileName = "PDIM_PLANT_ALC_NA_SECTION_" + CAA90007FormatUtil.convertDateToString(sysdate, "yyyyMMddHHmmss");
              msgArgs = new ArrayList();
              msgArgs.add(String.valueOf(processLogDesc) + moreDesc);
              CAA90050CommonLogger.log(daoLog.getActiveConnection(), sAplID, SUBSYSTEM, 
                  FUNCTION_ID, sUserID, 
                  "MSTD7000BINF", "S", 
                  msgArgs, 2, 
                  3, getClass());
              alreadyLogBegin = true;
            } 
            lParam = new ArrayList();
            lParam.add(obPDIM.getPlant());
            lParam.add(obPDIM.getItem_code());
            lParam.add(location);
            lParam.add(FUNCTION_ID);
            alRet = new ArrayList();
            alRet = this.adapter.executeQuery(this.con, sqlPartInfo.toString(), lParam.toArray());
            Object[] obj = (Object[])alRet.get(0);
            String ITEM_DESC = obj[0].toString();
            String V_EMAIL_TO_DUP = obj[1].toString();
            msgArgs = new ArrayList();
            msgArgs.add("Duplicate found for " + ITEM_DESC + " in NA Section (" + obPDIM.getSerial_no() + 
                ") with ID_NO = " + obPDIM.getId_no() + ", URN = " + obPDIM.getUrn() + ", VIN_NO = " + obPDIM.getVin_no());
            CAA90050CommonLogger.log(daoLog.getActiveConnection(), sAplID, SUBSYSTEM, 
                FUNCTION_ID, sUserID, 
                "MSTD0000AWRN", "P", 
                msgArgs, 3, 
                3, getClass());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" insert into TB_R_EMAIL_DUP_QUEUE ");
            stringBuilder.append(" values ( ");
            stringBuilder.append("     TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') || '_' || TO_CHAR(EMAIL_SEQ.NEXTVAL) ");
            stringBuilder.append("    ,? ");
            stringBuilder.append("    ,? ");
            stringBuilder.append("    ,? ");
            stringBuilder.append("    ,?   ");
            stringBuilder.append("   ,?     ");
            stringBuilder.append("   ,'NA_SECTION_DUP'  ");
            stringBuilder.append("   ,? ");
            stringBuilder.append("   ,? ");
            stringBuilder.append("   ,? ");
            stringBuilder.append("   ,? ");
            stringBuilder.append("   )  ");
            lParam = new ArrayList();
            lParam.add(obPDIM.getPlant());
            lParam.add(fileName);
            lParam.add(V_EMAIL_TO_DUP);
            lParam.add(ITEM_DESC);
            lParam.add(obPDIM.getSerial_no());
            lParam.add(obPDIM.getId_no());
            lParam.add(String.valueOf(obPDIM.getBody_no()) + "_" + obPDIM.getUrn());
            lParam.add(obPDIM.getVin_no());
            lParam.add(String.valueOf(ITEM_DESC) + " is duplicated");
            this.adapter.executeUpdate(this.con, stringBuilder.toString(), lParam.toArray());
          } 
        } 
        StringBuilder sql = new StringBuilder();
        if (!alreadyInsertVhdHeader) {
          sql.append(" MERGE INTO TB_VH_NTF_H H              ");
          sql.append(" USING (                               ");
          sql.append("       SELECT                          ");
          sql.append("            ? IDENT_NO,              ");
          sql.append("            ? URN,                    ");
          sql.append("            ? VIN_NO,                 ");
          sql.append("            ? BODY_NO                 ");
          sql.append("       FROM DUAL) V                    ");
          sql.append(" ON (H.IDENT_NO = V.IDENT_NO           ");
          sql.append("     AND H.URN = V.URN)                ");
          sql.append(" WHEN NOT MATCHED THEN                 ");
          sql.append("   INSERT                              ");
          sql.append("     (IDENT_NO,                        ");
          sql.append("      PK_IDENT_LINE,                   ");
          sql.append("      PK_IDENT_MTH,                    ");
          sql.append("      PK_IDENT_DATE,                   ");
          sql.append("      PK_IDENT_SNO,                    ");
          sql.append("      URN,                             ");
          sql.append("      VIN_NO,                          ");
          sql.append("      PK_BODY_NO)                      ");
          sql.append("   VALUES                              ");
          sql.append("     (V.IDENT_NO,                      ");
          sql.append("      TRIM(SUBSTR(V.IDENT_NO,1,2)),    ");
          sql.append("      TRIM(SUBSTR(V.IDENT_NO,3,2)),    ");
          sql.append("      TRIM(SUBSTR(V.IDENT_NO,5,2)),    ");
          sql.append("      TRIM(SUBSTR(V.IDENT_NO,7)),      ");
          sql.append("      V.URN,                           ");
          sql.append("      V.VIN_NO,                        ");
          sql.append("      V.BODY_NO||'_'||V.URN)           ");
          lParam = new ArrayList();
          lParam.add(obPDIM.getId_no());
          lParam.add(obPDIM.getUrn());
          lParam.add(obPDIM.getVin_no());
          lParam.add(obPDIM.getBody_no());
          this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
          alreadyInsertVhdHeader = true;
        } 
        sql = new StringBuilder();
        sql.append(" MERGE INTO TB_VH_NTF_D D                 ");
        sql.append(" USING (                                  ");
        sql.append("       SELECT                             ");
        sql.append("            ? IDENT_NO,                 ");
        sql.append("            ? URN,                      ");
        sql.append("            ? VIN_NO,                   ");
        sql.append("            ? BODY_NO,                  ");
        sql.append("            ? ITEM_CODE,                ");
        sql.append("            ? ITEM_GROUP,               ");
        sql.append("            ? SERIAL_NO                 ");
        sql.append("       FROM DUAL                          ");
        sql.append("       ) V                                ");
        sql.append(" ON (D.URN = V.URN                        ");
        sql.append(" AND D.IDENT_NO = V.IDENT_NO              ");
        sql.append(" AND D.ITEM_CODE = V.ITEM_CODE)           ");
        sql.append(" WHEN MATCHED THEN                        ");
        sql.append("   UPDATE                                 ");
        sql.append("      SET D.SERIAL_NO     = V.SERIAL_NO,  ");
        sql.append("          D.UPDATE_DT     = ?      ,      ");
        sql.append("          D.UPDATE_BY     = 'SYSTEM'      ");
        sql.append(" WHEN NOT MATCHED THEN                    ");
        sql.append("   INSERT                                 ");
        sql.append("     (URN,                                ");
        sql.append("      IDENT_NO,                           ");
        sql.append("      ITEM_GROUP,                         ");
        sql.append("      ITEM_CODE,                          ");
        sql.append("      TRACE_TAG_CD,                       ");
        sql.append("      TRACE_TAG_NAME,                     ");
        sql.append("      PRO_TAG_CD,                         ");
        sql.append("      PRO_TAG_NAME,                       ");
        sql.append("      PRO_KEY_CD,                         ");
        sql.append("      VH_TYPE,                            ");
        sql.append("      SERIAL_NO,                          ");
        sql.append("      VH_UNIT,                            ");
        sql.append("      ORG_DATE_TIME,                      ");
        sql.append("      REC_DATE_TIME,                      ");
        sql.append("      CREATE_DT,                          ");
        sql.append("      CREATE_BY,                          ");
        sql.append("      UPDATE_DT,                          ");
        sql.append("      UPDATE_BY)                          ");
        sql.append("   VALUES                                 ");
        sql.append("     (V.URN,                              ");
        sql.append("      V.IDENT_NO,                         ");
        sql.append("      V.ITEM_GROUP,                       ");
        sql.append("      V.ITEM_CODE,                        ");
        sql.append("      NULL,                               ");
        sql.append("      NULL,                               ");
        sql.append("      NULL,                               ");
        sql.append("      NULL,                               ");
        sql.append("      NULL,                               ");
        sql.append("      NULL,                               ");
        sql.append("      V.SERIAL_NO,                        ");
        sql.append("      NULL,                               ");
        sql.append("      NULL,                               ");
        sql.append("      NULL,                               ");
        sql.append("      ?,                                  ");
        sql.append("      'SYSTEM',                           ");
        sql.append("      ?,                                  ");
        sql.append("      'SYSTEM')                           ");
        lParam = new ArrayList();
        lParam.add(obPDIM.getId_no());
        lParam.add(obPDIM.getUrn());
        lParam.add(obPDIM.getVin_no());
        lParam.add(obPDIM.getBody_no());
        lParam.add(obPDIM.getItem_group());
        lParam.add(obPDIM.getItem_code());
        lParam.add(obPDIM.getSerial_no());
        lParam.add(sysdate);
        lParam.add(sysdate);
        lParam.add(sysdate);
        this.adapter.executeUpdate(this.con, sql.toString(), lParam.toArray());
      } 
    } catch (Exception e) {
      status = 4;
      if ("".equals(sAplID)) {
        e.printStackTrace();
      } else {
        msgArgs = new ArrayList();
        msgArgs.add(e.getMessage());
        CAA90050CommonLogger.log(daoLog.getActiveConnection(), sAplID, SUBSYSTEM, 
            FUNCTION_ID, sUserID, 
            "MAA99999AERR", "E", 
            msgArgs, 4, 
            3, getClass());
      } 
    } 
    if (!"".equals(sAplID)) {
      if (3 == status) {
        msgArgs = new ArrayList();
        msgArgs.add(processLogDesc);
        CAA90050CommonLogger.log(daoLog.getActiveConnection(), sAplID, SUBSYSTEM, 
            FUNCTION_ID, sUserID, 
            "MSTD7032BWRN", "E", 
            msgArgs, 3, 
            3, getClass());
      } else if (4 == status) {
        msgArgs = new ArrayList();
        msgArgs.add(processLogDesc);
        CAA90050CommonLogger.log(daoLog.getActiveConnection(), sAplID, SUBSYSTEM, 
            FUNCTION_ID, sUserID, 
            "MSTD7030BINF", "E", 
            msgArgs, 4, 
            3, getClass());
      } 
      regisBatchSendDupEmail(fileName);
    } 
    if (daoLog != null) {
      daoLog.Destructor();
      daoLog = null;
    } 
  }
  
  public String getItemGroup(String sLocation, String itemCode) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append(" SELECT T.ITEM_GROUP ");
    sql.append(" FROM TB_M_VHD_MAP_ITEMCODE T  ");
    sql.append(" WHERE UPPER(T.LOCATION) = UPPER(?)  ");
    sql.append("   AND T.FUNCTION_ID = 'AA9601'  ");
    sql.append("   AND T.ITEM_CODE = ? ");
    lParam.add(sLocation);
    lParam.add(itemCode);
    List ls = this.adapter.executeQuery(this.con, sql.toString(), lParam.toArray());
    String itemGroup = "";
    if (ls != null && ls.size() > 0)
      itemGroup = (String) ls.get(0); 
    return itemGroup;
  }
  
  public String getBuyOffTP(String identNo) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append(" SELECT TP FROM TB_VHD_TRACKINGPOINT T  ");
    sql.append(" WHERE PK_FUNC = 'BO'  ");
    sql.append("   AND PK_IDENT_LINE = TRIM(SUBSTR(?,1,2)) ");
    lParam.add(identNo);
    List ls = this.adapter.executeQuery(this.con, sql.toString(), lParam.toArray());
    String buyOffTP = "";
    if (ls != null && ls.size() > 0)
      buyOffTP = (String) ls.get(0); 
    return buyOffTP;
  }
  
  public void regisBatchSendDupEmail(String fileName) throws CSTD0056CommonException {
    CSTD0068ConfigurationResolver resolver = new CSTD0068ConfigurationResolver("th/co/toyota/rvhd/resources/AA9Resources.properties");
    CSTD0068ConfigurationResolver resolverSTD = new CSTD0068ConfigurationResolver();
    String batchID = resolver.getProperty("NA_SECTION_DUP_EMAIL_BATCH");
    String projectCode = resolverSTD.getProperty("sysid").toUpperCase();
    CSTD0080BatchInfoVO batch = new CSTD0080BatchInfoVO();
    batch.setRequestID("NA_PART");
    batch.setBatchID(batchID);
    batch.setRequestBy("SYSTEM");
    batch.setProjectCode(projectCode);
    ArrayList batchParams = new ArrayList();
    batchParams.add(fileName);
    batch.setParameters(batchParams);
    CSTD0080BatchQueueRequest queue = new CSTD0080BatchQueueRequest();
    if (!queue.postBatch(batch))
      throw CSTD0054ErrorUtil.generateError("MSTD1009AERR", "Unable to post in Batch Framework."); 
  }
  
  public String checkUseWebServiceAndHaveBCControlNTFSetup(String plant, String tp) throws Exception {
    StringBuilder sql = new StringBuilder();
    List lParam = new ArrayList();
    sql.append(" SELECT CASE WHEN VALUE = 'Y' AND    ");
    sql.append(" (SELECT COUNT(*) FROM TB_M_BC_TP_CNTRL_NTF WHERE PLANT = ? AND TP = ?) > 0 THEN 'Y' ELSE 'N' END FLAG   ");
    sql.append(" FROM TB_M_SYSTEM WHERE CATEGORY = 'COMMON' AND SUB_CATEGORY = 'WEBSERVICE' AND CD = 'NTF'  ");
    lParam.add(plant);
    lParam.add(tp);
    List result = this.adapter.executeQuery(this.con, sql.toString(), lParam.toArray());
    String FLAG = "N";
    if (result != null && result.size() > 0)
      FLAG = (String) result.get(0); 
    return FLAG;
  }
}
