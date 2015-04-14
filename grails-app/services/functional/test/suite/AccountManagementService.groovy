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
				birthdayInfo: [month: 2, day: 15, year: 1753]
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
