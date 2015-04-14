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
		executeMapiUserRequest("get", "orders/$defaultStoreId-${orderId}", null, oAuthToken)
	}
	def orderHistory(def oAuthToken) {
		executeMapiUserRequest("get", "orders", null, oAuthToken)
	}

	def addItemToOrder(def oAuthToken, def orderJson, String itemPlu) {
		executeMapiUserRequest("post", "orders/$defaultStoreId-${orderJson.orderId}/items", [plu: itemPlu, quantity: 1, modifierOptions: []], oAuthToken)
	}

	def updateOrderItem(def oAuthToken, def orderJson, String itemPlu, String orderItemId, BigInteger quantity) {
		executeMapiUserRequest("put", "orders/$defaultStoreId-${orderJson.orderId}/items/$orderItemId", [orderItemId: orderItemId, plu: itemPlu, quantity: quantity, modifierOptions: []], oAuthToken)
	}

	def removeItemFromOrder(def oAuthToken, def orderJson, String itemPlu) {
		executeMapiUserRequest("delete", "orders/$defaultStoreId-${orderJson.orderResponse.orderId}/items/$itemPlu", null, oAuthToken)
	}

	def orderTotal(String oAuthToken, String orderId) {
		executeMapiUserRequest("get", "orders/$defaultStoreId-${orderId}/total", null, oAuthToken)
	}

	def submitOrder(String token, String storeNumber, String orderId, Map checkoutData, boolean guest = false, String deviceIdentifier = null){
		log.debug("Submit Order")
		def checkoutResult = executeMapiUserRequest("post", "orders/$storeNumber-${orderId}/checkout", checkoutData, token, guest, deviceIdentifier)
		checkoutResult
	}

	def cancelOrder(def oAuthToken, def orderJson) {
		executeMapiUserRequest("delete", "orders/$defaultStoreId-${orderJson.orderId}", null, oAuthToken)
	}

	def moveOrder(String oAuthToken, String storeNumber, String orderId, def data) {
		executeMapiUserRequest("post","orders/$storeNumber-$orderId/move", data, oAuthToken)
	}

	def reOrder(String oAuthToken, String storeNumber, String orderId) {
		executeMapiUserRequest("post", "users/me/orders/$storeNumber-$orderId/reorder", null, oAuthToken)
	}
}
