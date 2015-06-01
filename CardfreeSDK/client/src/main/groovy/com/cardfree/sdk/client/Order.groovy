package com.cardfree.sdk.client

/**
 * Created by gabe on 3/19/15.
 */
class Order extends OauthClient{

	public Order(String storeNumber) {
		defaultStoreId = storeNumber
	}

	String defaultStoreId
	
	def createEmptyOrder(def oAuthToken, String storeId = defaultStoreId) {
		executeMapiUserRequest("post", "orders", [restaurantId: storeId], oAuthToken)
	}

	// todo factor out orderJson from following calls
	def order(def oAuthToken, String orderId) {
		println("   Get Order")
		executeMapiUserRequest("get", "orders/$defaultStoreId-${orderId}", null, oAuthToken)
	}
	def orderHistory(def oAuthToken) {
		println("   Get Order History")
		executeMapiUserRequest("get", "orders", null, oAuthToken)
	}

	def addItemToOrder(def oAuthToken, def orderJson, String itemPlu) {
		println("   Add Order Item")
		executeMapiUserRequest("post", "orders/$defaultStoreId-${orderJson.orderId}/items", [plu: itemPlu, quantity: 1, modifierOptions: []], oAuthToken)
	}

	def updateOrderItem(def oAuthToken, def orderJson, String itemPlu, String orderItemId, BigInteger quantity) {
		println("   Add Order Item")
		executeMapiUserRequest("put", "orders/$defaultStoreId-${orderJson.orderId}/items/$orderItemId", [orderItemId: orderItemId, plu: itemPlu, quantity: quantity, modifierOptions: []], oAuthToken)
	}

	def removeItemFromOrder(def oAuthToken, def orderJson, String itemPlu) {
		println("   Remove Order Item")
		executeMapiUserRequest("delete", "orders/$defaultStoreId-${orderJson.orderResponse.orderId}/items/$itemPlu", null, oAuthToken)
	}

	def orderTotal(String oAuthToken, String orderId) {
		println("   Order Total")
		executeMapiUserRequest("get", "orders/$defaultStoreId-${orderId}/total", null, oAuthToken)
	}

	def submitOrder(String token, String storeNumber, String orderId, Map checkoutData, boolean guest = false, String deviceIdentifier = null){
		println("   Submit Order")
		log.debug("Submit Order")
		def checkoutResult = executeMapiUserRequest("post", "orders/$storeNumber-${orderId}/checkout", checkoutData, token, guest, deviceIdentifier)
		checkoutResult
	}

	def cancelOrder(def oAuthToken, def orderJson) {
		println("   Cancel Order")
		executeMapiUserRequest("delete", "orders/$defaultStoreId-${orderJson.orderId}", null, oAuthToken)
	}

	def moveOrder(String oAuthToken, String storeNumber, String orderId, def data) {
		println("   Move Order")
		executeMapiUserRequest("post","orders/$storeNumber-$orderId/move", data, oAuthToken)
	}

	def reOrder(String oAuthToken, String storeNumber, String orderId) {
		println("   Re-Order")
		executeMapiUserRequest("post", "users/me/orders/$storeNumber-$orderId/reorder", null, oAuthToken)

	}

	def pickupOrder(String token, String storeNumber, String orderId, boolean guest = false, String deviceIdentifier = null){
		println("   Pickup Order")
		log.debug("Pickup Order")
		if (!storeNumber) { storeNumber = config.orderInformation.storeNumber }
		def locationData = [
				"pickupLocation" : "drive-through",
				"pickupTime" : "2015-04-15T17:25:00-07:00"
		]
		def checkoutResult = executeMapiUserRequest("post", "orders/$storeNumber-${orderId}/pickup", locationData, token, guest, deviceIdentifier)
		checkoutResult
	}

	//TODO: JAR - Remove this
	def testAPICall(String oAuthToken) {
		println("   Test API Call")
		executeMapiUserRequest("get", "jeff-test", null, oAuthToken)

	}
}
