package com.ikanow.infinit.e.harvest.enrichment.custom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * JavaScriptUtils
 * @author cvitter
 */
public class JavaScriptUtils 
{
	// initScript - used to pass document in to the script engine
	public static String initScript = "var _doc = eval('(' + document + ')'); \n";
	
	// scripts to pass entity and event json objects into javascript methods
	public static String iteratorDocScript = "var _iterator = eval('(' + _iterator + ')'); \n";
	
	// genericFunctionCall - all functions passed in via $SCRIPT get the following name
	public static String genericFunctionCall = "getValue";
	
	/**
	 * getScript
	 * Extracts JavaScript code from $SCRIPT() and wraps in getVal function
	 * @param script - $SCRIPT()
	 * @return String - function getVal()
	 */
	public static String getScript(String script)
	{
		// The start and stop index use to substring the script
		int start = script.indexOf("(");
		int end = script.lastIndexOf(")");
		
		if (script.toLowerCase().startsWith("$script"))
		{
			// Remove $SCRIPT() wrapper and then wrap script in 'function getValue() { xxxxxx }'
			return "function " + genericFunctionCall + "() { " + script.substring(start + 1, end) + " }";
		}
		else
		{
			// Simply remove $FUNC() wrapper
			return script.substring(start + 1, end);
		}
	}
	
	
	/**
	 * containsScript
	 * Determines whether or not value passed in contains script or a function
	 * call via $SCRIPT or $FUNC
	 * @param s
	 * @return
	 */
	public static Boolean containsScript(String s)
	{
		if (s.toLowerCase().startsWith("$script") || s.toLowerCase().startsWith("$func"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	
	/**
	 * getJavaScriptFile
	 * Retrieve a JavaScript file located at the fileUrl
	 * @param fileUrl - http://somewhere.com/javascript.js
	 * @return
	 */
	public static String getJavaScriptFile(String fileUrl)
	{
		StringBuffer javaScript = new StringBuffer();
		try
		{
			// Create java.net.URL from fileUrl if possible
			URL url = new URL(fileUrl);

			// Read the contents of the url into a BufferedReader
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));

			// Read the contents of r line by line and append to our StringBuffer
			while (javaScript.append(r.readLine()) != null) {}

			// Close our reader
			r.close();
		}
		catch (Exception e)
		{			
		}
		return javaScript.toString();
	}

}
