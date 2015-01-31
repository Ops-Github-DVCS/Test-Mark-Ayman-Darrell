package functional.test.suite

import grails.util.Holders

class GiftCardService extends MobileApiService {
    static def config = Holders.config
    def creditCardService = new CreditCardService()

    def provisionGiftCardWithNewCC(Double amount, Boolean defaultCard, Boolean saveCC, token, CreditCardService.CreditCardType cardType, def inputPassword = config.userInformation.password){
        def cardData = creditCardService.getTestCreditCard(cardType)
        def data = [
                registrationRequestType: "ProvisionWithFunds",
                newCard                : [
                        setAsUserDefaultGiftCard         : defaultCard,
                        loadAmount                       : [
                                amount      : amount,
                                currencyCode: config.userInformation.currencyCode
                        ],
                        cardDesignId                     : 3,
                        customSuppliedUserDesign         : true,
                        customSuppliedUserDesignImageName: "test-me-image",
                        checkoutDetails                  : cardData.data,
                        savePaymentInformation: saveCC
                        ],
                password: inputPassword
                ]
        return provisionGiftCard(data, token)
    }

    def provisionGiftCard(def data, def token){
        executeMapiRegisteredUserRequest("post", "gift-cards", data, token)
    }
}
