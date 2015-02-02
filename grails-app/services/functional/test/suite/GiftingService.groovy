package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper

class GiftingService extends MobileApiService{

    def sendEGift(def token, def checkoutDetails, def recipientEmail){
        TestOutputHelper.printServiceCall("Send EGift")
        def data = [
                recipientName           : "Gift Recipient",
                message                 : "This is a gift",
                provisionModel          : [
                        loadAmount  : [
                                amount      : 5,
                                currencyCode: config.userInformation.currencyCode
                        ],
                        cardDesignId: 1
                ],
                setUserDefaultGiftCard  : true,
                customSuppliedUserDesign: false,
                recipientEmailOrSmsNumber: recipientEmail
        ]
        data.provisionModel.checkoutDetails = checkoutDetails
        executeMapiRegisteredUserRequest("post", "gifting", data, token)
    }

    def static validateEGiftSendResult(def sentGiftResult){
        assert sentGiftResult.status.statusCode == 200
        assert sentGiftResult?.json?.gift?.status?.equals("Created")
        assert sentGiftResult?.json?.gift?.giftCard?.cardNumber?.toString().startsWith("77")
        true
    }
}
