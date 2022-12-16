package th.co.toyota.rvhd.batch.ntf;

import th.co.toyota.standard.batch.handler.CSTD0082ScriptHandler;
import java.io.File;
import java.util.HashMap;
import th.co.toyota.rvhd.shared.util.CAA90007FormatUtil;
import java.sql.Timestamp;
import java.util.Date;
import th.co.toyota.rvhd.common.db.CAA90050CommonLogger;
import th.co.toyota.rvhd.common.dao.CAA90011BaseBatchDAO;
import th.co.toyota.rvhd.batch.bo.CAA97010NTFFromPlantALCBO;
import java.io.OutputStream;
import com.google.gson.Gson;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import th.co.toyota.rvhd.shared.constant.CAA90001Constant;
import java.util.Properties;
import org.apache.log4j.Logger;

public class CAA97010NTFFromPlantALCByPort implements Runnable
{
    private CAA97010NTFFromPlantALCMain server;
    private String sLocation;
    int intID;
    private Logger log;
    private int port;
    private String prefixLog;
    private String reProcess;
    private String ntfQueueErrMsg;
    private String webServiceURL;
    private String X_ALC_STICKET;
    private final String DS_PDIM = "PDIM";
    private final String DS_VHD = "VHD GALC";
    private final String SUBSYSTEM = "AA95";
    private final String FUNCTION_ID = "AA9701";
    private final String sUserID = "SYSTEM";
    private int lengthChkOfSerial;
    private String[] testOnly;
    static /* synthetic */ Class class$0;
    static /* synthetic */ Class class$1;
    
    public CAA97010NTFFromPlantALCByPort(final int id, final CAA97010NTFFromPlantALCMain server, final String sLocation, final int port, final String sreProcess) {
        this.sLocation = null;
        this.log = null;
        this.port = 0;
        this.prefixLog = "";
        this.reProcess = "N";
        this.ntfQueueErrMsg = "";
        this.webServiceURL = "";
        this.X_ALC_STICKET = "";
        this.lengthChkOfSerial = 30;
        this.server = server;
        this.intID = id;
        this.sLocation = sLocation;
        this.port = port;
        this.reProcess = sreProcess;
        final String ftp_iniFile = "/th/co/toyota/rvhd/batch/ntf/pdim_ntf.properties";
        InputStream is = null;
        final Properties prop = new Properties();
        this.prefixLog = "Thread no." + this.intID + " ";
        try {
            is = this.getClass().getResourceAsStream(ftp_iniFile);
            prop.load(is);
            if (this.log == null) {
                if (CAA90001Constant.REPROCESS_FLAG.equals(this.reProcess)) {
                    System.setProperty("strport", "Reprocess");
                }
                else {
                    System.setProperty("strport", Integer.toString(port));
                }
                DOMConfigurator.configure(prop.getProperty("log4jConfig2"));
            }
            this.log = LogManager.getLogger((Class)this.getClass());
            this.webServiceURL = prop.getProperty("WEB_SERVICE_URL");
            this.X_ALC_STICKET = prop.getProperty("X_ALC_STICKET");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.log.error((Object)(String.valueOf(this.prefixLog) + "Exception: " + e.getMessage()));
            this.log.trace((Object)e);
        }
    }
    
    private List callWebService(final NTFProcessQueue ntfQueue) {
        final List ntfList = new ArrayList();
        String jsonStr = "";
        this.log.info((Object)"Begin Call Web Service Plant ALC ");
        final StringBuilder jsonInStr = new StringBuilder();
        final StringBuilder condi = new StringBuilder();
        condi.append("\"SF.V026\",");
        condi.append("\"SF.V001\",");
        condi.append("\"SF.T518\",");
        condi.append("\"SF.V011\",");
        condi.append("\"TRACE_TAG_CD\",");
        condi.append("\"TYPE\",");
        condi.append("\"VALUE\",");
        condi.append("\"REC_DATE_TIME\",");
        condi.append("\"LO_DATE\",");
        condi.append("\"PRO_TAG_CD\",");
        condi.append("\"PRO_TAG_NAME\",");
        condi.append("\"PRO_KEY_CD\",");
        condi.append("\"TRACE_TAG_NAME\",");
        condi.append("\"UNIT\",");
        condi.append("\"ORG_DATE_TIME\"");
        final String searchCondition = "\"Search_Condition\":\"SF.V026 = '" + ntfQueue.getUrn() + "'\" ";
        final String outputItem = "\"Output_Item\":[" + condi.toString() + "]";
        final String orderType = "\"Output_File_Type\":\"JSON\"";
        final String sortItem = "\"Sort_Item\":[\"REC_DATE_TIME\",\"TRACE_TAG_CD\"]";
        final String sortType = "\"Sort_Type\":[\"asc\",\"desc\"]";
        jsonInStr.append("{");
        jsonInStr.append(searchCondition).append(",");
        jsonInStr.append(outputItem).append(",");
        jsonInStr.append(orderType).append(",");
        jsonInStr.append(sortItem).append(",");
        jsonInStr.append(sortType);
        jsonInStr.append("}");
        OutputStream os = null;
        HttpURLConnection conn = null;
        int responseCode = 0;
        int reConCnt = 1;
        while (reConCnt <= 3) {
            try {
                URL url = new URL(this.webServiceURL);
                if (this.testOnly != null && this.testOnly.length == 3) {
                    this.log.info((Object)("param processResult=" + this.testOnly[0]));
                    this.log.info((Object)("param blankCase=" + this.testOnly[1]));
                    this.log.info((Object)("param returnPartCount=" + this.testOnly[2]));
                    url = new URL(String.valueOf(this.webServiceURL) + "?processResult=" + this.testOnly[0] + "&blankCase=" + this.testOnly[1] + "&returnPartCount=" + this.testOnly[2]);
                }
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                this.log.info((Object)("Connect URL : " + url.toString() + " round " + reConCnt));
                this.log.info((Object)"Post with json body is >>> ");
                this.log.info((Object)jsonInStr.toString());
                final byte[] postData = jsonInStr.toString().getBytes(StandardCharsets.UTF_8);
                final int postDataLength = postData.length;
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("X-ALC-STICKET", this.X_ALC_STICKET);
                if (this.testOnly != null && this.testOnly.length == 3 && "04".equals(this.testOnly[0])) {
                    conn.setRequestProperty("X-ALC-STICKET", String.valueOf(this.X_ALC_STICKET) + "test");
                }
                os = conn.getOutputStream();
                final byte[] input = jsonInStr.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            catch (UnknownHostException e) {
                this.log.error((Object)("UnknownHostException " + e.getMessage()));
                this.ntfQueueErrMsg = "Unknown Host " + e.getMessage();
                reConCnt = 4;
            }
            catch (IOException e2) {
                this.log.error((Object)("IOException " + e2.getMessage()));
                if (e2.getMessage().contains("Connection refused: connect")) {
                    this.ntfQueueErrMsg = "Connect Plant ALC web service time out.";
                }
                else {
                    this.ntfQueueErrMsg = e2.getMessage();
                }
            }
            catch (Exception e3) {
                this.log.error((Object)e3.getMessage());
                reConCnt = 4;
            }
            finally {
                try {
                    responseCode = conn.getResponseCode();
                    this.log.info((Object)("ResponseCode from Plant ALC of round " + reConCnt + " = " + responseCode));
                }
                catch (Exception e4) {
                    e4.printStackTrace();
                    this.log.error((Object)e4.getMessage());
                }
                if (responseCode != 200) {
                    ++reConCnt;
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e5) {
                        e5.printStackTrace();
                    }
                }
                else {
                    this.ntfQueueErrMsg = "";
                    reConCnt = 4;
                }
                if (os != null) {
                    try {
                        os.close();
                    }
                    catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
            }
            try {
                responseCode = conn.getResponseCode();
                this.log.info((Object)("ResponseCode from Plant ALC of round " + reConCnt + " = " + responseCode));
            }
            catch (Exception e4) {
                e4.printStackTrace();
                this.log.error((Object)e4.getMessage());
            }
            if (responseCode != 200) {
                ++reConCnt;
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException e5) {
                    e5.printStackTrace();
                }
            }
            else {
                this.ntfQueueErrMsg = "";
                reConCnt = 4;
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
        }
        this.log.info((Object)("ResponseCode from Plant ALC = " + responseCode));
        if (responseCode == 200) {
            try {
                final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output = "";
                this.log.info((Object)"Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    jsonStr = output;
                }
                this.log.info((Object)("Output =>>> " + jsonStr));
                conn.disconnect();
                final Gson gson2 = new Gson();
                final String s = jsonStr;
                Class class$0;
                if ((class$0 = CAA97010NTFFromPlantALCByPort.class$0) == null) {
                    try {
                        class$0 = (CAA97010NTFFromPlantALCByPort.class$0 = Class.forName("th.co.toyota.rvhd.batch.ntf.NTFData"));
                    }
                    catch (ClassNotFoundException ex) {
                        throw new NoClassDefFoundError(ex.getMessage());
                    }
                }
                final NTFData objCheck = (NTFData)gson2.fromJson(s, class$0);
                final String processResult = objCheck.getHeader().getProcessResult();
                this.log.info((Object)("Process Result=" + processResult));
                if ("00".equals(processResult)) {
                    this.log.info((Object)"Get NTF Part : Java object created from JSON String :");
                    this.log.info((Object)("List JSON object of NTF data=" + objCheck.getHeader().getListCount()));
                    final List list = objCheck.getData();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); ++i) {
                            final Object obj = list.get(i);
                            final Gson gson = new Gson();
                            jsonStr = gson.toJson(obj);
                            final String[] sss = jsonStr.split("\",\"");
                            final StringBuilder newJson = new StringBuilder("");
                            for (int index = 0; index < sss.length; ++index) {
                                final String tmpStr = sss[index];
                                final String[] sss2 = tmpStr.split("\":\"");
                                String tmp1 = sss2[0];
                                String tmp2 = "";
                                if (sss2.length > 1) {
                                    tmp2 = sss2[1];
                                }
                                if (index > 0) {
                                    newJson.append("\",\"");
                                }
                                if (tmp1.indexOf(".") > -1) {
                                    tmp1 = tmp1.replace(".", "_");
                                }
                                newJson.append(tmp1.toLowerCase()).append("\":\"").append(tmp2);
                            }
                            this.log.info((Object)("No. " + i + " jsonStr for convert to NTFDetail =>>> " + newJson.toString()));
                            final Gson gson3 = new Gson();
                            final String string = newJson.toString();
                            Class class$2;
                            if ((class$2 = CAA97010NTFFromPlantALCByPort.class$1) == null) {
                                try {
                                    class$2 = (CAA97010NTFFromPlantALCByPort.class$1 = Class.forName("th.co.toyota.rvhd.batch.ntf.NTFDetail"));
                                }
                                catch (ClassNotFoundException ex2) {
                                    throw new NoClassDefFoundError(ex2.getMessage());
                                }
                            }
                            final NTFDetail ntfData = (NTFDetail)gson3.fromJson(string, class$2);
                            this.log.info((Object)("After convert No." + i + " =>>> URN(SF.V026)=" + ntfData.getSf_v026() + ", IdentNo(SF.V001)=" + ntfData.getSf_v001() + ", VinNo(SF.T518)=" + ntfData.getSf_t518() + ", BodyNo(SF.V011)=" + ntfData.getSf_v011() + ", TRACE_TAG_CD=" + ntfData.getTrace_tag_cd() + ", VALUE=" + ntfData.getValue() + ", REC_DATE_TIME=" + ntfData.getRec_date_time() + "\n"));
                            ntfData.setSf_v026(ntfQueue.getUrn());
                            if (ntfQueue.getIdNo() != null && ntfQueue.getIdNo().length() > 0) {
                                ntfData.setSf_v001(ntfQueue.getIdNo());
                            }
                            if (ntfQueue.getVinNo() != null && ntfQueue.getVinNo().length() > 0) {
                                ntfData.setSf_t518(ntfQueue.getVinNo());
                            }
                            if (ntfQueue.getBodyNo() != null && ntfQueue.getBodyNo().length() > 0) {
                                ntfData.setSf_v011(ntfQueue.getBodyNo());
                            }
                            if (ntfQueue.getLoDt() != null && ntfQueue.getLoDt().length() > 0) {
                                ntfData.setLo_date(ntfQueue.getLoDt());
                            }
                            ntfList.add(ntfData);
                        }
                    }
                }
                else if ("01".equals(processResult)) {
                    this.ntfQueueErrMsg = "NTF Service Process Result is 01 : Abnormal end";
                }
                else if ("02".equals(processResult)) {
                    this.ntfQueueErrMsg = "NTF Service Process Result is 02 : Timeout";
                }
                else if ("03".equals(processResult)) {
                    this.ntfQueueErrMsg = "NTF Service Process Result is 03 : Running process count is Max";
                }
                else if ("04".equals(processResult)) {
                    this.ntfQueueErrMsg = "NTF Service Process Result is 04 : Authentication error";
                }
            }
            catch (IOException e2) {
                e2.printStackTrace();
                this.ntfQueueErrMsg = "99 : Abnormal end, IOException";
            }
            catch (Exception e3) {
                e3.printStackTrace();
                this.ntfQueueErrMsg = "99 : Abnormal end, Json Syntax Exception";
            }
        }
        else if ("".equals(this.ntfQueueErrMsg)) {
            if (404 == responseCode) {
                this.ntfQueueErrMsg = "404 Not Found, Plant ALC web service " + this.webServiceURL;
            }
            else {
                this.ntfQueueErrMsg = "Plant ALC web service not available.";
            }
        }
        this.log.info((Object)"End Call Web Service Plant ALC ");
        return ntfList;
    }
    
    public void run() {
        boolean threadStop = false;
        CAA97010NTFFromPlantALCBO obBO = null;
        try {
            while (true) {
                boolean sleepFlag = false;
                if (obBO == null) {
                    try {
                        obBO = new CAA97010NTFFromPlantALCBO(this.log);
                    }
                    catch (Exception e) {
                        this.log.error((Object)("Exception: " + e.getMessage()));
                        throw e;
                    }
                }
                this.testOnly = obBO.getObDAO().TestOnly();
                final List ntfQueueList = obBO.getObDAO().getNTFProcessQueue(this.port, this.reProcess);
                if (ntfQueueList != null && ntfQueueList.size() > 0) {
                    CAA90011BaseBatchDAO daoLog = new CAA90011BaseBatchDAO();
                    try {
                        this.log.info((Object)("Found NTF Process Queue for call web service is " + ntfQueueList.size()));
                        for (int i = 0; i < ntfQueueList.size(); ++i) {
                            int processStatus = 0;
                            boolean foundLengthError = false;
                            boolean sendErrEmail = false;
                            //final String sAplID = obBO.getObDAO().getAPLID();
                            this.ntfQueueErrMsg = "";
                            final NTFProcessQueue ntfQueue = (NTFProcessQueue) ntfQueueList.get(i);
                            final String processLogDesc = "Retrieve NTF PDIM data from Plant ALC Batch";
                            final String moreDesc = " by Port=" + ntfQueue.getPort() + ", URN=" + ntfQueue.getUrn() + " , Doc No.=" + ntfQueue.getDocNo();
                            ArrayList msgArgs = new ArrayList();
                            msgArgs.add(String.valueOf(processLogDesc) + moreDesc);
                            CAA90050CommonLogger.log(daoLog.getActiveConnection(), "AA95", "AA9701", "SYSTEM", "MSTD7000BINF", "S", msgArgs, 2, 3, (Class)this.getClass());
                            this.log.info((Object)("Process queue " + processLogDesc + moreDesc));
                            final List ntfList = this.callWebService(ntfQueue);
                            int INSERT_PDIM_DUPLICATED = 0;
                            int INSERT_BC_ITEMCHK_NTF = 0;
                            int INSERT_BC_HISTORY = 0;
                            if (ntfList.size() == 0) {
                                if (!"".equals(this.ntfQueueErrMsg)) {
                                    msgArgs = new ArrayList();
                                    msgArgs.add(this.ntfQueueErrMsg);
                                    this.log.debug((Object)this.ntfQueueErrMsg);
                                    CAA90050CommonLogger.log(daoLog.getActiveConnection(), "AA95", "AA9701", "SYSTEM", "MAA99999AERR", "P", msgArgs, 4, 3, (Class)this.getClass());
                                    final String fileName = "AA9701_" + ntfQueue.getDocNo();
                                    obBO.getObDAO().insertEmailErrQueue(fileName, ntfQueue.getIdNo(), ntfQueue.getBodyNo(), ntfQueue.getVinNo(), this.ntfQueueErrMsg);
                                    processStatus = 1;
                                    sendErrEmail = true;
                                }
                                else {
                                    msgArgs = new ArrayList();
                                    msgArgs.add("Not found NTF data in Plant ALC (URN=" + ntfQueue.getUrn() + ")");
                                    this.log.debug(msgArgs.get(0));
                                    CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MAA99999AERR", "P", msgArgs, 4, 3, (Class)this.getClass());
                                    processStatus = 0;
                                }
                            }
                            else {
                                final String logprefix = "URN:" + ntfQueue.getUrn();
                                if (ntfList.size() > 0) {
                                    final List ntfListChk = new ArrayList();
                                    this.log.info((Object)(String.valueOf(logprefix) + " Mapping ITEM_CODE with TRACE_TAG_CD in VH Mapping NTF master "));
                                    final HashMap mapNTF = obBO.getObDAO().getVHMappingWithNTF(this.sLocation);
                                    this.log.info((Object)(String.valueOf(logprefix) + " Length validation check "));
                                    for (int j = 0; j < ntfList.size(); ++j) {
                                        final NTFDetail ntfdetail = (NTFDetail) ntfList.get(j);
                                        try {
                                            final NTFMappingItem mapItem = (NTFMappingItem) mapNTF.get(ntfdetail.getTrace_tag_cd());
                                            if (mapItem != null) {
                                                ntfdetail.setMapItem(mapItem);
                                                boolean passValid = true;
                                                int lenChk = this.lengthChkOfSerial;
                                                try {
                                                    lenChk = Integer.parseInt(mapItem.getLengthChk());
                                                }
                                                catch (Exception e6) {
                                                    lenChk = this.lengthChkOfSerial;
                                                }
                                                final String valueChk = (ntfdetail.getValue() == null) ? "" : ntfdetail.getValue();
                                                if (valueChk.length() > lenChk) {
                                                    msgArgs = new ArrayList();
                                                    msgArgs.add(String.valueOf(mapItem.getPartName()) + " (" + valueChk + ")");
                                                    msgArgs.add(Integer.toString(lenChk));
                                                    CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MSTD0051AERR", "P", msgArgs, 4, 3, (Class)this.getClass());
                                                    final String msg = "Invalid length of " + msgArgs.get(0) + ". The length can not be more than " + msgArgs.get(1) + ".";
                                                    final String fileName2 = "AA9701_" + ntfQueue.getDocNo();
                                                    obBO.getObDAO().insertEmailErrQueue(fileName2, ntfQueue.getIdNo(), ntfQueue.getBodyNo(), ntfQueue.getVinNo(), msg);
                                                    this.log.error((Object)(String.valueOf(logprefix) + "  MSTD0051AERR: " + msg));
                                                    passValid = false;
                                                    sendErrEmail = true;
                                                    foundLengthError = true;
                                                }
                                                if (passValid) {
                                                    ntfListChk.add(ntfdetail);
                                                }
                                            }
                                            else {
                                                msgArgs = new ArrayList();
                                                msgArgs.add("Traceability Tag Code=" + ntfdetail.getTrace_tag_cd() + " don't set in VH Mapping NTF master.");
                                                CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MAA99999AERR", "P", msgArgs, 3, 3, (Class)this.getClass());
                                            }
                                        }
                                        catch (Exception e2) {
                                            e2.printStackTrace();
                                            this.log.error((Object)(String.valueOf(logprefix) + " Traceability Tag Code=" + ntfdetail.getTrace_tag_cd() + " : " + e2.getMessage()));
                                            this.log.trace((Object)e2);
                                            msgArgs = new ArrayList();
                                            msgArgs.add(e2.getMessage());
                                            CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MAA99999AERR", "P", msgArgs, 4, 3, (Class)this.getClass());
                                        }
                                    }
                                    final Timestamp sysdate = new Timestamp(new Date().getTime());
                                    final String sysdateStr = CAA90007FormatUtil.convertDateToString((Date)sysdate, "yyyy-MM-dd HH:mm:ss");
                                    this.log.info((Object)("Getting Item Codes for Duplicate Checking in TB_M_BC_TP_CNTRL_NTF for Plant=" + ntfQueue.getPlant() + ", TP=" + ntfQueue.getLine() + ntfQueue.getTrackingPoint()));
                                    final List listFromBC_TP_CNTRL = obBO.getObDAO().getCheckForDuplicate(this.sLocation, ntfQueue.getPlant(), ntfQueue.getTrackingPoint(), ntfQueue.getLine());
                                    for (int y = 0; y < listFromBC_TP_CNTRL.size(); ++y) {
                                        final Object[] obj = (Object[]) listFromBC_TP_CNTRL.get(y);
                                        final String chkDupFlag = (String)obj[0];
                                        final String itemCode = (String)obj[1];
                                        final String itemGroup = (String)obj[2];
                                        try {
                                            String serialNo = "";
                                            final PDIMItemVO obPDIM = new PDIMItemVO();
                                            obPDIM.setDocNo(ntfQueue.getDocNo());
                                            obPDIM.setUnique_key(ntfQueue.getUniqueKey());
                                            obPDIM.setBc_seq_no(ntfQueue.getBcSeqNo());
                                            obPDIM.setItem_code(itemCode);
                                            obPDIM.setItem_group(itemGroup);
                                            boolean matchedItem = false;
                                            for (int k = 0; k < ntfListChk.size(); ++k) {
                                                final NTFDetail ntfdetail2 = (NTFDetail) ntfListChk.get(k);
                                                final NTFMappingItem mapItem2 = ntfdetail2.getMapItem();
                                                if (mapItem2 != null && mapItem2.getItemCode().equals(itemCode)) {
                                                    serialNo = ((ntfdetail2.getValue() == null) ? "" : ntfdetail2.getValue());
                                                    obPDIM.setSerial_no(serialNo);
                                                    obPDIM.setId_no(ntfdetail2.getSf_v001());
                                                    obPDIM.setVin_no(ntfdetail2.getSf_t518());
                                                    obPDIM.setBody_no(ntfdetail2.getSf_v011());
                                                    obPDIM.setPlant(ntfQueue.getPlant());
                                                    obPDIM.setTp(ntfQueue.getTrackingPoint());
                                                    String loDate = "";
                                                    if (ntfdetail2.getLo_date() != null && ntfdetail2.getLo_date().trim().length() > 0) {
                                                        if (ntfdetail2.getLo_date().trim().length() > 8) {
                                                            loDate = ntfdetail2.getLo_date().trim();
                                                        }
                                                        else {
                                                            loDate = String.valueOf(ntfdetail2.getLo_date().trim()) + "000000";
                                                        }
                                                    }
                                                    else {
                                                        loDate = "";
                                                    }
                                                    obPDIM.setLo_dt(loDate);
                                                    obPDIM.setUrn(ntfdetail2.getSf_v026());
                                                    obPDIM.setTracking_time(ntfdetail2.getRec_date_time());
                                                    obPDIM.setSource_db("PDIM");
                                                    if (!"".equals(serialNo)) {
                                                        if ("Y".equalsIgnoreCase(chkDupFlag)) {
                                                            final List lDupBC = obBO.getDuplicateBCData(itemGroup, itemCode, serialNo, ntfQueue.getUrn(), ntfQueue.getIdNo());
                                                            if (lDupBC != null && lDupBC.size() > 0) {
                                                                for (int l = 0; l < lDupBC.size(); ++l) {
                                                                    final PDIMItemVO vo = (PDIMItemVO) lDupBC.get(l);
                                                                    vo.setDocNo(ntfQueue.getDocNo());
                                                                    vo.setUnique_key(ntfQueue.getUniqueKey());
                                                                    vo.setBc_seq_no(ntfQueue.getBcSeqNo());
                                                                    vo.setLo_dt(loDate);
                                                                    vo.setTracking_time(ntfdetail2.getRec_date_time());
                                                                    vo.setSource_db("VHD GALC");
                                                                    lDupBC.set(l, vo);
                                                                }
                                                            }
                                                            if (lDupBC != null && lDupBC.size() > 0) {
                                                                this.log.info((Object)("Saving PDIM Duplicate Table for ITEM CODE=" + itemCode));
                                                                obBO.getObDAO().insertPDIM_DUPLICATE_D(lDupBC, itemCode);
                                                                INSERT_PDIM_DUPLICATED += lDupBC.size();
                                                            }
                                                        }
                                                        this.log.info((Object)("Merge data to BC NTF Table (TB_R_BC_ITEMCHK_NTF) for ITEM CODE=" + itemCode));
                                                        obBO.getObDAO().mergeBCData(itemGroup, itemCode, serialNo, obPDIM, sysdateStr, ntfQueue, ntfdetail2);
                                                        ++INSERT_BC_ITEMCHK_NTF;
                                                    }
                                                    this.log.info((Object)("Saving to PDIM History Table for ITEM CODE=" + itemCode));
                                                    obBO.getObDAO().insertPDIM_HISTORY_D(obPDIM);
                                                    matchedItem = true;
                                                    break;
                                                }
                                            }
                                            if (!matchedItem) {
                                                obPDIM.setSerial_no(serialNo);
                                                obPDIM.setId_no(ntfQueue.getIdNo());
                                                obPDIM.setVin_no(ntfQueue.getVinNo());
                                                obPDIM.setBody_no(ntfQueue.getBodyNo());
                                                obPDIM.setPlant(ntfQueue.getPlant());
                                                obPDIM.setTp(ntfQueue.getTrackingPoint());
                                                obPDIM.setLo_dt(ntfQueue.getLoDt());
                                                obPDIM.setUrn(ntfQueue.getUrn());
                                                obPDIM.setTracking_time(sysdateStr);
                                                obPDIM.setSource_db("PDIM");
                                                this.log.info((Object)("Saving to PDIM History Table for ITEM CODE=" + itemCode));
                                                obBO.getObDAO().insertPDIM_HISTORY_D(obPDIM);
                                            }
                                            this.log.info((Object)("Saving to BC History Table for ITEM CODE=" + itemCode));
                                            obBO.getObDAO().insertBCHistory(itemGroup, itemCode, serialNo, obPDIM);
                                            ++INSERT_BC_HISTORY;
                                        }
                                        catch (Exception e3) {
                                            e3.printStackTrace();
                                            this.log.error((Object)("Exception: " + e3.getMessage()));
                                            this.log.trace((Object)e3);
                                            msgArgs = new ArrayList();
                                            msgArgs.add(e3.getMessage());
                                            CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MAA99999AERR", "P", msgArgs, 4, 3, (Class)this.getClass());
                                            processStatus = 1;
                                        }
                                    }
                                }
                            }
                            this.log.info((Object)("Process Status =" + processStatus));
                            this.log.info((Object)("manageNTFProcessQueue by Port=" + ntfQueue.getPort() + ", URN=" + ntfQueue.getUrn() + ", BC Seq No.=" + ntfQueue.getBcSeqNo()));
                            obBO.getObDAO().manageNTFProcessQueue(ntfQueue.getPort(), ntfQueue.getDocNo(), processStatus, this.reProcess, this.ntfQueueErrMsg);
                            obBO.getObDAO().deletePDIMHistoryForNotDupData();
                            StringBuilder str = new StringBuilder();
                            str.append("Insert into TB_R_BC_HISTORY " + INSERT_BC_HISTORY + " rows.");
                            msgArgs = new ArrayList();
                            msgArgs.add(str.toString());
                            CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MAA99999AINF", "P", msgArgs, 2, 3, (Class)this.getClass());
                            str = new StringBuilder();
                            str.append("Insert/Update into TB_R_BC_ITEMCHK_NTF " + INSERT_BC_ITEMCHK_NTF + " rows.");
                            msgArgs = new ArrayList();
                            msgArgs.add(str.toString());
                            CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MAA99999AINF", "P", msgArgs, 2, 3, (Class)this.getClass());
                            str = new StringBuilder();
                            str.append("Insert into TB_R_PDIM_DUPLICATE_D " + INSERT_PDIM_DUPLICATED + " rows.");
                            msgArgs = new ArrayList();
                            msgArgs.add(str.toString());
                            CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MAA99999AINF", "P", msgArgs, 2, 3, (Class)this.getClass());
                            if (processStatus == 1 || foundLengthError) {
                                msgArgs = new ArrayList();
                                msgArgs.add(processLogDesc);
                                CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MSTD7031BERR", "E", msgArgs, 4, 3, (Class)this.getClass());
                            }
                            else {
                                msgArgs = new ArrayList();
                                msgArgs.add(processLogDesc);
                                CAA90050CommonLogger.log(daoLog.getActiveConnection(),  "AA95", "AA9701", "SYSTEM", "MSTD7030BINF", "E", msgArgs, 2, 3, (Class)this.getClass());
                            }
                            if (sendErrEmail) {
                                final String fileName3 = "AA9701_" + ntfQueue.getDocNo();
                                this.log.info((Object)("Call send email BAA97010SendErrEmail.sh by " + fileName3));
                                this.executeScript(fileName3, "BAA97010SendErrEmail.sh");
                            }
                        }
                        sleepFlag = false;
                        if (CAA90001Constant.REPROCESS_FLAG.equals(this.reProcess)) {
                            threadStop = true;
                        }
                    }
                    finally {
                        daoLog.Destructor();
                        daoLog = null;
                    }
                    daoLog.Destructor();
                    daoLog = null;
                }
                else {
                    sleepFlag = true;
                    if (CAA90001Constant.REPROCESS_FLAG.equals(this.reProcess)) {
                        threadStop = true;
                    }
                }
                if (threadStop && this.server != null) {
                    try {
                        obBO.getObDAO().resetPIDtoNULL(this.port, this.reProcess);
                        if (obBO != null) {
                            obBO.releaseDAO();
                            obBO = null;
                        }
                    }
                    catch (Exception ex) {}
                    this.server.stopThread();
                }
                else if (obBO != null) {
                    obBO.releaseDAO();
                    obBO = null;
                }
                if (sleepFlag) {
                    Thread.sleep(2000L);
                }
            }
        }
        catch (Exception e4) {
            e4.printStackTrace();
            this.log.error((Object)("Exception: " + e4.getMessage()));
            this.log.trace((Object)e4);
            try {
                if (obBO != null) {
                    obBO.releaseDAO();
                    obBO = null;
                }
            }
            catch (Exception e5) {
                this.log.error((Object)("BO-DAO Exception: " + e5.getMessage()));
                this.log.trace((Object)e5);
            }
        }
        finally {
            try {
                if (obBO != null) {
                    obBO.releaseDAO();
                    obBO = null;
                }
            }
            catch (Exception e5) {
                this.log.error((Object)("BO-DAO Exception: " + e5.getMessage()));
                this.log.trace((Object)e5);
            }
        }
    }
    
    public void executeScript(final String fileName, final String shellName) {
        try {
            final String homeDirectory = System.getProperty("user.home");
            final String shell = homeDirectory.concat(File.separator).concat("release/bin/").concat(shellName).concat(" ").concat(fileName);
            this.log.info((Object)homeDirectory);
            final int result = CSTD0082ScriptHandler.getInstance().executeShell(shell);
            this.log.info((Object)(String.valueOf(shell) + " result=" + result));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}