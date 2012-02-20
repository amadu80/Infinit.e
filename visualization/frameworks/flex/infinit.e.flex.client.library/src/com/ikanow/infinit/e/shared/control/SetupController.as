package com.ikanow.infinit.e.shared.control
{
	import com.ikanow.infinit.e.shared.control.base.InfiniteController;
	import com.ikanow.infinit.e.shared.event.SetupEvent;
	import com.ikanow.infinit.e.shared.model.manager.SetupManager;
	import com.ikanow.infinit.e.shared.model.vo.Setup;
	import com.ikanow.infinit.e.shared.model.vo.ui.ServiceResponse;
	import com.ikanow.infinit.e.shared.model.vo.ui.ServiceResult;
	import com.ikanow.infinit.e.shared.service.setup.ISetupServiceDelegate;
	import mx.collections.ArrayCollection;
	import mx.rpc.events.ResultEvent;
	
	/**
	 * Setup Controller
	 */
	public class SetupController extends InfiniteController
	{
		
		//======================================
		// public properties 
		//======================================
		
		[Inject]
		public var setupServiceDelegate:ISetupServiceDelegate;
		
		[Inject]
		public var setupManager:SetupManager;
		
		
		//======================================
		// public methods 
		//======================================
		
		[EventHandler( event = "SetupEvent.GET_MODULES_ALL" )]
		/**
		 * Get Modules All
		 * @param event
		 */
		public function getModulesAll( event:SetupEvent ):void
		{
			executeServiceCall( "SetupController.getModulesAll()", event, setupServiceDelegate.getModulesAll( event ), getModulesAll_resultHandler, defaultFaultHandler );
		}
		
		/**
		 * Get Modules All Result Handler
		 * @param event
		 */
		public function getModulesAll_resultHandler( event:ResultEvent ):void
		{
			setupManager.setWidgets( ServiceResult( event.result ).data as ArrayCollection );
		}
		
		[EventHandler( event = "SetupEvent.GET_MODULES_USER" )]
		/**
		 * Get Modules User
		 * @param event
		 */
		public function getModulesUser( event:SetupEvent ):void
		{
			executeServiceCall( "SetupController.getModulesUser()", event, setupServiceDelegate.getModulesUser( event ), getModulesUser_resultHandler, defaultFaultHandler );
		}
		
		/**
		 * Get Modules User Result Handler
		 * @param event
		 */
		public function getModulesUser_resultHandler( event:ResultEvent ):void
		{
			setupManager.setUserWidgets( ServiceResult( event.result ).data as ArrayCollection );
		}
		
		[EventHandler( event = "SetupEvent.GET_SETUP" )]
		/**
		 * Get Setup
		 * @param event
		 */
		public function getSetup( event:SetupEvent ):void
		{
			executeServiceCall( "SetupController.getSetup()", event, setupServiceDelegate.getSetup( event ), getSetup_resultHandler, defaultFaultHandler );
		}
		
		/**
		 * Get Setup Result Handler
		 * @param event
		 */
		public function getSetup_resultHandler( event:ResultEvent ):void
		{
			setupManager.setSetup( ServiceResult( event.result ).data as Setup );
		}
		
		[EventHandler( event = "SetupEvent.RESET" )]
		/**
		 * Reset Setup
		 * @param event
		 */
		public function resetSetup( event:SetupEvent ):void
		{
			setupManager.reset();
		}
		
		[EventHandler( event = "SetupEvent.SAVE_SETUP" )]
		/**
		 * Save Setup
		 * @param event
		 */
		public function saveSetup( event:SetupEvent ):void
		{
			setupManager.saveSetup();
		}
		
		[EventHandler( event = "SetupEvent.SELECT_MODULE_FAVORITE" )]
		/**
		 * Select Module Favorite
		 * @param event
		 */
		public function selectModuleFavorite( event:SetupEvent ):void
		{
			setupManager.setUserWidget( event.moduleId, event.selected );
		}
		
		[EventHandler( event = "SetupEvent.SET_MODULES_USER" )]
		/**
		 * Set Modules User
		 * @param event
		 */
		public function setModulesUser( event:SetupEvent ):void
		{
			executeServiceCall( "SetupController.setModulesUser()", event, setupServiceDelegate.setModulesUser( event ), setModulesUser_resultHandler, defaultFaultHandler );
		}
		
		/**
		 * Set Modules User Result Handler
		 * @param event
		 */
		public function setModulesUser_resultHandler( event:ResultEvent ):void
		{
			// nothing to do here
		}
		
		[EventHandler( event = "SetupEvent.UPDATE_SETUP" )]
		/**
		 * Update Setup
		 * @param event
		 */
		public function updateSetup( event:SetupEvent ):void
		{
			executeServiceCall( "SetupController.updateSetup()", event, setupServiceDelegate.updateSetup( event ), updateSetup_resultHandler, defaultFaultHandler );
		}
		
		/**
		 * Update Setup Result Handler
		 * @param event
		 */
		public function updateSetup_resultHandler( event:ResultEvent ):void
		{
			var verify:Boolean = ( verifyServiceResponseSuccess( "updateSetup()", event.result as ServiceResult ) );
		}
	}
}
