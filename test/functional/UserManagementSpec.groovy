import functional.test.suite.AccountManagementService
import spock.lang.Specification

class UserManagementSpec extends Specification{

    def accountManagementService = new AccountManagementService()
    def userResult

    def "random user creation"() {
        when:
        userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)
    }

}
