import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import functional.test.suite.OrderManagementService
import org.hibernate.id.GUIDGenerator
import spock.lang.Ignore

class OrderManagementSpec extends FunctionalSpecBase{

    @Ignore
    def "Submit order with saved Gift Card"(){
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
        def addGCResult = giftCardService.provisionGiftCardWithNewCC(100.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult)

        //Create Order
        when:
        def createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        def savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        def submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Update Card Balance
        when:
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, addGCResult?.json?.cardId)

        then:
        getBalanceResult != null

        //Check Transaction History
        when:
        def transactionHistoryResult = giftCardService.getGiftCardTransactionHistory(userToken, addGCResult?.json?.cardId)

        then:
        transactionHistoryResult != null
    }

    @Ignore
    def "Submit order with saved Gift Card to trigger auto reload"(){
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

        //Add GC to user using a new Visa CC
        when:
        def addGCResult = giftCardService.provisionGiftCardWithSavedCC(10.00, false, userToken, addCreditCardResult?.json?.creditCardId)

        then:
        GiftCardService.validateNewGiftCardResult(addGCResult)

        //Setup Auto Reload Settings
        when:
        def autoRealoadUpdateResult = giftCardService.setupAutoReloadSettings(userToken, addGCResult?.json?.cardId, addCreditCardResult?.json?.creditCardId)

        then:
        autoRealoadUpdateResult != null

        //Create Order
        when:
        def createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        def savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        def submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        //OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        /*
        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Create Order
        when:
        createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        savedGCCheckoutData = orderManagementService.getSavedGiftCardCheckoutData(addGCResult?.json?.cardId)
        submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)
*/

        //Update Card Balance
        when:
        def getBalanceResult = giftCardService.getGiftCardBalance(userToken, addGCResult?.json?.cardId)

        then:
        getBalanceResult != null
        //getBalanceResult?.json?.availableBalance?.amount > 10

        //Check Transaction History
        when:
        def transactionHistoryResult = giftCardService.getGiftCardTransactionHistory(userToken, addGCResult?.json?.cardId)

        then:
        transactionHistoryResult != null
    }

    @Ignore
    def "Submit GUEST order with new CC"(){
        //Setup Device Identifier
        setup:
        def deviceIdentifier = GUIDGenerator.toString()

        //Create Order
        when:
        def createOrderResult = orderManagementService.createOrder(null, true, deviceIdentifier)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        def creditCardCheckoutDetails = creditCardService.visaCheckoutDetails
        creditCardCheckoutDetails.email = "jim@jimberry.net"
        def submitOrderResult = orderManagementService.submitOrderToStore(null, createOrderResult?.json?.orderId, creditCardCheckoutDetails, true, deviceIdentifier)

        then:
        creditCardCheckoutDetails != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)
    }

    def "Pickup order with new Credit Card"(){
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

        //Create Order
        when:
        def createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        def creditCardCheckoutDetails = creditCardService.visaCheckoutDetails
        def submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, creditCardCheckoutDetails)

        then:
        creditCardCheckoutDetails != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Pickup Order
        when:
        def pickupOrderResult = orderManagementService.pickupOrder(userToken, null, createOrderResult?.json?.orderId)

        then:
        pickupOrderResult.status == 200
    }

    @Ignore
    def "Submit order with Bad Credit Card"(){
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

        //Create Order
        when:
        def createOrderResult = orderManagementService.createOrder(userToken)

        then:
        OrderManagementService.validateCreateOrderResponse(createOrderResult)

        //Submit Order
        when:
        def creditCardCheckoutDetails = creditCardService.visaCheckoutDetails
        creditCardCheckoutDetails.cardNumber = "6011000259505851"
        def submitOrderResult = orderManagementService.submitOrderToStore(userToken, createOrderResult?.json?.orderId, creditCardCheckoutDetails)

        then:
        creditCardCheckoutDetails != null
        OrderManagementService.validateSubmitOrderResponse(submitOrderResult)

        //Check Transaction History
        when:
        def transactionHistoryResult = giftCardService.getGiftCardTransactionHistory(userToken, "b5033382-73b5-46bc-a39c-af2b969a75c4")

        then:
        transactionHistoryResult != null

    }

}
