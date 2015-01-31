import functional.test.suite.AccountManagementService
import spock.lang.Specification

class UserManagementSpec extends Specification{

    def accountManagementService = new AccountManagementService()

    def "random user creation"() {
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)
    }

}
