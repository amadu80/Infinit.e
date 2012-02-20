package com.ikanow.infinit.e.api.social.community;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import com.ikanow.infinit.e.api.utils.RESTTools;
import com.ikanow.infinit.e.data_model.api.ResponsePojo;
import com.ikanow.infinit.e.data_model.api.ResponsePojo.ResponseObject;

/**
 * @author cvitter
 */
public class CommunityInterface extends Resource 
{
	private CommunityHandler community = new CommunityHandler();
	
	// 
	private String communityId = null;
	private String personId = null;
	private String userType = null;
	private String userStatus = null;
	private String name = null;
	private String description = null;
	private String parentId = null;
	private String parentName = null;
	private String ownerId = null;
	private String ownerDisplayName = null;
	private String ownerEmail = null;
	private String tags = null;
	
	private String action = "";
	
	private String cookieLookup = null;
	private String cookie = null;
	private String requestId = null;
	private String resp = null;
	private String urlStr = null;
	private String json = null;
	
	public CommunityInterface(Context context, Request request, Response response) throws UnsupportedEncodingException {		 
		super(context, request, response);
		 
		Map<String,Object> attributes = request.getAttributes();
		
		cookie = request.getCookies().getFirstValue("infinitecookie", true);	
		urlStr = request.getResourceRef().toString();
		
		// Method.POST
		if (request.getMethod() == Method.POST) 
		{
			communityId = RESTTools.decodeRESTParam("communityid", attributes);
		}
		
		// Method.GET
		if (request.getMethod() == Method.GET) 
		{
			if (urlStr.contains("/community/get/") )
			{
				if (RESTTools.decodeRESTParam("communityid", attributes) != null) 
					communityId = RESTTools.decodeRESTParam("communityid", attributes);
				action = "getCommunity";
			}
			
			else if (urlStr.contains("/community/getsystem") )
			{
				action = "getSystemCommunity";
			}
			
			else if (urlStr.contains("/community/getall") )
			{
				action = "getAllCommunities";
			}
			
			else if (urlStr.contains("/community/getpublic") )
			{
				action = "getPublicCommunities";
			}
			
			else if (urlStr.contains("/community/getprivate") )
			{
				action = "getPrivateCommunities";
			}
			
			else if (urlStr.contains("/community/add/") )
			{
				name = RESTTools.decodeRESTParam("name", attributes);
				description = RESTTools.decodeRESTParam("description", attributes);
				if (RESTTools.decodeRESTParam("tags", attributes) != null) tags = RESTTools.decodeRESTParam("tags", attributes);
				if (RESTTools.decodeRESTParam("parentid", attributes) != null) parentId = RESTTools.decodeRESTParam("parentid", attributes);
				action = "addCommunity";
			}
			
			else if (urlStr.contains("/community/addwithid/") )
			{
				communityId = RESTTools.decodeRESTParam("id", attributes);
				name = RESTTools.decodeRESTParam("name", attributes);
				description = RESTTools.decodeRESTParam("description", attributes);
				parentId = RESTTools.decodeRESTParam("parentid", attributes);
				parentName = RESTTools.decodeRESTParam("parentname", attributes);
				tags = RESTTools.decodeRESTParam("tags", attributes);
				ownerId = RESTTools.decodeRESTParam("ownerid", attributes);
				ownerDisplayName = RESTTools.decodeRESTParam("ownerdisplayname", attributes);
				ownerEmail = RESTTools.decodeRESTParam("owneremail", attributes);
				action = "addCommunityWithId";
			}
			
			else if ( urlStr.contains("/community/remove/"))
			{
				communityId = RESTTools.decodeRESTParam("id", attributes);
				action = "removeCommunityById";
			}
			
			else if (urlStr.contains("/community/member/update/status") )
			{

				communityId = RESTTools.decodeRESTParam("communityid", attributes);
				personId = RESTTools.decodeRESTParam("personid", attributes);
				userStatus = RESTTools.decodeRESTParam("userstatus", attributes);
				action = "updateMemberStatus";
			}
			
			else if (urlStr.contains("/community/member/update/type") )
			{
				communityId = RESTTools.decodeRESTParam("communityid", attributes);
				personId = RESTTools.decodeRESTParam("personid", attributes);
				userType = RESTTools.decodeRESTParam("usertype", attributes);
				action = "updateMemberType";
			}
			
			else if ( urlStr.contains("/community/update/"))
			{
				communityId = RESTTools.decodeRESTParam("communityid", attributes);
				// Use URLDecoder on the json string
				try 
				{
					json = URLDecoder.decode(json, "UTF-8");
				}
				catch (UnsupportedEncodingException e) 
				{
					throw e;
				}
				action = "updateCommunity";
			}
			
			else if ( urlStr.contains("/community/member/join/"))
			{
				communityId = RESTTools.decodeRESTParam("communityid", attributes);
				//personId = RESTTools.decodeRESTParam("personid", attributes);
				action = "joinCommunity";
			}
			
			else if (urlStr.contains("/community/member/leave/"))
			{
				communityId = RESTTools.decodeRESTParam("communityid", attributes);
				//personId = RESTTools.decodeRESTParam("personid", attributes);
				action = "leaveCommunity";
			}
			
			else if (urlStr.contains("/community/member/invite/"))
			{
				communityId = RESTTools.decodeRESTParam("communityid", attributes);
				personId = RESTTools.decodeRESTParam("personid", attributes);
				action = "inviteCommunity";
			}
			
			else if ( urlStr.contains("/community/requestresponse/"))
			{
				requestId = RESTTools.decodeRESTParam("requestid", attributes);
				resp = RESTTools.decodeRESTParam("response", attributes);
				action = "requestresponse";
			}
			
		}
		 
		// All modifications of this resource
		this.setModifiable(true);
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	
	
	
	/**
	 * Handles a POST
	 * acceptRepresentation
	 * @param entity
	 * @return
	 * @throws ResourceException
	 */
	public void acceptRepresentation(Representation entity) throws ResourceException 
	{
		if (Method.POST == getRequest().getMethod()) 
		{
			try 
			{
				json = entity.getText();
				if ( urlStr.contains("/community/update/") )
				{
					action = "updateCommunity";
				}	
			}
			catch (Exception e) 
			{
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		Representation response = represent(null);
		this.getResponse().setEntity(response);
	}

	
	
	/**
	 * Represent the community object in the requested format.	 * 
	 * @param variant
	 * @return
	 * @throws ResourceException
	 */
	public Representation represent(Variant variant) throws ResourceException 
	{
		 ResponsePojo rp = new ResponsePojo();
		 Date startTime = new Date();  
		 
		 cookieLookup = RESTTools.cookieLookup(cookie);
		 
		 //these functions do not require cookies
		 if (action.equals("requestresponse")) 
		 {
			 rp = this.community.requestResponse(requestId, resp);
		 }
		 else if ( cookieLookup == null ) //no cookie found
		 {
			 rp = new ResponsePojo();
			 rp.setResponse(new ResponseObject("Cookie Lookup",false,"Cookie session expired or never existed, please login first"));			 
		 }
		 else //requires cookies
		 {
			 if (action.equals("getCommunity"))
			 {
				 if ( RESTTools.validateCommunityIds(cookieLookup, communityId) )
				 {
					 rp = this.community.getCommunity(communityId);
				 }
				 else
				 {
					 rp = new ResponsePojo();
					 rp.setResponse(new ResponseObject("Verifying Communities",false,"Community Ids are not valid for this user"));
				 }
			 }
			 else if (action.equals("getSystemCommunity"))
			 {
				 rp = this.community.getSystemCommunity();
			 }
			 else if (action.equals("getAllCommunities"))
			 {
				 rp = this.community.getCommunities(cookieLookup);
			 }
			 else if (action.equals("getPublicCommunities"))
			 {
				 rp = this.community.getCommunities(cookieLookup, true);
			 }
			 else if (action.equals("getPrivateCommunities")) 
			 {
				 rp = this.community.getCommunities(cookieLookup, false);
			 }
			 else if (action.equals("updateMemberStatus"))
			 {
				 rp = this.community.updateMemberStatus(cookieLookup, personId, communityId, userStatus);
			 }
			 else if (action.equals("updateMemberType"))
			 {
				 rp = this.community.updateMemberType(cookieLookup, personId, communityId, userType);
			 }
			 else if (action.equals("addCommunity"))
			 {
				 rp = this.community.addCommunity(cookieLookup, name, description, parentId, tags);
			 }
			 else if (action.equals("addCommunityWithId"))
			 {
				 rp = this.community.addCommunity(cookieLookup, communityId, name, description, parentId, 
						 parentName, tags, ownerId, ownerDisplayName, ownerEmail);
			 }
			 else if ( action.equals("removeCommunityById"))
			 {
				 rp = this.community.removeCommunity(cookieLookup, communityId);
			 }
			 else if ( action.equals("updateCommunity"))
			 {
				 rp = this.community.updateCommunity(cookieLookup, communityId, json);
			 }
			 else if (action.equals("joinCommunity"))
			 {
				 rp = this.community.joinCommunity(cookieLookup, communityId);
			 }
			 else if ( action.equals("leaveCommunity"))
			 {
				 rp = this.community.leaveCommunity(cookieLookup, communityId);
			 }
			 else if ( action.equals("inviteCommunity"))
			 {
				 rp = this.community.inviteCommunity(cookieLookup, personId, communityId);
			 }
		 }
		 
		 Date endTime = new Date();
		 rp.getResponse().setTime(endTime.getTime() - startTime.getTime());
		 return new StringRepresentation(rp.toApi(), MediaType.APPLICATION_JSON);
	}
}
