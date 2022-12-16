package th.co.toyota.rvhd.batch.engine;

import th.co.toyota.rvhd.shared.util.CAA90013OndemandBatchUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.io.File;
import java.io.InputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import java.util.Properties;
import org.apache.log4j.Logger;

public class CAA90022ReceiveDaemon
{
	class CAA90022ReceiveDaemon$1 implements Comparator {
	    public CAA90022ReceiveDaemon$1(CAA90022ReceiveDaemon caa90022ReceiveDaemon) {
			// TODO Auto-generated constructor stub
		}

		public int compare(final Object o1, final Object o2) {
	        return ((String)o1).compareToIgnoreCase((String)o2);
	    }
	}

	public static final String WORK_DIR = "work_dir";
    public static final String INPUT_DIR = "input_dir";
    public static final String LOG_PROP_DIR = "log_prop_dir";
    public static final String AA9_PROP = "/th/co/toyota/rvhd/resources/AA9Resources.properties";
    public static final String AA9_FID = "daemon.file.id";
    private Logger log;
    private Properties prop;
    private String[] strFIDs;
    private String strInputDir;
    private String strWorkDir;
    
    public CAA90022ReceiveDaemon() throws Exception {
        this.log = null;
        this.prop = new Properties();
        this.strFIDs = null;
        this.strInputDir = null;
        this.strWorkDir = null;
        final InputStream is = this.getClass().getResourceAsStream("/th/co/toyota/rvhd/resources/AA9Resources.properties");
        this.prop.load(is);
        this.strFIDs = this.prop.get("daemon.file.id").toString().split(",");
        if (this.log == null) {
            DOMConfigurator.configure(System.getProperty("log_prop_dir"));
        }
        this.log = LogManager.getLogger((Class)this.getClass());
        this.strWorkDir = System.getProperty("work_dir").toString();
        this.strInputDir = System.getProperty("input_dir").toString();
    }
    
    public void start() throws Exception {
        this.log.info((Object)"About to start daemon");
        try {
            while (true) {
                Thread.sleep(2000L);
                final File fWork = new File(this.strWorkDir);
                if (fWork.isDirectory()) {
                    final String[] strWorkFiles = fWork.list();
                    if (strWorkFiles.length <= 0) {
                        continue;
                    }
                    this.log.info((Object)"Interface file(s) found!");
                    Arrays.sort(strWorkFiles, (Comparator<? super String>)new CAA90022ReceiveDaemon.CAA90022ReceiveDaemon$1(this));
                    String strCurrentIF = null;
                    for (int i = 0; i < strWorkFiles.length; ++i) {
                        if (strCurrentIF == null || !strWorkFiles[i].toUpperCase().startsWith(strCurrentIF.toUpperCase())) {
                            for (int x = 0; x < this.strFIDs.length; ++x) {
                                if (strWorkFiles[i].startsWith(this.strFIDs[x])) {
                                    this.log.info((Object)("Found in Work Dir(" + this.strWorkDir + "): " + strWorkFiles[i]));
                                    this.log.info((Object)("Check if " + strWorkFiles[i] + " is completely transfered by C:D or any FTP Server..."));
                                    final long lCurFileDate = new File(this.strWorkDir, strWorkFiles[i]).lastModified();
                                    final long lCurFileSize = new File(this.strWorkDir, strWorkFiles[i]).length();
                                    Thread.sleep(3000L);
                                    final long lNewFileDate = new File(this.strWorkDir, strWorkFiles[i]).lastModified();
                                    final long lNewFileSize = new File(this.strWorkDir, strWorkFiles[i]).length();
                                    boolean isComplete = false;
                                    if (lCurFileDate == lNewFileDate && lCurFileSize == lNewFileSize) {
                                        isComplete = true;
                                        this.log.info((Object)(String.valueOf(strWorkFiles[i]) + " was completely transfered by C:D or any FTP Server."));
                                    }
                                    else {
                                        this.log.error((Object)(String.valueOf(strWorkFiles[i]) + " is still being transfered by C:D or any FTP Server!"));
                                    }
                                    if (isComplete) {
                                        this.log.info((Object)("Checking Input Dir (" + this.strInputDir + ") if another I/F exist with ID " + this.strFIDs[x] + "..."));
                                        final File inputDir = new File(this.strInputDir);
                                        final String[] strInputIFs = inputDir.list();
                                        boolean canProceed = true;
                                        if (strInputIFs.length > 0) {
                                            for (int z = 0; z < strInputIFs.length; ++z) {
                                                if (strInputIFs[z].toUpperCase().startsWith(this.strFIDs[x].toUpperCase())) {
                                                    canProceed = false;
                                                    strCurrentIF = new String(this.strFIDs[x]);
                                                    this.log.error((Object)("Another I/F exists in Input Dir (" + this.strInputDir + ") with ID " + this.strFIDs[x] + "!"));
                                                    z = strInputIFs.length;
                                                }
                                                else {
                                                    strCurrentIF = null;
                                                }
                                            }
                                        }
                                        if (canProceed) {
                                            this.log.info((Object)("I/F with ID " + this.strFIDs[x] + " clear to be moved to Input Dir (" + this.strInputDir + ")"));
                                            if (new File(this.strWorkDir, strWorkFiles[i]).renameTo(new File(this.strInputDir, strWorkFiles[i]))) {
                                                this.log.info((Object)("Moved to Input Dir(" + this.strInputDir + "): " + strWorkFiles[i]));
                                                final String strScreenID = this.prop.getProperty(String.valueOf(this.strFIDs[x]) + ".screen.id").toString();
                                                final String strBatchID = this.prop.getProperty(String.valueOf(this.strFIDs[x]) + ".batch.id").toString();
                                                final String[] strParams = this.prop.getProperty(String.valueOf(this.strFIDs[x]) + ".params").toString().split(",");
                                                final ArrayList params = new ArrayList();
                                                if (strParams != null && strParams.length > 0) {
                                                    for (int y = 0; y < strParams.length; ++y) {
                                                        params.add(strParams[y]);
                                                    }
                                                }
                                                this.log.info((Object)("About to queue on ODB the batch of " + strWorkFiles[i] + ", batch ID is " + strBatchID));
                                                final CAA90013OndemandBatchUtil queue = new CAA90013OndemandBatchUtil();
                                                if (!queue.OnDemandBatchCall(strScreenID, strBatchID, "SYSTEM", params)) {
                                                    this.log.error((Object)("Cannot queue the batch of " + strWorkFiles[i] + " via ODB!"));
                                                }
                                                else {
                                                    this.log.info((Object)("Posted via ODB: " + strWorkFiles[i] + " using batch id: " + strBatchID));
                                                }
                                            }
                                            else {
                                                this.log.error((Object)("Cannot move " + strWorkFiles[i] + " to" + this.strInputDir + "!!!"));
                                            }
                                        }
                                    }
                                    x = this.strFIDs.length;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            this.log.error((Object)("Exception: " + e.getMessage()));
            throw e;
        }
    }
    
    public static void main(final String[] args) {
        try {
            final CAA90022ReceiveDaemon daemon = new CAA90022ReceiveDaemon();
            daemon.start();
        }
        catch (Exception e) {
            System.out.println("Error in running daemon: " + e.getMessage());
            System.exit(1);
        }
    }
}