import com.cardfree.functionaltests.specbase.SonicFunctionalSpecBase

class SonicServicesSpec extends SonicFunctionalSpecBase {

    def "login verified sonic user"(){
        //Login User
        when:
        def userToken = mobileApiService.getRegisteredUserToken(mobileApiService.getValidatedUserEmail(), mobileApiService.getValidatedUserPassword())

        then:
        userToken != null
        !userToken.isEmpty()
    }

    def "get registration promotion for existing user"(){
        //Login User
        when:
        def userToken = mobileApiService.getRegisteredUserToken(mobileApiService.getValidatedUserEmail(), mobileApiService.getValidatedUserPassword())

        then:
        userToken != null
        !userToken.isEmpty()

        //Get Registration promotion
        when:
        def registrationPromoResult = mobileApiService.executeMapiUserRequest("get", "registration-promo", null, userToken)

        then:
        registrationPromoResult != null
    }
}
