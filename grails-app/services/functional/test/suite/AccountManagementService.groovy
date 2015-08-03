package functional.test.suite

import com.cardfree.sdk.client.AccountManagement
import grails.util.Holders

class AccountManagementService extends AccountManagement{

	AccountManagementService() {
		init(Holders.config)
	}

    private String fetchRandomUserNameAndEmail() {
        def randomEmailSupplement = UUID.randomUUID().toString()
        def userNameAndEmail = "${config.userInformation.emailPrefix}+${randomEmailSupplement.split("-")[0]}${config.userInformation.emailSuffix}"
        userNameAndEmail
    }

    def provisionNewRandomUser(){
 	        return provisionNewUser(fetchRandomUserNameAndEmail(), config.userInformation.password, getDefaultUserDetailInformation())
    }

	def getUser(String oAuthToken) {
		def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
		executeRestRequest("get", "users/me", getAccountManagementRequestEndpoint(), null, header, "application/json")
	}

    def updateUserLoyaltyId(String oAuthToken, String loyaltyId, Boolean loyaltyOptedIn) {
        def data = [
                loyaltyId: loyaltyId,
                loyaltyOptedIn: loyaltyOptedIn
        ]
        def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
        executeRestRequest("put", "users/me/loyalty-id", getAccountManagementRequestEndpoint(), data, header, "application/json")
    }

    def postOrderLoyaltyEvent(String oAuthToken, String orderId) {
        def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
        executeRestRequest("post", "users/me/orders/${config.orderInformation.storeNumber}-$orderId/loyalty-events", getAccountManagementRequestEndpoint(), [:], header, "application/json")

    }

    def getUserAddress(String oAuthToken, int userAddressId) {
        def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
        executeRestRequest("get", "users/me/address/${userAddressId}", getAccountManagementRequestEndpoint(), null, header, "application/json")
    }

    def createUserAddress(String oAuthToken, data) {
        def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
        executeRestRequest("post", "users/me/address", getAccountManagementRequestEndpoint(), data, header, "application/json")
    }

    def updateUserAddress(String oAuthToken, userAddressId, data) {
        def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
        executeRestRequest("put", "users/me/address/${userAddressId}", getAccountManagementRequestEndpoint(), data, header, "application/json")
    }

    public Map getUserAddressInformation1(){
        def data = [
                addressLine1          : '2082 Golden Horse Arbor',
                addressLine2          : '',
                city: 'Hobby',
                state: 'Indiana',
                postalCode: '47845-7214'
        ]
        return data
    }

    public Map getUserAddressInformation2(){
        def data = [
                addressLine1          : '1605 Velvet Boulevard',
                addressLine2          : '',
                city: 'Galaxy',
                state: 'Kentucky',
                postalCode: '40583-6824'
        ]
        return data
    }

    private Map getDefaultUserDetailInformation(){
        def data = [
                firstName             : config.userInformation.firstName,
                lastName              : config.userInformation.lastName,
                versionOfTermsAccepted: config.userInformation.versionOfTerms,
                device                : [
                        deviceIdentifier      : config.userInformation.deviceIdentifier,
                        model                 : config.userInformation.model,
                        operatingSystem       : config.userInformation.operatingSystem,
                        operatingSystemVersion: config.userInformation.operatingSystemVersion
                ],
				zip: '80202',
				birthdayInfo: [month: 2, day: 15, year: 1980],
                loyaltyId: UUID.randomUUID().toString(),
                loyaltyOptedIn: Boolean.TRUE
        ]
        return data
    }

    def static validateNewUser(def userResult){
        assert userResult?.status?.statusCode == 201
        assert userResult?.json?.email?.contains("@")
        assert !userResult?.json?.name?.isEmpty()
        return true
    }
}
