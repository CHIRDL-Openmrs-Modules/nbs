package org.openmrs.module.nbsmodule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.nbsmodule.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import ca.uhn.hl7v2.app.HL7ServerTestHelper;

/**
 * Determines which encounters require a response hl7 to RMRS, 
 * and creates the out-bound hl7 messages to send on the specified port.
 * @author msheley
 *
 */
public class ResponseGenerator extends AbstractTask {

	private Log log = LogFactory.getLog(this.getClass());
	private TaskDefinition taskConfig;
	private String host;
	private Integer port;
	private Socket socket = null;
	private OutputStream os = null;
	private InputStream is = null;
	
	
	@Override
	public void initialize(TaskDefinition config)
	{

		Context.openSession();
		try {
			if (Context.isAuthenticated() == false)
				authenticate();
			
			this.taskConfig = config;
			//port to export
			String portName = this.taskConfig.getProperty("port");
			host  = this.taskConfig.getProperty("host");
			if (host == null){
				host = "localhost";
			}

			if (portName != null){
				port = Integer.parseInt(portName);
			} else
			{
				port = 5002;
			}
			
		
		}finally{
			Context.closeSession();
		}

	}

	/* 
	 * Executes loading the queue table for sessions requiring hl7 responses
	 * Constructs the hl7 messages, sends the message, and saves text to a file
	 */
	@Override
	public void execute()
	{
		
		Context.openSession();
		
		try
		{
			if (Context.isAuthenticated() == false)
				authenticate();

			
			NBSService nbsservice = Context.getService(NBSService.class);
			AdministrationService adminService = Context.getAdministrationService();
	        String hl7ConfigFile = adminService
				.getGlobalProperty("sockethl7listener.hl7ConfigFile");
	        Properties prop = Util.getProps(hl7ConfigFile);
	        List<String> messages = nbsservice.getAlerts(null, prop);
	        int retVal = 0;
	        openSocket();
	        for (String message : messages){
	        	 sendMessage(host, port, message);
	        	 readAck();
	        	 retVal++;
	        }
	       
	      
		   
			
		} catch (Exception e){
			log.error(e.getMessage());	
			
		} finally
		{
			closeSocket();
			Context.closeSession();
		}

	}
	
	public void openSocket() throws IOException{
        try {
			socket = new Socket(host, port);
			socket.setSoLinger(true, 10000);
			
			os = socket.getOutputStream();
			is = socket.getInputStream();
		} catch (Exception e) {
			log.error(e.getMessage());	
			e.printStackTrace();
		}
    } 
	
	 public void closeSocket() {
        try {
            Socket sckt = socket;
            socket = null;
            if (sckt != null)
                sckt.close();
        }
        catch (Exception e) {
            
        }
    }
	 
	 private String readAck() throws IOException
	{
		StringBuffer stringbuffer = new StringBuffer();
		int i = 0;
		do {
			i = is.read();
			if (i == -1)
				return null;
            
			stringbuffer.append((char) i);
		}
		while (i != 28);        
		return stringbuffer.toString();
	}
	 
	 
	/**
	 * Prepares message and sends message on designated port
	 * @param host
	 * @param port
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String host, Integer port, String message) throws IOException{
		
		String Hl7StartMessage = "\u000b";
		String Hl7EndMessage = "\u001c";
		os.write( Hl7StartMessage.getBytes() );
		os.write(message.getBytes());
        os.write( Hl7EndMessage.getBytes() );
        os.write(13);
        os.flush();
	}
	
	
	

	@Override
	public void shutdown()
	{
		super.shutdown();
		try
		{
			//this.server.stop();
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
	}
	
	
	
	
	
	
}
