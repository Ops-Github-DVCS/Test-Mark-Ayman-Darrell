import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import spock.lang.Ignore

class GiftCardManagementSpec extends FunctionalSpecBase{

    @Ignore
    def "Provision gift card with new credit card"(){
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

        //Add GC to user using a new Visa CC
        when:
        def addGCResult = giftCardService.provisionGiftCardWithNewCC(5.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult)
    }

    def "Add physical Gift Cards"(){
        //Create New User
        when:
        def userResult = accountManagementService.provisionNewRandomUser()

        then:
        AccountManagementService.validateNewUser(userResult)

        //Login User
        when:
        String userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

        then:
        !userToken.isEmpty()

        //Add Physical Visa Gift Card to Account
        when:
        def addVisaGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberVisa, config.giftCardInformation.physicalCardPinVisa)

        then:
        GiftCardService.validateNewGiftCardResult(addVisaGCResult)
    }

}
