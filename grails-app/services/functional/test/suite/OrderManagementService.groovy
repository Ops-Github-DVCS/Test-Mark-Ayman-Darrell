package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper
import com.cardfree.sdk.client.Order
import grails.util.Holders

class OrderManagementService extends Order{

    public OrderManagementService(String storeNumber) {
        super(storeNumber)
        init(Holders.config)
    }


    def createOrder(def token, def guest = false, def deviceIdentifier = null) {
        TestOutputHelper.printServiceCall("Create Order")
        def orderData = [
                restaurantId: config.orderInformation.storeNumber,
                storeNumber : "9",
                orderItem   : [
                        plu            : config.orderInformation.plu,
                        quantity       : 1,
                        modifierOptions: []
                ]
        ]
        def orderResult = executeMapiUserRequest("post", "orders", orderData, token, guest, deviceIdentifier)
        orderResult
    }

    // todo merge with following call
    def submitOrderDefaultCC(String oAuthToken, def orderJson, def paymentData = null) {
        Map defaultPaymentData = [
                "paymentType": "NewCreditCard",
                "postalCode":"00000",
                "nameOnCard":"testfirst testlast",
                "cvv": "111",
                "cardNumber":"4111111111111111",
                "expiration":[
                        "month":6,
                        "year":2018
                ]
        ]
        executeMapiUserRequest("post", "orders/${config.orderInformation.storeNumber}-${orderJson.orderId}/checkout", paymentData ?: defaultPaymentData, oAuthToken)
    }


    def submitOrderToStore(String token, String orderId, def checkoutData, boolean guest = false, String deviceIdentifier = null){
		submitOrder(token, config.orderInformation.storeNumber, orderId, checkoutData, guest, deviceIdentifier)
	}

    def getSavedGiftCardCheckoutData(def giftCardId) {
        def checkoutData = [
                paymentType: "SavedGiftCard",
                giftCardId : giftCardId
        ]
        return checkoutData
    }

    def static validateCreateOrderResponse(def createOrderResponse){
        assert createOrderResponse.status.statusCode == 201
        assert createOrderResponse?.json?.total > 0.10
        assert !createOrderResponse?.json?.orderId?.isEmpty()
        true
    }

    def static validateSubmitOrderResponse(def submitOrderResponse){
        assert submitOrderResponse.status.statusCode == 200
        assert submitOrderResponse?.json?.total > 0.10
        assert submitOrderResponse?.json?.orderState?.equals("SubmitOrderAsComplete")
        assert submitOrderResponse?.json?.status?.equals("Success")
        true
    }
}
