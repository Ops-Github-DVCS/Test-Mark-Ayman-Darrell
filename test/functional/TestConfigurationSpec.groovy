import functional.test.suite.MobileApiService
import grails.util.Environment
import spock.lang.Specification

class TestConfigurationSpec extends Specification {

    def mobileApiService = new MobileApiService()

    def "spock configured"(){
        setup:
        def mathResult = 0

        when:
        mathResult = 1+1

        then:
        mathResult == 2
    }

    def "read environment variables"(){
        when:
        def env = System.getenv()

        then:
        env.size() > 0
        env.merchant?.size() > 1
    }

    def "read test merchant"(){
        when:
        String testMerchant = mobileApiService.getTestMerchant()

        then:
        !testMerchant?.isEmpty()
    }

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
