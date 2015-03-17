package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper

class AccountManagementService extends MobileApiService{

    private String fetchRandomUserNameAndEmail() {
        def randomEmailSupplement = UUID.randomUUID().toString()
        def userNameAndEmail = "${config.userInformation.emailPrefix}${randomEmailSupplement.split("-")[0]}${config.userInformation.emailSuffix}"
        userNameAndEmail
    }

    def provisionNewRandomUser(){
        return provisionNewUser(fetchRandomUserNameAndEmail(), config.userInformation.password)
    }

    def provisionNewUser(userName, password) {
        TestOutputHelper.printServiceCall("Provision New User")
        def data = getDefaultUserDetailInformation()
        data.password = password
        data.userName = userName
        data.email = userName

        return executeMapiUserCreationRequest(data)
    }

	def getUser(String oAuthToken) {
		def header = [Authorization: "bearer ${oAuthToken}", Accept: "application/json"]
		executeRestRequest("get", "users/me", getAccountManagementRequestEndpoint(), null, header, "application/json")
	}


    private def getDefaultUserDetailInformation(){
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
