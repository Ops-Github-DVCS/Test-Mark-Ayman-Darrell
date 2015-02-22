package functional.test.suite

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
}
