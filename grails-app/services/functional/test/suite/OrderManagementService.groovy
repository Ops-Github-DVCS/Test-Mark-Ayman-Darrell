package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper

class OrderManagementService extends MobileApiService{

    def createOrder(def token, def guest = false) {
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
        def orderResult = executeMapiUserRequest("post", "orders", orderData, token, guest)
        orderResult
    }

    def submitOrder(def token, def orderId, def checkoutData, def guest = false, def guestSubmit = false){
        TestOutputHelper.printServiceCall("Submit Order")
        def checkoutResult = executeMapiUserRequest("post", "orders/${config.orderInformation.storeNumber}-${orderId}/checkout", checkoutData, token, guest, guestSubmit)
        checkoutResult
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
