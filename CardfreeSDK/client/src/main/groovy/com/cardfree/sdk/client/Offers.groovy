package com.cardfree.sdk.client

/**
 * Created by gabe on 3/18/15.
 */
class Offers extends OauthClient{
	Map getOffers(String oAuthToken, String localTime = new Date().format("yyyy-MM-ddTHH:mm:ss")) {
		executeMapiUserRequest("get", "offers?localTime=$localTime", null, oAuthToken)
	}

	Map getLoyaltyRewards(String oAuthToken, String localTime = new Date().format("yyyy-MM-ddTHH:mm:ss")) {
		// userloyalty/{loyaltyProgramId}/loyaltyreward
		executeMapiUserRequest("get", "userloyalty/monoLoyaltyProgram1/loyaltyReward?localTime=$localTime", null, oAuthToken)

	}
}
