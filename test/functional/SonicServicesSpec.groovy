import com.cardfree.functionaltests.specbase.SonicFunctionalSpecBase

class SonicServicesSpec extends SonicFunctionalSpecBase {

    def "login verified sonic user"(){
        //Login User
        when:
        def userToken = accountManagementService.getRegisteredUserToken(mobileApiService.getValidatedUserEmail(), mobileApiService.getValidatedUserPassword())

        then:
        !userToken.isEmpty()

    }
}
