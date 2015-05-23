package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper
import com.cardfree.sdk.client.GiftCard
import grails.util.Holders

class GiftCardService extends GiftCard {
    def creditCardService = new CreditCardService()
    //def config = Holders.config

    def provisionGiftCardWithNewCC(Double amount, Boolean defaultCard, Boolean saveCC, token, CreditCardService.CreditCardType cardType, def inputPassword = Holders.config.userInformation.password){
        def cardData = creditCardService.getVisaCheckoutDetails()
        def data = [
                registrationRequestType: "ProvisionWithFunds",
                newCard                : [
                        setAsUserDefaultGiftCard         : defaultCard,
                        loadAmount                       : [
                                amount      : amount,
                                currencyCode: Holders.config.userInformation.currencyCode
                        ],
                        cardDesignId                     : 3,
                        customSuppliedUserDesign         : true,
                        customSuppliedUserDesignImageName: "test-me-image",
                        savePaymentInformation: saveCC
                        ],
                password: inputPassword
                ]
        data.newCard.checkoutDetails = cardData
        return provisionGiftCard(token, data)
    }

    def provisionGiftCardWithSavedCC(Double amount, Boolean defaultCard, token, savedCreditCardToken, def inputPassword = Holders.config.userInformation.password){
        def data = [
                registrationRequestType: "ProvisionWithFunds",
                newCard                : [
                        setAsUserDefaultGiftCard         : defaultCard,
                        loadAmount                       : [
                                amount      : amount,
                                currencyCode: Holders.config.userInformation.currencyCode
                        ],
                        cardDesignId                     : 3,
                        customSuppliedUserDesign         : true,
                        customSuppliedUserDesignImageName: "test-me-image",
                ],
                password: inputPassword
        ]
        data.newCard.checkoutDetails = [
                paymentType: "SavedCreditCard",
                token: savedCreditCardToken
        ]
        return provisionGiftCard(token, data)
    }


    def setupAutoReloadSettings(def token, def giftCardId, def creditCardId, triggerAmount = 10, reloadAmount = 20){
        def autoReloadData = [
            enabled:true,
            thresholdTriggerAmount:triggerAmount,
            reloadAmount:reloadAmount,
            paymentTypeCode:"CC",
            paymentCreditCardId:creditCardId
        ]
        executeMapiUserRequest("put", "gift-cards/${giftCardId}/autoreload-settings", autoReloadData, token)
    }

    def provisionGiftCard(def data, def token){
        TestOutputHelper.printServiceCall("Provision Gift Card")
        executeMapiUserRequest("post", "gift-cards", data, token)
    }

    def addPhysicalGiftCard(def token, def cardNumber, def pin = null){
        TestOutputHelper.printServiceCall("Add Physical Gift Card")
        def dataAddPhysical = [
                registrationRequestType: "RegisterExisting",
                existingCard                : [
                        userConfirmedConversionOfLegacyCard : true,
                        setAsUserDefaultGiftCard         : false,
                        cardNumber            : cardNumber
                ]
        ]
        if(pin){
            dataAddPhysical.existingCard.pin = pin
        }
        executeMapiUserRequest("post", "gift-cards", dataAddPhysical, token)
    }

    def transferGiftCardBalance(def token, def sourceGiftCardId, def destinationGiftCardId){
        TestOutputHelper.printServiceCall("Transfer Gift Card Balance")
        def transferData = [
                destinationCardId: destinationGiftCardId
        ]
        executeMapiUserRequest("post", "gift-cards/${sourceGiftCardId}/balance-transfers", transferData, token)
    }

    def loadValueOnExistingGiftCard(def token, def gcNumber){
        def loadValueData = [
            loadAmount:[
                amount:20,
                currencyCode:"USD"
            ],
            checkoutDetails:[
                paymentType:"NewCreditCard",
                postalCode:"00000",
                nameOnCard:"save while loading",
                cvv:"0000",
                cardNumber:"371449635398431",
                expiration:[
                    month:5,
                    year:2060
                ],
                savePaymentInformation:false
            ]
        ]
        executeMapiUserRequest("post", "gift-cards/${gcNumber}/transactions", loadValueData, token)
    }


    def static validateNewGiftCardResult(addGCResult){
        assert addGCResult.status.statusCode == 201
        assert !addGCResult?.json?.cardId?.isEmpty()
        assert !addGCResult?.json?.cardNumber?.isEmpty()
        //Make sure this is a FD card
        //assert addGCResult?.json?.cardNumber?.toString().startsWith("77")
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
