import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.GiftingService
import spock.lang.Ignore

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
        GiftingService.validateEGiftSendResult(sentGiftResult)
    }

    @Ignore
    def "retrieve sent eGifts for user"() {
        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken("sumankr3999@gmail.com", "Cts-123456")

        then:
        !userToken.isEmpty()

        //Get all gifts
        when:
        def getAllGiftsResult = giftCardService.getAllGiftingGiftCardsForUser(userToken)

        then:
        getAllGiftsResult?.json.sentGiftCards?.size() > 1

    }
}
