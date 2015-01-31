import functional.test.suite.MobileApiService
import spock.lang.Specification

class HelloSpec extends Specification {

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
        env.endpoint?.size() > 1
        env.merchant?.size() > 1
    }

    def "read test merchant"(){
        when:
        String testMerchant = mobileApiService.getTestMerchant()

        then:
        !testMerchant?.isEmpty()
    }

    def "read test endpoint"(){
        when:
        String testEndpoint = mobileApiService.getTestEndpoint()

        then:
        !testEndpoint?.isEmpty()
    }
}
