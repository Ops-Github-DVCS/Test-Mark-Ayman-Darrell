package offers

import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import spock.lang.Specification


/**
 * Created by gabe on 3/15/15.
 */
class GetOffersSpec extends FunctionalSpecBase {

	def "Checkers users get offers"() {
		//Create New User
		when:
		def userResult = accountManagementService.provisionNewRandomUser()

		then:
		AccountManagementService.validateNewUser(userResult)

		//Login User
		when:
		def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

		then:
		!userToken.isEmpty()

		when:
		def offerResult = offerService.getOffers(userToken, "2015-03-07T12:00:01")
		def offers = offerResult.json.data
		ArrayList<String> codes = new ArrayList<String>()
		def numOffers = offers.size()

		then:
		offerResult.status.statusCode == 200
		offers
		offers.each {
			assert it.viewed == false;
			assert it.redemptionCode != null;
			codes.add(it.redemptionCode)
		}
	}

	def "user with 5 points should get free fries automated reward with Checkers"() {
		//Create New User
		when:
		def userResult = accountManagementService.provisionNewRandomUser()

		then:
		AccountManagementService.validateNewUser(userResult)

		//Login User
		when:
		def userToken = accountManagementService.getRegisteredUserToken(userResult.json.email, config.userInformation.password)

		then:
		!userToken.isEmpty()

		when:
		def offerResult = offerService.getLoyaltyRewards(userToken, "2015-03-07T12:00:01")
		def rewards = offerResult.json.data

		then:
		offerResult.status.statusCode == 200
		rewards.size() == 0

		when:
		def addGCResult = giftCardService.provisionGiftCardWithNewCC(75.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

		then:
		addGCResult.status.statusCode == 201

		when:
		offerResult = offerService.getLoyaltyRewards(userToken, "2015-03-07T12:00:02")
		rewards = offerResult.json.data

		then:
		offerResult.status.statusCode == 200
		rewards.size() == 1

	}
}