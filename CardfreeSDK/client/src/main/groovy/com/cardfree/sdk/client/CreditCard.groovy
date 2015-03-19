package com.cardfree.sdk.client

/**
 * Created by gabe on 3/18/15.
 */
class CreditCard extends OauthClient{
	def addCreditCardToAccount(String token, Map cardData){
		executeMapiUserRequest("post", "credit-cards", cardData, token)
	}
}
