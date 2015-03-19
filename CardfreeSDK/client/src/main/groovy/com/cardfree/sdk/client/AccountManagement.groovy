package com.cardfree.sdk.client

import groovy.transform.InheritConstructors

/**
 * Created by gabe on 3/18/15.
 */
class AccountManagement extends OauthClient{

	def provisionNewUser(String userName, String password, Map userData) {
		log.debug("Provision New User")
		def data = userData
		data.password = password
		data.userName = userName
		data.email = userName

		return executeMapiUserCreationRequest(data)
	}

	def getUser(String oAuthToken) {
		def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
		executeRestRequest("get", "users/me", getAccountManagementRequestEndpoint(), null, header, "application/json")
	}

	def getUserInformation(String token) {
		log.debug("Get User Information")
		return executeMapiUserRequest("get", "/", null, token)
	}
}