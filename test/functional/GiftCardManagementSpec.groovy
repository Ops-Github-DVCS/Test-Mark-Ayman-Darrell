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

    @Ignore
    def "Add physical Visa Gift Card"(){
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

    def "Transfer Balance from First Data Gift Card to First Data Gift Card"(){
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

        //Add FD GC 1 to user using a new Visa CC
        when:
        def addGCResult1 = giftCardService.provisionGiftCardWithNewCC(5.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult1)

        //Add FD GC 2 to user using a new Visa CC
        when:
        def addGCResult2 = giftCardService.provisionGiftCardWithNewCC(10.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult2)

        //Transfer balance from GC 1 to GC 2
        when:
        def transferResult = giftCardService.transferGiftCardBalance(userToken, addGCResult1.json.cardId, addGCResult2.json.cardId)

        then:
        transferResult != null
    }

}
