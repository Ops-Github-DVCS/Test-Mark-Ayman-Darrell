package offers

import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import spock.lang.Ignore
import spock.lang.IgnoreRest

import java.math.MathContext
import java.math.RoundingMode


/**
 * Created by gabe on 4/14/15.
 */
class TBOffersIntegrationSpec extends FunctionalSpecBase {

	private  String STORE_ID = config.orderInformation.storeNumber
	private  boolean STORE_TAXES_DISCOUNT = config.orderInformation.storeTaxesDiscount
	private  String MOVE_TO_STOREID = config.orderInformation.moveToStoreNumber

	private  String BURRITO_PLU = '22449'
	private  String TACO_PLU = '22100'
	private String QUESADILLA_COMBO = '22607'

	private  String TWO_BUCKS_OFF = '009250'
	private  String FREE_TACO_WITH_PURCHASE = '009235'
	private String TWENTY_PERCENT_OFF = '009251'



	def oAuthToken
	def me
	String userNameAndEmail

	def setup() {
		// todo replace with accountManagementService.fetchRandomUserNameAndEmail
		userNameAndEmail = accountManagementService.fetchRandomUserNameAndEmail() // offerService.getNewUserName()
		me = accountManagementService.provisionNewUser(userNameAndEmail, config.userInformation.password, accountManagementService.getDefaultUserDetailInformation())
		oAuthToken = accountManagementService.getRegisteredUserToken(userNameAndEmail, config.userInformation.password)
	}

	def cleanup() {
		oAuthToken = null
	}

	def "Apply 009235, Free taco with any purchase"() {
		setup:
		String redemptionCode = FREE_TACO_WITH_PURCHASE

		when: "create an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		then: "we get an empty order"
		!createOrderResponse.json.orderItems
		!createOrderResponse.json.offerDiscounts

		when: "we add 2 tacos"
		def addTacoResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, TACO_PLU)
		addTacoResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, TACO_PLU)
		def itemPrice = new BigDecimal(addTacoResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods)

		then:
		addTacoResponse.status?.statusCode.equals(201)

		when: "apply an offer: free taco"
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then: "the offer discounts a taco"
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == itemPrice //  1.02 ||  offerResponse.json.offerDiscounts[0].amount == 1.19 // uat store E720329
		!offerResponse.json.offerDiscounts[0].reasonCode
		!offerResponse.json.offerDiscounts[0].reason

	}

	def "An offer can be applied to an order"() {
		setup:
		def redemptionCode = FREE_TACO_WITH_PURCHASE

		when: "create an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		then:
		createOrderResponse.status.statusCode == 201

		when: "apply an offer"
		def jsonData = [ orderId: createOrderResponse.json.orderId,
						 storeNumber: STORE_ID
		]
		def result = offerService.applyOffer(oAuthToken, redemptionCode, jsonData)
		then:
		result.status?.statusCode?.equals(200)

	}

	def "Applying the same offer multiple times results in a single offer"() {
		setup:
		def redemptionCode = FREE_TACO_WITH_PURCHASE

		when: "create an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		then:
		createOrderResponse.status.statusCode == 201

		when: "apply an offer"
		def jsonData = [ orderId: createOrderResponse.json.orderId,
						 storeNumber: STORE_ID
		]
		def result = offerService.applyOffer(oAuthToken, redemptionCode, jsonData)
		then:
		result.status?.statusCode?.equals(200)
		result.json.offerDiscounts
		result.json.offerDiscounts.size() == 1

		when: "apply an offer again"
		def result2 = offerService.applyOffer(oAuthToken, redemptionCode, jsonData)
		then:
		result2.status?.statusCode?.equals(200)
		result2.json.offerDiscounts
		result2.json.offerDiscounts.size() == 1
	}

	def "Applying different offers multiple times results in a single offer"() {
		setup:
		def redemptionCode = FREE_TACO_WITH_PURCHASE

		when: "create an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		then:
		createOrderResponse.status.statusCode == 201

		when: "apply an offer"
		def jsonData = [ orderId: createOrderResponse.json.orderId,
						 storeNumber: STORE_ID
		]
		def result = offerService.applyOffer(oAuthToken, redemptionCode, jsonData)
		then:
		result.status?.statusCode?.equals(200)
		result.json.offerDiscounts
		result.json.offerDiscounts.size() == 1

		when: "apply an offer again"
		def result2 = offerService.applyOffer(oAuthToken, TWO_BUCKS_OFF, jsonData)
		then:
		result2.status?.statusCode?.equals(200)
		result2.json.offerDiscounts
		result2.json.offerDiscounts.size() == 1
		result2.json.offerDiscounts[0].redemptionCode == TWO_BUCKS_OFF

	}

	def "Applying and removing different offers multiple times results in a single offer"() {
		setup:
		def redemptionCode = FREE_TACO_WITH_PURCHASE

		when: "create an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		then:
		createOrderResponse.status.statusCode == 201

		when: "apply an offer"
		def jsonData = [ orderId: createOrderResponse.json.orderId,
						 storeNumber: STORE_ID
		]
		def result = offerService.applyOffer(oAuthToken, redemptionCode, jsonData)
		then:
		result.status?.statusCode?.equals(200)
		result.json.offerDiscounts
		result.json.offerDiscounts.size() == 1

		when: "apply an offer again"
		def result2 = offerService.applyOffer(oAuthToken, TWO_BUCKS_OFF, jsonData)
		then:
		result2.status?.statusCode?.equals(200)
		result2.json.offerDiscounts
		result2.json.offerDiscounts.size() == 1

		when:
		def removeOfferResponse = offerService.removeOffer(oAuthToken, TWO_BUCKS_OFF, result.json)

		then:
		removeOfferResponse.status?.statusCode?.equals(200)
		!removeOfferResponse.json.offerDiscounts
	}

	def "An order can be started and an offer applied at the same time"() {

		when: "crate an order and an offer"
		def createOrderResponse = offerService.createEmptyOrderWithOffer(oAuthToken, TWO_BUCKS_OFF, config.orderInformation.storeNumber)
		then:
		createOrderResponse.status.statusCode == 201
		createOrderResponse.json.offerDiscounts != null
		createOrderResponse.json.offerDiscounts[0].amount == 0
		createOrderResponse.json.offerDiscounts[0].reasonCode != ""
	}

	def "An offer can be removed from an order"() {
		setup:
		def redemptionCode = TWO_BUCKS_OFF

		when: "crate an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		then:
		createOrderResponse.status.statusCode == 201

		when: "apply an offer"
		def jsonData = [ orderId: createOrderResponse.json.orderId,
						 storeNumber: STORE_ID
		]
		def result = offerService.applyOffer(oAuthToken, redemptionCode, jsonData)
		then:
		result.status?.statusCode?.equals(200)
		result.json.offerDiscounts
		result.json.offerDiscounts[0]
		result.json.offerDiscounts[0].reasonCode

		when:
		def removeOfferResponse = offerService.removeOffer(oAuthToken, redemptionCode, result.json)

		then:
		removeOfferResponse.status?.statusCode?.equals(200)
		!removeOfferResponse.json.offerDiscounts
		//!removeOfferResponse.json.offerDiscounts[0]

	}

	def "adding and removing items to an order with an applied offer" () {
		setup:
		def redemptionCode = TWO_BUCKS_OFF

		when: "create an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		then: "we get an empty order"
		!createOrderResponse.json.orderItems
		!createOrderResponse.json.offerDiscounts

		when: "apply an offer requiring ten dollar spend"
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)
		then: "the offer has an error due to not enough spend"
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 0.0
		offerResponse.json.offerDiscounts[0].reasonCode == "2007"

		when: "we add a burrito"
		def addBurritoResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		then: "the offer still invalid"
		addBurritoResponse.json.orderResponse.offerDiscounts.size() == 1
		addBurritoResponse.json.orderResponse.offerDiscounts[0].amount == 0.0
		addBurritoResponse.json.orderResponse.offerDiscounts[0].reasonCode == "2007"

		when: "we add  more burritos"
		def addMoreBurritoResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		addMoreBurritoResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		then: "offer is valid for \$2 off"
		addMoreBurritoResponse.json.orderResponse.offerDiscounts.size() == 1
		addMoreBurritoResponse.json.orderResponse.offerDiscounts[0].amount == 2.0
		! addMoreBurritoResponse.json.orderResponse.offerDiscounts[0].reasonCode

		when: "we remove a burrito"
		def notEnoughBurritosResponse = orderManagementService.removeItemFromOrder(oAuthToken, addMoreBurritoResponse.json, addMoreBurritoResponse.json.orderResponse.orderItems.first().orderItemId)
		then: "offer is invalid again"
		notEnoughBurritosResponse.json.offerDiscounts?.size() == 1
		notEnoughBurritosResponse.json.offerDiscounts[0].amount == 0.0
		notEnoughBurritosResponse.json.offerDiscounts[0].reasonCode == "2007"

		when: "we add a burrito"
		def onceAgainEnoughBurritosResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		then: "offer is valid for \$2 off"
		onceAgainEnoughBurritosResponse.json.orderResponse.offerDiscounts.size() == 1
		onceAgainEnoughBurritosResponse.json.orderResponse.offerDiscounts[0].amount == 2.0
		! onceAgainEnoughBurritosResponse.json.orderResponse.offerDiscounts[0].reasonCode

	}

	def "updating items in an order with an applied offer" () {
		setup:
		def redemptionCode = TWO_BUCKS_OFF

		when: "create an order"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		then: "we get an empty order"
		!createOrderResponse.json.orderItems
		!createOrderResponse.json.offerDiscounts

		when: "apply an offer requiring ten dollar spend"
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)
		then: "the offer has an error due to not enough spend"
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 0.0
		offerResponse.json.offerDiscounts[0].reasonCode == "2007"

		when: "we add a burrito"
		def addBurritoResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		then: "the offer still invalid"
		addBurritoResponse.json.orderResponse.offerDiscounts.size() == 1
		addBurritoResponse.json.orderResponse.offerDiscounts[0].amount == 0.0
		addBurritoResponse.json.orderResponse.offerDiscounts[0].reasonCode == "2007"

		when: "we add 2 more burritos by updating quantity"
		def addMoreBurritoResponse = orderManagementService.updateOrderItem(oAuthToken, createOrderResponse.json, BURRITO_PLU, addBurritoResponse.json.orderResponse.orderItems[0].orderItemId, 3)

		then: "offer is valid for 10 % off"
		addMoreBurritoResponse.json.orderResponse.offerDiscounts.size() == 1
		addMoreBurritoResponse.json.orderResponse.offerDiscounts[0].amount == 2.0
		! addMoreBurritoResponse.json.orderResponse.offerDiscounts[0].reasonCode

		when: "we remove a burrito"
		def notEnoughBurritosResponse = orderManagementService.removeItemFromOrder(oAuthToken, addMoreBurritoResponse.json, addMoreBurritoResponse.json.orderResponse.orderItems.first().orderItemId)
		then: "offer is invalid again"
		notEnoughBurritosResponse.json.offerDiscounts?.size() == 1
		notEnoughBurritosResponse.json.offerDiscounts[0].amount == 0.0
		notEnoughBurritosResponse.json.offerDiscounts[0].reasonCode == "2007"

	}

	def "move order applies the offer when moved"() {
		setup:
		def redemptionCode = TWO_BUCKS_OFF
		def cheapStore = STORE_ID
		def expensiveStore = MOVE_TO_STOREID
		def expensiveRestaurantNumber = "21523"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken, cheapStore)
		def plu = "30029" // bacon am cruncwrap

		when: "add enough items for the offer"
		4.times { orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, plu) }
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, plu)
		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		addItemResponse.json.orderResponse.total == 13.45

		when: "apply an offer requiring six dollar spend"
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)
		then: "the offer is valid"
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 2.00
		!offerResponse.json.offerDiscounts[0].reasonCode

		when: "we move the order to a store with MORE expensive item"
		def data = [newRestaurantId: expensiveStore, newStoreNumber: expensiveRestaurantNumber]
		def moveResponse = orderManagementService.moveOrder(oAuthToken, STORE_ID, createOrderResponse.json.orderId, data)
		then: "offer is still valid"
		moveResponse.status?.statusCode?.equals(200)
		moveResponse.json.offerDiscounts.size() == 1
		moveResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		moveResponse.json.offerDiscounts[0].amount == 2.00
		!offerResponse.json.offerDiscounts[0].reasonCode
	}

	def "re-order does not use offers"() {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 3 burritos"
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods).setScale(2, BigDecimal.ROUND_HALF_UP)
		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		addItemResponse.json.orderResponse.total > itemPrice * 3
		! addItemResponse.json.orderResponse.offerDiscounts

		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = TWO_BUCKS_OFF
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)
		then: ""
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 2.0
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		offerResponse.json.offerDiscounts[0].plu.toString() == "null"
		when: "order total is checked"
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)
		orderTotalResponse.json.offerDiscounts.size() == 1
		orderTotalResponse.json.offerDiscounts[0].amount == 2.0
		orderTotalResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		orderTotalResponse.json.offerDiscounts[0].plu.toString() == "null"
		!orderTotalResponse.json.offerDiscounts[0].plu
		!orderTotalResponse.json.offerDiscounts[0].reasonCode
		!orderTotalResponse.json.offerDiscounts[0].reason

		orderTotalResponse.json.subtotal == itemPrice * 3 -2
		orderTotalResponse.json.total == orderTotalResponse.json.subtotal?.toBigDecimal() + orderTotalResponse.json.tax?.toBigDecimal()

		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'

		when: "submit the order"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)
		then: "we get the discount"
		submitOrderResponse != null
		submitOrderResponse.json.offerDiscounts != null
		submitOrderResponse.json.offerDiscounts[0].amount == 2.0
		submitOrderResponse.json.offerDiscounts[0].transactionId != null
		submitOrderResponse.json.offerDiscounts[0].transactionId != ""
		!submitOrderResponse.json.offerDiscounts[0].reasonCode
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.subtotal == itemPrice * 3 - 2
		submitOrderResponse.json.total == itemPrice * 3 - 2 + submitOrderResponse.json.tax
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

		when: "re-order"
		def reorderResponse = orderManagementService.reOrder(oAuthToken, STORE_ID, createOrderResponse.json.orderId)
		then: "the offer is used"
		reorderResponse.json.subtotal == submitOrderResponse.json.subtotal
		reorderResponse.json.tax == offerResponse.json.tax // We have not yet applied the offer with Tillster, tax should not reflect discount
		reorderResponse.json.total == submitOrderResponse.json.total + 0.16 // tax difference
		reorderResponse.json.offerDiscounts == null
//			reorderResponse.json.offerDiscounts[0].amount == 2.0
//			reorderResponse.json.offerDiscounts[0].transactionId != null
//			!reorderResponse.json.offerDiscounts[0].reasonCode
//			reorderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
	}

	@Ignore
	def "submit order with a valid sku offer"() {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		def comboPlu = '22601'
		def drinkPlu = '1000'

		when: "add combo and drink"
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, comboPlu)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, drinkPlu)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods).round(new MathContext(2, RoundingMode.HALF_UP))

		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
//            addItemResponse.json.orderResponse.subtotal == itemPrice * 3
//            addItemResponse.json.orderResponse.total > itemPrice * 3
		!addItemResponse.json.orderResponse.offerDiscounts

		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = '009231' // free drink with purchase
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 1.39
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		offerResponse.json.offerDiscounts[0].plu.toString() == "null"
		offerResponse.json.subtotal == 5.05
		offerResponse.json.total == offerResponse.json.subtotal + offerResponse.json.tax

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)

		then: "the discount is applied"
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
		submitOrderResponse.json.offerDiscounts[0].amount == 1.39
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.offerDiscounts[0].plu.toString() == "null"
//            submitOrderResponse.json.total == 11.64
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

	}

	def "submit large order with a valid offer" () {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 30 BURRITOS"
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods).setScale(2, BigDecimal.ROUND_HALF_UP)
		def updateItemResponse = orderManagementService.updateOrderItem(oAuthToken, createOrderResponse.json, BURRITO_PLU, addItemResponse.json.orderResponse.orderItems[0].orderItemId, 30)

		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		updateItemResponse.json.orderResponse.subtotal == itemPrice * 30
		updateItemResponse.json.orderResponse.total > itemPrice * 30
		! updateItemResponse.json.orderResponse.offerDiscounts

		when: "add 30 tacos"
		def addItemResponse2 = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, TACO_PLU)
		def itemPrice2 = new BigDecimal(addItemResponse2.json.orderResponse.orderItems[1].pricePerUnitBeforeMods).setScale(2, BigDecimal.ROUND_HALF_UP);
		def updateItemResponse2 = orderManagementService.updateOrderItem(oAuthToken, createOrderResponse.json, TACO_PLU, addItemResponse2.json.orderResponse.orderItems[1].orderItemId, 30)

		then: "no discounts are included"
		addItemResponse2.status?.statusCode?.equals(201)
		updateItemResponse2.json.orderResponse.subtotal == itemPrice * 30 + itemPrice2 * 30
		updateItemResponse2.json.orderResponse.total > itemPrice * 30 + itemPrice2 * 30
		! updateItemResponse2.json.orderResponse.offerDiscounts


		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = TWENTY_PERCENT_OFF
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
//		offerResponse.json.offerDiscounts[0].amount == 2.0
//		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
//		offerResponse.json.offerDiscounts[0].plu.toString() == "null"
//		!offerResponse.json.offerDiscounts[0].reasonCode
//		!offerResponse.json.offerDiscounts[0].reason
//
//		offerResponse.json.subtotal == itemPrice * 3 - 2
//		offerResponse.json.total == offerResponse.json.subtotal + offerResponse.json.tax

		when: "order total is checked"
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)
		orderTotalResponse.json.offerDiscounts.size() == 1
		//orderTotalResponse.json.offerDiscounts[0].amount == (itemPrice * 30 + itemPrice2 * 30) * 0.2
		orderTotalResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		orderTotalResponse.json.offerDiscounts[0].plu.toString() == "null"
		!orderTotalResponse.json.offerDiscounts[0].plu
		!orderTotalResponse.json.offerDiscounts[0].reasonCode
		!orderTotalResponse.json.offerDiscounts[0].reason

		//orderTotalResponse.json.subtotal == (itemPrice * 30 + itemPrice2 * 30) - orderTotalResponse.json.offerDiscounts[0].amount
		//orderTotalResponse.json.total == orderTotalResponse.json.subtotal + orderTotalResponse.json.tax

		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)
		// should we check for submitOrderResponse.status == 400 && submitOrderResponse.errors[0].message.startsWith('The payment method was invalid or declined) and resubmit?
		// this error seems to happen periodically


		then: "the discount is applied"
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
//		submitOrderResponse.json.offerDiscounts[0].amount == 23.94
//		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
//		submitOrderResponse.json.offerDiscounts[0].plu.toString() == "null"
//		!submitOrderResponse.json.offerDiscounts[0].reasonCode
//		!submitOrderResponse.json.offerDiscounts[0].reason
//		submitOrderResponse.json.subtotal == itemPrice * 3 - 2 // 95.76  103.42
//		submitOrderResponse.json.total == new BigDecimal(itemPrice * 3 - 2 + submitOrderResponse.json.tax).round(new MathContext(4, RoundingMode.HALF_UP))
//		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
//		submitOrderResponse.json.status == 'Success'

	}

	def "submit order with a valid offer" () {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 3 burritos"
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods).setScale(2, BigDecimal.ROUND_HALF_UP)
		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		addItemResponse.json.orderResponse.subtotal == itemPrice * 3
		addItemResponse.json.orderResponse.total > itemPrice * 3
		! addItemResponse.json.orderResponse.offerDiscounts

		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = TWO_BUCKS_OFF // FREE_TACO_WITH_PURCHASE // $1 off everything
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 2.0
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		offerResponse.json.offerDiscounts[0].plu.toString() == "null"
		!offerResponse.json.offerDiscounts[0].reasonCode
		!offerResponse.json.offerDiscounts[0].reason

		offerResponse.json.subtotal == itemPrice * 3 - 2
		offerResponse.json.total == offerResponse.json.subtotal?.toBigDecimal() + offerResponse.json.tax?.toBigDecimal()

		when: "order total is checked"
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)
		orderTotalResponse.json.offerDiscounts.size() == 1
		orderTotalResponse.json.offerDiscounts[0].amount == 2.0
		orderTotalResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		orderTotalResponse.json.offerDiscounts[0].plu.toString() == "null"
		!orderTotalResponse.json.offerDiscounts[0].plu
		!orderTotalResponse.json.offerDiscounts[0].reasonCode
		!orderTotalResponse.json.offerDiscounts[0].reason

		orderTotalResponse.json.subtotal == itemPrice * 3 -2
		orderTotalResponse.json.total == orderTotalResponse.json.subtotal?.toBigDecimal() + orderTotalResponse.json.tax?.toBigDecimal()

		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)
		// should we check for submitOrderResponse.status == 400 && submitOrderResponse.errors[0].message.startsWith('The payment method was invalid or declined) and resubmit?
		// this error seems to happen periodically


		then: "the discount is applied"
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
		submitOrderResponse.json.offerDiscounts[0].amount == 2.0
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.offerDiscounts[0].plu.toString() == "null"
		!submitOrderResponse.json.offerDiscounts[0].reasonCode
		!submitOrderResponse.json.offerDiscounts[0].reason
		submitOrderResponse.json.subtotal == itemPrice * 3 - 2
		submitOrderResponse.json.total == itemPrice * 3 - 2 + submitOrderResponse.json.tax?.toBigDecimal()
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

//        when: "we call getAvailableOffers"
//            def offers = svcRestCall("get", "/centralAccounts/$me.json.masterAccountId/promotionPurses")
//        then: "the offer has been used"
//            offers.json.offers.any { it.redemptionCode == redemptionCode && it.totalUses == 1 }

	}

	def "submit order and getOrderTotal with no offer" () {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 3 burritos"
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods)

		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		! addItemResponse.json.orderResponse.offerDiscounts
		addItemResponse.json.orderResponse.subtotal == itemPrice * 3
		addItemResponse.json.orderResponse.total == addItemResponse.json.orderResponse.subtotal?.toBigDecimal() + addItemResponse.json.orderResponse.tax?.toBigDecimal()

		when: "order total is checked"
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, createOrderResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)

		orderTotalResponse.json.subtotal == itemPrice * 3
		orderTotalResponse.json.total == orderTotalResponse.json.subtotal?.toBigDecimal() + orderTotalResponse.json.tax?.toBigDecimal()

		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, createOrderResponse.json)
		// should we check for submitOrderResponse.status == 400 && submitOrderResponse.errors[0].message.startsWith('The payment method was invalid or declined) and resubmit?
		// this error seems to happen periodically


		then:
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.subtotal == itemPrice * 3

		submitOrderResponse.json.total == submitOrderResponse.json.subtotal + submitOrderResponse.json.tax
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

		submitOrderResponse.json.subtotal == orderTotalResponse.json.subtotal
		submitOrderResponse.json.tax == orderTotalResponse.json.tax
		submitOrderResponse.json.total == orderTotalResponse.json.total
	}

	def "submit order and getOrderTotal tax matches" () {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 3 burritos"
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods)

		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		! addItemResponse.json.orderResponse.offerDiscounts
		addItemResponse.json.orderResponse.subtotal == itemPrice * 3
		addItemResponse.json.orderResponse.total == addItemResponse.json.orderResponse.subtotal?.toBigDecimal() + addItemResponse.json.orderResponse.tax?.toBigDecimal()


		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = TWO_BUCKS_OFF // FREE_TACO_WITH_PURCHASE // $1 off everything
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 2.0
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		!offerResponse.json.offerDiscounts[0].plu
		!offerResponse.json.offerDiscounts[0].reasonCode
		!offerResponse.json.offerDiscounts[0].reason

		offerResponse.json.subtotal == itemPrice * 3 -2
		offerResponse.json.total == offerResponse.json.subtotal?.toBigDecimal() + offerResponse.json.tax?.toBigDecimal()


		when: "order total is checked"
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)
		orderTotalResponse.json.offerDiscounts.size() == 1
		orderTotalResponse.json.offerDiscounts[0].amount == 2.0
		orderTotalResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		orderTotalResponse.json.offerDiscounts[0].plu.toString() == "null"
		!orderTotalResponse.json.offerDiscounts[0].plu
		!orderTotalResponse.json.offerDiscounts[0].reasonCode
		!orderTotalResponse.json.offerDiscounts[0].reason

		orderTotalResponse.json.subtotal == itemPrice * 3 -2
		orderTotalResponse.json.total == orderTotalResponse.json.subtotal?.toBigDecimal() + orderTotalResponse.json.tax?.toBigDecimal()

		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)
		// should we check for submitOrderResponse.status == 400 && submitOrderResponse.errors[0].message.startsWith('The payment method was invalid or declined) and resubmit?
		// this error seems to happen periodically


		then: "the discount is applied"
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
		submitOrderResponse.json.offerDiscounts[0].amount == 2.0
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.offerDiscounts[0].plu.toString() == "null"
		!submitOrderResponse.json.offerDiscounts[0].plu
		!submitOrderResponse.json.offerDiscounts[0].reasonCode
		!submitOrderResponse.json.offerDiscounts[0].reason
		submitOrderResponse.json.subtotal == itemPrice * 3 -2

		submitOrderResponse.json.total == submitOrderResponse.json.subtotal?.toBigDecimal() + submitOrderResponse.json.tax?.toBigDecimal()
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

		submitOrderResponse.json.subtotal == orderTotalResponse.json.subtotal
		submitOrderResponse.json.tax == orderTotalResponse.json.tax
		submitOrderResponse.json.total == orderTotalResponse.json.total
	}

	def "submit order with a valid offer and check history" () {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 3 burritos"
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods).setScale(2, BigDecimal.ROUND_HALF_UP)

		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		addItemResponse.json.orderResponse.subtotal == itemPrice * 3
		addItemResponse.json.orderResponse.total > itemPrice * 3
		! addItemResponse.json.orderResponse.offerDiscounts

		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = TWO_BUCKS_OFF // FREE_TACO_WITH_PURCHASE // $1 off everything
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 2.0
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		offerResponse.json.offerDiscounts[0].plu.toString() == "null"
		!offerResponse.json.offerDiscounts[0].reasonCode
		!offerResponse.json.offerDiscounts[0].reason

		offerResponse.json.subtotal == itemPrice * 3 - 2
		offerResponse.json.total == offerResponse.json.subtotal?.toBigDecimal() + offerResponse.json.tax?.toBigDecimal()

		when: "order total is checked"
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)
		orderTotalResponse.json.offerDiscounts.size() == 1
		orderTotalResponse.json.offerDiscounts[0].amount == 2.0
		orderTotalResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		orderTotalResponse.json.offerDiscounts[0].plu.toString() == "null"
		!orderTotalResponse.json.offerDiscounts[0].plu
		!orderTotalResponse.json.offerDiscounts[0].reasonCode
		!orderTotalResponse.json.offerDiscounts[0].reason

		orderTotalResponse.json.subtotal == itemPrice * 3 -2
		orderTotalResponse.json.total == orderTotalResponse.json.subtotal?.toBigDecimal() + orderTotalResponse.json.tax?.toBigDecimal()

		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)
		// should we check for submitOrderResponse.status == 400 && submitOrderResponse.errors[0].message.startsWith('The payment method was invalid or declined) and resubmit?
		// this error seems to happen periodically


		then: "the discount is applied"
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
		submitOrderResponse.json.offerDiscounts[0].amount == 2.0
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.offerDiscounts[0].plu.toString() == "null"
		!submitOrderResponse.json.offerDiscounts[0].reasonCode
		!submitOrderResponse.json.offerDiscounts[0].reason
		submitOrderResponse.json.subtotal == itemPrice * 3 -2
		submitOrderResponse.json.total == itemPrice * 3 - 2 + submitOrderResponse.json.tax?.toBigDecimal()
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

		when:
		def orderHistoryResponse = orderManagementService.orderHistory(oAuthToken)

		then:
		// let's look at the order since there is no history
		def orderResponse = order(oAuthToken, offerResponse.json.orderId)
		orderResponse.json.offerDiscounts.size() == 1
		orderResponse.json.offerDiscounts[0].amount == 2.0
		orderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		!orderResponse.json.offerDiscounts[0].plu
		!orderResponse.json.offerDiscounts[0].reasonCode
		!orderResponse.json.offerDiscounts[0].reason

		orderHistoryResponse

	}

	@Ignore("this needs a new promo code")
	def "Two plu order with offer, offer only applies to one plu" () {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 3 burritos"
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods)

		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
		addItemResponse.json.orderResponse.total > itemPrice * 3
		! addItemResponse.json.orderResponse.offerDiscounts

		when: "add 3 soft tacos"
		String plu2 = '22110' // 22200 = bean burrito
		def foo = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, plu2)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, plu2)
		def addItemResponse2 = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, plu2)

		then: "taco is added"
		addItemResponse2.status?.statusCode?.equals(201)
		addItemResponse2.json.orderResponse.subtotal == 19.44
		addItemResponse2.json.orderResponse.total > 19.44  // tax = 1.56
		! addItemResponse2.json.orderResponse.offerDiscounts

		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = TWO_BUCKS_OFF
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then: "the discount is applied"
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 2.00
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		offerResponse.json.offerDiscounts[0].plu.toString() == "null"

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)

		then:
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
		submitOrderResponse.json.offerDiscounts[0].amount == 2.00
//            submitOrderResponse.json.subtotal == 17.50
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.offerDiscounts[0].plu.toString() == "null"
//            submitOrderResponse.json.total ==  18.90 // tax 1.40281750
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

		when: "we get SVC transaction history"
		def promoPurses = svcRestCall("get", "centralAccounts/$me.json.masterAccountId/promotionPurses")
		then: "the offer transaction is returned"
		// TODO: check to make sure there are two purses, and that it corresponds to the discount from the offer in the order
		promoPurses

		when: "we call getAvailableOffers"
		then: "the offer is no longer listed"
	}

	def "Cancel an order"() {
		when:
		def offerResult = offerService.getOffers(oAuthToken, "2015-03-01T12:00:01")
		def offers = offerResult.json.data
		def offer2Dollar = offers.find {it.redemptionCode == TWO_BUCKS_OFF}
		def startingUsesRemaining = offer2Dollar?.usesRemaining

		then:
		offer2Dollar && startingUsesRemaining >= 1

		when:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		def redemptionCode = TWO_BUCKS_OFF // FREE_TACO_WITH_PURCHASE // $1 off everything
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def updateItemResponse = orderManagementService.updateOrderItem(oAuthToken, createOrderResponse.json, BURRITO_PLU, addItemResponse.json.orderResponse.orderItems[0].orderItemId, 3)
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, createOrderResponse.json)


		then:
		createOrderResponse.status.statusCode == 201
		offerResponse.status.statusCode == 200
		addItemResponse.status.statusCode == 201
		updateItemResponse.status.statusCode == 201
		orderTotalResponse.status.statusCode == 200
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.total // == 12.93
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

		when:
		offerResult = offerService.getOffers(oAuthToken, "2015-03-01T12:00:01")
		offers = offerResult.json.data
		offer2Dollar = offers.find {it.redemptionCode == TWO_BUCKS_OFF}

		then:
		!offer2Dollar || offer2Dollar.usesRemaining == startingUsesRemaining - 1


		when:
		def cancelOrderResponse = orderManagementService.cancelOrder(oAuthToken, createOrderResponse.json)

		then:
		cancelOrderResponse.status?.statusCode?.equals(204)

		when:
		offerResult = offerService.getOffers(oAuthToken, "2015-03-01T12:00:01")
		offers = offerResult.json.data
		offer2Dollar = offers.find {it.redemptionCode == TWO_BUCKS_OFF}

		then:
		offer2Dollar && offer2Dollar.usesRemaining == startingUsesRemaining

		// todo Check SVC for redemption reversed
	}

	def "Unsuccessful order does not consume offer"() {
		when:
		def offerResult = offerService.getOffers(oAuthToken, "2015-03-01T12:00:01")
		def offers = offerResult.json.data
		def offer2Dollar = offers.find {it.redemptionCode == TWO_BUCKS_OFF}
		def startingUsesRemaining = offer2Dollar?.usesRemaining

		then:
		offer2Dollar && startingUsesRemaining >= 1

		when:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		def redemptionCode = TWO_BUCKS_OFF // FREE_TACO_WITH_PURCHASE // $1 off everything
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def updateItemResponse = orderManagementService.updateOrderItem(oAuthToken, createOrderResponse.json, BURRITO_PLU, addItemResponse.json.orderResponse.orderItems[0].orderItemId, 3)
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		createOrderResponse.status.statusCode == 201
		offerResponse.status.statusCode == 200
		addItemResponse.status.statusCode == 201
		updateItemResponse.status.statusCode == 201
		orderTotalResponse.status.statusCode == 200

		when: "submit an order where payment will fail"
		Map defaultPaymentData = [
				"paymentType": "NewCreditCard",
				"postalCode":"00000",
				"nameOnCard":"testfirst testlast",
				"cvv": "111",
				"cardNumber":"123456789101213",
				"expiration":[
						"month":6,
						"year":2018
				]
		]

		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, createOrderResponse.json)

		then:
		submitOrderResponse.status?.statusCode?.equals(400)
//		submitOrderResponse.json.total // == 12.93
//		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
//		submitOrderResponse.json.status == 'Success'

		when:
		offerResult = offerService.getOffers(oAuthToken, "2015-03-01T12:00:01")
		offers = offerResult.json.data
		offer2Dollar = offers.find {it.redemptionCode == TWO_BUCKS_OFF}

		then:
		!offer2Dollar || offer2Dollar.usesRemaining == startingUsesRemaining

		// todo Check SVC for redemption reversed
	}

	def "Happy hour offers have appropriate RedeemableNow false"() {
		when:
		def offerResult = offerService.getOffers(oAuthToken, "2015-03-10T12:00:01")
		def offers = offerResult.json.data
		def offer1 = offers[0]
		def offer009228 = offers.find {it.redemptionCode == "009228"}

		then: "Happy Hour offer is not redeemable at noon"
		offerResult.status.statusCode == 200
		offers
		offer1
		offer009228

		offer1.redeemableNow == true
		offer009228.redeemableNow == false

		when: "Happy Hour offer is redeemable at 3pm (2-5 is valid)"
		offerResult = offerService.getOffers(oAuthToken, "2015-03-10T15:00:01" )
		offers = offerResult.json.data
		offer1 = offers[0]
		offer009228 = offers.find {it.redemptionCode == "009228"}

		then:
		offerResult.status.statusCode == 200
		offers
		offer1
		offer009228

		offer1.redeemableNow == true
		offer009228.redeemableNow == true
	}

	def "Mark offers as viewed"() {
		when:
		def offerResult = offerService.getOffers(oAuthToken, "2015-03-07T12:00:01")
		def offers = offerResult.json.data
		ArrayList<String> codes = new ArrayList<String>()

		then:
		offerResult.status.statusCode == 200
		offers
		offers.each{
			assert it.viewed == false;
			assert it.redemptionCode != null;
			codes.add(it.redemptionCode)
		}

		when:
		def data = [redemptionCodes: codes.toArray()]
		def markResult = offerService.markOffersAsViewed(oAuthToken, data)

		then:
		markResult.status.statusCode == 200

		when:
		offerResult = offerService.getOffers(oAuthToken, "2015-03-07T12:00:01")
		offers = offerResult.json.data

		then:
		offerResult.status.statusCode == 200
		offers
		offers.each {
			assert it.viewed == true
		}
	}

	def "Item level email discounts are displayed, identical PLUs are handled by svc"() {
		def COMBO_PLU = "22604"
		def COMBO_BOGO_OFFER = '009221'

		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 2 combos"
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, COMBO_PLU)
		addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, COMBO_PLU)
		addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, COMBO_PLU)
// 		def updateItemResponse = orderManagementService.updateOrderItem(oAuthToken, createOrderResponse.json, COMBO_PLU, addItemResponse.json.orderResponse.orderItems[0].orderItemId, 3)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods)
		then: "no discounts are included"
		addItemResponse.status?.statusCode?.equals(201)
//		addItemResponse.json.orderResponse.subtotal == itemPrice * 3
//		addItemResponse.json.orderResponse.total > itemPrice * 3
//		! addItemResponse.json.orderResponse.offerDiscounts

		when: "apply a single use offer BOGO"
		def redemptionCode = COMBO_BOGO_OFFER
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == itemPrice
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		offerResponse.json.offerDiscounts[0].plu.toString() == COMBO_PLU
//		offerResponse.json.subtotal == itemPrice * 3 - 2
//		offerResponse.json.total == offerResponse.json.subtotal + offerResponse.json.tax

		when: "the order is submitted"
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)
		// should we check for submitOrderResponse.status == 400 && submitOrderResponse.errors[0].message.startsWith('The payment method was invalid or declined) and resubmit?
		// this error seems to happen periodically


		then: "the discount is applied"
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
//		submitOrderResponse.json.offerDiscounts[0].amount == 2.0
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.offerDiscounts[0].plu.toString() == COMBO_PLU
//            submitOrderResponse.json.total == 11.64
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

	}

	// for BLIM-3447
	def "apply 20% off offer before adding items to cart 2" () {
		setup:
		def redemptionCode = "009251" // 20% off order offer
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when:
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode == 200

		when: "add and remove an item" // BLIM-3445
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, TACO_PLU)
		def removeItemResponse = orderManagementService.removeItemFromOrder(oAuthToken, addItemResponse.json, addItemResponse.json.orderResponse.orderItems[0].orderItemId)

		then:
		removeItemResponse.status?.statusCode == 200
	}

	def "free drink on registration happy path"() {
		setup:
		def redemptionCode = "RTACO0000000001" // From seed data
		def drinkPlu = "1001"

		when: "Create mapi user"
		// user created in setup. just reference "me" and "oauthToken"
		def centralAccountNumber = me.json.masterAccountId
		def svcCentralAccount = svcRestCall("get", "/centralAccounts/$centralAccountNumber")

		then: "SVC has a corresponding account"
		"success" == svcCentralAccount.json.status
		centralAccountNumber == svcCentralAccount.json.centralAccount.accountNumber

		when: "call getAvailableOffers"
		def offers = svcRestCall("get", "/centralAccounts/$centralAccountNumber/promotionPurses")
		then: "there's a free drink offer"
		offers.json.offers.any { it.redemptionCode == redemptionCode && it.totalUses == 0 }

		when: "submit order with drink and free drink offer"
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, drinkPlu)
		orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, drinkPlu)
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)
		def submitOrderResponse = orderManagementService.submitOrderDefaultCC(oAuthToken, offerResponse.json)

		then: "discount!"
		submitOrderResponse != null
		submitOrderResponse.status?.statusCode?.equals(200)
		submitOrderResponse.json.offerDiscounts.size() == 1
		submitOrderResponse.json.offerDiscounts[0].amount == 1.49
		submitOrderResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		submitOrderResponse.json.offerDiscounts[0].plu.toString() == drinkPlu
		submitOrderResponse.json.total == 1.61
		submitOrderResponse.json.orderState == 'SubmitOrderAsComplete'
		submitOrderResponse.json.status == 'Success'

	}

	def "cause promo code to be cleared with Tillster"() {
		setup:
		def createOrderResponse = orderManagementService.createEmptyOrder(oAuthToken)

		when: "add 3 burritos"
		def addItemResponse = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse2 = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		def addItemResponse3 = orderManagementService.addItemToOrder(oAuthToken, createOrderResponse.json, BURRITO_PLU)
		BigDecimal itemPrice = new BigDecimal(addItemResponse.json.orderResponse.orderItems[0].pricePerUnitBeforeMods)

		then: "no discounts are included"
		addItemResponse3.status?.statusCode?.equals(201)
		! addItemResponse3.json.orderResponse.offerDiscounts
		addItemResponse3.json.orderResponse.subtotal == itemPrice * 3
		addItemResponse3.json.orderResponse.total == addItemResponse3.json.orderResponse.subtotal + addItemResponse3.json.orderResponse.tax


		when: "apply a single use offer requiring a minimum purchase amount to the order"
		def redemptionCode = TWO_BUCKS_OFF // FREE_TACO_WITH_PURCHASE // $1 off everything
		def offerResponse = offerService.applyOffer(oAuthToken, redemptionCode, createOrderResponse.json)

		then:
		offerResponse.status?.statusCode?.equals(200)
		offerResponse.json.offerDiscounts.size() == 1
		offerResponse.json.offerDiscounts[0].amount == 2.0
		offerResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		!offerResponse.json.offerDiscounts[0].plu
		!offerResponse.json.offerDiscounts[0].reasonCode
		!offerResponse.json.offerDiscounts[0].reason

		offerResponse.json.subtotal == itemPrice * 3 -2
		offerResponse.json.total == offerResponse.json.subtotal?.toBigDecimal() + offerResponse.json.tax?.toBigDecimal()


		when: "order total is checked"
		def orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)
		orderTotalResponse.json.offerDiscounts.size() == 1
		orderTotalResponse.json.offerDiscounts[0].amount == 2.0
		orderTotalResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		orderTotalResponse.json.offerDiscounts[0].plu.toString() == "null"
		!orderTotalResponse.json.offerDiscounts[0].plu
		!orderTotalResponse.json.offerDiscounts[0].reasonCode
		!orderTotalResponse.json.offerDiscounts[0].reason

		orderTotalResponse.json.subtotal == itemPrice * 3 -2

		orderTotalResponse.json.total == orderTotalResponse.json.subtotal?.toBigDecimal() + orderTotalResponse.json.tax?.toBigDecimal()
		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'

		when: "confirm that order total is idempotent"
		orderTotalResponse = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse
		orderTotalResponse != null
		orderTotalResponse.status?.statusCode?.equals(200)
		orderTotalResponse.json.offerDiscounts.size() == 1
		orderTotalResponse.json.offerDiscounts[0].amount == 2.0
		orderTotalResponse.json.offerDiscounts[0].redemptionCode == redemptionCode
		orderTotalResponse.json.offerDiscounts[0].plu.toString() == "null"
		!orderTotalResponse.json.offerDiscounts[0].plu
		!orderTotalResponse.json.offerDiscounts[0].reasonCode
		!orderTotalResponse.json.offerDiscounts[0].reason

		orderTotalResponse.json.subtotal == itemPrice * 3 -2

		orderTotalResponse.json.total == orderTotalResponse.json.subtotal?.toBigDecimal() + orderTotalResponse.json.tax?.toBigDecimal()
		orderTotalResponse.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse.json.status == 'Success'


		when:
		def notEnoughItemsResponse = orderManagementService.removeItemFromOrder(oAuthToken, addItemResponse.json, addItemResponse.json.orderResponse.orderItems.first().orderItemId)
		def notEnoughItemsResponse2 = orderManagementService.removeItemFromOrder(oAuthToken, addItemResponse3.json, addItemResponse3.json.orderResponse.orderItems[1].orderItemId)

		then:
		notEnoughItemsResponse.status?.statusCode.equals(200)
		notEnoughItemsResponse2.status?.statusCode.equals(200)

		when: "return to order total so ClearPromoCodes will be called with Tillster"
		def orderTotalResponse2 = orderManagementService.orderTotal(oAuthToken, offerResponse.json.orderId)

		then:
		orderTotalResponse2.status?.statusCode.equals(200)

		orderTotalResponse2.json.offerDiscounts.size() == 1
		orderTotalResponse2.json.offerDiscounts[0].amount == 0.0
		orderTotalResponse2.json.offerDiscounts[0].redemptionCode == redemptionCode
		!orderTotalResponse2.json.offerDiscounts[0].plu
		orderTotalResponse2.json.offerDiscounts[0].reasonCode
		orderTotalResponse2.json.offerDiscounts[0].reason

		orderTotalResponse2.json.subtotal == itemPrice

		orderTotalResponse2.json.total == orderTotalResponse2.json.subtotal?.toBigDecimal() + orderTotalResponse2.json.tax?.toBigDecimal()
		orderTotalResponse2.json.orderState == 'ValidateOrderAtPos'
		orderTotalResponse2.json.status == 'Success'

	}

}