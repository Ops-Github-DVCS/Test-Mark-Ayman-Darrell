package com.cardfree.sdk.client

/**
 * Created by gabe on 3/19/15.
 */
class Gifting extends OauthClient{
	def getGiftsForUser(String token){
		executeMapiUserRequest("get", "gifting", null, token)
	}
}
