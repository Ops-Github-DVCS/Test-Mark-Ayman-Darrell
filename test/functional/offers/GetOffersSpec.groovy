package offers

import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import spock.lang.IgnoreRest
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

	@IgnoreRest
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
		def loyaltyProgramResult1 = offerService.getLoyaltyProgramDetails(userToken)

		then:
		loyaltyProgramResult1 != null

		when:
		def offerResult = offerService.getLoyaltyRewards(userToken, "2015-03-07T12:00:01")
		def rewards = offerResult.json.data

		then:
		offerResult.status.statusCode == 200
		rewards.size() == 0

		/*
		when:
		def giftCardService2 = new GiftCardService(creditCardService)
		def addGCResult = giftCardService2.provisionGiftCardWithNewCC(75.00, false, false, userToken, CreditCardService.CreditCardType.VISA)

		then:
		addGCResult.status.statusCode == 201
		*/

		when:
 		def checkinEventResult = offerService.postLoyaltyCheckinEvent("8fa637d3-9f60-4659-8464-2ff867ae0af9", userResult.json.email, userToken)
		16.times {
			offerService.postLoyaltyCheckinEvent("8fa637d3-9f60-4659-8464-2ff867ae0af9", userResult.json.email, userToken)
		}

		then:
		checkinEventResult != null

		when:
		def loyaltyProgramResult = offerService.getLoyaltyProgramDetails(userToken)

		then:
		loyaltyProgramResult != null

		when:
		offerResult = offerService.getLoyaltyRewards(userToken, "2015-03-07T12:00:02")
		rewards = offerResult.json.data

		then:
		offerResult.status.statusCode == 200
		rewards.size() == 1

	}
}