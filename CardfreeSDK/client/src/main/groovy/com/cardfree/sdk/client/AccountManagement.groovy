package com.cardfree.sdk.client

import groovy.transform.InheritConstructors

/**
 * Created by gabe on 3/18/15.
 */
class AccountManagement extends OauthClient{

	def provisionNewUser(String userName, String password, Map userData) {
		println("   Provision New User")
		log.debug("Provision New User")
		def data = userData
		data.password = password
		data.userName = userName
		data.email = userName

		return executeMapiUserCreationRequest(data)
	}

	def getUser(String oAuthToken) {
		println("   Get User")
		def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
		executeRestRequest("get", "users/me", getAccountManagementRequestEndpoint(), null, header, "application/json")
	}

	def getUserInformation(String token) {
		println("   Get User Information")
		log.debug("Get User Information")
		return executeMapiUserRequest("get", "/", null, token)
	}

    def deliverOffer(String oAuthToken, String encryptedOfferString) {
        println("   Deliver Offer")
        return executeMapiUserRequest("post", "loyalty-reward-delivery", [promotionId: encryptedOfferString], oAuthToken)
    }

}
