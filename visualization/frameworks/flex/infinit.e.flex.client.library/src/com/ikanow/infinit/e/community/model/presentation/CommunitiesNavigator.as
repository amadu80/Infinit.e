package com.ikanow.infinit.e.community.model.presentation
{
	import com.ikanow.infinit.e.shared.model.constant.NavigationConstants;
	import com.ikanow.infinit.e.shared.model.constant.types.NavigationItemTypes;
	import com.ikanow.infinit.e.shared.model.presentation.base.Navigator;
	import com.ikanow.infinit.e.shared.util.StateUtil;
	import mx.collections.ArrayCollection;
	import mx.core.UIComponent;
	
	/**
	 * Community Navigator
	 */
	public class CommunitiesNavigator extends Navigator
	{
		
		//======================================
		// private static properties 
		//======================================
		
		private static const LIST_ID:String = NavigationConstants.COMMUNITY_LIST_ID;
		
		private static const REQUEST_ID:String = NavigationConstants.COMMUNITY_REQUEST_ID;
		
		
		//======================================
		// public properties 
		//======================================
		
		/**
		 * Presentation Model
		 */
		[Bindable]
		[Inject]
		public var model:CommunitiesModel;
		
		//======================================
		// constructor 
		//======================================
		
		/**
		 * Constructor
		 */
		public function CommunitiesNavigator()
		{
			navigatorId = NavigationConstants.SOURCES_COMMUNITY_ID;
			parentNavigatorId = NavigationConstants.WORKSPACES_SOURCES_ID;
		}
		
		
		//======================================
		// public static methods 
		//======================================
		
		/**
		 * Update View States
		 * Used to override the states of a view with the
		 * full state navigation item ids from the
		 * associated Navigator.
		 * @param component - the component to update states
		 * @param state - the current state to set after the update
		 */
		public static function updateViewStates( component:UIComponent, state:String = "" ):void
		{
			StateUtil.setStates( component, [ LIST_ID, REQUEST_ID ], state );
		}
		
		
		//======================================
		// public methods 
		//======================================
		
		/**
		 * Show Community List View
		 */
		public function showCommunityListView():void
		{
			navigateById( LIST_ID );
		}
		
		/**
		 * Show Community Request View
		 */
		public function showCommunityRequetView():void
		{
			navigateById( REQUEST_ID );
		}
		
		//======================================
		// protected methods 
		//======================================
		
		/**
		 * Create States
		 */
		override protected function createStates():void
		{
			var navStates:ArrayCollection = new ArrayCollection();
			
			// list
			navStates.addItem( createNavigationItem( LIST_ID, NavigationItemTypes.STATE, LIST_ID ) );
			
			// request
			navStates.addItem( createNavigationItem( REQUEST_ID, NavigationItemTypes.STATE, REQUEST_ID ) );
			
			// set states - default list
			setStates( navStates, LIST_ID );
		}
	}
}

