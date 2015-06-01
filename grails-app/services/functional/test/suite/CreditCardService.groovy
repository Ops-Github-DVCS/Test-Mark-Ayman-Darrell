package functional.test.suite

import com.cardfree.sdk.client.CreditCard

class CreditCardService extends CreditCard{

    enum CreditCardType{
        VISA, MASTERCARD, AMEX
    }

	def addTestCreditCardToAccount(def token){
		def addCreditCardData = getVisaTestCard()
		addCreditCardToAccount(token, addCreditCardData)
	}


	Map getVisaTestCard() {
        Map data = [
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


    Map getVisaCheckoutDetails(){
        return [
                paymentType           : "NewCreditCard",
                postalCode            : "11111",
                nameOnCard            : "test test",
                cvv                   : "111",
                cardNumber            : "4111111111111111",
                /*cardNumber            : "4788250000028291",*/
                expiration            : [
                        month: 12,
                        year : 2018
                ],
                savePaymentInformation: false
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

    def static validateAddCreditCardResult(def addCreditCardResult){
        assert addCreditCardResult?.status.statusCode == 201
        assert !addCreditCardResult?.json?.creditCardId?.isEmpty()
        assert !addCreditCardResult?.json?.nameOnCard?.isEmpty()
        return true
    }
}
