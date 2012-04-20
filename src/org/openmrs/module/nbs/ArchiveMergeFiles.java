/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.nbs;

import java.io.File;
import java.io.FileFilter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;


/**
 *
 * @author Steve McKee
 */
public class ArchiveMergeFiles extends AbstractTask {
	
	private Log log = LogFactory.getLog(this.getClass());
	protected static String archiveDirectoryStr = "Archive";
	protected static String pendingDirectoryStr = "Pending";
	private TaskDefinition taskConfig;
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		Context.openSession();
		
		try {
			log.info("Starting Archive Merge Files at " + new Timestamp(new Date().getTime()));
			archiveMergeFiles();
			log.info("Finished Archive Merge Files at " + new Timestamp(new Date().getTime()));
		}
		catch (Exception e) {
			log.info("Archive Merge Files Errored Out at " + new Timestamp(new Date().getTime()));
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
		finally {
			Context.closeSession();
		}
	}
	
	/**
	 * Archive any files in the merge and pending directories that are over the "daysToKeep" property set for the this 
	 * scheduled task.
	 */
	private void archiveMergeFiles() {
		ATDService chirdlUtilBackportsService = Context.getService(ATDService.class);
		List<String> mergeDirectories = chirdlUtilBackportsService.getFormAttributesByNameAsString("defaultMergeDirectory");
		if (mergeDirectories == null) {
			return;
		}
		
		String days = this.taskConfig.getProperty("daysToKeep");
	
		int daysToKeep = 1;
		try {
			daysToKeep = Integer.parseInt(days);
		} catch (NumberFormatException e) {}
		FileFilter oldFilesFilter = new OldFilesFilter(daysToKeep);
		for (String mergeDirectoryStr : mergeDirectories) {
			File mergeDirectory = new File(mergeDirectoryStr);
			if (!mergeDirectory.exists()) {
				continue;
			}
			
			// Do the Pending folder
            File pendingDirectory = new File(mergeDirectory, pendingDirectoryStr);
			if (pendingDirectory.exists()) {
				// Check to see if an archive directory exists.  If not, create it.
				File pendingArchiveDirectory = new File(pendingDirectory, archiveDirectoryStr);
				pendingArchiveDirectory.mkdir();
	            
	            File[] files = pendingDirectory.listFiles(oldFilesFilter);
	            if (files != null && files.length > 0) {
	            	moveFiles(files, pendingDirectory, pendingArchiveDirectory);
	            }
			}
			
			// Check to see if an archive directory exists.  If not, create it.
			File archiveDirectory = new File(mergeDirectory, archiveDirectoryStr);
			archiveDirectory.mkdir();
            
			// Do the merge directory
            File[] files = mergeDirectory.listFiles(oldFilesFilter);
            if (files != null && files.length > 0) {
            	moveFiles(files, mergeDirectory, archiveDirectory);
            }
		}
	}
	
	/**
	 * Moves the specified list of files to the target directory.  Once moved, the original file will be deleted.
	 * 
	 * @param files The files to move to the target directory.
	 * @param sourceDirectory The directory where the files being moved reside.
	 * @param targetDirectory The directory where the files will be moved to.
	 */
	protected void moveFiles(File[] files, File sourceDirectory, File targetDirectory) {
		int filesMoved = 0;
		log.info("Archiving files in the following directory: " + sourceDirectory.getAbsolutePath());
        for (File file : files) {        	
        	// Attempt to copy the file.
        	File archiveFile = new File(targetDirectory, file.getName());
        	if (archiveFile.exists()) {
        		// The file already exists...leave it alone.
        		continue;
        	}
        	
        	try {
        		if (file.length() > 0) {
        			IOUtil.copyFile(file.getAbsolutePath(), archiveFile.getAbsolutePath(), true);
        		}
            } catch (Exception e) {
                log.error("Error copying file from " + file.getAbsolutePath() + " to " + 
                	archiveFile.getAbsolutePath(), e);
                if (archiveFile.exists()) {
                	// Delete the archive file we created.
                	try {
                		archiveFile.delete();
                	} catch (Exception ex) {
                		log.error("Error deleting archived file after failure from " + 
                			archiveFile.getAbsolutePath(), ex);
                	}
                }
                
                continue;
            }
            
            // Delete the original file.
            try {
            	file.delete();
            } catch (Exception e) {
            	log.error("Error deleting file from " + file.getAbsolutePath(), e);
            	continue;
            }
            
            filesMoved++;
        }
        
        log.info("Successfully archived " + filesMoved + " files from " + sourceDirectory.getAbsolutePath() + 
        	" to " + targetDirectory.getAbsolutePath());
	}
	
	/**
	 * File filter class used to return any files (not directories) outside the "daysToKeep" property set for this 
	 * scheduled task.
	 *
	 * @author Steve McKee
	 */
	protected class OldFilesFilter implements FileFilter {
		
		int daysToKeep = 1;
		
		/**
		 * Constructor method
		 */
		public OldFilesFilter(int daysToKeep) {
			this.daysToKeep = daysToKeep;
		}

		/**
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			// Directories do not get moved.
			if (pathname.isDirectory()) {
				return false;
			}
			
			// Create a Calendar to hold the last modified date/time of the file.
	        Calendar fileCal = Calendar.getInstance();
	        fileCal.setTime(new Date(pathname.lastModified()));
	        
	        // Create a Calendar that is the current date/time minus the number of days we keep.
	        Calendar compareCal = Calendar.getInstance();
	        compareCal.add(Calendar.DAY_OF_MONTH, -daysToKeep);
	        // Accept the file if its last modified date/time is outside the number of days we want to keep.
	        if (fileCal.compareTo(compareCal) < 0) {
	        	return true;
	        }
	        
	        return false;
        }
		
	}
}
