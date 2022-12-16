package th.co.toyota.rvhd.batch.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class CAA96010BCFromGALCMain {
  ServerSocket echoServer = null;
  
  Socket clientSocket = null;
  
  Logger log = null;
  
  Properties ftp_prop = new Properties();
  
  int numConnections = 0;
  
  int port;
  
  String xmlPath;
  
  String sLocation;
  
  public static void main(String[] args) {
    int port = 0;
    String sLocation = null;
    if (args != null && args.length > 1)
      try {
        System.out.println("Parameter are : [" + args.toString() + "]");
        port = Integer.parseInt(args[0].toString());
        sLocation = args[1];
        System.out.println("ALC Port : [" + port + "] in location " + sLocation);
        if (port > 0) {
          CAA96010BCFromGALCMain server = new CAA96010BCFromGALCMain(port, sLocation);
          server.startServer();
        } 
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }  
  }
  
  public CAA96010BCFromGALCMain(int port, String sLocation) throws Exception {
    String ftp_iniFile = "/th/co/toyota/rvhd/batch/engine/pdim.properties";
    InputStream is = null;
    is = getClass().getResourceAsStream(ftp_iniFile);
    this.ftp_prop.load(is);
    this.xmlPath = this.ftp_prop.get("XML_PATH").toString();
    if (this.log == null) {
      System.setProperty("strport", Integer.toString(port));
      DOMConfigurator.configure(this.ftp_prop.getProperty("log4jConfig1"));
    } 
    this.log = LogManager.getLogger(getClass());
    this.port = port;
    this.sLocation = sLocation;
    this.log.info("Request Server open port: " + port);
  }
  
  public void stopServer() {
    this.log.info("Server cleaning up.");
    System.exit(0);
  }
  
  public void startServer() {
    try {
      this.echoServer = new ServerSocket(this.port);
      this.log.info("Server already open port: " + this.port);
    } catch (IOException e) {
      e.printStackTrace();
      this.log.error(e);
      System.out.println(e);
    } 
    this.log.info(" Server is started and is waiting for connections.");
    this.log.info(" With multi-threading, multiple connections are allowed.");
    this.log.info(" Any client can send 0 to stop the server.");
    while (true) {
      try {
        this.clientSocket = this.echoServer.accept();
        this.numConnections++;
        CAA96011BCFromGALCSocket conn = new CAA96011BCFromGALCSocket(this.clientSocket, this.numConnections, this, 
            this.sLocation, this.port);
        (new Thread(conn)).start();
      } catch (IOException e) {
        e.printStackTrace();
        this.log.error("IOException at=> CAA96010BCFromGALCMain");
        this.log.error(e.getMessage());
        this.log.trace(e);
      } catch (Exception e) {
        e.printStackTrace();
        this.log.error("Exception at=> CAA96010BCFromGALCMain");
        this.log.error(e.getMessage());
        this.log.trace(e);
      } 
    } 
  }
}
