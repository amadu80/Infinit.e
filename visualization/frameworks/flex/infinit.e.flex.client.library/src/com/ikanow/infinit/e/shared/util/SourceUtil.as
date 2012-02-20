package com.ikanow.infinit.e.shared.util
{
	import com.ikanow.infinit.e.shared.model.constant.Constants;
	import com.ikanow.infinit.e.shared.model.vo.Source;
	import mx.utils.ObjectUtil;
	import spark.components.gridClasses.GridColumn;
	
	public class SourceUtil
	{
		
		//======================================
		// constructor 
		//======================================
		
		/**
		 *
		 * Constructor
		 *
		 */
		public function SourceUtil()
		{
		
		}
		
		
		//======================================
		// public static methods 
		//======================================
		
		/**
		 * Sort compare function for the community column
		 * @param itemA
		 * @param itemB
		 * @param column
		 * @return
		 */
		public static function sortCompareCommunity( itemA:Source, itemB:Source, column:GridColumn ):int
		{
			if ( !( itemA && itemB ) )
				return 0;
			
			if ( itemA.title && !itemB.title )
				return -1;
			
			if ( itemB.title && !itemA.title )
				return 1;
			
			var itemAString:String = itemA.community + Constants.SPACE + itemA.title + Constants.SPACE + itemA.tagsString + Constants.SPACE + itemA.mediaType;
			var itemBString:String = itemB.community + Constants.SPACE + itemB.title + Constants.SPACE + itemB.tagsString + Constants.SPACE + itemB.mediaType;
			
			return ObjectUtil.stringCompare( itemAString, itemBString, true );
		}
		
		/**
		 * Sort compare function for the media type column
		 * @param itemA
		 * @param itemB
		 * @param column
		 * @return
		 */
		public static function sortCompareMediaType( itemA:Source, itemB:Source, column:GridColumn ):int
		{
			if ( !( itemA && itemB ) )
				return 0;
			
			if ( itemA.title && !itemB.title )
				return -1;
			
			if ( itemB.title && !itemA.title )
				return 1;
			
			var itemAString:String = itemA.mediaType + Constants.SPACE + itemA.title + Constants.SPACE + itemA.tagsString + Constants.SPACE + itemA.community;
			var itemBString:String = itemB.mediaType + Constants.SPACE + itemB.title + Constants.SPACE + itemB.tagsString + Constants.SPACE + itemB.community;
			
			return ObjectUtil.stringCompare( itemAString, itemBString, true );
		}
		
		/**
		 * Sort compare function for the tags column
		 * @param itemA
		 * @param itemB
		 * @param column
		 * @return
		 */
		public static function sortCompareTags( itemA:Source, itemB:Source, column:GridColumn ):int
		{
			if ( !( itemA && itemB ) )
				return 0;
			
			if ( itemA.title && !itemB.title )
				return -1;
			
			if ( itemB.title && !itemA.title )
				return 1;
			
			var itemAString:String = itemA.tagsString + Constants.SPACE + itemA.title + Constants.SPACE + itemA.mediaType + Constants.SPACE + itemA.community;
			var itemBString:String = itemB.tagsString + Constants.SPACE + itemB.title + Constants.SPACE + itemB.mediaType + Constants.SPACE + itemB.community;
			
			return ObjectUtil.stringCompare( itemAString, itemBString, true );
		}
		
		/**
		 * Sort compare function for the title column
		 * @param itemA
		 * @param itemB
		 * @param column
		 * @return
		 */
		public static function sortCompareTitle( itemA:Source, itemB:Source, column:GridColumn ):int
		{
			if ( !( itemA && itemB ) )
				return 0;
			
			if ( itemA.title && !itemB.title )
				return -1;
			
			if ( itemB.title && !itemA.title )
				return 1;
			
			var itemAString:String = itemA.title + Constants.SPACE + itemA.tagsString + Constants.SPACE + itemA.mediaType + Constants.SPACE + itemA.community;
			var itemBString:String = itemB.title + Constants.SPACE + itemB.tagsString + Constants.SPACE + itemB.mediaType + Constants.SPACE + itemB.community;
			
			return ObjectUtil.stringCompare( itemAString, itemBString, true );
		}
	}
}
