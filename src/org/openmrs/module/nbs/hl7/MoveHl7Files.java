/**
 * 
 */
package org.openmrs.module.nbs.hl7;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * @author Tammy Dugan
 */

public class MoveHl7Files extends AbstractTask {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private static boolean isInitialized = false;
	
	private boolean keepLooking = true;
	
	private static Date lastMoveMergeFiles;
	
	private static Date nextLatestMoveMergeFiles;
	
	private static Boolean TFAMP_Alert = false;
	
	@Override
	public void initialize(TaskDefinition config) {
		super.initialize(config);
		isInitialized = false;
	}
	
	@Override
	public void execute() {
		while (this.keepLooking) {
			Context.openSession();
			
			try {
				if (!isInitialized) {
					isInitialized = true;
				}
				
				//move files from pending to merge directory
				moveHL7Files();
				
			}
			catch (Exception e) {
				log.error(e.getMessage());
				log.error(Util.getStackTrace(e));
			}
			finally {
				Context.closeSession();
			}
			try {
				Thread.sleep(10000);//check every ten seconds
			}
			catch (InterruptedException e) {
				log.error("Error generated", e);
			}
		}
	}
	
	@Override
	public void shutdown() {
		this.keepLooking = false;
		
		super.shutdown();
	}
	
	private void moveHL7Files() {
		
		AdministrationService adminService = Context.getAdministrationService();
		ChirdlUtilService chirlUtilService = Context.getService(ChirdlUtilService.class);
		
		//look at a global property for the batch size of merge files that
		//Teleform is able to process
		Integer numFilesToMove = null;
		ArrayList<String> directories = new ArrayList<String>();
		
		try {
			numFilesToMove = Integer.parseInt(adminService.getGlobalProperty("nbs.hl7DirectoryBatchSize"));
		}
		catch (NumberFormatException e) {}
		
		if (numFilesToMove == null) {
			return;
		}
		
		
		try {
			LocationAttributeValue ISDHDefaultDirectory = chirlUtilService.getLocationAttributeValue(1, "ISDHDefaultHl7Directory");
			LocationAttributeValue TriggerDirectory = chirlUtilService.getLocationAttributeValue(1, "TriggerDefaultHl7Directory");
			
			if (ISDHDefaultDirectory != null)  {
				String ISDHDefaultDirectoryString = ISDHDefaultDirectory.getValue();	
				if (ISDHDefaultDirectoryString != null){
					directories.add(ISDHDefaultDirectoryString);
				}
			}
			
			if (TriggerDirectory != null) {
				String TriggerDirectoryString = TriggerDirectory.getValue();	
				if (TriggerDirectoryString != null){
					directories.add(TriggerDirectoryString);
				}
			}
		} catch (Exception e) {
			log.error("Error reading default directories from attributes:" + e.toString());
			
		}
		
		for (String directory : directories) {
			
			//get the value of the pending merge directory for the form			
			
			//looking up all of the default and pending directories individually
			//was very slow so I am doing a substring match to speed this method up
			String pendingDirectory = directory+"/Pending/";
			
			//see how many xml files are in the default merge directory for the form
			String[] fileExtensions = new String[] { ".hl7", ".dat" };
			File[] files = IOUtil.getFilesInDirectory(directory, fileExtensions);
			
			Integer numFiles = 0;
			
			if (files != null) {
				numFiles = files.length;
			}
			
			files = IOUtil.getFilesInDirectory(pendingDirectory, fileExtensions);
			
			if (files == null) {
				continue;
			}
			
			//move files from the pending merge directory to the default merge directory
			//until the batch size numbper of files are in the default merge directory
			int i = 0;
			for (i = 0; numFiles <= numFilesToMove && i < files.length; numFiles++, i++) {
				File fileToMove = files[i];
				if (fileToMove.length() == 0) {
					numFiles--;
					continue;
				}
				String sourceFilename = fileToMove.getPath();
				String targetFilename = directory + "/" + IOUtil.getFilenameWithoutExtension(sourceFilename)
				        + ".move";
				try {
					//copy the file to .20 extension so Teleform doesn't
					//pick up the file before it is done writing
					IOUtil.copyFile(sourceFilename, targetFilename, true);
					//rename the file to .xml so teleform will see it
					IOUtil.renameFile(targetFilename, directory + "/"
					        + IOUtil.getFilenameWithoutExtension(targetFilename) + ".hl7");
					
					File srcFile = new File(sourceFilename);
//					File tgtFile = new File(targetFilename);
					
					
					//check if the file exists under the following file extensions
					targetFilename = directory + "/" + IOUtil.getFilenameWithoutExtension(sourceFilename)
					        + ".hl7";
					
					File tgtFile = new File(targetFilename);
					
					if (!tgtFile.exists()) {
						
						targetFilename = directory + "/" + IOUtil.getFilenameWithoutExtension(sourceFilename)
						        + ".dat";
						tgtFile = new File(targetFilename);

					}
					
					

					//if the source file is bigger than 
					//the target file, the copy was truncated so
					//don't mark it as copied
					if (tgtFile.exists()) {
						if (srcFile.length() > tgtFile.length()) {
							// don't rename the pendingMergeDirectory file so
							// that it will get picked up on the next time
							// around
							log.error("copied file: " + tgtFile.getPath() + " is truncated. Target will be deleted " +
									" and recopied" );
							//delete the truncated xml so it won't break the
							//Teleform merger
							IOUtil.deleteFile(targetFilename);
						} else {
							IOUtil.renameFile(sourceFilename, pendingDirectory + "/"
							        + IOUtil.getFilenameWithoutExtension(sourceFilename) + ".copy");
							
						}
					}	
						
					
				}
				catch (Exception e) {
					log.error("File copy exception:" + e.toString());
					continue;
				}
				
			}
			
		}
	}
	
}
