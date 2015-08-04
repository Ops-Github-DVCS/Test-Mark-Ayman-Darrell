import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import functional.test.suite.OrderManagementService
import spock.lang.Ignore
import spock.lang.IgnoreRest

class GiftCardManagementSpec extends FunctionalSpecBase{

    @Ignore
    def "Get Gift Card Transactions"(){
        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken("johnnyfutah_mobile_2_11@gmail.com", "Password1")

        then:
        !userToken.isEmpty()

        //Update Card Balance
        when:
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, "200000238")

        then:
        getBalanceResult != null

        //Check Transaction History
        when:
        def transactionHistoryResult = giftCardService.getGiftCardTransactionHistory(userToken, "200000238")

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

        when:
        def getUserResult = accountManagementService.getUserInformation(userToken)

        then:
        getUserResult != null
    }

    @Ignore
    def "Reload fails with bad credit card"(){
        when: "Create New User"
        def userResult = accountManagementService.provisionNewRandomUser()

        then: "get a valid user"
        AccountManagementService.validateNewUser(userResult)

        when: "Login User"
        def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

        then: "valid token"
        !userToken.isEmpty()

        when: "Add GC to user using a new Visa CC"
        def addGCResult = giftCardService.provisionGiftCardWithNewCC(5.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult)

        when: "Reload Existing GC"
        def loadValuleresult = giftCardService.loadValueOnExistingGiftCard(userToken, addGCResult?.json?.cardId)

        then:
        loadValuleresult != null

        when: "Update Card Balance"
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
        def addVisaGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberVisa)

        then:
        GiftCardService.validateNewGiftCardResult(addVisaGCResult)

        //Get all gift cards for user
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def getGiftCards = giftCardService.getAllGiftingGiftCardsForUser(userToken)

        then:
        getGiftCards != null
        getGiftCards?.json?.data?.size() ==0
        !getGiftCards?.json?.data[0].cardId?.isEmpty()
    }

    @Ignore
    def "Reload GC that was purchased down to zero"(){

        //Login User
        when:
        String userToken = accountManagementService.getRegisteredUserToken("johnnyfutah_mobile_2_11@gmail.com", config.userInformation.password)

        then:
        !userToken.isEmpty()

        //Load Value on GC
        when:
        def loadValuleresult = giftCardService.loadValueOnExistingGiftCard(userToken)

        then:
        loadValuleresult != null
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
        def addFirstDataGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberFD,
                config.giftCardInformation.physicalCardPinFD)

        then:
        GiftCardService.validateNewGiftCardResult(addFirstDataGCResult)

        //Update Card Balance
        when:
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, addFirstDataGCResult?.json?.cardId)

        then:
        getBalanceResult != null
    }

    @Ignore
    def "Test Default GC Settings"(){
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

        //Add GC to user using a new Visa CC
        when:
        def addGCResult = giftCardService.provisionGiftCardWithNewCC(5.00, true, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult)

        //Add Physical Visa Gift Card to Account
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def addFirstDataGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberFD,
                config.giftCardInformation.physicalCardPinFD)

        then:
        GiftCardService.validateNewGiftCardResult(addFirstDataGCResult)

        //Update Card Balance
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, addFirstDataGCResult?.json?.cardId)

        then:
        getBalanceResult != null
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

    @Ignore
    def "Get multiple gift cards from user"(){
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

        //Get all gift cards for user
        when:
        def getGiftCards = giftCardService.getAllGiftingGiftCardsForUser(userToken)

        then:
        getGiftCards != null
        getGiftCards?.json?.data?.size() > 1
        !getGiftCards?.json?.data[0].cardId?.isEmpty()
    }

    @Ignore
    def "Transfer Balance from Visa to FD GC and test auto reload"(){
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

        //Add Credit Card to user
        when:
        def addCreditCardResult = creditCardService.addTestCreditCardToAccount(userToken)

        then:
        CreditCardService.validateAddCreditCardResult(addCreditCardResult)
        addCreditCardResult != null

        //Add Physical Visa Gift Card to Account
        when:
        def addVisaGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberVisa,
                config.giftCardInformation.physicalCardPinVisa)

        then:
        GiftCardService.validateNewGiftCardResult(addVisaGCResult)

        //Add FD GC 1 to user using a new Visa CC
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def addGCResult1 = giftCardService.provisionGiftCardWithNewCC(5.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult1)

        //Transfer balance from GC 1 to GC 2
        when:
        def transferResult = giftCardService.transferGiftCardBalance(userToken, addVisaGCResult.json.cardId, addGCResult1.json.cardId)

        then:
        //GiftCardService.validateGiftCardBalanceTransferResult(transferResult)
        transferResult != null

        //Get all gift cards for user
        when:
        def getGiftCards = giftCardService.getAllGiftingGiftCardsForUser(userToken)

        then:
        getGiftCards != null
        getGiftCards?.json?.data?.size() == 1
        !getGiftCards?.json?.data[0].cardId?.isEmpty()

        //Setup Auto Reload Settings
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def autoRealoadUpdateResult = giftCardService.setupAutoReloadSettings(userToken, getGiftCards?.json?.data[0].cardId, addCreditCardResult?.json?.creditCardId, 20, 20)

        then:
        autoRealoadUpdateResult != null

        //Create Order
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(getGiftCards?.json?.data[0].cardId)
        def submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Update Card Balance
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, getGiftCards?.json?.data[0].cardId)

        then:
        getBalanceResult != null
        getBalanceResult?.json?.availableBalance?.amount > 10

        //Check Transaction History
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def transactionHistoryResult = giftCardService.getGiftCardTransactionHistory(userToken, getGiftCards?.json?.data[0].cardId)

        then:
        transactionHistoryResult != null
    }

    @Ignore
    def "Transfer Balance from Visa to FD GC and test auto reload without order"(){
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

        //Add Credit Card to user
        when:
        def addCreditCardResult = creditCardService.addTestCreditCardToAccount(userToken)

        then:
        CreditCardService.validateAddCreditCardResult(addCreditCardResult)
        addCreditCardResult != null

        //Add Physical Visa Gift Card to Account $5
        when:
        def addVisaGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberVisa,
                config.giftCardInformation.physicalCardPinVisa)

        then:
        GiftCardService.validateNewGiftCardResult(addVisaGCResult)

        //Add FD GC 1 to user using a new Visa CC $20
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def addGCResult1 = giftCardService.provisionGiftCardWithNewCC(20.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult1)

        //Transfer balance from GC 1 to GC 2 $25
        when:
        def transferResult = giftCardService.transferGiftCardBalance(userToken, addVisaGCResult.json.cardId, addGCResult1.json.cardId)

        then:
        //GiftCardService.validateGiftCardBalanceTransferResult(transferResult)
        transferResult != null

        //Get all gift cards for user
        when:
        def getGiftCards = giftCardService.getAllGiftingGiftCardsForUser(userToken)

        then:
        getGiftCards != null
        getGiftCards?.json?.data?.size() == 1
        !getGiftCards?.json?.data[0].cardId?.isEmpty()

        //Setup Auto Reload Settings
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def autoRealoadUpdateResult = giftCardService.setupAutoReloadSettings(userToken, getGiftCards?.json?.data[0].cardId, addCreditCardResult?.json?.creditCardId, 20, 20)

        then:
        autoRealoadUpdateResult != null

        //Update Card Balance
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, getGiftCards?.json?.data[0].cardId)

        then:
        getBalanceResult != null
        getBalanceResult?.json?.availableBalance?.amount == 25
    }

    @Ignore
    def "Transfer Balance from FD to Visa GC and test auto reload without order"(){
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

        //Add Credit Card to user
        when:
        def addCreditCardResult = creditCardService.addTestCreditCardToAccount(userToken)

        then:
        CreditCardService.validateAddCreditCardResult(addCreditCardResult)
        addCreditCardResult != null

        //Add Physical Visa Gift Card to Account $5
        when:
        def addVisaGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberVisa,
                config.giftCardInformation.physicalCardPinVisa)

        then:
        GiftCardService.validateNewGiftCardResult(addVisaGCResult)

        //Add FD GC 1 to user using a new Visa CC $20
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def addGCResult1 = giftCardService.provisionGiftCardWithNewCC(20.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult1)

        //Transfer balance from GC 1 to GC 2 $25
        when:
        def transferResult = giftCardService.transferGiftCardBalance(userToken, addGCResult1.json.cardId, addVisaGCResult.json.cardId)

        then:
        //GiftCardService.validateGiftCardBalanceTransferResult(transferResult)
        transferResult != null

        //Get all gift cards for user
        when:
        def getGiftCards = giftCardService.getAllGiftingGiftCardsForUser(userToken)

        then:
        getGiftCards != null
        getGiftCards?.json?.data?.size() == 1
        !getGiftCards?.json?.data[0].cardId?.isEmpty()

        //Setup Auto Reload Settings
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def autoRealoadUpdateResult = giftCardService.setupAutoReloadSettings(userToken, getGiftCards?.json?.data[0].cardId, addCreditCardResult?.json?.creditCardId, 20, 20)

        then:
        autoRealoadUpdateResult != null

        //Update Card Balance
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, getGiftCards?.json?.data[0].cardId)

        then:
        getBalanceResult != null
        getBalanceResult?.json?.availableBalance?.amount == 25
    }

    @Ignore
    def "Transfer Balance from FD to Visa GC and test Visa GC removed from account"() {
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

        //Add Physical Visa Gift Card to Account $5
        when:
        def addVisaGCResult = giftCardService.addPhysicalGiftCard(userToken, config.giftCardInformation.physicalCardNumberVisa,
                config.giftCardInformation.physicalCardPinVisa)

        then:
        GiftCardService.validateNewGiftCardResult(addVisaGCResult)

        //Add FD GC 1 to user using a new Visa CC $20
        when:
        userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)
        def addGCResult1 = giftCardService.provisionGiftCardWithNewCC(20.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult1)

        //Transfer balance from GC 1 to GC 2 $25
        when:
        def transferResult = giftCardService.transferGiftCardBalance(userToken, addVisaGCResult.json.cardId, addGCResult1.json.cardId )

        then:
        //GiftCardService.validateGiftCardBalanceTransferResult(transferResult)
        transferResult != null

        //Get all gift cards for user
        when:
        def getGiftCards = giftCardService.getAllGiftingGiftCardsForUser(userToken)

        then:
        getGiftCards != null
        getGiftCards?.json?.data?.size() == 1
        !getGiftCards?.json?.data[0].cardId?.isEmpty()
    }

    @IgnoreRest
    def "Auto-reload"(){
        when: "Create New User"
        def userResult = accountManagementService.provisionNewRandomUser()

        then: "get a valid user"
        AccountManagementService.validateNewUser(userResult)

        when: "Login User"
        def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

        then: "valid token"
        !userToken.isEmpty()

        when: "Add GC to user using a newly added Visa CC"
        def addCreditCardResult = creditCardService.addTestCreditCardToAccount(userToken)
        def addGCResult = giftCardService.provisionGiftCardWithSavedCC(21.00, false, userToken, addCreditCardResult?.json?.creditCardId)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult)

        when: "Set up auto reload"
        def autoReloadUpdateResult = giftCardService.setupAutoReloadSettings(userToken, addGCResult.json.cardId, addCreditCardResult?.json?.creditCardId, 20, 20)

        then:
        autoReloadUpdateResult

        when: "get svc balance"
            def svcBalanceBeforeTransaction = mobileApiService.executeSVCRequest("get", "/accounts/$addGCResult.json.cardId/balance")
        then: "initial load amount"
            svcBalanceBeforeTransaction.json.balance == 21.00

        when: "do a transaction"
        def data = [
            accountNumber: addGCResult.json.cardId,
            transactionAmount: 2.00,
            transactionCurrency: "USD",
            transactionType: "TransactionAuthorization",
//            merchantId: "999995", // uat
            merchantId: "99997", // dev
            transactionTime: new Date(),
            tranCode: "8001"
                ]
        def postTXresponse = mobileApiService.executeSVCRequest("post", "/transactions", data)


        then: "auto reload happens"
        postTXresponse

        when: "get svc balance"
            def svcBalanceAfterTransaction = mobileApiService.executeSVCRequest("get", "/accounts/$addGCResult.json.cardId/balance")
        then: "either original - purchase, or original - purchase + reload"
            svcBalanceAfterTransaction.json.balance // == 21.00

        when: "Update mapi Card Balance"
        sleep(1000)
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, addGCResult?.json?.cardId)

        then:
        getBalanceResult.json.amount == 39.00
        getBalanceResult.status.statusCode == 200
    }

}
