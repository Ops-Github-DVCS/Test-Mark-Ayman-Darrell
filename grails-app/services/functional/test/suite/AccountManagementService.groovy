package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper
import grails.util.Holders


class AccountManagementService {

    static def config = Holders.config
    def mobileApiService = new MobileApiService()

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

        return mobileApiService.executeMapiUserCreationRequest(data)
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
                ]
        ]
        return data
    }
}
