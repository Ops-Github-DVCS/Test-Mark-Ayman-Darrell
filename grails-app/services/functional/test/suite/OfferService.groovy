package functional.test.suite

class OfferService  extends MobileApiService{

	def getOffers(def oAuthToken, String localTime = "2015-03-01T12:00:00") {
		executeMapiUserRequest("get", "offers?localTime=$localTime", null, oAuthToken)
	}
}
