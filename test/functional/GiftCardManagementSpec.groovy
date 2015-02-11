import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import spock.lang.Ignore

class GiftCardManagementSpec extends FunctionalSpecBase{

    @Ignore
    def "add guest GC"(){

    }

    @Ignore
    def "Get Gift Card Transactions"(){
        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken("johnnyfutahb8bcabe0555@gmail.com", "Password1")

        then:
        !userToken.isEmpty()

        //Update Card Balance
        when:
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, "2000000013")

        then:
        getBalanceResult != null

        //Check Transaction History
        when:
        def transactionHistoryResult = giftCardService.getGiftCardTransactionHistory(userToken, "2000000013")

        then:
        transactionHistoryResult != null
    }

    @Ignore
    def "Check gift card balance"(){
        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken("nelia.stasuk+3@gmail.com", "Korona0")

        then:
        !userToken.isEmpty()

        //Update Card Balance
        when:
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, "b5033382-73b5-46bc-a39c-af2b969a75c4")

        then:
        getBalanceResult != null
        getBalanceResult.status.statusCode == 200

        //Check Transaction History
        when:
        def transactionHistoryResult = giftCardService.getGiftCardTransactionHistory(userToken, "b5033382-73b5-46bc-a39c-af2b969a75c4")

        then:
        transactionHistoryResult != null
    }

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

        //Update Card Balance
        when:
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, addGCResult?.json?.cardId)

        then:
        getBalanceResult != null
        getBalanceResult.status.statusCode == 200
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

    @Ignore
    def "Add physical First Data Gift Card"(){
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
        def addVisaGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberFD,
                config.giftCardInformation.physicalCardPinFD)

        then:
        GiftCardService.validateNewGiftCardResult(addVisaGCResult)
    }

    @Ignore
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
        GiftCardService.validateGiftCardBalanceTransferResult(transferResult)
    }

}
