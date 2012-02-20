/**
 * <p>Infinit.e</p>
 *
 * <p>Copyright (c) 2011 IKANOW, llc.</p>
 * <p>http://www.ikanow.com</p> 
 *
 * <p>NOTICE:  IKANOW permits you to use this this file in accordance with the terms of the license agreement 
 * accompanying it.  For information about the licensing and copyright of this Plug-In please contact IKANOW, llc. 
 * at support&#64;ikanow.com.</p>
 *
 * <p>http://www.ikanow.com/terms-conditions/</p>
 * 
 */
package com.ikanow.infinit.e.widget.library.data
{	
	import com.ikanow.infinit.e.widget.library.frameworkold.QueryResults;
	import com.ikanow.infinit.e.widget.library.utility.Authentication;
	import com.ikanow.infinit.e.widget.library.widget.IResultSet;
	
	import mx.collections.ArrayCollection;
	import mx.formatters.DateFormatter;
	import mx.utils.ObjectUtil;
	
	import system.data.Map;
	import system.data.maps.HashMap;

	/**
	 * @private
	 * Data object used to hold query results, extends
	 * IResultSet to allow setting the results.  Used
	 * by framework.
	 */
	public class ResultSet implements IResultSet
	{
		// Type of object: base (false) or derived (true)
		private var _bDerived:Boolean = false;
		private var _description:String = null; // A human-readable version generated by the API
		
		// Base Query results
		private var _queryBase:QueryResults = null; // the actual returned values from the query
		private var _queryBaseObject:Object = null; // The object used to perform the query
		
		// Derived Query results
		//TODO (can I just use _queryBaseObject)
		
		// Location in the various chains:
		private var _nPosInChain:int = 0;
		private var _filterChain:ArrayCollection = null; // Starting from top documents, applied filters
		private var _queryChain:ArrayCollection = null; // Starting from base query, additional queries
		private var _filterChild:ResultSet = null;  // The next level down in the filter chain (n/a for query children)
		private var _queryChild:ResultSet = null; // The next level down in the query chain (n/a for filter children)
		private var _parent:ResultSet = null; // The parent (filter and query chains only intersect at base queries)
		
		//______________________________________________________________________________________
		//
		// VARIOUS CONSTRUCTORS
				
		// 1. Base query object	
		
		public static function createBaseQuery(queryBase:QueryResults, queryBaseObject:Object, description:String):ResultSet {
			var res:ResultSet = new ResultSet();
			res._queryBase = queryBase;
			res._queryBaseObject = queryBaseObject;
			res._description = description;
			
			res._filterChain = new ArrayCollection();
			res._filterChain.addItem(res);
			res._filterChild = ResultSet.createDocumentSet(res);
			res._filterChain.addItem(res._filterChild);
				// (always add "top results" even if there aren't any documents, so people don't need to null ptr check everywhere)
			
			res._queryChain = new ArrayCollection();
			res._queryChain.addItem(res);
			return res;
		}
		
		// 2. Child query objects
		
		public static function createChildQuery(queryBase:QueryResults, queryBaseObject:Object, description:String, parent:ResultSet):ResultSet {
			var res:ResultSet = new ResultSet();
			res._queryBase = queryBase;
			res._queryBaseObject = queryBaseObject;
			res._description = description;
			
			res._filterChain = new ArrayCollection();
			res._filterChain.addItem(res);
			res._filterChild = ResultSet.createDocumentSet(res);
			res._filterChain.addItem(res._filterChild);
				// (always add "top results" even if there aren't any documents, so people don't need to null ptr check everywhere)
			
			res._parent = parent;
			res._queryChain = parent.getChain();
			res._queryChain.addItem(res);
			res._nPosInChain = parent.getPositionInChain() + 1;
			if (null != res._parent) { //Insert/append myself
				res._queryChild = res._parent._queryChild;
				res._parent._queryChild = res;
			}
			return res;
		}
		
		// 3. "Top documents" from query
		
		public static function createDocumentSet(parent:ResultSet):ResultSet {
			var res:ResultSet = new ResultSet();
			res._parent = parent;
			res._bDerived = true;
			res._description = parent.getDescription();
			
			// Set data:
			var dummyData:Object = new Object();
			res._queryBase = new QueryResults();
			res._queryBase.populateQueryResults(dummyData, null, res._queryBase.getContext());
			res._queryBase.getFeeds().source = parent.getTopDocuments().source;

			// Chaining
			res._filterChain = parent._filterChain;
			res._nPosInChain = 1;
			return res;
		}
		
		// 4. Filters
		
		public static function createFilteredDocumentSet(parent:IResultSet, docs:ArrayCollection, filterDescription:String):IResultSet {
			var res:ResultSet = new ResultSet();
			res._parent = parent as ResultSet;
			res._bDerived = true;
			res._description = filterDescription;
			if (null != res._parent) { //append myself, remove parent's child (ie this is now the end of the chain)
				res._parent._filterChild = res;
			}
			
			// Set data:
			var dummyData:Object = new Object();
			res._queryBase = new QueryResults();
			res._queryBase.populateQueryResults(dummyData, null, res._queryBase.getContext());
			res._queryBase.getFeeds().source = docs.source;
			
			// Chaining
			res._filterChain = parent.getChain();
			res._nPosInChain = parent.getPositionInChain() + 1;
			return res;			
		}
		
		//______________________________________________________________________________________
		//
		// METADATA
		
		/**
		 * A textual description of the contents of the result set
		 */
		public function getDescription():String {
			return _description;
		}
		
		/**
		 * The query object used to generated the results set (null if this IResultSet generated from a filter)
		 * See the REST API documentation for more details on the query object format.
		 */
		public function getQueryObject():Object {
			return _queryBaseObject;
		}
		
		//______________________________________________________________________________________
		//
		// DATA ACCESS
		
		/**
		 * Returns the ArrayCollection holding the top document objects from the query (or filter) - null if none exist
		 * See the REST API documentation for more details on the document object and its various children (events, entities).
		 */
		public function getTopDocuments():ArrayCollection {
			// 3 cases:
			// 1] Base query, get return documents
			// 2] Top query, use the same pointer
			// 3] Filter object, pointer overridden with a new object
			return _queryBase.getFeeds();
		}
		
		//_________________
		
		// Object Aggregations
		
		/**
		 * If "event timeline" aggregations are available (either directly or derived from the documents), an array
		 * of "event" objects (see the REST API for more details on the "event" object) - null if none exist
		 * An "event" is a relationship between more than 1 entity of a transient nature (eg "person visits place")
		 * The "event timeline" aggregates events and summaries by day, and facts over the entire timerange. 
		 */
		public function getEventsTimeline():ArrayCollection {
			if (_bDerived) {
				if (0 == _queryBase.getEventsTimeline().length) {
					_queryBase.getEventsTimeline().source = getAggregatedEventsTimeline().getValues();
				}
			}
			return _queryBase.getEventsTimeline();		
		}
		/**
		 * If entity aggregations are available (either directly or derived from the documents), an array
		 * of entity objects (see the REST API for more details on the entity object) - null if none exist
		 */
		public function getEntities():ArrayCollection {
			if (_bDerived) {
				if (0 == _queryBase.getEntities().length) {
					_queryBase.getEntities().source = getDeDuplicatedEntities().getValues();					
				}
				return _queryBase.getEntities();
			}
			else {
				return _queryBase.getEntities();
			}
		}
		
		/**
		 * If "event" aggregations are available (either directly or derived from the documents), an array
		 * of "event" objects (see the REST API for more details on the "event" object) - null if none exist
		 * An "event" is a relationship between more than 1 entity of a transient nature (eg "person visits place")
		 */
		public function getEvents():ArrayCollection {
			if (_bDerived) {
				if (0 == _queryBase.getEvents().length) {
					_queryBase.getEvents().source = getDeDuplicatedEventsOrFacts("Event").getValues();					
				}
				return _queryBase.getEvents();
			}
			else {
				return _queryBase.getEvents();
			}			
		}
		/**
		 * If "fact" aggregations are available (either directly or derived from the documents), an array
		 * of "fact" objects (see the REST API for more details on the "fact" object) - null if none exist
		 * A "fact" is a relationship between more than 1 entity with some degree of persistence (eg "person works for company")
		 */
		public function getFacts():ArrayCollection {
			if (_bDerived) {
				if (0 == _queryBase.getFacts().length) {
					_queryBase.getFacts().source = getDeDuplicatedEventsOrFacts("Fact").getValues();					
				}
				return _queryBase.getFacts();
			}
			else {
				return _queryBase.getFacts();
			}			
		}
		/**
		 * If available (either directly from the query, or from the documents), an array collection of
		 * "moments" (see REST API for more details on the moment object), every "getMomentInterval()" seconds apart
		 */
		public function getMoments():ArrayCollection {
			return null; // Not yet supported
		}
		
		/**
		 * For "moments" (See getMoments), the interval over which documents are aggregated, in seconds.
		 */		
		public function getMomentInterval():Number {
			return null; // Not yet supported
		}
		
		//_________________
		
		// Counts
		
		/**
		 * If available (either directly from the query, or from the documents), an array collection of
		 * geotags (format: {lat:number, lon:number, count:number}) ordered by lat/long.
		 * Note that due to the granularity of the geohash used to aggregate lat/longs, distinct elements can have the 
		 * same lat/long - since they will always be adjacent in the array, they can simply be summed.
		 */
		public function getGeoCounts():ArrayCollection {
			if (_bDerived) {
				//TODO
				return null;
			}
			else {
				return _queryBase.getGeoCounts();
			}									
		}
		/**
		 * For "geo-counts" (See getGeoCounts), the maximum count in the query (since unlike other counts, geo is ordered by lat/long)
		 */		
		public function getGeoMaxCount():int {
			if (_bDerived) {
				//TODO
				return 0;
			}
			else {
				return _queryBase.getGeoMaxCount();
			}												
		}
		/**
		 * For "geo-counts" (See getGeoCounts), the minimum count in the query (since unlike other counts, geo is ordered by lat/long)
		 */		
		public function getGeoMinCount():int {
			if (_bDerived) {
				//TODO
				return 0;
			}
			else {
				return _queryBase.getGeoMinCount();
			}															
		}
		
		
		/**
		 * If available (either directly from the query, or from the documents), an array collection of
		 * {start_time:number, count:number}, every "getTimeCountInterval()" seconds apart and ordered by count
		 */
		public function getTimeCounts():ArrayCollection {
			if (_bDerived) {
				//TODO
				return null;
			}
			else {
				return _queryBase.getTimeCounts();
			}																		
		}
		/**
		 * For "time counts" (See getTimeCounts), the interval over which documents are aggregated, in seconds.
		 */		
		public function getTimeCountInterval():Number {
			if (_bDerived) {
				//TODO
				return 0;
			}
			else {
				return _queryBase.getTimeCountInterval();
			}																					
		}
		
		/**
		 * If available (either directly from the query, or from the documents), an array collection of
		 * {source_key:string, count:number}, ordered by count
		 */
		public function getSourceKeyCounts():ArrayCollection {
			if (_bDerived) {
				//TODO
				return null;
			}
			else {
				return _queryBase.getSourceKeyCounts();
			}																								
		}
		/**
		 * If available (either directly from the query, or from the documents), an array collection of
		 * {source_tag:string, count:number}, ordered by count
		 */
		public function getSourceTagCounts():ArrayCollection {
			if (_bDerived) {
				//TODO
				return null;
			}
			else {
				return _queryBase.getSourceTagCounts();
			}																											
		}
		/**
		 * If available (either directly from the query, or from the documents), an array collection of
		 * {source_type:string, count:number}, ordered by count
		 */
		public function getSourceTypeCounts():ArrayCollection {
			if (_bDerived) {
				//TODO
				return null;
			}
			else {
				return _queryBase.getSourceTypeCounts();
			}																														
		}
		
		/**
		 * Developers can specify their own aggregations using the ElasticSearch "facet" interface - this object
		 * is a map (vs the string specified in the request) of ArrayCollections of term/counts, ordered by count.
		 */
		public function getUserAggregations():Object {
			return _queryBase.getOther();
		}
		
		//______________________________________________________________________________________
		//
		// FILTERING
		
		/**
		 * Returns the IResultSet backed by the top N documents (null if no documents returned by the query)
		 * See the REST API documentation for more details on the document object and its various children (events, entities).
		 * @return The IResultSet, if documents have been returned for the query
		 */
		public function getTopQueryResults():IResultSet {
			if (_bDerived) { // Only really has meaning for base query objects 
				return this;
			}
			else {
				return _filterChild;
			}
		}
		
		/**
		 * If a filter has been applied to the original query, returns the corresponding IResultSet (null otherwise)
		 * @return The filtered IResultSet, if it exists
		 */
		public function getFilteredQueryResults():IResultSet {
			if (_bDerived || (null == _filterChild._filterChild)) {
				return _filterChild;
			}
			else { // Base query - top documents unless that's been filtered
				return _filterChild._filterChild;
			}
		}
		
		/**
		 * If this IResultSet is the result of a filter, returns the parent IResultSet
		 * @return - the parent IResultSet
		 */
		public function getParentQueryResults():IResultSet {
			return _parent;
		}
		
		/**
		 * An array collection of IResultSet objects representing *either* a query and its sub-queried children
		 * *or* the top results from a query and its filtered children
		 * You can think of a query and its sub-queried children as a horizontal chain, with vertical chains dropping down from it,
		 *  the first of which is parent.getTopQueryResults()
		 * @return an array collection of IResultSets
		 */		
		public function getChain():ArrayCollection {
			if (_bDerived) { // Filters, always return the filter chain
				return _filterChain;
			}
			else { // Base queries, always return the query chain
				return _queryChain;
			}
		}
		
		/**
		 * The position of the current IResultSet object in the getFilterChain() array collection
		 * @return the index of the current query in the filter chain (see getFilterChain)
		 */
		public function getPositionInChain():int {
			return _nPosInChain;
		}		
		
		/**
		 * Returns the number of results matching the query
		 * @return number of results in this query
		 */
		public function getQuerySetSize():Number {
			if ( _bDerived ) {
				return _queryBase.getFeeds().length;
			}
			else
			{
				return _queryBase.getBaseData().stats.found;
			}
		}
		//______________________________________________________________________________________
		//
		// DERIVED UTILITIES
		
		// Get the de-duplicated entities:
		
		private function getDeDuplicatedEntities():Map
		{			
			var queryDeDupeMap:HashMap = new HashMap();
			for each ( var doc:Object in this.getTopDocuments() )
			{
				if ( null != doc.entities )
				{
					for each ( var entity:Object in doc.entities )
					{
						var currEnt:Object = queryDeDupeMap.get(entity.index);
						if ( null == currEnt ) //if no ent yet, put this one in
						{
							currEnt = ObjectUtil.copy(entity);
							queryDeDupeMap.put(currEnt.index,currEnt);
						}
						else {
							//Get MAX totalfreq,doccount,frequency,docsig,relevance,etc
							if ( entity.totalfrequency > currEnt.totalfrequency ) // (in the entire dataset - sometimes get wrong numbers from the API)
								currEnt.totalfrequency = entity.totalfrequency;
							if ( entity.doccount > currEnt.doccount )
								currEnt.doccount = entity.doccount; // (in the entire dataset - sometimes get wrong numbers from the API)
							if ( entity.frequency > currEnt.frequency )
								currEnt.frequency = entity.frequency; // (ie max freq) 
							if ( entity.significance > currEnt.significance )
								currEnt.significance = entity.significance; // (ie max "doc" significance)
						}
					}
				}
			}
			return queryDeDupeMap;
		}
		private function getDeDuplicatedEventsOrFacts(type:String):Map
		{			
			var queryDeDupeMap:HashMap = new HashMap();
			for each ( var doc:Object in this.getTopDocuments() )
			{
				if ( null != doc.associations )
				{
					// Association: pre-calc:
					if (type != "Summary") {
						var entityHash:Object = new Object();
						for each (var entity:Object in doc.entities) {
							entityHash[entity.index] = entity.datasetSignificance;
						}
					}
					
					for each ( var event:Object in doc.associations )
					{
						if (event.assoc_type != type) {
							continue;
						}
						var event_index:String = event.entity1_index + "|" + event.entity2_index + "|"
							+ event.verb_category + "|" + event.geo_index;
						var currEvent:Object = queryDeDupeMap.get(event_index);
						if ( null == currEvent ) //if no ent yet, put this one in
						{
							currEvent = ObjectUtil.copy(event);
							
							// Association significance
							var assoc_sig:Number = 0.0;
							var ent1_sig:Number = -1.0; 
							if (null != event.entity1_index) ent1_sig = entityHash[event.entity1_index];
							var ent2_sig:Number = -1.0;
							if (null != event.entity2_index) entityHash[event.entity2_index];
							var geo_sig:Number = -1.0;
							if (null != event.geo_index) entityHash[event.geo_index];
							if (ent1_sig > 0.0) assoc_sig += ent1_sig*ent1_sig;
							if (ent2_sig > 0.0) assoc_sig += ent2_sig*ent2_sig;
							if (geo_sig > 0.0) assoc_sig += 0.25*geo_sig*geo_sig;
							currEvent["assoc_sig"] = Math.sqrt(assoc_sig);
							
							currEvent["doccount"] = 0;
							queryDeDupeMap.put(event_index, currEvent);
						}
						currEvent.doccount++;
					}
				}
			}
			return queryDeDupeMap;
		}
		
		private function getAggregatedEventsTimeline():Map
		{			
			var eventsHashMap:HashMap = new HashMap();			
			var df:DateFormatter = new DateFormatter();
			df.formatString = "YYYY-MM-DD";
			for each ( var doc:Object in this.getTopDocuments() )
			{
				if ( null != doc.associations )
				{
					var pubdateString:String = doc.publishedDate; //CHECK NAME?
					var pubdate:String = df.format(pubdateString);
					for each ( var event:Object in doc.associations )
					{
						var timelineEvent:Object = ObjectUtil.copy(event);
						////////////////////DONT NEED THIS I THINK THE SERVER AGGREGATION DOES THIS AUTOMATICALLY////////////
						//set start date to pubdate if a start date does not exist
						if ( null == timelineEvent.time_start && pubdate != null )
						{
							timelineEvent["time_start"] = pubdate;						
						}
						
						//fix start/end dates by removing hours
						if ( timelineEvent.time_start != null )
							timelineEvent.time_start = df.format(timelineEvent.time_start);
						if ( timelineEvent.time_end != null )
							timelineEvent.time_end = df.format(timelineEvent.time_end);
						////////////////////END THE DONT THINK I NEED SECTION (NOTE MAYBE IT DOESNT UPDATE THE ORIGINAL EVENT OBJECT THOUGH IN THE DOCS? I THINK IT DOES////////////
						
						// Association significance
						var this_doc_sig:Number = doc.aggregateSignif;
						var type:String = timelineEvent.assoc_type as String;
						if (type.charAt(0) == 'S') { // Summary
							if ((timelineEvent.entity1 == null)||(timelineEvent.entity2 == null)) {
								// (A bad summary)
								this_doc_sig *= 0.5;								
							}
							else {
								this_doc_sig *= 0.8;																
							}						
						}
						
						//try to get this event if its in hashmap already
						var eventHashString:String = eventHash(timelineEvent);
						var oldEvent:Object = eventsHashMap.get(eventHashString);
						if ( oldEvent == null )
						{
							//not in hashmap, set doccount to 1 and add
							timelineEvent["doccount"] = 1;
							timelineEvent["assoc_sig"] = this_doc_sig;
							eventsHashMap.put(eventHashString,timelineEvent);
						}
						else
						{
							oldEvent.doccount += 1;
							oldEvent.assoc_sig = Math.sqrt(this_doc_sig*this_doc_sig + oldEvent.assoc_sig*oldEvent.assoc_sig); 
						}
					}
				}
			}
			return eventsHashMap;
		}
		
		private function eventHash(event:Object):String
		{
			var eventString:String = "";
			if ( event.entity1_index != null )
				eventString += event.entity1_index;
			if ( event.verb != null )
				eventString += event.verb;
			if ( event.verb_category != null )
				eventString += event.verb_category;
			if ( event.entity2_index != null )
				eventString += event.entity2_index;
			return Authentication.hashPassword(eventString);
		}		
	}
}