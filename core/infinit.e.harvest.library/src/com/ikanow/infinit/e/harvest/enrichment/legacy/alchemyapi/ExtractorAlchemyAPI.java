package com.ikanow.infinit.e.harvest.enrichment.legacy.alchemyapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.ikanow.infinit.e.data_model.InfiniteEnums;
import com.ikanow.infinit.e.data_model.InfiniteEnums.ExtractorDailyLimitExceededException;
import com.ikanow.infinit.e.data_model.InfiniteEnums.ExtractorDocumentLevelException;
import com.ikanow.infinit.e.data_model.store.document.DocumentPojo;
import com.ikanow.infinit.e.data_model.store.document.EntityPojo;
import com.ikanow.infinit.e.data_model.store.document.GeoPojo;
import com.ikanow.infinit.e.harvest.enrichment.legacy.EntityExtractorEnum;
import com.ikanow.infinit.e.harvest.enrichment.legacy.IEntityExtractor;
import com.ikanow.infinit.e.harvest.extraction.text.legacy.ITextExtractor;
import com.ikanow.infinit.e.harvest.utils.DimensionUtility;
import com.ikanow.infinit.e.harvest.utils.PropertiesManager;

public class ExtractorAlchemyAPI implements IEntityExtractor, ITextExtractor 
{
	@Override
	public String getName() { return "alchemyapi"; }
	
	private static final Logger logger = Logger.getLogger(ExtractorAlchemyAPI.class);
	private AlchemyAPI_JSON _alch = AlchemyAPI_JSON.GetInstanceFromProperties();
	private Map<EntityExtractorEnum, String> _capabilities = new HashMap<EntityExtractorEnum, String>();
	

	// Post processing to clean up people and geo entities
	AlchemyEntityPersonCleanser postProcPerson = null;
	AlchemyEntityGeoCleanser postProcGeo = null;

	/**
	 * Construtor, adds capabilities of Alchemy to hashmap
	 */
	public ExtractorAlchemyAPI()
	{
		//insert capabilities of this extractor
		_capabilities.put(EntityExtractorEnum.Name, "AlchemyAPI");
		_capabilities.put(EntityExtractorEnum.Quality, "1");
		_capabilities.put(EntityExtractorEnum.URLTextExtraction, "true");
		_capabilities.put(EntityExtractorEnum.GeotagExtraction, "true");
		_capabilities.put(EntityExtractorEnum.SentimentExtraction, "true");
		
		// Alchemy configuration:
		try {
			PropertiesManager properties = new PropertiesManager(); 
			int n = properties.getAlchemyPostProcessingSetting();
			
			if (0 != (1 & n)) {
				postProcPerson = new AlchemyEntityPersonCleanser();
				postProcPerson.initialize();
			} 
			if (0 != (2 & n)) {
				postProcGeo = new AlchemyEntityGeoCleanser();
				postProcGeo.initialize();
			} 
		}		
		catch (Exception e) {
			postProcPerson = null; // (just don't do post processing)
			postProcGeo = null; // (just don't do post processing)
		} 
	}
	
	//_______________________________________________________________________
	//_____________________________ENTITY EXTRACTOR FUNCTIONS________________
	//_______________________________________________________________________
	
	/**
	 * Takes a doc with some of the information stored in it
	 * such as title, desc, etc, and needs to parse the full
	 * text and add entities, events, and other metadata.
	 * 
	 * @param partialDoc The feedpojo before extraction with fulltext field to extract on
	 * @return The feedpojo after extraction with entities, events, and full metadata
	 * @throws ExtractorDocumentLevelException 
	 */
	@Override
	public void extractEntities(DocumentPojo partialDoc) throws ExtractorDocumentLevelException 
	{		
		// Run through specified extractor need to pull these properties from config file
		try
		{			
			// First off, some logic to check whether there's enough text for it to be worth doing anything:
			if (partialDoc.getFullText().length() < 16) { // Try and elongate full text
				partialDoc.setFullText(partialDoc.getTitle() + ": " + partialDoc.getDescription() + ". " + partialDoc.getFullText());
			}
			if (partialDoc.getFullText().length() < 16) { // Else don't waste Extractor call/error logging				
				throw new InfiniteEnums.ExtractorDocumentLevelException();				
			}
			
			String json_doc = _alch.TextGetRankedNamedEntities(partialDoc.getFullText());
			try
			{
				checkAlchemyErrors(json_doc, partialDoc.getUrl());
			}
			catch ( InfiniteEnums.ExtractorDocumentLevelException ex )
			{
				throw ex;
			}
			catch ( InfiniteEnums.ExtractorDailyLimitExceededException ex )
			{
				throw ex;
			}
			
			//Deserialize json into AlchemyPojo Object
			Gson gson = new Gson();
			AlchemyPojo sc = gson.fromJson(json_doc,AlchemyPojo.class);
			List<EntityPojo> ents = convertToEntityPoJo(sc);
			if (null != partialDoc.getEntities()) {
				partialDoc.getEntities().addAll(ents);
				partialDoc.setEntities(partialDoc.getEntities());
			}
			else {
				partialDoc.setEntities(ents);
			}
			
			// Alchemy post processsing:
			this.postProcessEntities(partialDoc);
		}
		catch (Exception e)
		{
			//Collect info and spit out to log
			logger.error("Exception Message: doc=" + partialDoc.getUrl() + " error=" +  e.getMessage(), e);
			throw new InfiniteEnums.ExtractorDocumentLevelException();
		}	
	}


	/**
	 * Simliar to extractEntities except this case assumes that
	 * text extraction has not been done and therefore takes the
	 * url and extracts the full text and entities/events.
	 * 
	 * @param partialDoc The feedpojo before text extraction (empty fulltext field)
	 * @return The feedpojo after text extraction and entity/event extraction with fulltext, entities, events, etc
	 * @throws ExtractorDocumentLevelException 
	 */
	@Override
	public void extractEntitiesAndText(DocumentPojo partialDoc) throws ExtractorDocumentLevelException 
	{
		// Run through specified extractor need to pull these properties from config file
		try
		{			
			String json_doc = _alch.URLGetRankedNamedEntities(partialDoc.getUrl());
			try
			{
				checkAlchemyErrors(json_doc, partialDoc.getUrl());
			}
			catch ( InfiniteEnums.ExtractorDocumentLevelException ex )
			{
				throw ex;
			}
			catch ( InfiniteEnums.ExtractorDailyLimitExceededException ex )
			{
				throw ex;
			}
			//Deserialize json into AlchemyPojo Object			
			AlchemyPojo sc = new Gson().fromJson(json_doc,AlchemyPojo.class);			
			//pull fulltext
			partialDoc.setFullText(sc.text);
			//pull entities
			List<EntityPojo> ents = convertToEntityPoJo(sc);
			if (null != partialDoc.getEntities()) {
				partialDoc.getEntities().addAll(ents);
				partialDoc.setEntities(partialDoc.getEntities());
			}
			else if (null != ents) {
				partialDoc.setEntities(ents);
			}
			
			// Alchemy post processsing:
			this.postProcessEntities(partialDoc);
		}
		catch (Exception e)
		{
			//Collect info and spit out to log
			logger.error("Exception Message: doc=" + partialDoc.getUrl() + " error=" +  e.getMessage(), e);
			throw new InfiniteEnums.ExtractorDocumentLevelException();
		}	
	}

	private void postProcessEntities(DocumentPojo doc) {
		if (null != postProcPerson) {
			try {
				postProcPerson.cleansePeopleInDocu(doc);
			}
			catch (Exception e) {} // do nothing, just carry on
		}
		if (null != postProcGeo) {
			try {
				postProcGeo.cleanseGeoInDocu(doc);
			}
			catch (Exception e) {} // do nothing, just carry on
		}		
	}
	
	/**
	 * Attempts to lookup if this extractor has a given capability,
	 * if it does returns value, otherwise null
	 * 
	 * @param capability Extractor capability we are looking for
	 * @return Value of capability, or null if capability not found
	 */
	@Override
	public String getCapability(EntityExtractorEnum capability) 
	{
		return _capabilities.get(capability);
	}	
	
	//_______________________________________________________________________
	//_____________________________TEXT EXTRACTOR FUNCTIONS________________
	//_______________________________________________________________________
	
	/**
	 * Takes a url and spits back the text of the
	 * site, usually cleans it up some too.
	 * 
	 * @param url Site we want the text extracted from
	 * @return The fulltext of the site
	 * @throws ExtractorDocumentLevelException 
	 */
	@Override
	public void extractText(DocumentPojo doc) throws ExtractorDocumentLevelException
	{
		try
		{
			
			String json_doc = _alch.URLGetText(doc.getUrl());
			try
			{
				checkAlchemyErrors(json_doc, doc.getUrl());
			}
			catch ( InfiniteEnums.ExtractorDocumentLevelException ex )
			{
				throw ex;
			}
			catch ( InfiniteEnums.ExtractorDailyLimitExceededException ex )
			{
				throw ex;
			}
			//Deserialize json into AlchemyPojo Object
			Gson gson = new Gson();
			AlchemyPojo sc = gson.fromJson(json_doc,AlchemyPojo.class);	
			doc.setFullText(sc.text);
		}
		catch (Exception e)
		{
			//Collect info and spit out to log
			logger.error("Exception Message: doc=" + doc.getUrl() + " error=" +  e.getMessage(), e);
			throw new InfiniteEnums.ExtractorDocumentLevelException();
		}	
	}
	
	//_______________________________________________________________________
	//______________________________UTILIY FUNCTIONS_______________________
	//_______________________________________________________________________
	
	/**
	 * Converts the json return from alchemy into a list
	 * of entitypojo objects.
	 * 
	 * @param json The json text that alchemy creates for a document
	 * @return A list of EntityPojo's that have been extracted from the document.
	 */
	private List<EntityPojo> convertToEntityPoJo(AlchemyPojo sc)
	{
		
		//convert alchemy object into a list of entity pojos
		List<EntityPojo> ents = new ArrayList<EntityPojo>();
		if ( sc.entities != null)
		{
			for ( AlchemyEntityPojo ae : sc.entities)
			{
				EntityPojo ent = convertAlchemyEntToEntPojo(ae);
				if ( ent != null )
					ents.add(ent);
			}
		}
		return ents;	
	}
	
	/**
	 * Checks the json returned from alchemy so we can handle
	 * any exceptions
	 * 
	 * @param json_doc
	 * @return
	 * @throws ExtractorDailyLimitExceededException 
	 * @throws ExtractorDocumentLevelException 
	 */
	private void checkAlchemyErrors(String json_doc, String feed_url) throws ExtractorDailyLimitExceededException, ExtractorDocumentLevelException 
	{
		if ( json_doc.contains("daily-transaction-limit-exceeded") )
		{
			logger.error("AlchemyAPI daily limit exceeded");
			throw new InfiniteEnums.ExtractorDailyLimitExceededException();			
		}
		else if ( json_doc.contains("cannot-retrieve:http-redirect") )
		{
			logger.error("AlchemyAPI redirect error on url=" + feed_url);
			throw new InfiniteEnums.ExtractorDocumentLevelException();
		}
		else if ( json_doc.contains("cannot-retrieve:http-error:4") )
		{
			logger.error("AlchemyAPI cannot retrieve error on url=" + feed_url);
			throw new InfiniteEnums.ExtractorDocumentLevelException();			
		}
	}
	
	// Utility function to convert an Alchemy entity to an Infinite entity
	
	public static EntityPojo convertAlchemyEntToEntPojo(AlchemyEntityPojo pojoToConvert)
	{
		try
		{
			EntityPojo ent = new EntityPojo();
			ent.setActual_name(pojoToConvert.text);
			ent.setType(pojoToConvert.type);
			ent.setRelevance(Double.parseDouble(pojoToConvert.relevance));
			ent.setFrequency(Long.parseLong(pojoToConvert.count));
			if (null != pojoToConvert.sentiment) {
				if (null != pojoToConvert.sentiment.score) {
					ent.setSentiment(Double.parseDouble(pojoToConvert.sentiment.score));
				}
				else { // neutral
					ent.setSentiment(0.0);
				}
			}
			// (else no sentiment present)
			
			if ( pojoToConvert.disambiguated != null )
			{
				ent.setSemanticLinks(new ArrayList<String>());
				ent.setDisambiguatedName(pojoToConvert.disambiguated.name);
				if ( pojoToConvert.disambiguated.geo != null )
				{
					GeoPojo geo = new GeoPojo();
					String[] geocords = pojoToConvert.disambiguated.geo.split(" ");
					geo.lat = Double.parseDouble(geocords[0]);
					geo.lon = Double.parseDouble(geocords[1]);
					ent.setGeotag(geo);
				}
				//Add link data if applicable
				if ( pojoToConvert.disambiguated.census != null)
					ent.getSemanticLinks().add(pojoToConvert.disambiguated.census);
				if ( pojoToConvert.disambiguated.ciaFactbook != null)
					ent.getSemanticLinks().add(pojoToConvert.disambiguated.ciaFactbook);
				if ( pojoToConvert.disambiguated.dbpedia != null)
					ent.getSemanticLinks().add(pojoToConvert.disambiguated.dbpedia);
				if ( pojoToConvert.disambiguated.freebase != null)
					ent.getSemanticLinks().add(pojoToConvert.disambiguated.freebase);
				if ( pojoToConvert.disambiguated.opencyc != null)
					ent.getSemanticLinks().add(pojoToConvert.disambiguated.opencyc);
				if ( pojoToConvert.disambiguated.umbel != null)
					ent.getSemanticLinks().add(pojoToConvert.disambiguated.umbel);
				if ( pojoToConvert.disambiguated.yago != null)
					ent.getSemanticLinks().add(pojoToConvert.disambiguated.yago);
				
				if ( ent.getSemanticLinks().size() == 0)
					ent.setSemanticLinks(null); //If no links got added, remove the list
			}
			else
			{
				//sets the disambig name to actual name if
				//there was no disambig name for this ent
				//that way all entities have a disambig name
				ent.setDisambiguatedName(ent.getActual_name());
			}
			//Calculate Dimension based on ent type
			ent.setDimension(DimensionUtility.getDimensionByType(ent.getType()));
			return ent;
		}
		catch (Exception ex)
		{
			logger.error("Line: [" + ex.getStackTrace()[2].getLineNumber() + "] " + ex.getMessage());
			ex.printStackTrace();
			//******************BUGGER***********
			//LOG ERROR TO A LOG
		}
		return null;
	}
}
