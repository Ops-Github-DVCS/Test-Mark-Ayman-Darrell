package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper

class GiftCardService extends MobileApiService {
    def creditCardService = new CreditCardService()

    def provisionGiftCardWithNewCC(Double amount, Boolean defaultCard, Boolean saveCC, token, CreditCardService.CreditCardType cardType, def inputPassword = config.userInformation.password){
        def cardData = creditCardService.getVisaCheckoutDetails()
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
                        savePaymentInformation: saveCC
                        ],
                password: inputPassword
                ]
        data.newCard.checkoutDetails = cardData
        return provisionGiftCard(data, token)
    }

    def provisionGiftCard(def data, def token){
        TestOutputHelper.printServiceCall("Provision Gift Card")
        executeMapiRegisteredUserRequest("post", "gift-cards", data, token)
    }

    def addPhysicalGiftCard(def token, def cardNumber, def pin){
        TestOutputHelper.printServiceCall("Add Physical Gift Card")
        def dataAddPhysical = [
                registrationRequestType: "RegisterExisting",
                existingCard                : [
                        userConfirmedConversionOfLegacyCard : true,
                        setAsUserDefaultGiftCard         : false,
                        cardNumber            : cardNumber,
                        pin : pin
                ]
        ]
        executeMapiRegisteredUserRequest("post", "gift-cards", dataAddPhysical, token)
    }

    def static validateNewGiftCardResult(addGCResult){
        assert addGCResult.status.statusCode == 201
        assert !addGCResult?.json?.cardId?.isEmpty()
        assert !addGCResult?.json?.cardNumber?.isEmpty()
        //Make sure this is a FD card
        assert !addGCResult?.json?.cardNumber?.toString().startsWith("77")
        return true
    }
}
