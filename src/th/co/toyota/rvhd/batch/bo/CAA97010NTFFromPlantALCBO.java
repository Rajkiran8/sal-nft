package th.co.toyota.rvhd.batch.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import th.co.toyota.rvhd.batch.dao.CAA97010NTFFromPlantALCDAO;
import th.co.toyota.rvhd.batch.ntf.PDIMItemVO;

public class CAA97010NTFFromPlantALCBO {
  private CAA97010NTFFromPlantALCDAO obDAO = null;
  
  public CAA97010NTFFromPlantALCBO(Logger log) {
    try {
      this.obDAO = new CAA97010NTFFromPlantALCDAO(log);
    } catch (Exception exception) {}
  }
  
  public CAA97010NTFFromPlantALCDAO getObDAO() {
    return this.obDAO;
  }
  
  public void setObDAO(CAA97010NTFFromPlantALCDAO obDAO) {
    this.obDAO = obDAO;
  }
  
  public List getDuplicateBCData(String strItemGroup, String strItemCode, String strSerialNo, String strURN, String strIDNo) throws Exception {
    List alVOs = new ArrayList();
    List alRet = this.obDAO.getDuplicateBCData(strSerialNo, strItemGroup);
    if (alRet != null)
      for (int i = 0; i < alRet.size(); i++) {
        Object[] obj = (Object[]) alRet.get(i);
        String strURNTemp = (String)obj[0];
        String strIDNoTemp = (String)obj[1];
        String strItemCodeTemp = (String)obj[6];
        if (!strItemCodeTemp.equals(strItemCode) || !strURNTemp.equals(strURN) || !strIDNoTemp.equals(strIDNo)) {
          PDIMItemVO vo = new PDIMItemVO();
          vo.setUrn((String)obj[0]);
          vo.setId_no((String)obj[1]);
          vo.setVin_no((String)obj[2]);
          vo.setBody_no((String)obj[3]);
          vo.setPlant((String)obj[4]);
          vo.setTp((String)obj[5]);
          vo.setItem_code(strItemCodeTemp);
          vo.setItem_group(strItemGroup);
          vo.setSerial_no(strSerialNo);
          alVOs.add(vo);
        } 
      }  
    return alVOs;
  }
  
  public String getNext_BCSeqNo() throws Exception {
    List alRet = null;
    try {
      alRet = this.obDAO.getNext_BCSeqNo();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } 
    return alRet.get(0).toString();
  }
  
  public void releaseDAO() throws Exception {
    this.obDAO.Destructor();
  }
}
