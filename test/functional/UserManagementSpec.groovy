import functional.test.suite.AccountManagementService
import spock.lang.Specification

class UserManagementSpec extends Specification{

    def accountManagementService = new AccountManagementService()
    def userResult

    def "random user creation"(){
        when:
        userResult = accountManagementService.provisionNewRandomUser()

        then:
        passesUserChecks(userResult)
    }

    def passesUserChecks(def userResult){
        assert userResult?.status?.statusCode == 201
        assert !userResult?.json?.email?.contains("@")
        assert !userResult?.json?.name?.isEmpty()
        return true
    }

}
