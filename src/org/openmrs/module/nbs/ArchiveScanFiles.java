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
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.scheduler.TaskDefinition;



/**
 *
 * @author Steve McKee
 */
public class ArchiveScanFiles extends ArchiveMergeFiles {
	private Log log = LogFactory.getLog(this.getClass());
	private TaskDefinition taskConfig;
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		Context.openSession();
		
		try {
			log.info("Starting Archive Scan Files at " + new Timestamp(new Date().getTime()));
			archiveScanFiles();
			log.info("Finished Archive Scan Files at " + new Timestamp(new Date().getTime()));
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
	 * Archive any files in the scan directory that are over the "daysToKeep" property set for the this 
	 * scheduled task.
	 */
	private void archiveScanFiles() {

		ATDService chirdlUtilBackportsService = Context.getService(ATDService.class);
		List<String> scanDirectories = chirdlUtilBackportsService.getFormAttributesByNameAsString("defaultExportDirectory");
		if (scanDirectories == null) {
			return;
		}
		String days = this.taskConfig.getProperty("daysToKeep");
		int daysToKeep = 1;
		try {
			daysToKeep = Integer.parseInt(days);
		} catch (NumberFormatException e) {}
		FileFilter oldFilesFilter = new OldFilesFilter(daysToKeep);
		for (String scanDirectoryStr : scanDirectories) {
			File scanDirectory = new File(scanDirectoryStr);
			if (!scanDirectory.exists()) {
				continue;
			}
			
			// Check to see if an archive directory exists.  If not, create it.
			File archiveDirectory = new File(scanDirectory, archiveDirectoryStr);
			archiveDirectory.mkdir();
            
			// Do the scan directory
            File[] files = scanDirectory.listFiles(oldFilesFilter);
            if (files != null && files.length > 0) {
            	moveFiles(files, scanDirectory, archiveDirectory);
            }
		}
	}
}
