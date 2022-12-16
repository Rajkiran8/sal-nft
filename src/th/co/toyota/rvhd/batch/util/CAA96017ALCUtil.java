package th.co.toyota.rvhd.batch.util;

import java.net.Socket;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import th.co.toyota.rvhd.batch.vo.CAA96015BCNoticeVO;

public class CAA96017ALCUtil {
  private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
  
  public static synchronized void setSchema(Document schema, Element element, String stBCData, List aListResponse, CAA96015BCNoticeVO vo, Socket clientSocket, String stProcessType, String xmlPath) throws TransformerException {
    System.out.println("goto setSchema");
    try {
      CAA96019Message message = null;
      int len = 0;
      vo.setStrProcessResult("00");
      CAA96018ALCFixedLengthParser noticeParser = new CAA96018ALCFixedLengthParser();
      noticeParser.setSchema(String.valueOf(xmlPath) + "ALCNotice.xml", stBCData);
      int limit = noticeParser.getLimitLen();
      System.out.println(" Data from G-ALC : [" + stBCData + "]");
      System.out.println(" Limit : [" + limit + "]");
      if (stBCData.length() > 0) {
        message = noticeParser.parseRecord(stBCData);
        len = message.items.length;
      } 
      String itemName = "";
      String itemValue = "";
      for (int i = 0; i < len; i++) {
        itemName = (message.items[i]).name.toString();
        itemValue = (message.items[i]).value;
        if (isNotEmpty(itemValue.trim()))
          if (itemName.equals("DestinationLogicalName")) {
            vo.setStrDestLGName(itemValue.trim());
          } else if (itemName.equals("TransmissionLogicalName")) {
            vo.setStrTrnBaseLGName(itemValue.trim());
          } else if (itemName.equals("SerialNo")) {
            vo.setStrSerialNo(itemValue.trim());
          } else if (itemName.equals("Mode")) {
            vo.setStrMode(itemValue.trim());
          } else if (itemName.equals("Length")) {
            vo.setStrLength(itemValue.trim());
          } else if (itemName.equals("ProcessType")) {
            vo.setStrProcessType(itemValue.trim());
          } else if (itemName.equals("Line")) {
            vo.setStrLine(itemValue.trim());
          } else if (itemName.equals("TrackPoint")) {
            vo.setStrTP(itemValue.trim());
          } else if (itemName.equals("BCSequenceNo")) {
            vo.setStrBCSeqNo(itemValue.trim());
          } else if (itemName.equals("URN")) {
            vo.setStrURN(itemValue.trim());
          } else if (itemName.equals("BodyNo")) {
            vo.setStrBodyNo(itemValue.trim());
          } else if (itemName.equals("VIN_NO")) {
            vo.setStrVinNo(itemValue.trim());
          } else if (itemName.equals("ID_NO")) {
            vo.setStrIDNO(itemValue.trim());
          } else if (itemName.equals("LO_DT")) {
            vo.setLoDate(itemValue.trim());
          } else if (itemName.equals("PLANT")) {
            vo.setStrPlant(itemValue.trim());
          } else if (itemName.equals("TRACK_TIME")) {
            vo.setTracktime(itemValue.trim());
          } else if (itemName.equals("AB_DRIVER")) {
            vo.setStrABDriver(itemValue.trim());
          } else if (itemName.equals("AB_PASSENGER")) {
            vo.setStrABPassenger(itemValue.trim());
          } else if (itemName.equals("AB_CSA_LH")) {
            vo.setStrABCSALH(itemValue.trim());
          } else if (itemName.equals("AB_CSA_RH")) {
            vo.setStrABCSARH(itemValue.trim());
          } else if (itemName.equals("AB_SIDE_LH")) {
            vo.setStrABSideLH(itemValue.trim());
          } else if (itemName.equals("AB_SIDE_RH")) {
            vo.setStrABSideRH(itemValue.trim());
          } else if (itemName.equals("TRANS_NO")) {
            vo.setStrTransNo(itemValue.trim());
          } else if (itemName.equals("FUEL_NO")) {
            vo.setStrFuelNo(itemValue.trim());
          } else if (itemName.equals("CNG_NO")) {
            vo.setStrCNGNo(itemValue.trim());
          } else if (itemName.equals("PART1")) {
            vo.setStrPart1(itemValue.trim());
          } else if (itemName.equals("PART2")) {
            vo.setStrPart2(itemValue.trim());
          } else if (itemName.equals("PART3")) {
            vo.setStrPart3(itemValue.trim());
          } else if (itemName.equals("PART4")) {
            vo.setStrPart4(itemValue.trim());
          } else if (itemName.equals("PART5")) {
            vo.setStrPart5(itemValue.trim());
          } else if (itemName.equals("PART6")) {
            vo.setStrPart6(itemValue.trim());
          } else if (itemName.equals("PART7")) {
            vo.setStrPart7(itemValue.trim());
          } else if (itemName.equals("PART8")) {
            vo.setStrPart8(itemValue.trim());
          } else if (itemName.equals("PART9")) {
            vo.setStrPart9(itemValue.trim());
          } else if (itemName.equals("PART10")) {
            vo.setStrPart10(itemValue.trim());
          } else if (itemName.equals("PART11")) {
            vo.setStrPart11(itemValue.trim());
          } else if (itemName.equals("PART12")) {
            vo.setStrPart12(itemValue.trim());
          } else if (itemName.equals("PART13")) {
            vo.setStrPart13(itemValue.trim());
          } else if (itemName.equals("PART14")) {
            vo.setStrPart14(itemValue.trim());
          } else if (itemName.equals("PART15")) {
            vo.setStrPart15(itemValue.trim());
          } else if (itemName.equals("PART16")) {
            vo.setStrPart16(itemValue.trim());
          } else if (itemName.equals("PART17")) {
            vo.setStrPart17(itemValue.trim());
          } else if (itemName.equals("PART18")) {
            vo.setStrPart18(itemValue.trim());
          } else if (itemName.equals("PART19")) {
            vo.setStrPart19(itemValue.trim());
          } else if (itemName.equals("PART20")) {
            vo.setStrPart20(itemValue.trim());
          } else if (itemName.equals("PART21")) {
            vo.setStrPart21(itemValue.trim());
          } else if (itemName.equals("PART22")) {
            vo.setStrPart22(itemValue.trim());
          } else if (itemName.equals("PART23")) {
            vo.setStrPart23(itemValue.trim());
          } else if (itemName.equals("PART24")) {
            vo.setStrPart24(itemValue.trim());
          } else if (itemName.equals("PART25")) {
            vo.setStrPart25(itemValue.trim());
          } else if (itemName.equals("PART26")) {
            vo.setStrPart26(itemValue.trim());
          } else if (itemName.equals("PART27")) {
            vo.setStrPart27(itemValue.trim());
          } else if (itemName.equals("PART28")) {
            vo.setStrPart28(itemValue.trim());
          } else if (itemName.equals("PART29")) {
            vo.setStrPart29(itemValue.trim());
          } else if (itemName.equals("PART30")) {
            vo.setStrPart30(itemValue.trim());
          } else if (itemName.equals("PART31")) {
            vo.setStrPart31(itemValue.trim());
          } else if (itemName.equals("PART32")) {
            vo.setStrPart32(itemValue.trim());
          } else if (itemName.equals("PART33")) {
            vo.setStrPart33(itemValue.trim());
          } else if (itemName.equals("PART34")) {
            vo.setStrPart34(itemValue.trim());
          } else if (itemName.equals("PART35")) {
            vo.setStrPart35(itemValue.trim());
          } else if (itemName.equals("PART36")) {
            vo.setStrPart36(itemValue.trim());
          } else if (itemName.equals("PART37")) {
            vo.setStrPart37(itemValue.trim());
          } else if (itemName.equals("PART38")) {
            vo.setStrPart38(itemValue.trim());
          } else if (itemName.equals("PART39")) {
            vo.setStrPart39(itemValue.trim());
          } else if (itemName.equals("PART40")) {
            vo.setStrPart40(itemValue.trim());
          } else if (itemName.equals("PART41")) {
            vo.setStrPart41(itemValue.trim());
          } else if (itemName.equals("PART42")) {
            vo.setStrPart42(itemValue.trim());
          } else if (itemName.equals("PART43")) {
            vo.setStrPart43(itemValue.trim());
          } else if (itemName.equals("PART44")) {
            vo.setStrPart44(itemValue.trim());
          } else if (itemName.equals("PART45")) {
            vo.setStrPart45(itemValue.trim());
          } else if (itemName.equals("PART46")) {
            vo.setStrPart46(itemValue.trim());
          } else if (itemName.equals("PART47")) {
            vo.setStrPart47(itemValue.trim());
          } else if (itemName.equals("PART48")) {
            vo.setStrPart48(itemValue.trim());
          } else if (itemName.equals("PART49")) {
            vo.setStrPart49(itemValue.trim());
          } else if (itemName.equals("PART50")) {
            vo.setStrPart50(itemValue.trim());
          } else if (itemName.equals("PART51")) {
            vo.setStrPart51(itemValue.trim());
          } else if (itemName.equals("PART52")) {
            vo.setStrPart52(itemValue.trim());
          } else if (itemName.equals("PART53")) {
            vo.setStrPart53(itemValue.trim());
          } else if (itemName.equals("PART54")) {
            vo.setStrPart54(itemValue.trim());
          } else if (itemName.equals("PART55")) {
            vo.setStrPart55(itemValue.trim());
          } else if (itemName.equals("PART56")) {
            vo.setStrPart56(itemValue.trim());
          } else if (itemName.equals("PART57")) {
            vo.setStrPart57(itemValue.trim());
          } else if (itemName.equals("PART58")) {
            vo.setStrPart58(itemValue.trim());
          } else if (itemName.equals("PART59")) {
            vo.setStrPart59(itemValue.trim());
          } else if (itemName.equals("PART60")) {
            vo.setStrPart60(itemValue.trim());
          } else if (itemName.equals("PART61")) {
            vo.setStrPart61(itemValue.trim());
          } else if (itemName.equals("PART62")) {
            vo.setStrPart62(itemValue.trim());
          } else if (itemName.equals("PART63")) {
            vo.setStrPart63(itemValue.trim());
          } else if (itemName.equals("PART64")) {
            vo.setStrPart64(itemValue.trim());
          } else if (itemName.equals("PART65")) {
            vo.setStrPart65(itemValue.trim());
          } else if (itemName.equals("PART66")) {
            vo.setStrPart66(itemValue.trim());
          } else if (itemName.equals("PART67")) {
            vo.setStrPart67(itemValue.trim());
          } else if (itemName.equals("PART68")) {
            vo.setStrPart68(itemValue.trim());
          } else if (itemName.equals("PART69")) {
            vo.setStrPart69(itemValue.trim());
          } else if (itemName.equals("PART70")) {
            vo.setStrPart70(itemValue.trim());
          } else if (itemName.equals("PART71")) {
            vo.setStrPart71(itemValue.trim());
          } else if (itemName.equals("PART72")) {
            vo.setStrPart72(itemValue.trim());
          } else if (itemName.equals("PART73")) {
            vo.setStrPart73(itemValue.trim());
          } else if (itemName.equals("PART74")) {
            vo.setStrPart74(itemValue.trim());
          } else if (itemName.equals("PART75")) {
            vo.setStrPart75(itemValue.trim());
          } else if (itemName.equals("PART76")) {
            vo.setStrPart76(itemValue.trim());
          } else if (itemName.equals("PART77")) {
            vo.setStrPart77(itemValue.trim());
          } else if (itemName.equals("PART78")) {
            vo.setStrPart78(itemValue.trim());
          } else if (itemName.equals("PART79")) {
            vo.setStrPart79(itemValue.trim());
          } else if (itemName.equals("PART80")) {
            vo.setStrPart80(itemValue.trim());
          } else if (itemName.equals("PART81")) {
            vo.setStrPart81(itemValue.trim());
          } else if (itemName.equals("PART82")) {
            vo.setStrPart82(itemValue.trim());
          } else if (itemName.equals("PART83")) {
            vo.setStrPart83(itemValue.trim());
          } else if (itemName.equals("PART84")) {
            vo.setStrPart84(itemValue.trim());
          } else if (itemName.equals("PART85")) {
            vo.setStrPart85(itemValue.trim());
          } else if (itemName.equals("PART86")) {
            vo.setStrPart86(itemValue.trim());
          } else if (itemName.equals("PART87")) {
            vo.setStrPart87(itemValue.trim());
          } else if (itemName.equals("PART88")) {
            vo.setStrPart88(itemValue.trim());
          } else if (itemName.equals("PART89")) {
            vo.setStrPart89(itemValue.trim());
          } else if (itemName.equals("PART90")) {
            vo.setStrPart90(itemValue.trim());
          } else if (itemName.equals("PART91")) {
            vo.setStrPart91(itemValue.trim());
          } else if (itemName.equals("PART92")) {
            vo.setStrPart92(itemValue.trim());
          } else if (itemName.equals("PART93")) {
            vo.setStrPart93(itemValue.trim());
          } else if (itemName.equals("PART94")) {
            vo.setStrPart94(itemValue.trim());
          } else if (itemName.equals("PART95")) {
            vo.setStrPart95(itemValue.trim());
          } else if (itemName.equals("PART96")) {
            vo.setStrPart96(itemValue.trim());
          } else if (itemName.equals("PART97")) {
            vo.setStrPart97(itemValue.trim());
          } else if (itemName.equals("PART98")) {
            vo.setStrPart98(itemValue.trim());
          } else if (itemName.equals("PART99")) {
            vo.setStrPart99(itemValue.trim());
          } else if (itemName.equals("PART100")) {
            vo.setStrPart100(itemValue.trim());
          }  
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static Logger getLogger(String alcId, String tp) {
    return Logger.getLogger(String.valueOf(alcId) + "-" + tp);
  }
  
  public static Date getSQLDate(long time) {
    return new Date(time);
  }
  
  public static Timestamp getTimestamp(long time) {
    return new Timestamp(time);
  }
  
  public static String get(String[] str) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < str.length; i++) {
      buf.append("\n");
      buf.append(str[i]);
    } 
    return buf.toString();
  }
  
  public static String prefixSpace(String str, int length) {
    return prefix(str, ' ', length);
  }
  
  public static String prefix(String str, char prefix, int length) {
    if (isEmpty(str))
      str = ""; 
    StringBuffer sb = new StringBuffer();
    if (str.length() < length)
      for (int index = 0; index < length - str.length(); index++)
        sb.append(prefix);  
    String str2 = sb.toString().concat(str);
    if (str2.length() > length)
      str2 = str2.substring(0, length); 
    return str2;
  }
  
  public static String postfix(String str, char postfix, int length) {
    if (isEmpty(str))
      str = ""; 
    StringBuffer sb = new StringBuffer();
    if (str.length() < length)
      for (int index = 0; index < length - str.length(); index++)
        sb.append(postfix);  
    String str2 = str.concat(sb.toString());
    if (str2.length() > length)
      str2 = str2.substring(0, length); 
    return str2;
  }
  
  public static boolean isEmpty(String str) {
    if (str == null || str.trim().length() == 0)
      return true; 
    return false;
  }
  
  public static boolean isNotEmpty(String str) {
    if (!isEmpty(str))
      return true; 
    return false;
  }
  
  public static CAA96018ALCFixedLengthParser getFixedLengthParser(String key) {
    CAA96018ALCFixedLengthParser parser = (CAA96018ALCFixedLengthParser)CAA9601BObjectBindings.getObject(key);
    return parser;
  }
  
  public static Date convertToSQLDate(String stDate) throws ParseException {
    return (stDate == null || stDate.length() == 0) ? null : new Date(df.parse(stDate).getTime());
  }
}
