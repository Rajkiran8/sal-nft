package th.co.toyota.rvhd.batch.ntf;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import th.co.toyota.rvhd.shared.constant.CAA90001Constant;

public class CAA97010NTFFromPlantALCMain {
  Logger log = null;
  
  Properties ftp_prop = new Properties();
  
  int numConnections = 0;
  
  int port;
  
  String xmlPath;
  
  String sLocation;
  
  String reProcess;
  
  public static void main(String[] args) {
    int port = 0;
    String sLocation = null;
    String sReProcess = "";
    try {
      if (args != null && args.length == 2) {
        port = Integer.parseInt(args[0].toString());
        sLocation = args[1];
        sReProcess = "";
      } else if (args != null && args.length == 3) {
        sLocation = args[1];
        sReProcess = args[2];
      } 
      CAA97010NTFFromPlantALCMain server = new CAA97010NTFFromPlantALCMain(port, sLocation, sReProcess);
      server.startThread();
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public CAA97010NTFFromPlantALCMain(int port, String sLocation, String sReProcess) throws Exception {
    this.port = port;
    this.sLocation = sLocation;
    this.reProcess = sReProcess;
    String ftp_iniFile = "/th/co/toyota/rvhd/batch/ntf/pdim_ntf.properties";
    InputStream is = null;
    is = getClass().getResourceAsStream(ftp_iniFile);
    this.ftp_prop.load(is);
    this.xmlPath = this.ftp_prop.get("XML_PATH").toString();
    if (this.log == null) {
      if (CAA90001Constant.REPROCESS_FLAG.equals(sReProcess)) {
        System.setProperty("strport", "Reprocess");
      } else {
        System.setProperty("strport", Integer.toString(port));
      } 
      DOMConfigurator.configure(this.ftp_prop.getProperty("log4jConfig1"));
    } 
    this.log = LogManager.getLogger(getClass());
  }
  
  public void stopThread() {
    this.log.info("Thread cleaning up.");
    System.exit(0);
  }
  
  public void startThread() {
    if (CAA90001Constant.REPROCESS_FLAG.equals(this.reProcess)) {
      this.log.info(" Thread start for Reprocess call Web Service from Plant ALC by location " + this.sLocation);
    } else {
      this.log.info(" Thread start for call Web Service from Plant ALC by Port : [" + this.port + "] in location " + this.sLocation);
    } 
    try {
      this.numConnections++;
      CAA97010NTFFromPlantALCByPort conn = new CAA97010NTFFromPlantALCByPort(this.numConnections, this, 
          this.sLocation, this.port, this.reProcess);
      (new Thread(conn)).start();
    } catch (Exception e) {
      e.printStackTrace();
      this.log.error("Exception at=> CAA97010NTFFromPlantALCMain");
      this.log.error(e.getMessage());
      this.log.trace(e);
    } 
  }
}
