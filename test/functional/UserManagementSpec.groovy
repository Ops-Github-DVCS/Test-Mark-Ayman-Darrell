import functional.test.suite.AccountManagementService
import spock.lang.Specification

class UserManagementSpec extends Specification{

    def accountManagementService = new AccountManagementService()

    def "random user creation"(){
        when:
        def userresult = accountManagementService.provisionNewRandomUser()

        then:
        userresult?.status?.statusCode == 201
        userresult?.json?.email?.contains("@")
        !userresult?.json?.name?.isEmpty()
    }
}
