package functional.test.suite

class CreditCardService extends MobileApiService{

    enum CreditCardType{
        VISA, MASTERCARD, AMEX
    }

    def getVisaTestCard() {
        def data = [
                "billingAddress": [
                        "name": "test",
                        "line1": "1072 Faxon Commons",
                        "city": "Quincy",
                        "state": "MA",
                        "postalCode": "02169",
                ],
                "nameOnCard"    : "test test",
                "cardNumber"    : "4111111111111111",
                "cvv"           : "111",
                "expiration"    : [
                        "month": 12,
                        "year" : 2018
                ]
        ]
        data
    }

    def getVisaCheckoutDetails(){
        return [
                paymentType           : "NewCreditCard",
                postalCode            : "02169",
                nameOnCard            : "test test",
                cvv                   : "111",
                cardNumber            : "4111111111111111",
                expiration            : [
                        month: 12,
                        year : 2018
                ],
                savePaymentInformation: true
                ]
    }

    def getAmexTestCard(){
        null
    }

    def getMasterCardTestCard(){
        null
    }

    def getTestCreditCard(CreditCardType creditCardType = CreditCardType.VISA){
        switch (creditCardType){
            case CreditCardType.VISA:
                return getVisaTestCard()
                break
            case CreditCardType.AMEX:
                return getAmexTestCard()
                break
            case CreditCardType.MASTERCARD:
                return getMasterCardTestCard()
                break
            default:
                return getVisaTestCard()
        }
    }
}
