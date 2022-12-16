package th.co.toyota.rvhd.batch.engine;

import th.co.toyota.rvhd.batch.dao.CAA96013BCFromGALCDAO;
import java.net.SocketException;
import th.co.toyota.rvhd.batch.ntf.PDIMItemVO;
import java.util.ArrayList;
import th.co.toyota.standard.util.CSTD2011DocNoGenUtil;
import th.co.toyota.rvhd.shared.util.CAA90007FormatUtil;
import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;
import th.co.toyota.rvhd.batch.bo.CAA96012BCFromGALCBO;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.List;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import java.util.Properties;
import org.apache.log4j.Logger;
import java.util.HashMap;
import org.w3c.dom.Document;
import th.co.toyota.rvhd.batch.vo.CAA96015BCNoticeVO;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.io.PrintStream;

public class CAA96011BCFromGALCSocket implements Runnable
{
    private String sLocation;
    private PrintStream os;
    private Socket clientSocket;
    private CAA96010BCFromGALCMain server;
    private BufferedReader inFromClient;
    private InputStreamReader isFromClient;
    private CAA96015BCNoticeVO obVO;
    private String stClientIP;
    int intID;
    private Document doc;
    private Document doc2;
    private HashMap hLayout;
    private Logger log;
    private int port;
    static /* synthetic */ Class class$0;
    static /* synthetic */ Class class$1;
    
    public CAA96011BCFromGALCSocket(final Socket clientSocket, final int id, final CAA96010BCFromGALCMain server, final String sLocation, final int port) {
        this.sLocation = null;
        this.obVO = null;
        this.doc = null;
        this.doc2 = null;
        this.hLayout = null;
        this.log = null;
        this.port = 0;
        this.clientSocket = clientSocket;
        this.intID = id;
        this.server = server;
        this.sLocation = sLocation;
        this.port = port;
        final String ftp_iniFile = "/th/co/toyota/rvhd/batch/engine/pdim.properties";
        InputStream is = null;
        final Properties prop = new Properties();
        try {
            is = this.getClass().getResourceAsStream(ftp_iniFile);
            prop.load(is);
            if (this.log == null) {
                System.setProperty("strport", Integer.toString(this.port));
                DOMConfigurator.configure(prop.getProperty("log4jConfig2"));
            }
            (this.log = LogManager.getLogger((Class)this.getClass())).info((Object)("Initializing ALCSchema.xml for port " + this.port));
            final File f = new File(String.valueOf(server.xmlPath) + "ALCSchema.xml");
            this.log.info((Object)("ALCSchema path = " + server.xmlPath + "ALCSchema.xml for port " + this.port));
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            this.log.info((Object)("Parsing ALCSchema.xml for port " + this.port));
            this.doc = db.parse(f);
            this.doc.getDocumentElement().normalize();
            this.log.info((Object)("Initializing ALCNotice.xml for port " + this.port));
            this.log.info((Object)("ALCNotice path = " + server.xmlPath + "ALCNotice.xml for port " + this.port));
            final File f2 = new File(String.valueOf(server.xmlPath) + "ALCNotice.xml");
            final DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db2 = dbf2.newDocumentBuilder();
            this.log.info((Object)("Parsing ALCSchema.xml for port " + this.port));
            this.doc2 = db2.parse(f2);
            this.doc2.getDocumentElement().normalize();
            this.log.info((Object)("Setting ALCSchema.xml layout for port " + this.port));
            this.setHashLayout();
            this.log.info((Object)("Retrieving stream for port " + this.port));
            this.os = new PrintStream(clientSocket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
            this.log.error((Object)("Client socket cannot established!!!" + clientSocket));
            this.log.error((Object)"Please contact client administrator.");
            this.log.error((Object)("IOException: " + e.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e);
        }
        catch (NullPointerException e2) {
            e2.printStackTrace();
            this.log.error((Object)("Client socket cannot established!!!" + clientSocket));
            this.log.error((Object)"Please contact client administrator.");
            this.log.error((Object)("NullPointerException: " + e2.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e2);
        }
        catch (Exception e3) {
            e3.printStackTrace();
            this.log.error((Object)("Exception: " + e3.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e3);
        }
    }
    
    private void setHashLayout() throws Exception {
        final NodeList nl = this.doc2.getElementsByTagName("Item");
        for (int i = 0; i < nl.getLength(); ++i) {
            final Element elem = (Element)nl.item(i);
            final String[] val = { elem.getAttribute("start"), elem.getAttribute("size"), elem.getAttribute("finish") };
            if (!val[2].equals("true")) {
                val[2] = null;
            }
            if (this.hLayout == null) {
                this.hLayout = new HashMap();
            }
            this.hLayout.put(elem.getAttribute("name"), val);
        }
    }
    
    private int[] getLayoutNumbers(final String sKey) throws Exception {
        final int[] rInt = new int[3];
        final String[] val = (String[]) this.hLayout.get(sKey);
        if (val == null) {
            return rInt;
        }
        rInt[0] = Integer.parseInt(val[0]);
        rInt[1] = Integer.parseInt(val[1]);
        try {
            rInt[2] = Integer.parseInt(val[2]);
        }
        catch (NumberFormatException e) {
            rInt[2] = 0;
        }
        return rInt;
    }
    
    private String getSerialNo(final List lsItemMap, final CAA96015BCNoticeVO vo, final String stItemCode) {
        String stSerialNo = null;
        for (int i = 0; i < lsItemMap.size(); ++i) {
            final Object[] obj = (Object[]) lsItemMap.get(i);
            final String ITEM_CODE = (String)obj[0];
            String methodName = (String)obj[2];
            if (ITEM_CODE.equals(stItemCode)) {
                try {
                    final String firstLetter = new StringBuffer().append(methodName.charAt(0)).toString();
                    methodName = "get" + firstLetter.toUpperCase() + methodName.substring(1);
                    Class class$0;
                    if ((class$0 = CAA96011BCFromGALCSocket.class$0) == null) {
                        try {
                            class$0 = (CAA96011BCFromGALCSocket.class$0 = Class.forName("th.co.toyota.rvhd.batch.vo.CAA96015BCNoticeVO"));
                        }
                        catch (ClassNotFoundException ex) {
                            throw new NoClassDefFoundError(ex.getMessage());
                        }
                    }
                    stSerialNo = (String)class$0.getMethod(methodName, (Class[])null).invoke(vo, (Object[])null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return stSerialNo;
    }
    
    private void setValueFromBC(final List lsItemMap, final CAA96015BCNoticeVO vo, final StringBuffer sbClient) throws Exception {
        int[] iNum = null;
        final String strDataSocket = sbClient.toString();
        for (int i = 0; i < lsItemMap.size(); ++i) {
            final Object[] obj = (Object[]) lsItemMap.get(i);
            final String COLUMN_NAME = (String)obj[1];
            String methodName = (String)obj[2];
            iNum = this.getLayoutNumbers(COLUMN_NAME);
            final String serialNo = strDataSocket.substring(iNum[0], iNum[0] + iNum[1]).trim();
            if (serialNo != null && serialNo.length() > 0) {
                final String firstLetter = new StringBuffer().append(methodName.charAt(0)).toString();
                methodName = "set" + firstLetter.toUpperCase() + methodName.substring(1);
                Class class$0;
                if ((class$0 = CAA96011BCFromGALCSocket.class$0) == null) {
                    try {
                        class$0 = (CAA96011BCFromGALCSocket.class$0 = Class.forName("th.co.toyota.rvhd.batch.vo.CAA96015BCNoticeVO"));
                    }
                    catch (ClassNotFoundException ex) {
                        throw new NoClassDefFoundError(ex.getMessage());
                    }
                }
                final String name = methodName;
                final Class[] parameterTypes = { null };
                final int n = 0;
                Class class$2;
                if ((class$2 = CAA96011BCFromGALCSocket.class$1) == null) {
                    try {
                        class$2 = (CAA96011BCFromGALCSocket.class$1 = Class.forName("java.lang.String"));
                    }
                    catch (ClassNotFoundException ex2) {
                        throw new NoClassDefFoundError(ex2.getMessage());
                    }
                }
                parameterTypes[n] = class$2;
                final Method m = class$0.getMethod(name, (Class[])parameterTypes);
                m.invoke(vo, serialNo);
            }
        }
    }
    
    public void run() {
        boolean serverStop = false;
        CAA96012BCFromGALCBO obBO = null;
        int iAdder = 0;
        try {
            this.log.info((Object)("About to start thread for port " + this.port));
            Thread.sleep(2000L);
            while (true) {
                StringBuffer sbClient = new StringBuffer();
                this.isFromClient = new InputStreamReader(this.clientSocket.getInputStream());
                this.inFromClient = new BufferedReader(this.isFromClient);
                byte[] btmp = new byte[3000];
                int streamlen = 0;
                boolean isLoop = true;
                StringBuffer stmp = null;
                while (isLoop) {
                    final char[] ctmp = new char[3000];
                    streamlen = this.inFromClient.read(ctmp, 0, ctmp.length);
                    if (stmp == null && streamlen != -1) {
                        stmp = new StringBuffer(new String(ctmp, 0, streamlen));
                        this.log.info((Object)("First packet length received is " + streamlen + " for port " + this.port));
                    }
                    else if (streamlen != -1) {
                        stmp.append(new String(ctmp, 0, streamlen));
                        this.log.info((Object)("Succeeding packet length received is " + streamlen + " for port " + this.port));
                        streamlen = stmp.toString().length();
                    }
                    else {
                        isLoop = false;
                    }
                    if (stmp != null && stmp.toString().endsWith("###BCEND###")) {
                        btmp = stmp.toString().getBytes();
                        iAdder = "###BCEND###".length();
                        isLoop = false;
                    }
                    else {
                        if (stmp == null || !stmp.toString().equals("0")) {
                            continue;
                        }
                        btmp = stmp.toString().getBytes();
                        isLoop = false;
                    }
                }
                this.log.info((Object)("Received and about to format InputStream for port " + this.port));
                this.log.info((Object)("Stream Data Length received is " + streamlen + " for port " + this.port));
                if (streamlen == -1) {
                    btmp = null;
                    sbClient = null;
                    break;
                }
                boolean bcNoticeOK = false;
                int intDataLength = 0;
                int intBCSeqNo = 0;
                int intBCLastSeqNo_DB = 0;
                for (int i = 0; i < streamlen; ++i) {
                    sbClient.append(new String(new byte[] { btmp[i] }));
                }
                if (sbClient.toString() == null) {
                    break;
                }
                if (sbClient.toString().equals("0")) {
                    serverStop = true;
                    break;
                }
                if (sbClient.toString().trim().equals("")) {
                    sbClient = new StringBuffer();
                    break;
                }
                try {
                    this.log.info((Object)("Initializing business object for port " + this.port));
                    obBO = new CAA96012BCFromGALCBO();
                    this.log.info((Object)("Business object initialized for port " + this.port));
                }
                catch (Exception e) {
                    this.log.error((Object)("Exception: " + e.getMessage() + " for port " + this.port));
                    throw e;
                }
                this.log.info((Object)("Stream Data receive = [" + sbClient.toString() + "]" + this.port));
                if (sbClient.toString() != null && streamlen > 37) {
                    boolean bolRet = true;
                    int[] iNum = this.getLayoutNumbers("Length");
                    final String stLength = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                    iNum = this.getLayoutNumbers("ProcessType");
                    final String stProcessType = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                    String stLine = null;
                    String stTP = null;
                    String stBCSeqNo = null;
                    String stBodyNo = null;
                    String stVinNo = null;
                    String stIDNo = null;
                    String stPlant = null;
                    String stURN = null;
                    String stLoDt = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                    if (stProcessType.equals("00") || stProcessType.equals("01") || stProcessType.equals("0E")) {
                        iNum = this.getLayoutNumbers("Line");
                        stLine = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                        iNum = this.getLayoutNumbers("TrackPoint");
                        stTP = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                        iNum = this.getLayoutNumbers("BCSequenceNo");
                        stBCSeqNo = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                        intBCSeqNo = Integer.parseInt(stBCSeqNo);
                        iNum = this.getLayoutNumbers("PLANT");
                        stPlant = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                        iNum = this.getLayoutNumbers("ID_NO");
                        stIDNo = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                        if (!stProcessType.equals("0E")) {
                            iNum = this.getLayoutNumbers("BODY_NO");
                            stBodyNo = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                            iNum = this.getLayoutNumbers("VIN_NO");
                            stVinNo = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                            iNum = this.getLayoutNumbers("URN");
                            stURN = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                            iNum = this.getLayoutNumbers("LO_DT");
                            stLoDt = sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]).trim();
                        }
                    }
                    final StringBuffer sbRes = new StringBuffer();
                    iNum = this.getLayoutNumbers("TransmissionLogicalName");
                    sbRes.append(sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]));
                    iNum = this.getLayoutNumbers("DestinationLogicalName");
                    sbRes.append(sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]));
                    iNum = this.getLayoutNumbers("SerialNo");
                    sbRes.append(sbClient.toString().substring(iNum[0], iNum[0] + iNum[1]));
                    sbRes.append("000000");
                    sbRes.append(stProcessType);
                    int intLen = 0;
                    int intBCLength = 0;
                    try {
                        intLen = Integer.parseInt(stLength);
                    }
                    catch (NumberFormatException ex) {
                        bolRet = false;
                        this.os.print("Input data stream invalid!");
                        this.os.flush();
                        ex.printStackTrace();
                        this.log.error((Object)"Input data stream invalid!");
                        this.log.error((Object)("NumberFormatException: " + ex.getMessage() + ", for port " + this.port));
                        this.log.trace((Object)ex);
                    }
                    intDataLength = sbClient.toString().substring(26, streamlen).length();
                    this.obVO = new CAA96015BCNoticeVO();
                    final CAA96013BCFromGALCDAO dao = obBO.getDao();
                    final List lsItemMap = dao.getVHDMapItemCode(this.sLocation);
                    if (stProcessType.equals("00") || stProcessType.equals("01") || stProcessType.equals("0E")) {
                        if (bolRet) {
                            intBCLength = 2851 + iAdder;
                            if (intBCLength != streamlen - 37) {
                                bolRet = false;
                                sbRes.append("91");
                                this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                                this.os.flush();
                                dao.updateBCResponse(stPlant, String.valueOf(stLine) + stTP, this.port, "91");
                                this.log.error((Object)("###ERROR 91: BC data length Error [" + (streamlen - 37) + "] for port " + this.port));
                            }
                        }
                        if (bolRet && intLen != intDataLength) {
                            bolRet = false;
                            sbRes.append("76");
                            this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                            this.os.flush();
                            dao.updateBCResponse(stPlant, String.valueOf(stLine) + stTP, this.port, "76");
                            this.log.error((Object)("###ERROR 76: Length Error (Length,dataLength)=" + intLen + "," + intDataLength + " for port " + this.port));
                        }
                        if (bolRet) {
                            final List lInitData = dao.getInitialData(this.sLocation, stPlant, String.valueOf(stLine) + stTP);
                            if (lInitData.size() > 0) {
                                final Object[] tmp = (Object[]) lInitData.get(0);
                                intBCLastSeqNo_DB = Integer.parseInt((tmp[3] == null) ? "0" : ((BigDecimal)tmp[3]).toString());
                                this.obVO.setStrTP(stTP);
                                this.obVO.setStrBodyNo(stBodyNo);
                                this.obVO.setStrVinNo(stVinNo);
                                this.obVO.setStrIDNO(stIDNo);
                                this.obVO.setStrPlant(stPlant);
                                this.obVO.setStrBCSeqNo(new StringBuffer().append(Integer.parseInt(stBCSeqNo)).toString());
                                this.obVO.setStrURN(stURN);
                                this.obVO.setLoDate(stLoDt);
                                this.setValueFromBC(lsItemMap, this.obVO, sbClient);
                            }
                            else {
                                bolRet = false;
                                sbRes.append("13");
                                this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                                this.os.flush();
                                dao.updateBCResponse(stPlant, String.valueOf(stLine) + stTP, this.port, "13");
                                this.log.error((Object)("###ERROR 13: Conversion Error [There are no Plant and TP in database] for port " + this.port));
                            }
                        }
                        if (bolRet) {
                            if (intBCLastSeqNo_DB == 999 && intBCSeqNo == 0) {
                                sbRes.append("00");
                                this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                                this.os.flush();
                                dao.updateBCResponse(stPlant, String.valueOf(stLine) + stTP, this.port, "00");
                                this.log.info((Object)("BC Sequence No. skipped after Sub-sys reset for port " + this.port));
                                bcNoticeOK = true;
                            }
                            else {
                                if (intBCSeqNo != intBCLastSeqNo_DB + 1) {
                                    bolRet = false;
                                    sbRes.append("90");
                                    this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                                    this.os.flush();
                                    dao.updateBCResponse(stPlant, String.valueOf(stLine) + stTP, this.port, "90");
                                    this.log.error((Object)("###ERROR 90: BC Sequence No. Error for port " + this.port));
                                }
                                if (bolRet && stProcessType.equals("00") && (stBodyNo.trim().length() == 0 || stURN.trim().length() == 0 || stIDNo.trim().length() == 0)) {
                                    bolRet = false;
                                    sbRes.append("13");
                                    this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                                    this.os.flush();
                                    dao.updateBCResponse(stPlant, String.valueOf(stLine) + stTP, this.port, "13");
                                    this.log.error((Object)("###ERROR 13: Conversion Error for port " + this.port));
                                }
                                if (bolRet) {
                                    sbRes.append("00");
                                    this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                                    this.os.flush();
                                    bcNoticeOK = true;
                                    dao.updateBCResponse(stPlant, String.valueOf(stLine) + stTP, this.port, "00");
                                    if (stProcessType.equals("01") || stProcessType.equals("0E")) {
                                        dao.updateBCSeq(stPlant, String.valueOf(stLine) + stTP, stBCSeqNo);
                                    }
                                    this.log.info((Object)("NO ERROR: Responded 00 for port " + this.port));
                                }
                            }
                        }
                    }
                    else {
                        sbRes.append("00");
                        this.os.print(new StringBuffer(String.valueOf(sbRes.toString())).toString());
                        this.os.flush();
                        this.log.info((Object)("----- Process Type is " + stProcessType + " ----- for port " + this.port));
                    }
                    this.log.info((Object)("Getting Client IP Address for port " + this.port));
                    final String sttmp = this.clientSocket.getInetAddress().toString();
                    final int slash = sttmp.indexOf("/");
                    this.stClientIP = sttmp.substring(slash + 1, sttmp.length());
                    this.obVO.setClientIP(this.stClientIP);
                    this.log.info((Object)("Client IP Address is " + this.stClientIP + " for port " + this.port));
                    if (bcNoticeOK) {
                        final Timestamp sysdate = new Timestamp(new Date().getTime());
                        final String sysdateStr = CAA90007FormatUtil.convertDateToString((Date)sysdate, "yyyyMMddHHmmss");
                        final NodeList nl = this.doc.getElementsByTagName("TrackPoints");
                        if (nl.getLength() > 0) {
                            final CSTD2011DocNoGenUtil docGen = new CSTD2011DocNoGenUtil();
                            final String docNo = docGen.genDOCNO("PDIM_DUP", (Date)sysdate, new String[] { "_" });
                            try {
                                if (stProcessType.equalsIgnoreCase("00")) {
                                    String loDate = null;
                                    if (this.obVO.getLoDate() != null && this.obVO.getLoDate().trim().length() > 0) {
                                        loDate = String.valueOf(this.obVO.getLoDate().substring(0, 8)) + this.obVO.getLoDate().substring(9);
                                    }
                                    this.log.info((Object)("Start Normal Process for port " + this.port));
                                    this.log.info((Object)("Updating BC Sequence for port " + this.port));
                                    dao.updateBCSeq(stPlant, String.valueOf(stLine) + stTP, stBCSeqNo);
                                    final String lineTP = String.valueOf(stLine) + stTP;
                                    final String flag = dao.checkUseWebServiceAndHaveBCControlNTFSetup(stPlant, lineTP);
                                    this.log.info((Object)("checkUseWebServiceAndHaveBCControlNTFSetup=" + flag + " for Plant=" + stPlant + ", Line + TP=" + lineTP));
                                    if ("Y".equalsIgnoreCase(flag)) {
                                        dao.insertNTFWebServiceProcess(this.port, this.obVO, sysdate, stLine, docNo);
                                    }
                                    this.log.info((Object)("Getting Item Codes for Duplicate Checking for port " + this.port));
                                    final List listFromBC_TP_CNTRL = dao.getCheckForDuplicate(stPlant, stTP, stLine, this.sLocation);
                                    final List lsNApartOfR0 = new ArrayList();
                                    for (int y = 0; y < listFromBC_TP_CNTRL.size(); ++y) {
                                        final Object[] obj = (Object[]) listFromBC_TP_CNTRL.get(y);
                                        final String chkDupFlag = obj[0].toString();
                                        final String itemCode = obj[1].toString();
                                        final String tableName = obj[2].toString();
                                        String serialNo = this.getSerialNo(lsItemMap, this.obVO, itemCode);
                                        serialNo = ((serialNo == null) ? "" : serialNo);
                                        String itemGroup = "";
                                        final PDIMItemVO obPDIM = new PDIMItemVO();
                                        obPDIM.setUnique_key(sysdateStr);
                                        obPDIM.setBc_seq_no(this.obVO.getStrBCSeqNo());
                                        obPDIM.setItem_code(itemCode);
                                        obPDIM.setItem_group(itemGroup);
                                        obPDIM.setSerial_no(serialNo);
                                        obPDIM.setId_no(this.obVO.getStrIDNO());
                                        obPDIM.setVin_no(this.obVO.getStrVinNo());
                                        obPDIM.setBody_no(this.obVO.getStrBodyNo());
                                        obPDIM.setPlant(this.obVO.getStrPlant());
                                        obPDIM.setTp(this.obVO.getStrTP());
                                        obPDIM.setTracking_time(sysdateStr);
                                        obPDIM.setLo_dt(loDate);
                                        obPDIM.setUrn(this.obVO.getStrURN());
                                        obPDIM.setSource_db("PDIM");
                                        if (itemCode.startsWith("A")) {
                                            itemGroup = dao.getItemGroup(this.sLocation, itemCode);
                                            obPDIM.setItem_group(itemGroup);
                                            lsNApartOfR0.add(obPDIM);
                                        }
                                        if (!"".equals(serialNo)) {
                                            if ("Y".equalsIgnoreCase(chkDupFlag.toUpperCase())) {
                                                this.log.info((Object)("Checking BC Duplicate for ITEM CODE " + itemCode + " for port " + this.port));
                                                final List lDupBC = obBO.getDuplicateBCData(tableName, itemCode, serialNo, this.obVO.getStrURN(), this.obVO.getStrIDNO(), itemGroup);
                                                final List lDupAll = new ArrayList();
                                                if (lDupBC != null && lDupBC.size() > 0) {
                                                    this.log.info((Object)("BC Duplicate found for ITEM CODE " + itemCode + " for port " + this.port));
                                                    for (int k = 0; k < lDupBC.size(); ++k) {
                                                        final PDIMItemVO vo = (PDIMItemVO) lDupBC.get(k);
                                                        vo.setUnique_key(sysdateStr);
                                                        vo.setBc_seq_no(this.obVO.getStrBCSeqNo());
                                                        vo.setLo_dt(loDate);
                                                        vo.setSource_db("VHD GALC");
                                                        lDupAll.add(vo);
                                                    }
                                                }
                                                if (lDupAll != null && lDupAll.size() > 0) {
                                                    this.log.info((Object)("Saving Duplicate Table for ITEM CODE " + itemCode + " for port " + this.port));
                                                    dao.insertPDIM_DUPLICATE_D(lDupAll, sysdateStr, docNo, itemCode);
                                                }
                                                this.log.info((Object)("Saving to BC Table for ITEM CODE " + itemCode + " for port " + this.port));
                                                dao.mergeBCData(tableName, itemCode, serialNo, this.obVO, sysdate, itemGroup);
                                            }
                                            else if (itemCode.startsWith("A")) {
                                                this.log.info((Object)("Saving to BC Table for ITEM CODE " + itemCode + " for port " + this.port));
                                                dao.mergeBCData(tableName, itemCode, serialNo, this.obVO, sysdate, itemGroup);
                                            }
                                        }
                                        this.log.info((Object)("Saving to PDIM History Table for ITEM CODE " + itemCode + " for port " + this.port));
                                        dao.insertPDIM_HISTORY_D(obPDIM, sysdate, docNo);
                                        this.log.info((Object)("Saving to BC History Table for ITEM CODE " + itemCode + " for port " + this.port));
                                        dao.insertBCHistory(itemCode, serialNo, this.obVO, sysdate, itemGroup);
                                    }
                                    if (lsNApartOfR0 != null && lsNApartOfR0.size() > 0) {
                                        final String buyOffTP = dao.getBuyOffTP(this.obVO.getStrIDNO());
                                        if (buyOffTP.equalsIgnoreCase(this.obVO.getStrTP())) {
                                            this.log.info((Object)("Duplicate data checking at Vehicle history for NA Part of TP=R0 for port " + this.port));
                                            dao.duplicateCheckingOfNASection(lsNApartOfR0, sysdate, this.sLocation, this.port);
                                        }
                                    }
                                }
                            }
                            catch (Exception e2) {
                                e2.printStackTrace();
                                this.log.error((Object)("Exception: " + e2.getMessage() + ", for port " + this.port));
                                this.log.trace((Object)e2);
                            }
                        }
                    }
                }
                else {
                    this.os.print("Data Input Stream wrong format layout!!!");
                    this.os.flush();
                    this.log.error((Object)("Data Input Stream wrong format layout!!! for port " + this.port));
                }
                btmp = null;
                sbClient = null;
                break;
            }
            if (serverStop) {
                try {
                    this.inFromClient.close();
                }
                catch (Exception ex2) {}
                try {
                    this.isFromClient.close();
                }
                catch (Exception ex3) {}
                try {
                    this.os.close();
                }
                catch (Exception ex4) {}
                try {
                    this.clientSocket.close();
                }
                catch (Exception ex5) {}
                try {
                    this.server.stopServer();
                }
                catch (Exception ex6) {}
            }
        }
        catch (SocketException e3) {
            e3.printStackTrace();
            this.log.error((Object)("SocketException: " + e3.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e3);
        }
        catch (NumberFormatException e4) {
            e4.printStackTrace();
            this.log.error((Object)("NumberFormatException: " + e4.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e4);
        }
        catch (IOException e5) {
            e5.printStackTrace();
            this.log.error((Object)("IOException: " + e5.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e5);
        }
        catch (Exception e6) {
            e6.printStackTrace();
            this.log.error((Object)("Exception: " + e6.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e6);
        }
        finally {
            try {
                if (obBO != null) {
                    this.log.info((Object)("About to close BO-DAO, for port " + this.port));
                    obBO.releaseDAO();
                    obBO = null;
                    this.log.info((Object)("Closed BO-DAO, for port " + this.port));
                }
            }
            catch (Exception e7) {
                this.log.error((Object)("BO-DAO Exception: " + e7.getMessage() + ", for port " + this.port));
                this.log.trace((Object)e7);
            }
        }
        try {
            if (obBO != null) {
                this.log.info((Object)("About to close BO-DAO, for port " + this.port));
                obBO.releaseDAO();
                obBO = null;
                this.log.info((Object)("Closed BO-DAO, for port " + this.port));
            }
        }
        catch (Exception e7) {
            this.log.error((Object)("BO-DAO Exception: " + e7.getMessage() + ", for port " + this.port));
            this.log.trace((Object)e7);
        }
    }
    
    public static void main(final String[] args) {
    }
}