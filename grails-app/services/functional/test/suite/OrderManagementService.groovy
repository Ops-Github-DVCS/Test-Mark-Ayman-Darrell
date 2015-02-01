package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper

class OrderManagementService extends MobileApiService{

    def createOrder(def token) {
        TestOutputHelper.printServiceCall("Create Order")
        def orderData = [
                restaurantId: config.orderInformation.storeNumber,
                storeNumber : "9",
                orderItem   : [
                        plu            : config.orderInformation.storeNumber,
                        quantity       : 1,
                        modifierOptions: []
                ]
        ]
        def orderResult = executeMapiRegisteredUserRequest("post", "orders", orderData, token)
        orderResult
    }

    def submitOrder(def token, def orderId, def checkoutData){
        TestOutputHelper.printServiceCall("Submit Order")
        def checkoutResult = executeMapiRegisteredUserRequest("post", "orders/${config.orderInformation.storeNumber}-${orderId}/checkout", checkoutData, token)
        checkoutResult
    }

    def getSavedGiftCardCheckoutData(def giftCardId) {
        def checkoutData = [
                paymentType: "SavedGiftCard",
                giftCardId : giftCardId
        ]
        return checkoutData
    }
}
