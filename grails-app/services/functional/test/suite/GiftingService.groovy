package functional.test.suite

class GiftingService extends MobileApiService{

    def sendEGift(def token, def checkoutDetails, def recipientEmail){
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
}
