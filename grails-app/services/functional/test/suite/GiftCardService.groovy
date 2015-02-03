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
        executeMapiUserRequest("post", "gift-cards", data, token)
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
        executeMapiUserRequest("post", "gift-cards", dataAddPhysical, token)
    }

    def transferGiftCardBalance(def token, def sourceGiftCardId, def destinationGiftCardId){
        TestOutputHelper.printServiceCall("Transfer Gift Card Balance")
        def transferData = [
                destinationCardId: destinationGiftCardId
        ]
        executeMapiUserRequest("post", "gift-cards/${sourceGiftCardId}/balance-transfers", transferData, token)
    }

    def static validateNewGiftCardResult(addGCResult){
        assert addGCResult.status.statusCode == 201
        assert !addGCResult?.json?.cardId?.isEmpty()
        assert !addGCResult?.json?.cardNumber?.isEmpty()
        //Make sure this is a FD card
        assert addGCResult?.json?.cardNumber?.toString().startsWith("77")
        return true
    }

    def static validateGiftCardBalanceTransferResult(transferResult){
        assert transferResult.status.statusCode == 200
        assert transferResult.json.sourceCardBalance.startingBalance.amount == 5
        assert transferResult.json.sourceCardBalance.endingBalance.amount == 0
        assert transferResult.json.destinationCardBalance.endingBalance.amount == 15
        assert transferResult.json.destinationCardBalance.startingBalance.amount == 10
        true
    }
}
