/**
 * 
 */
package com.ikanow.infinit.e.core.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

import com.ikanow.infinit.e.data_model.Globals;


/**
 * @author cmorgan
 *
 */
public class PropertiesManager {
	
	private static final Logger logger = Logger.getLogger(PropertiesManager.class);

	/** 
	  * Class Constructor used to pull the properties object
	 * @throws IOException  
	  */
	public PropertiesManager() 
	{
		try 
		{
			//LINUX VERSION
			FileInputStream fis = new FileInputStream(Globals.getAppPropertiesLocation());
			properties = new Properties();
			properties.load(fis);
			fis.close();
		} 
		catch (Exception e) 
		{
			// If an exception occurs log the error
			logger.error("Exception Message: " + e.getMessage(), e);
			properties = null;
		}		
	}
	/** 
	  * Private Class Variables
	  */
	private Properties properties = null;

	/**
	 * Get a property based only on the key
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
// Misc application metadata: (API and DB)	
	
	/** 
	  * Get application name
	  */
	public String getApplicationName() {
		return this.getProperty("app.appname");
	}
	/** 
	  * Get application copyright
	  */
	public String getApplicationCopyright() {
		return this.getProperty("app.copyright");
	}
	/** 
	  * Get application version
	  */
	public String getApplicationVersion() {
		return this.getProperty("app.version");
	}
	
// General harvester configuration	
	
	public long getMinimumHarvestTimeMs() {
		String s = this.getProperty("harvest.mintime.ms");
		if (null == s) {
			return 300000; // (default: 5 minutes)
		}
		else {
			return Long.parseLong(s);
		}
	}
	
	// Format is either (type:nthreads_per_type)+ for types in (file,db,feed) or just an integer (across all types)
	// Defaults to "5"
	public String getHarvestThreadConfig() {
		String s = this.getProperty("harvest.threads");
		if (null == s) {
			return "1";
		}
		else {
			return s;
		}			
	}
	
	// Distribution batch size (can be separate for harvest vs sync):
	// The number of sources to batch together (for smaller numbers of sources may need to be low, eg 1, or
	// the distribution won't work)
	public int getDistributionBatchSize(boolean bSync) {
		String s = "harvest.distribution.batch.";
		if (bSync) {
			s += "sync";
		}
		else {
			s += "harvest";
		}
		s = this.getProperty(s);
		if (null == s) {
			return 20;
		}
		return Integer.parseInt(s);
	}
}


