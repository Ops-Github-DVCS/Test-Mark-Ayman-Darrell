package com.cardfree.sdk.client
import grails.util.Holders

/**
 * Created by gabe on 3/19/15.
 */
class GiftCard extends OauthClient{

    public GiftCard() {
        init(Holders.config)
    }

	def provisionGiftCard(String token, def data){
		log.debug("Provision Gift Card")
		executeMapiUserRequest("post", "gift-cards", data, token)
	}

	def getAllGiftingGiftCardsForUser(String token){
		executeMapiUserRequest("get", "gifting", null, token)
	}

	def addPhysicalGiftCard(String token, String cardNumber, String pin){
		log.debug("Add Physical Gift Card")
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

	def transferGiftCardBalance(String token, String sourceGiftCardId, String destinationGiftCardId){
		log.debug("Transfer Gift Card Balance")
		def transferData = [
				destinationCardId: destinationGiftCardId
		]
		executeMapiUserRequest("post", "gift-cards/${sourceGiftCardId}/balance-transfers", transferData, token)
	}

	def getGiftCardBalance(String token, String giftCardId){
		log.debug("Get Gift Card Balance")
		executeMapiUserRequest("get", "gift-cards/${giftCardId}/balance", null, token)
	}

	def getGiftCardTransactionHistory(String token, String giftCardId){
		log.debug("Get Gift Card Transaction History")
		executeMapiUserRequest("get", "gift-cards/${giftCardId}/transactions", null, token)
	}
	
}
