package Objects;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;



public class ProcessExecutor {

	private static int lastChartProcessID = 0;
	private static String final_script = "";
	private static ArrayList arrCmdConvertFinal = new ArrayList();

	public static File getJarDir() throws UnsupportedEncodingException {
	    String path = URLDecoder.decode(ProcessExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
	    return new File(path).getParentFile();
	}

	public static void copyFile(File dir, String fileName, boolean overwrite, boolean executable) throws FileNotFoundException, IOException
	{
	    File f = new File(dir, fileName);
	    if(overwrite == false && f.exists()) return;

	    byte [] buff = new byte[500000];
	    FileOutputStream fs = new FileOutputStream(f);
	    InputStream is = ProcessExecutor.class.getResourceAsStream(fileName);
	    int r = 0;

	    while( (r=is.read(buff)) > 0 ){
		    fs.write(buff, 0, r);					
	    }

	    fs.close();
	    is.close();
	    if(executable) f.setExecutable(true, false);
	}
	
    public static String deploy(String featureScript, String batchScript) throws IOException {
	try {
	    File base = ProcessExecutor.getJarDir();
	    File bin = new File(base, "bin");
            Path path = null;

            if (!bin.exists()) {
                if (!base.canWrite()) {
                    path = Files.createTempDirectory("itagplotscripts");
                    if (path != null) {
                        base = path.toFile();
                        //base.deleteOnExit();
                        bin = new File(base, "bin");
                    }
                }
                if (!bin.mkdir()) {
                    throw new IOException("Can't creat a bin directory in the iTagPlot directory");
                }
	    }

            ProcessExecutor.copyFile(base, featureScript, false, true);
	    ProcessExecutor.copyFile(base, batchScript, false, true);
            return base.getCanonicalPath();
	} catch (IOException ex) {
	    throw new IOException("Can't deploy necessary perl scripts");
	}
    }

    private static String []command2Array(String cmd){		
	    String []arr;
	    arr = cmd.split(" ");
	    ArrayList arrList = new ArrayList();

	    String tmp = null;
	    for(int i = 0; i < arr.length; i++){
		if(arr[i].length() != 0){
		    if(arr[i].startsWith("\"")){
			    if(arr[i].endsWith("\""))
				    arrList.add(arr[i].substring(1, arr[i].length()-1));
			    else{
				    tmp = arr[i].substring(1);						
			    }
		    }
		    else if(arr[i].endsWith("\"")){
			    if(tmp != null)
				    tmp += " "+ arr[i].substring(0, arr[i].length()-1);
			    else
				    tmp = arr[i].substring(0, arr[i].length()-1);

			    arrList.add(tmp);
			    tmp = null;
		    }
		    else if(tmp != null)
			    tmp += arr[i];
		    else
			    arrList.add(arr[i]);
		}
	    }
		
	    return (String [])arrList.toArray(new String [arrList.size()]);
	}
	
	static Thread thread_stdout;
	static Thread thread_error;
	
	private static Process proc = null;
	private static boolean isStop = true;
	//private ProcessFilter stdout;
  //private ProcessFilter stderr;
  //private Thread waiter;
		
	static public boolean isRunning(){
		//return processID;
		return (proc == null?  false : true);
	}
/*
	public static boolean formatdb(String fileName, String dbDir){
		
		if(checkBlastBin() == true){
			String str;
			try {
				str = GlobalContext.getProperty(GlobalContext.KEY_BLAST_BIN);
			
				if(str == null || str.length() == 0) str = "formatdb -p F -i " + fileName;
				else str += File.separatorChar + "formatdb -p F -i " + fileName;
				
				SystemUtils.runExec(str, false, false, dbDir) ;
				(new File("formatdb.log")).delete();
				MessageConsole.printMsg("'formatdb' have been executed for '" +fileName+ "'.", MessageConsole.INFO);
			} catch (Exception e) {
				MessageBox.showErrorMessage(ESTClean.getMainWin(), e.getMessage());
				System.err.println(e.getMessage());
				MessageConsole.printMsg("Fail to execute 'formatdb' - " +e.getMessage()+ ".", MessageConsole.ERROR);
				return false;
			}
			return true;
		}
		return false;
		
	}

	public static boolean checkPerl(){
		try {
			SystemUtils.runExec("perl -v", false, null);
			return true;
		} catch (Exception e) {}
		
		MessageConsole.printMsg("It needs a 'PERL' program.\nYou can download it from the 'http://www.activestate.com/activeperl' site", MessageConsole.WARNING);
		return false;
	}
*/	
}


class CommandRunThread implements Runnable{
	BufferedReader bufferReader;	
	int msgType;
	public CommandRunThread(BufferedReader br, int msgType) 
	{ bufferReader = br; this.msgType = msgType; }
	
	public void run()
	{	
		String str;
		try
		{
			while ((str = bufferReader.readLine()) != null) { 
				//str=new String(str.getBytes("ISO-8859-1"),"EUC-KR");
//				MessageConsole.printMsg(str, msgType);
				//System.out.println(str);
			} 
		}
		catch(Exception e) { 
//			try{MessageConsole.printMsg(ErrorStr.getErrorStr(e), MessageConsole.ERROR);}
//			catch(Exception er) {}
		}
	}		
}


