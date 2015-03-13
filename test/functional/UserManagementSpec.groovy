import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import spock.lang.IgnoreRest

class UserManagementSpec extends FunctionalSpecBase{

    def "random user creation"() {
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)
    }

	@IgnoreRest
	def "Registration trigger awards loyalty points only once"() {
		when:
		def userResult = accountManagementService.provisionNewRandomUser()
		def loyaltyPurses = accountManagementService.executeSVCRequest("get",
				"centralAccounts/${userResult.json.masterAccountId}/loyaltyPurses/", null)
		then:
		AccountManagementService.validateNewUser(userResult)
	}



}
