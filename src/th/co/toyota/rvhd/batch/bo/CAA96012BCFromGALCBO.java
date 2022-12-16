package th.co.toyota.rvhd.batch.bo;

import java.util.ArrayList;
import java.util.List;

import th.co.toyota.rvhd.batch.dao.CAA96013BCFromGALCDAO;
import th.co.toyota.rvhd.batch.ntf.PDIMItemVO;

public class CAA96012BCFromGALCBO {
  private CAA96013BCFromGALCDAO dao = null;
  
  public CAA96012BCFromGALCBO() {
    try {
      this.dao = new CAA96013BCFromGALCDAO();
    } catch (Exception exception) {}
  }
  
  public List getDuplicateBCData(String strTableName, String strItemCode, String strSerialNo, String strURN, String strIDNo, String strItemGroup) throws Exception {
    List alVOs = new ArrayList();
    List alRet = this.dao.getDuplicateBCData(strTableName, strSerialNo, strItemGroup);
    if (alRet != null)
      for (int i = 0; i < alRet.size(); i++) {
        Object[] obj = (Object[]) alRet.get(i);
        String strURNTemp = (String)obj[0];
        String strIDNoTemp = (String)obj[1];
        String strItemCodeTemp = (String)obj[7];
        if (!strItemCode.equals(strItemCodeTemp) || !strURNTemp.equals(strURN) || !strIDNoTemp.equals(strIDNo)) {
          PDIMItemVO vo = new PDIMItemVO();
          vo.setUrn((String)obj[0]);
          vo.setId_no((String)obj[1]);
          vo.setVin_no((String)obj[2]);
          vo.setBody_no((String)obj[3]);
          vo.setPlant((String)obj[4]);
          vo.setTp((String)obj[5]);
          vo.setItem_code((String)obj[7]);
          vo.setItem_group(strItemGroup);
          vo.setSerial_no(strSerialNo);
          alVOs.add(vo);
        } 
      }  
    return alVOs;
  }
  
  public void releaseDAO() throws Exception {
    this.dao.Destructor();
  }
  
  public CAA96013BCFromGALCDAO getDao() {
    return this.dao;
  }
  
  public void setDao(CAA96013BCFromGALCDAO dao) {
    this.dao = dao;
  }
}
