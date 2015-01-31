import functional.test.suite.MobileApiService
import spock.lang.Specification

class TestConfigurationSpec extends Specification {

    def mobileApiService = new MobileApiService()

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
