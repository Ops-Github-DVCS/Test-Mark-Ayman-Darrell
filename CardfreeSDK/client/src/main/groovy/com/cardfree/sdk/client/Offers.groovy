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

	def markOffersAsViewed(def oAuthToken, def data) {
		executeMapiUserRequest("put", "offers", data, oAuthToken)
	}

	// todo factor out orderJson from following calls
	def applyOffer(def oAuthToken, def redemptionCode, def orderJson) {
		def data = [redemptionCode: redemptionCode]
		executeMapiUserRequest("post", "orders/${orderJson.storeNumber}-${orderJson.orderId}/offers", data, oAuthToken)
	}

	def removeOffer(def oAuthToken, def redemptionCode, orderJson) {
		def data = [redemptionCode: redemptionCode]
		executeMapiUserRequest("delete", "orders/${orderJson.storeNumber}-${orderJson.orderId}/offers", data, oAuthToken)
	}

	def createEmptyOrderWithOffer(def oAuthToken, String redemptionCode, storeNumber) {
		executeMapiUserRequest("post", "orders", [restaurantId: storeNumber, redemptionCode: redemptionCode], oAuthToken)
	}

}
