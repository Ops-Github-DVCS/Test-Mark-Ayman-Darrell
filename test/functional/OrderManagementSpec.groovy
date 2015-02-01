import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import functional.test.suite.OrderManagementService

class OrderManagementSpec extends FunctionalSpecBase{

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
        def addGCResult = giftCardService.provisionGiftCardWithNewCC(5.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

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
        def submitOrderResult = orderManagementService.submitOrder(userToken, createOrderResult?.json?.orderId, savedGCCheckoutData)

        then:
        savedGCCheckoutData != null
        submitOrderResult != null
    }

}
