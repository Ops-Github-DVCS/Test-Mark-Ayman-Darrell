import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import spock.lang.IgnoreRest

class UserManagementSpec extends FunctionalSpecBase{

    def "random user creation"() {
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
		AccountManagementService.validateNewUser(userResult)

		when: "Retrieve user"
		def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
		def fetchUser = accountManagementService.getUser(userToken)

		then:
		fetchUser.json.data.zip
    }

    def "Registration trigger awards loyalty points only once"() {
        when:
            def userResult = accountManagementService.provisionNewRandomUser()
            def loyaltyPurses = accountManagementService.executeSVCRequest("get",
                    "/centralAccounts/${userResult.json.masterAccountId}/loyaltyPurses/", null)
        then:
            AccountManagementService.validateNewUser(userResult)
            loyaltyPurses.json.loyaltyPurse.lifetimePoints == 1
    }


}
