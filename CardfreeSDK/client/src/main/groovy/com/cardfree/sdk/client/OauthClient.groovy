package com.cardfree.sdk.client

/**
 * Created by gabe on 3/18/15.
 */
class OauthClient extends ConfiguredApiClient{

	def executeMapiUserRequest(String operation, String path, Map data, String token, boolean guest = false, String deviceIdentifier = null){
		if(!token){
			token = getNonRegisteredOauthToken()
		}
		def header = [Authorization: "bearer ${token}", Accept: "application/json"]
		if(guest && deviceIdentifier){
			header = [Authorization: "bearer ${token}", Accept: "application/json", "Device-Identifier": deviceIdentifier]
		}
		if(guest){
			executeRestRequest(operation, "" + path, getOrderManagementRequestEndpoint(), data, header, "application/json")
		} else {
			executeRestRequest(operation, "users/me/" + path, getAccountManagementRequestEndpoint(), data, header, "application/json")
		}
	}

	def executeMapiUserCreationRequest(Map userData){
		def header = [Authorization: "bearer ${getNonRegisteredOauthToken()}", Accept: "application/json"]
		executeRestRequest("post", "users", getAccountManagementRequestEndpoint(), userData, header, "application/vnd.cardfree.users+json; account-creation-type=cardfree")
	}

	String getNonRegisteredOauthToken() {
		log.debug("Get unregistered oAuth Token")
		def data = [
				grantType: "client_credentials",
				client   : [
						id: getOAuthId(),
						secret: getOAuthSecret()
				]
		]
		def oAuthResult = executeRestRequest("post", "oauth", getOAuthEndpoint(), data)
		if(!oAuthResult?.text?.isEmpty()){
			return oAuthResult.text
		}
		throw Exception("Could not provision non registered oAuth Token with id: ${getOAuthId()} and secret: ${getOAuthSecret()} for endpoint ${getOAuthEndpoint()}/oauth")
	}

	String getRegisteredUserToken(String userName, String password){
		log("Get registered user oAuth Token")
		def unregisteredToken = getNonRegisteredOauthToken()
		def data = [
				grantType: "password_grant",
				client   : [
						accessToken: unregisteredToken
				],
				user     : [
						userName: userName,
						password: password
				]
		]
		def oAuthResult = executeRestRequest("post", "oauth", getOAuthEndpoint(), data)
		if(!oAuthResult?.json?.accessToken?.isEmpty()){
			return oAuthResult.json.accessToken
		} else {
			throw new Exception("Could not generate oAuth token for a user with these credentials.")
		}
	}

	String getOAuthEndpoint(){
		getConfigurationPath("oauth", "url")
	}

	String getOAuthId(){
		getConfigurationPath("oauth", "id")
	}

	String getOAuthSecret(){
		getConfigurationPath("oauth", "secret")
	}

}
