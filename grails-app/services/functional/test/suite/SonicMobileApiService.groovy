package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper
import com.cardfree.sdk.client.OauthClient

class SonicMobileApiService extends OauthClient{

    String getValidatedUserId(){
        getConfigurationPath("verified_user", "id")
    }

    String getValidatedUserName(){
        getConfigurationPath("verified_user", "userName")
    }

    String getValidatedUserEmail(){
        getConfigurationPath("verified_user", "email")
    }

    String getValidatedUserPassword(){
        getConfigurationPath("verified_user", "password")
    }

    String getSonicOAuthToken(){
        getConfigurationPath("oauth", "token")
    }

    String getNonRegisteredOauthToken() {
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
        if(!oAuthResult?.json?.accessToken?.isEmpty()){
            return oAuthResult.json.accessToken
        } else {
            throw new Exception("Could not generate oAuth token for a user with these credentials.")
        }
        oAuthResult
    }
}
