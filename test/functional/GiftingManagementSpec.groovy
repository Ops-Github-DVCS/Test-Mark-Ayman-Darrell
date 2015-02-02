import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService

class GiftingManagementSpec extends FunctionalSpecBase{
    def "send egift to email user"() {
        //Create New User
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)

        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

        then:
        !userToken.isEmpty()

        //Send Gift
        when:
        def checkoutDetails = creditCardService.getVisaCheckoutDetails()
        def sentGiftResult = giftingService.sendEGift(userToken, checkoutDetails, "jim@jimberry.net")

        then:
        sentGiftResult != null
    }
}
