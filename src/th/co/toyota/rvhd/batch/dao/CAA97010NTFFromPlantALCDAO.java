package th.co.toyota.rvhd.batch.dao;

import th.co.toyota.standard.log.CSTD0050CommonLogger;
import th.co.toyota.standard.db.CSTD0061DirectSQLAdapter;
//import th.co.toyota.rvhd.download.dao.CAA91000ReportUtilDAO;
import java.math.BigDecimal;
import th.co.toyota.rvhd.batch.ntf.NTFDetail;
import th.co.toyota.rvhd.batch.ntf.NTFProcessQueue;
import th.co.toyota.rvhd.batch.ntf.PDIMItemVO;
import java.sql.ResultSet;
import th.co.toyota.rvhd.batch.ntf.NTFMappingItem;
import java.util.HashMap;
import java.sql.Connection;
import th.co.toyota.rvhd.common.db.CAA90017OracleAdapter;
import th.co.toyota.rvhd.shared.constant.CAA90001Constant;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import th.co.toyota.rvhd.common.dao.CAA90011BaseBatchDAO;

public class CAA97010NTFFromPlantALCDAO extends CAA90011BaseBatchDAO
{
    private Logger log;
    static /* synthetic */ Class class$0;
    
    public CAA97010NTFFromPlantALCDAO(final Logger log) throws SQLException {
        this.log = null;
        this.log = log;
    }
    
    public List getNTFProcessQueue(final int port, final String reProcess) throws Exception {
        final List lParam = new ArrayList();
        final StringBuilder sql = new StringBuilder();
        sql.append(" SELECT T.PORT || '' PORT,                       ");
        sql.append("        T.BC_SEQ_NO || '' BC_SEQ_NO,                  ");
        sql.append("        T.DOC_NO,                      ");
        sql.append("        T.ID_NO,                      ");
        sql.append("        T.URN,                        ");
        sql.append("        T.VIN_NO,                     ");
        sql.append("        T.BODY_NO,                    ");
        sql.append("        T.PLANT,                      ");
        sql.append("        T.TP,                         ");
        sql.append("        T.LINE,                         ");
        sql.append("        TO_CHAR(T.LO_DT, 'YYYYMMDDHH24MISS') LO_DT, ");
        sql.append("        TO_CHAR(T.UNIQUE_KEY, 'YYYYMMDDHH24MISS') UNIQUE_KEY ");
        if (CAA90001Constant.REPROCESS_FLAG.equalsIgnoreCase(reProcess)) {
            sql.append("   FROM TB_P_NTF_WS_PROCESS T         ");
        }
        else {
            sql.append("   FROM TB_R_NTF_WS_PROCESS T        ");
            sql.append("  WHERE T.PORT = ?       \t");
            lParam.add(new StringBuffer().append(port).toString());
        }
        sql.append("  ORDER BY T.UNIQUE_KEY, T.BC_SEQ_NO   ");
        final CAA90017OracleAdapter adapter = this.adapter;
        final Connection con = this.con;
        final String string = sql.toString();
        final Object[] array = lParam.toArray();
        Class class$0;
        if ((class$0 = CAA97010NTFFromPlantALCDAO.class$0) == null) {
            try {
                class$0 = (CAA97010NTFFromPlantALCDAO.class$0 = Class.forName("th.co.toyota.rvhd.batch.ntf.NTFProcessQueue"));
            }
            catch (ClassNotFoundException ex) {
                throw new NoClassDefFoundError(ex.getMessage());
            }
        }
        return adapter.executeQuery(con, string, (Object)array, class$0.getName());
    }
    
    public void manageNTFProcessQueue(final String port, final String docNo, final int statusCallWebService, final String reProcess, final String errMsg) throws Exception {
        StringBuilder sql = new StringBuilder();
        List lParam = new ArrayList();
        if (CAA90001Constant.REPROCESS_FLAG.equalsIgnoreCase(reProcess)) {
            if (statusCallWebService == 0) {
                sql.append(" DELETE TB_P_NTF_WS_PROCESS ");
                sql.append(" WHERE PORT = ? AND DOC_NO = ? ");
                lParam.add(port);
                lParam.add(docNo);
                this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
            }
            else {
                sql.append(" UPDATE TB_P_NTF_WS_PROCESS SET ERR_MSG = ? ");
                sql.append(" WHERE PORT = ? AND DOC_NO = ? ");
                lParam = new ArrayList();
                lParam.add(errMsg);
                lParam.add(port);
                lParam.add(docNo);
                this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
            }
        }
        else if (statusCallWebService == 0) {
            sql.append(" DELETE TB_R_NTF_WS_PROCESS ");
            sql.append(" WHERE PORT = ? AND DOC_NO = ? ");
            lParam.add(port);
            lParam.add(docNo);
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
        }
        else {
            sql.append(" INSERT INTO TB_P_NTF_WS_PROCESS ");
            sql.append(" SELECT T.*, ? FROM TB_R_NTF_WS_PROCESS T ");
            sql.append(" WHERE T.PORT = ? AND T.DOC_NO = ? ");
            lParam = new ArrayList();
            lParam.add(errMsg);
            lParam.add(port);
            lParam.add(docNo);
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
            sql = new StringBuilder();
            sql.append(" DELETE TB_R_NTF_WS_PROCESS ");
            sql.append(" WHERE PORT = ? AND DOC_NO = ? ");
            lParam = new ArrayList();
            lParam.add(port);
            lParam.add(docNo);
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
        }
    }
    
    public void resetPIDtoNULL(final int port, final String reProcess) throws Exception {
        final StringBuilder sql = new StringBuilder();
        if (CAA90001Constant.REPROCESS_FLAG.equalsIgnoreCase(reProcess)) {
            this.log.info((Object)"resetPIDtoNULL for reProcess = Y ");
            sql.append(" UPDATE TB_M_PDIM_SET_INFO_H SET STATUS_NTF_REPROCESS = '0', PID_NTF_REPROCESS = NULL ");
            sql.append(" WHERE PORT > 0 ");
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)null);
        }
        else {
            this.log.info((Object)("resetPIDtoNULL for port =" + port));
            sql.append(" UPDATE TB_M_PDIM_SET_INFO_H SET STATUS_NTF = '0', PID_NTF = NULL ");
            sql.append(" WHERE PORT = ").append(port);
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)null);
        }
    }
    
    public HashMap getVHMappingWithNTF(final String location) throws Exception {
        final StringBuilder sql = new StringBuilder();
        sql.append(" SELECT T.TRACE_TAG_CD,               ");
        sql.append("        T.ITEM_CODE,                  ");
        sql.append("        T.ITEM_GROUP,                 ");
        sql.append("        T.LENGTH_CHK,                 ");
        sql.append("        T.ITEM_DESC                   ");
        sql.append(" FROM TB_M_VHD_MAP_ITEMCODE T            ");
        sql.append(" WHERE UPPER(T.LOCATION) = UPPER('" + location + "') AND T.FUNCTION_ID = 'AA9701'           ");
        sql.append(" ORDER BY T.ITEM_GROUP, T.ITEM_CODE            ");
        final HashMap map = new HashMap();
        ResultSet rs = null;
        try {
            rs = this.adapter.executeQueryToResulSet(this.con, sql.toString());
            while (rs.next()) {
                final NTFMappingItem item = new NTFMappingItem();
                item.setTraceTagCd(rs.getString("TRACE_TAG_CD"));
                item.setItemCode(rs.getString("ITEM_CODE"));
                item.setItemGroup(rs.getString("ITEM_GROUP"));
                item.setLengthChk(rs.getString("LENGTH_CHK"));
                item.setPartName(rs.getString("ITEM_DESC"));
                map.put(item.getTraceTagCd(), item);
            }
        }
        catch (Exception ex) {}
        return map;
    }
    
    public List getDuplicateBCData(final String strSerialNo, final String itemGroup) throws Exception {
        List alRet = new ArrayList();
        final StringBuilder sql = new StringBuilder();
        final List lParam = new ArrayList();
        sql.append(" SELECT R.URN, ");
        sql.append("       R.ID_NO, ");
        sql.append("       R.VIN_NO, ");
        sql.append("       R.BODY_NO, ");
        sql.append("       R.PLANT, ");
        sql.append("       R.TP, ");
        sql.append("       R.ITEM_CODE ");
        sql.append(" FROM TB_R_BC_ITEMCHK_NTF R ");
        sql.append(" WHERE R.SERIAL_NO = ? ");
        sql.append("      AND NVL(R.ITEM_GROUP,' ') = NVL(?,' ') ");
        sql.append(" ORDER BY R.URN, ");
        sql.append("       R.ID_NO, ");
        sql.append("       R.ITEM_CODE,  ");
        sql.append("       R.PLANT, ");
        sql.append("       R.TP, ");
        sql.append("       R.VIN_NO, ");
        sql.append("       R.BODY_NO ");
        lParam.add(strSerialNo);
        lParam.add(itemGroup);
        alRet = this.adapter.executeQuery(this.con, sql.toString(), (Object)lParam.toArray());
        return alRet;
    }
    
    public List getCheckForDuplicate(final String location, final String strPlant, final String strTP, final String strLine) throws Exception {
        final StringBuilder sql = new StringBuilder();
        final List lParam = new ArrayList();
        sql.append(" SELECT D.CHKRECORD, ");
        sql.append("       D.ITEM_CODE, ");
        sql.append("       M.ITEM_GROUP ");
        sql.append(" FROM TB_M_PDIM_SET_INFO_D D ");
        sql.append(" LEFT JOIN TB_M_VHD_MAP_ITEMCODE M ");
        sql.append(" ON M.ITEM_CODE = D.ITEM_CODE ");
        sql.append(" AND UPPER(M.LOCATION) = UPPER('" + location + "')  ");
        sql.append(" AND M.FUNCTION_ID = 'AA9701'  ");
        sql.append(" WHERE D.MACHINE = (SELECT H.MACHINE ");
        sql.append("                   FROM TB_M_PDIM_SET_INFO_H H ");
        sql.append("                   WHERE H.PLANT = ? ");
        sql.append("                         AND H.TP = ? ");
        sql.append("                         AND H.LINE = ? ");
        sql.append("                         AND H.SYSTEM = ? ");
        sql.append("                         AND ROWNUM = 1) ");
        sql.append("      AND D.SYSTEM = ? ");
        sql.append("      AND EXISTS(SELECT 0 ");
        sql.append("                 FROM TB_M_BC_TP_CNTRL_NTF C ");
        sql.append("                 WHERE C.PLANT = ? ");
        sql.append("                       AND C.TP = ? ");
        sql.append("                       AND C.ITEM_CODE = D.ITEM_CODE) ");
        sql.append("      AND SUBSTR(D.ITEM_CODE, 1, 1) = 'N' ");
        sql.append(" ORDER BY D.ITEM_CODE ");
        lParam.add(strPlant);
        lParam.add(strTP);
        lParam.add(strLine);
        lParam.add("SYS02");
        lParam.add("SYS02");
        lParam.add(strPlant);
        lParam.add(String.valueOf(strLine) + strTP);
        return this.adapter.executeQuery(this.con, sql.toString(), (Object)lParam.toArray());
    }
    
    public void mergeBCData(final String strItemGroup, final String strItemCode, final String strSerialNo, final PDIMItemVO obVO, final String sysdate, final NTFProcessQueue ntfQueue, final NTFDetail ntfdetail) throws Exception {
        if (strSerialNo == null) {
            return;
        }
        if (strSerialNo.trim().length() == 0) {
            return;
        }
        final StringBuilder sql = new StringBuilder();
        final List lParam = new ArrayList();
        sql.append("MERGE INTO TB_R_BC_ITEMCHK_NTF T ");
        sql.append("USING (SELECT ? ITEM_GROUP,  ");
        sql.append("              ? ITEM_CODE,  ");
        sql.append("              ? SERIAL_NO,  ");
        sql.append("              ? URN,  ");
        sql.append("              ? ID_NO,  ");
        sql.append("              ? VIN_NO,  ");
        sql.append("              ? BODY_NO,  ");
        sql.append("              ? PLANT,  ");
        sql.append("              ? TP, ");
        sql.append("              ? BC_SEQ_NO, ");
        sql.append("              ? PRO_TAG_CD, ");
        sql.append("              ? PRO_TAG_NAME, ");
        sql.append("              ? PRO_KEY_CD, ");
        sql.append("              ? TRACE_TAG_CD, ");
        sql.append("              ? TRACE_TAG_NAME, ");
        sql.append("              ? TRACE_TYPE, ");
        sql.append("              ? UNIT, ");
        sql.append("              ? REC_DATE_TIME, ");
        sql.append("              ? ORG_DATE_TIME, ");
        sql.append("              ? CREATE_BY, ");
        sql.append("              TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE, ");
        sql.append("              ? UPDATE_BY, ");
        sql.append("              TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE ");
        sql.append("       FROM DUAL) SRC ");
        sql.append("ON (T.URN = SRC.URN ");
        sql.append("    AND T.ID_NO = SRC.ID_NO ");
        sql.append("    AND T.ITEM_CODE = SRC.ITEM_CODE ");
        sql.append("    AND T.ITEM_GROUP = SRC.ITEM_GROUP) ");
        sql.append("WHEN MATCHED THEN ");
        sql.append("     UPDATE SET T.SERIAL_NO = SRC.SERIAL_NO, ");
        sql.append("                T.VIN_NO = SRC.VIN_NO, ");
        sql.append("                T.BODY_NO = SRC.BODY_NO, ");
        sql.append("                T.PLANT = SRC.PLANT, ");
        sql.append("                T.TP = SRC.TP, ");
        sql.append("                T.BC_SEQ_NO = SRC.BC_SEQ_NO, ");
        sql.append("                T.PRO_TAG_CD = SRC.PRO_TAG_CD, ");
        sql.append("                T.PRO_TAG_NAME = SRC.PRO_TAG_NAME, ");
        sql.append("                T.PRO_KEY_CD = SRC.PRO_KEY_CD, ");
        sql.append("                T.TRACE_TAG_CD = SRC.TRACE_TAG_CD, ");
        sql.append("                T.TRACE_TAG_NAME = SRC.TRACE_TAG_NAME, ");
        sql.append("                T.TRACE_TYPE = SRC.TRACE_TYPE, ");
        sql.append("                T.UNIT = SRC.UNIT, ");
        sql.append("                T.REC_DATE_TIME = SRC.REC_DATE_TIME, ");
        sql.append("                T.ORG_DATE_TIME = SRC.ORG_DATE_TIME, ");
        sql.append("                T.UPDATE_BY = SRC.UPDATE_BY, ");
        sql.append("                T.UPDATE_DATE = SRC.UPDATE_DATE ");
        sql.append("WHEN NOT MATCHED THEN ");
        sql.append("     INSERT (ITEM_GROUP, ");
        sql.append("             ITEM_CODE, ");
        sql.append("             SERIAL_NO, ");
        sql.append("             URN, ");
        sql.append("             ID_NO, ");
        sql.append("             VIN_NO, ");
        sql.append("             BODY_NO, ");
        sql.append("             PLANT, ");
        sql.append("             TP, ");
        sql.append("             BC_SEQ_NO, ");
        sql.append("             PRO_TAG_CD, ");
        sql.append("             PRO_TAG_NAME, ");
        sql.append("             PRO_KEY_CD, ");
        sql.append("             TRACE_TAG_CD, ");
        sql.append("             TRACE_TAG_NAME, ");
        sql.append("             TRACE_TYPE, ");
        sql.append("             UNIT, ");
        sql.append("             REC_DATE_TIME, ");
        sql.append("             ORG_DATE_TIME, ");
        sql.append("             CREATE_BY, ");
        sql.append("             CREATE_DATE, ");
        sql.append("             UPDATE_BY, ");
        sql.append("             UPDATE_DATE) ");
        sql.append("     VALUES (SRC.ITEM_GROUP, ");
        sql.append("             SRC.ITEM_CODE, ");
        sql.append("             SRC.SERIAL_NO, ");
        sql.append("             SRC.URN, ");
        sql.append("             SRC.ID_NO, ");
        sql.append("             SRC.VIN_NO, ");
        sql.append("             SRC.BODY_NO, ");
        sql.append("             SRC.PLANT, ");
        sql.append("             SRC.TP, ");
        sql.append("             SRC.BC_SEQ_NO, ");
        sql.append("             SRC.PRO_TAG_CD, ");
        sql.append("             SRC.PRO_TAG_NAME, ");
        sql.append("             SRC.PRO_KEY_CD, ");
        sql.append("             SRC.TRACE_TAG_CD, ");
        sql.append("             SRC.TRACE_TAG_NAME, ");
        sql.append("             SRC.TRACE_TYPE, ");
        sql.append("             SRC.UNIT, ");
        sql.append("             SRC.REC_DATE_TIME, ");
        sql.append("             SRC.ORG_DATE_TIME, ");
        sql.append("             SRC.CREATE_BY, ");
        sql.append("             SRC.CREATE_DATE, ");
        sql.append("             SRC.UPDATE_BY, ");
        sql.append("             SRC.UPDATE_DATE)");
        lParam.add(strItemGroup);
        lParam.add(strItemCode);
        lParam.add(strSerialNo);
        lParam.add(obVO.getUrn());
        lParam.add(obVO.getId_no());
        lParam.add(obVO.getVin_no());
        lParam.add(obVO.getBody_no());
        lParam.add(obVO.getPlant());
        lParam.add(obVO.getTp());
        lParam.add(obVO.getBc_seq_no());
        lParam.add(ntfdetail.getPro_tag_cd());
        lParam.add(ntfdetail.getPro_tag_name());
        lParam.add(ntfdetail.getPro_key_cd());
        lParam.add(ntfdetail.getTrace_tag_cd());
        lParam.add(ntfdetail.getTrace_tag_name());
        lParam.add(ntfdetail.getType());
        lParam.add(ntfdetail.getUnit());
        lParam.add(ntfdetail.getRec_date_time());
        lParam.add(ntfdetail.getOrg_date_time());
        lParam.add("SYSTEM");
        lParam.add(sysdate);
        lParam.add("SYSTEM");
        lParam.add(sysdate);
        this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
    }
    
    public void insertBCHistory(final String strItemGroup, final String strItemCode, final String strSerialNo, final PDIMItemVO obVO) throws Exception {
        final StringBuilder sql = new StringBuilder();
        final List lParam = new ArrayList();
        sql.append("INSERT INTO TB_R_BC_HISTORY T ");
        sql.append("       (T.UNIQUE_KEY, ");
        sql.append("        T.ITEM_GROUP, ");
        sql.append("        T.ITEM_CODE, ");
        sql.append("        T.SERIAL_NO, ");
        sql.append("        T.URN, ");
        sql.append("        T.ID_NO, ");
        sql.append("        T.VIN_NO, ");
        sql.append("        T.BODY_NO, ");
        sql.append("        T.PLANT, ");
        sql.append("        T.TP, ");
        sql.append("        T.BC_SEQ_NO, ");
        sql.append("        T.SEQ_ID) ");
        sql.append("VALUES (TO_DATE(?,'YYYYMMDDHH24MISS'), ");
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
        lParam.add(obVO.getUnique_key());
        lParam.add(strItemGroup);
        lParam.add(strItemCode);
        lParam.add(strSerialNo);
        lParam.add(obVO.getUrn());
        lParam.add(obVO.getId_no());
        lParam.add(obVO.getVin_no());
        lParam.add(obVO.getBody_no());
        lParam.add(obVO.getPlant());
        lParam.add(obVO.getTp());
        lParam.add(obVO.getBc_seq_no());
        this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
    }
    
    public List getNext_BCSeqNo() throws SQLException {
        List lsRet = new ArrayList();
        final String sqlSelect = "SELECT SEQ_PDIM_BCSEQ.NEXTVAL FROM DUAL";
        lsRet = this.adapter.executeQuery(this.con, sqlSelect, (Object)null);
        return lsRet;
    }
    
    private boolean checkAlreadyInsertHeader(final String tableName, final String docNo) throws SQLException {
        final StringBuilder sql = new StringBuilder();
        sql.append(" SELECT (CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END) INSERTED ");
        sql.append(" FROM ").append(tableName);
        sql.append(" WHERE DOC_NO = ? ");
        final List ls = this.adapter.executeQuery(this.con, sql.toString(), (Object)new String[] { docNo });
        return ls != null && ls.size() > 0 && "Y".equals(ls.get(0));
    }
    
    public BigDecimal insertPDIM_DUPLICATE_H(final PDIMItemVO obVO) throws SQLException {
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
        lParam.add(obVO.getDocNo());
        lParam.add(obVO.getUnique_key());
        lParam.add(obVO.getBc_seq_no());
        lParam.add(obVO.getUrn());
        lParam.add(obVO.getVin_no());
        lParam.add(obVO.getBody_no());
        lParam.add(obVO.getId_no());
        lParam.add(obVO.getPlant());
        lParam.add(obVO.getTp());
        lParam.add(obVO.getLo_dt());
        List list = this.adapter.executeQuery(this.con, sql.toString(), (Object)lParam.toArray());
        BigDecimal refID_H = new BigDecimal(0);
        if (list != null && list.size() > 0) {
            refID_H = (BigDecimal) list.get(0);
        }
        else {
            sql = new StringBuilder();
            sql.append(" SELECT MAX(REF_ID) FROM TB_R_PDIM_DUPLICATE_H WHERE DOC_NO = '" + obVO.getDocNo() + "'  ");
            list = this.adapter.executeQuery(this.con, sql.toString(), (Object)null);
            BigDecimal maxRefId = new BigDecimal(0);
            if (list != null && list.size() > 0) {
                final BigDecimal tmp = (BigDecimal) list.get(0);
                if (tmp != null) {
                    maxRefId = tmp;
                }
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
            lParam.add(obVO.getDocNo());
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
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
        }
        return refID_H;
    }
    
    public void insertPDIM_DUPLICATE_D(final List list, final String itemCodePdim) throws Exception {
        if (list != null) {
            final StringBuilder sql = new StringBuilder();
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
            sql.append("   TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') ,  ");
            sql.append("   ? ,  ");
            sql.append("   ? ,  ");
            sql.append("   ? ,  ");
            sql.append("   ? ,  ");
            sql.append("   ?   ");
            sql.append(" ) ");
            for (int ix = 0; ix < list.size(); ++ix) {
                final PDIMItemVO obVO = (PDIMItemVO) list.get(ix);
                final BigDecimal refID_H = this.insertPDIM_DUPLICATE_H(obVO);
                final List lParam = new ArrayList();
                lParam.add(obVO.getDocNo());
                lParam.add(refID_H);
                lParam.add(obVO.getTracking_time());
                lParam.add(obVO.getItem_group());
                lParam.add(obVO.getItem_code());
                lParam.add(obVO.getSerial_no());
                lParam.add(obVO.getSource_db());
                lParam.add(itemCodePdim);
                this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
            }
        }
    }
    
    public void insertPDIM_HISTORY_H(final PDIMItemVO obVO) {
        final StringBuilder sql = new StringBuilder();
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
        final List lParam = new ArrayList();
        lParam.add(obVO.getDocNo());
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
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
        }
        catch (Exception ex) {}
    }
    
    public void updatePDIM_HISTORY_H(final PDIMItemVO obVO) {
        final StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE TB_R_PDIM_HISTORY_H ");
        sql.append(" SET  ");
        sql.append("   VIN_NO = (CASE WHEN NVL(VIN_NO,' ') = ' ' THEN ? ELSE VIN_NO END),  ");
        sql.append("   BODY_NO = (CASE WHEN NVL(BODY_NO,' ') = ' ' THEN ? ELSE BODY_NO END),  ");
        sql.append("   ID_NO  = (CASE WHEN NVL(ID_NO,' ') = ' ' THEN ? ELSE ID_NO END),  ");
        sql.append("   LO_DT = (CASE WHEN LO_DT IS NULL THEN TO_DATE(?, 'YYYYMMDDHH24MISS') ELSE LO_DT END) ");
        sql.append(" WHERE  DOC_NO = ?  ");
        final List lParam = new ArrayList();
        lParam.add(obVO.getVin_no());
        lParam.add(obVO.getBody_no());
        lParam.add(obVO.getId_no());
        lParam.add(obVO.getLo_dt());
        lParam.add(obVO.getDocNo());
        try {
            this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
        }
        catch (Exception ex) {}
    }
    
    public void insertPDIM_HISTORY_D(final PDIMItemVO obVO) throws Exception {
        final StringBuilder sql = new StringBuilder();
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
        sql.append("   TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') ,  ");
        sql.append("   ? ,  ");
        sql.append("   ? ,  ");
        sql.append("   ?    ");
        sql.append(" ) ");
        final boolean flag = this.checkAlreadyInsertHeader("TB_R_PDIM_HISTORY_H", obVO.getDocNo());
        if (!flag) {
            this.insertPDIM_HISTORY_H(obVO);
        }
        else {
            this.updatePDIM_HISTORY_H(obVO);
        }
        final List lParam = new ArrayList();
        lParam.add(obVO.getDocNo());
        lParam.add(obVO.getTracking_time());
        lParam.add(obVO.getItem_group());
        lParam.add(obVO.getItem_code());
        lParam.add(obVO.getSerial_no());
        this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
    }
    
//    public String getAPLID() {
//        String sAPLID = null;
//        try {
//            sAPLID = new CAA91000ReportUtilDAO().retrieveAPLID((CSTD0061DirectSQLAdapter)this.adapter);
//        }
//        catch (Exception e) {
//            CSTD0050CommonLogger.logStackTrace((Throwable)e);
//        }
//        return sAPLID;
//    }
    
    public void deletePDIMHistoryForNotDupData() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("  DELETE FROM TB_R_PDIM_HISTORY_D T            \n ");
        sql.append("  WHERE NOT EXISTS (SELECT 'X'                 \n ");
        sql.append("           FROM (SELECT P.DOC_NO               \n ");
        sql.append("                   FROM TB_R_NTF_WS_PROCESS P  \n ");
        sql.append("                  WHERE P.DOC_NO = T.DOC_NO    \n ");
        sql.append("                 UNION ALL                     \n ");
        sql.append("                 SELECT P.DOC_NO               \n ");
        sql.append("                   FROM TB_P_NTF_WS_PROCESS P  \n ");
        sql.append("                  WHERE P.DOC_NO = T.DOC_NO    \n ");
        sql.append("                ) P      \n ");
        sql.append("           )          \n ");
        sql.append("    AND NOT EXISTS (SELECT 'x'                 \n ");
        sql.append(" \t\t  FROM TB_R_PDIM_DUPLICATE_H D        \n ");
        sql.append(" \t      WHERE D.DOC_NO = T.DOC_NO)          \n ");
        this.adapter.executeUpdate(this.con, sql.toString(), (Object)null);
        sql = new StringBuilder();
        sql.append("  DELETE FROM TB_R_PDIM_HISTORY_H T            \n ");
        sql.append("  WHERE NOT EXISTS (SELECT 'X'                 \n ");
        sql.append("           FROM (SELECT P.DOC_NO               \n ");
        sql.append("                   FROM TB_R_NTF_WS_PROCESS P  \n ");
        sql.append("                  WHERE P.DOC_NO = T.DOC_NO    \n ");
        sql.append("                 UNION ALL                     \n ");
        sql.append("                 SELECT P.DOC_NO               \n ");
        sql.append("                   FROM TB_P_NTF_WS_PROCESS P  \n ");
        sql.append("                  WHERE P.DOC_NO = T.DOC_NO    \n ");
        sql.append("                ) P      \n ");
        sql.append("           )          \n ");
        sql.append("    AND NOT EXISTS (SELECT 'x'                 \n ");
        sql.append(" \t\t  FROM TB_R_PDIM_DUPLICATE_H D        \n ");
        sql.append(" \t      WHERE D.DOC_NO = T.DOC_NO)          \n ");
        this.adapter.executeUpdate(this.con, sql.toString(), (Object)null);
    }
    
    public void insertEmailErrQueue(final String fileName, final String identNo, final String bodyNo, final String vinNo, final String msg) throws Exception {
        final StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO TB_R_EMAIL_ERR_QUEUE \n");
        sql.append("   (CREATE_ID, \n");
        sql.append("    PLANT_CD, \n");
        sql.append("    IF_FILENAME, \n");
        sql.append("    EMAIL_ADDY, \n");
        sql.append("    MODULE, \n");
        sql.append("    IDENT_NO, \n");
        sql.append("    BODY_NO, \n");
        sql.append("    VIN_NO, \n");
        sql.append("    MSG) \n");
        sql.append(" VALUES \n");
        sql.append("   (TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') || '_' || TO_CHAR(EMAIL_SEQ.NEXTVAL), \n");
        sql.append("    (SELECT X.SHORT_PLANT_NAME \n");
        sql.append("       FROM TB_VHD_PLANT X \n");
        sql.append("      WHERE X.PK_PLANT_CD >= ' ' \n");
        sql.append("        AND X.IDENT_LINE LIKE '%' || SUBSTR(?, 1, 2) || '%' \n");
        sql.append("        AND ROWNUM = 1), \n");
        sql.append("    ?, \n");
        sql.append("    (SELECT M.EMAIL_ADDY \n");
        sql.append("       FROM TB_M_EMAIL_MASTER M \n");
        sql.append("      WHERE M.GROUP_ID = 'NTF_PDIM_ERR' \n");
        sql.append("        AND M.PLANT_CD = \n");
        sql.append("            (SELECT X.SHORT_PLANT_NAME \n");
        sql.append("               FROM TB_VHD_PLANT X \n");
        sql.append("              WHERE X.PK_PLANT_CD >= ' ' \n");
        sql.append("                AND X.IDENT_LINE LIKE '%' || SUBSTR(?, 1, 2) || '%' \n");
        sql.append("                AND ROWNUM = 1) \n");
        sql.append("        AND ROWNUM = 1), \n");
        sql.append("    'NTF_PDIM_ERR', \n");
        sql.append("    ?, \n");
        sql.append("    ?, \n");
        sql.append("    ?, \n");
        sql.append("    ?) \n");
        final List lParam = new ArrayList();
        lParam.add(identNo);
        lParam.add(fileName);
        lParam.add(identNo);
        lParam.add(identNo);
        lParam.add(bodyNo);
        lParam.add(vinNo);
        lParam.add(msg);
        this.adapter.executeUpdate(this.con, sql.toString(), (Object)lParam.toArray());
    }
    
    public String[] TestOnly() throws SQLException {
        final StringBuilder sql = new StringBuilder();
        sql.append(" SELECT SUB_CATEGORY, CD, VALUE FROM TB_M_SYSTEM ");
        sql.append(" WHERE CATEGORY = 'NTFWEBSERVICE' ");
        final List ls = this.adapter.executeQuery(this.con, sql.toString(), (Object)null);
        if (ls != null && ls.size() > 0) {
            final Object[] tmp = (Object[]) ls.get(0);
            return new String[] { (String)tmp[0], (String)tmp[1], (String)tmp[2] };
        }
        return null;
    }
}