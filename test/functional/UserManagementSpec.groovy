import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import spock.lang.Ignore

class UserManagementSpec extends FunctionalSpecBase{

    @Ignore
    def "random user creation"() {
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)

        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

        then:
        !userToken.isEmpty()

        when:
        def getUserResult = accountManagementService.getUserInformation(userToken)

        then:
        getUserResult != null
        getUserResult.json.data.zip
    }

    @Ignore
    def "Registration trigger awards loyalty points only once"() {
        when:
            def userResult = accountManagementService.provisionNewRandomUser()
            def loyaltyPurses = accountManagementService.executeSVCRequest("get",
                    "/centralAccounts/${userResult.json.masterAccountId}/loyaltyPurses/", null)
        then:
            AccountManagementService.validateNewUser(userResult)
            loyaltyPurses.json.loyaltyPurse.lifetimePoints == 1
    }

    def "Create new user and update the loyalty id"() {
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)

        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

        then:
        !userToken.isEmpty()

        when:
        def getUserResult = accountManagementService.getUserInformation(userToken)

        then:
        getUserResult != null
        getUserResult.json.loyaltyId

        when:
        def loyaltyId = UUID.randomUUID().toString()
        def loyaltyOptedIn = null;
        def updateLoyaltyIdResult = accountManagementService.updateUserLoyaltyId(userToken, loyaltyId, loyaltyOptedIn)

        then:
        updateLoyaltyIdResult != null

        when:
        def getUpdatedUserResult = accountManagementService.getUserInformation(userToken)

        then:
        getUpdatedUserResult != null
        getUpdatedUserResult.json.loyaltyId == loyaltyId
        getUpdatedUserResult.json.loyaltyOptedIn != loyaltyOptedIn
    }

    @Ignore
    def "Create new user and update the loyalty opted in flag"() {
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)

        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

        then:
        !userToken.isEmpty()

        when:
        def getUserResult = accountManagementService.getUserInformation(userToken)

        then:
        getUserResult != null
        getUserResult.json.loyaltyId

        when:
        def loyaltyId = null
        def loyaltyOptedIn = Boolean.FALSE;
        def updateLoyaltyIdResult = accountManagementService.updateUserLoyaltyId(userToken, loyaltyId, loyaltyOptedIn)

        then:
        updateLoyaltyIdResult != null

        when:
        def getUpdatedUserResult = accountManagementService.getUserInformation(userToken)

        then:
        getUpdatedUserResult != null
        getUpdatedUserResult.json.loyaltyId != loyaltyId
        getUpdatedUserResult.json.loyaltyOptedIn == loyaltyOptedIn
    }
}
