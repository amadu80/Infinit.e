package com.ikanow.infinit.e.data_model.store.social.community;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;

import com.google.gson.reflect.TypeToken;
import com.ikanow.infinit.e.data_model.store.BaseDbPojo;
import com.ikanow.infinit.e.data_model.store.social.person.*;;

/**
 * Class used to establish the community information in the environment
 * @author cvitter
 */
public class CommunityPojo extends BaseDbPojo
{
	// Standard static function for readability
	@SuppressWarnings("unchecked")
	static public TypeToken<List<CommunityPojo>> listType() { return new TypeToken<List<CommunityPojo>>(){}; }
	
	
	// (NOTE CAN'T CURRENTLY USE THIS AS QUERY POJO BECAUSE IT HAS DEFAULT PARAMS)
	/** 
	  * Private Class Variables
	  */
	private ObjectId _id = null;
	private Date created = null;
	private Date modified = null;
	private String name = null;
	private String description = null;
	private Boolean isSystemCommunity = false;
	private ObjectId parentId = null;
	private String parentName = null;
	private Boolean isPersonalCommunity = false;
	private List<String> tags = null;
	private Map<String, CommunityAttributePojo> communityAttributes = null;
	private Map<String, CommunityUserAttributePojo> userAttributes = null;
	private ObjectId ownerId = null;
	private String communityStatus = "active"; //communityStatusEnum defaults to active
	private String ownerDisplayName = null;
	private int numberOfMembers = 0;
	private Set<CommunityMemberPojo> members = null;

	public enum communityStatusEnum
	{
		ACTIVE {
		    public String toString() {
		        return "active";
		    }
		},
		DISABLED {
		    public String toString() {
		        return "disabled";
		    }
		},
		PENDING {
		    public String toString() {
		        return "pending";
		    }
		}
	}
	
	/** 
	  * Get the id
	  */
	public ObjectId getId() {
		return _id;
	}
	
	/** 
	  * Set the id
	  */
	public void setId(ObjectId _id) {
		this._id = _id;
	}
	
	public void setCommunityStatus(String communityStatus) {
		this.communityStatus = communityStatus;
	}
	
	public String getCommunityStatus() {
		return this.communityStatus;
	}
	
	/** 
	  * Get the created date
	  */
	public Date getCreated() {
		return created;
	}
	
	/** 
	  * Set the created date
	  */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/** 
	  * Get the modified date
	  */
	public Date getModified() {
		return modified;
	}
	
	/** 
	  * Set modified date
	  */
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	/** 
	  * Get the community name
	  */
	public String getName() {
		return name;
	}
	
	/** 
	  * Set the community name
	  */
	public void setName(String name) {
		this.name = name;
	}
	
	/** 
	  * Get the community description
	  */
	public String getDescription() {
		return description;
	}
	
	/** 
	  * Set the community description
	  */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param isSystemCommunity the isSystemCommunity to set
	 */
	public void setIsSystemCommunity(Boolean isSystemCommunity) {
		this.isSystemCommunity = isSystemCommunity;
	}

	/**
	 * @return the isSystemCommunity
	 */
	public Boolean getIsSystemCommunity() {
		return isSystemCommunity;
	}

	/** 
	  * Get the community parent id
	  */
	public ObjectId getParentId() {
		return parentId;
	}
	
	/** 
	  * Set the community parent id
	  */
	public void setParentId(ObjectId parentId) {
		this.parentId = parentId;
	}
	
	/** 
	  * Get the community parent named
	  */
	public String getParentName() {
		return parentName;
	}
	
	/** 
	  * Set the community parent name
	  */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	/** 
	  * Set the is personal community true/false
	  */
	public void setIsPersonalCommunity(Boolean isPersonalCommunity) {
		this.isPersonalCommunity = isPersonalCommunity;
	}

	/** 
	  * Get the is personal community value
	  */
	public Boolean getIsPersonalCommunity() {
		return isPersonalCommunity;
	}

	/** 
	  * Set the list of community tags
	  */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/** 
	  * Get the list of community tags
	  */
	public List<String> getTags() {
		return tags;
	}
		
	/**
	 * @param communityAttributes the communityAttributes to set
	 */
	public void setCommunityAttributes(Map<String, CommunityAttributePojo> communityAttributes) {
		this.communityAttributes = communityAttributes;
	}

	/**
	 * @return the communityAttributes
	 */
	public Map<String, CommunityAttributePojo> getCommunityAttributes() {
		return communityAttributes;
	}

	/**
	 * @param communityUserAttribute the communityUserAttribute to set
	 */
	public void setCommunityUserAttribute(Map<String, CommunityUserAttributePojo> communityUserAttribute) {
		this.userAttributes = communityUserAttribute;
	}

	/**
	 * @return the communityUserAttribute
	 */
	public Map<String, CommunityUserAttributePojo> getCommunityUserAttribute() {
		return userAttributes;
	}

	/** 
	  * Get the community owner id
	  */
	public ObjectId getOwnerId() {
		return ownerId;
	}
	
	/** 
	  * Set the community owner id
	  */
	public void setOwnerId(ObjectId ownerId) {
		this.ownerId = ownerId;
	}
	
	/** 
	  * Get the community owner display name
	  */
	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}
	
	/** 
	  * Set the community owner display name
	  */
	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}

	/**
	 * Set the number of community members
	 */
	public void setNumberOfMembers(int numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}

	/**
	 * Get the number of community members
	 */
	public int getNumberOfMembers() {
		return numberOfMembers;
	}
	
	/*
	 * Set the community members
	 */
	public void setMembers(Set<CommunityMemberPojo> members) {
		this.members = members;
	}
	
	/*
	 * Get the community members
	 */
	public Set<CommunityMemberPojo> getMembers() {
		return members;
	}
	
	/**
	 * Check a user is a member of this community
	 */
	public boolean isMember(ObjectId userID)
	{
		for ( CommunityMemberPojo cmp : members)
			if ( cmp.get_id().equals(userID) )
				return true;
		return false;
	}

	/**
	 * Check a user is the owner of this community
	 * 
	 * @param objectId
	 * @return
	 */
	public boolean isOwner(ObjectId userID) 
	{
		if ( isPersonalCommunity )
			return true; //personal group you are always owner
		else
		{
			for ( CommunityMemberPojo cmp : members)
			{
				if ( cmp.get_id().equals(userID) )
				{
					if ( cmp.getUserType().equals("owner") )
						return true;
					else
						return false;
				}
			}
			return false; //not a member
		}
	}
	


	/**
	 * addMember
	 * Attempts to add a user to community
	 * @param pp
	 * @throws IOException 
	 * @throws MongoException 
	 * @throws UnknownHostException 
	 */
	public void addMember(PersonPojo pp) throws UnknownHostException, MongoException, IOException 
	{
		addMember(pp, false);		
	}
	
	
	/**
	 * addMember
	 * Attempts to add a user to community
	 * @param pp
	 * @param isInvite
	 * @throws IOException 
	 * @throws MongoException 
	 * @throws UnknownHostException 
	 */
	public void addMember(PersonPojo pp, boolean isInvite) throws UnknownHostException, MongoException, IOException 
	{
		// If the person is not already a member of the community
		if ( !isMember(pp.get_id()) )
		{
			// Create the new member object
			CommunityMemberPojo cmp = new CommunityMemberPojo();
			cmp.set_id(pp.get_id());
			cmp.setEmail(pp.getEmail());
			cmp.setDisplayName(pp.getDisplayName());
			cmp.setUserType("member");
			if (isInvite)
			{
				cmp.setUserStatus("pending");
			}
			else
			{
				cmp.setUserStatus("active");
			}

			// Set the userAttributes based on default
			Set<CommunityMemberUserAttributePojo> cmua = new HashSet<CommunityMemberUserAttributePojo>();
			
			Map<String, CommunityUserAttributePojo> cua = this.getCommunityUserAttribute();

			Iterator<Map.Entry<String, CommunityUserAttributePojo>> it = cua.entrySet().iterator();
			while (it.hasNext())
			{
				CommunityMemberUserAttributePojo c = new CommunityMemberUserAttributePojo();
				Map.Entry<String, CommunityUserAttributePojo> pair = it.next();
				c.setType(pair.getKey().toString());
				CommunityUserAttributePojo v = (CommunityUserAttributePojo)pair.getValue();
				c.setValue(v.getDefaultValue());
				cmua.add(c);
			}
			cmp.setUserAttributes(cmua);

			// Get Person data to added to member record

			if (pp.getContacts() != null)
			{
				// Set contacts from person record
				Set<CommunityMemberContactPojo> contacts = new HashSet<CommunityMemberContactPojo>();
				Map<String, PersonContactPojo> pcp = pp.getContacts();
				Iterator<Map.Entry<String, PersonContactPojo>> it2 = pcp.entrySet().iterator();
				while (it.hasNext())
				{
					CommunityMemberContactPojo c = new CommunityMemberContactPojo();
					Map.Entry<String, PersonContactPojo> pair = it2.next();
					c.setType(pair.getKey().toString());
					PersonContactPojo v = (PersonContactPojo)pair.getValue();
					c.setValue(v.getValue());
					contacts.add(c);
				}
				cmp.setContacts(contacts);
			}

			// Set links from person record
			if (pp.getLinks() != null)
			{
				// Set contacts from person record
				Set<CommunityMemberLinkPojo> links = new HashSet<CommunityMemberLinkPojo>();
				Map<String, PersonLinkPojo> plp = pp.getLinks();
				Iterator<Map.Entry<String, PersonLinkPojo>> it3 = plp.entrySet().iterator();
				while (it.hasNext())
				{
					CommunityMemberLinkPojo c = new CommunityMemberLinkPojo();
					Map.Entry<String, PersonLinkPojo> pair = it3.next();
					c.setTitle(pair.getKey().toString());
					PersonLinkPojo v = (PersonLinkPojo)pair.getValue();
					c.setUrl(v.getUrl());
					links.add(c);
				}
				cmp.setLinks(links);
			}

			members.add(cmp);				
		}		
	}
	
	

	/**
	 * removeMember
	 * Attempts to remove a user from this community
	 * @param personId
	 */
	public void removeMember(ObjectId personId) 
	{
		for ( CommunityMemberPojo cmp : members)
		{
			if ( cmp.get_id().equals(personId) )
			{
				//found member, remove
				members.remove(cmp);
				numberOfMembers--;
				break;
			}
		}		
	}
	
	
	/**
	 * getOwner
	 * @return
	 */
	public CommunityMemberPojo getOwner()
	{
		for ( CommunityMemberPojo cmp : members)
		{
			if ( cmp.get_id().equals(ownerId) )
			{
				//found owner, return
				return cmp;
			}
		}	
		return null; //hopefully we find an owner
	}
	
	
	/**
	 * updateMemberStatus
	 * @param personId
	 * @param userStatus
	 * @return
	 */
	public boolean updateMemberStatus(String personId, String userStatus)
	{
		ObjectId userID = new ObjectId(personId);
		//verified user, update status
		for ( CommunityMemberPojo cmp : members)
		{							
			if ( cmp.get_id().equals(userID) )
			{
				cmp.setUserStatus(userStatus);
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * createCommunityApprove
	 * @param personId
	 * @param communityId
	 * @param requestType
	 * @param requesterId
	 * @return
	 */
	public CommunityApprovePojo createCommunityApprove(String personId, String communityId,
			String requestType, String requesterId) 
	{
		CommunityApprovePojo cap = new CommunityApprovePojo();
		cap.set_id(new ObjectId());
		cap.setCommunityId(communityId);
		cap.setIssueDate(new Date());
		cap.setPersonId(personId);
		cap.setRequesterId(requesterId);
		cap.setType(requestType);		
		return cap;
	}
}
