import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.MobileApiService

class TestConfigurationSpec extends FunctionalSpecBase{

    def "get oauth endpoint"(){
        when:
        def oauthEndpoint = mobileApiService.getOAuthEndpoint()

        then:
        !oauthEndpoint.isEmpty()
    }

    def "get oAuth Token"(){
        when:
        def oAuthToken = mobileApiService.getNonRegisteredOauthToken()

        then:
        !oAuthToken.isEmpty()
    }
}
