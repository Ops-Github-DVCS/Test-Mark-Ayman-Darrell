package com.cardfree.sdk.client

/**
 * Created by gabe on 3/19/15.
 */
class Order extends OauthClient{
	def submitOrder(String token, String storeNumber, String orderId, Map checkoutData, boolean guest = false, String deviceIdentifier = null){
		log.debug("Submit Order")
		def checkoutResult = executeMapiUserRequest("post", "orders/$storeNumber-${orderId}/checkout", checkoutData, token, guest, deviceIdentifier)
		checkoutResult
	}
}
