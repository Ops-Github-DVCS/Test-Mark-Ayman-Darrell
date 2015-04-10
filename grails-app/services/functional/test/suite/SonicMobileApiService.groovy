package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper

class SonicMobileApiService extends MobileApiService{

    def getValidatedUserId(){
        getConfigurationPath("verified_user", "id")
    }

    def getValidatedUserName(){
        getConfigurationPath("verified_user", "userName")
    }

    def getValidatedUserEmail(){
        getConfigurationPath("verified_user", "email")
    }

    def getValidatedUserPassword(){
        getConfigurationPath("verified_user", "password")
    }

    def getSonicOAuthToken(){
        getConfigurationPath("oauth", "token")
    }

    def getNonRegisteredOauthToken() {
        getSonicOAuthToken()
    }

    def getRegisteredUserToken(userName, password){
        TestOutputHelper.printServiceCall("Get Sonic registered user oAuth Token")
        def data = [
                grant_type: "password_grant",
                username: userName,
                password: password
        ]
        def header = [Authorization: "bearer ${getNonRegisteredOauthToken()}"]
        def oAuthResult = executeMapiRestRequest("post", "oauth/token", getOAuthEndpoint(), data, header)
        if(oAuthResult?.json?.accessToken?.isEmpty()){
            throw new Exception("Could not generate oAuth token for a user with these credentials.")
        }
        oAuthResult.json.access_token
    }

    def executeMapiUserRequest(def operation, def path, def data, def token, def guest = false, def deviceIdentifier = null){
        if(!token){
            token = getNonRegisteredOauthToken()
        }
        def header = [Authorization: "bearer ${token}", Accept: "application/json"]
        if(guest && deviceIdentifier){
            header = [Authorization: "bearer ${token}", Accept: "application/json", "Device-Identifier": deviceIdentifier]
        }
        if(guest){
            executeMapiRestRequest(operation, "" + path, getOrderManagementRequestEndpoint(), data, header, "application/json")
        } else {
            executeMapiRestRequest(operation, "users/" + path, getAccountManagementRequestEndpoint(), data, header, "application/json")
        }
    }
}
