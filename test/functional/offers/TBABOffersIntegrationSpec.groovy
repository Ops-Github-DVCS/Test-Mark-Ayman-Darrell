package offers

import com.cardfree.functionaltests.specbase.FunctionalSpecBase
import spock.lang.Ignore
import spock.lang.IgnoreRest

import java.math.MathContext
import java.math.RoundingMode

class TBABOffersIntegrationSpec extends FunctionalSpecBase {

    private String STORE_ID = config.orderInformation.storeNumber
    private boolean STORE_TAXES_DISCOUNT = config.orderInformation.storeTaxesDiscount
    private String MOVE_TO_STOREID = config.orderInformation.moveToStoreNumber

    private String BURRITO_PLU = '22449'
    private String TACO_PLU = '22100'
    private String QUESADILLA_COMBO = '22607'

    private String TWO_BUCKS_OFF = '009250'
    private String FREE_TACO_WITH_PURCHASE = '009235'
    private String TWENTY_PERCENT_OFF = '009251'


    def oAuthToken
    def me
    String userNameAndEmail

    def register() {
        // todo replace with accountManagementService.fetchRandomUserNameAndEmail
        userNameAndEmail = accountManagementService.fetchRandomUserNameAndEmail() // offerService.getNewUserName()
        me = accountManagementService.provisionNewUser(userNameAndEmail, config.userInformation.password, accountManagementService.getDefaultUserDetailInformation())
        oAuthToken = accountManagementService.getRegisteredUserToken(userNameAndEmail, config.userInformation.password)
        [
                userNameAndEmail: userNameAndEmail,
                me: me,
                oAuthToken: oAuthToken
        ]
    }

    @IgnoreRest
    def "encrypted offer"() {
        when: "create a user"
        def userInfo = register()
//        def userToken = accountManagementService.getRegisteredUserToken(userInfo.oAuthToken, config.userInformation.password)

        then:
            def result = accountManagementService.deliverOffer(userInfo.oAuthToken, "T88kXfmma5hydpt4oD5OgDNLrU9YvAtA5NBe2aselG/wxXUFYIVo+HJF9g+BfTmPZjNwrSWIGEwsBVL9r2arJLhr5Z7zYbdXusYdZklxhdhN9/GwnNahwY9bWz/lYkEJc4yT+o1wcHIShm5pOYrhZpawq0XPbT3NREhIsWm7yQP7wacEU6YdvlXXpwLSj0ZUliNuhSHldKsefs3F78bxmQ6gp1sgtvMUEfhaQPaXw+3zaYQ7bIg24tH+6k2F/p7+KMY5KCYCdVPRGb5HQ0syAD5iXIJl4TZeb2LO3JPJztpbMmrZWXtK5hbavLZJzg2dnlUdJL+4vhBbbnaWexBRHg==")
            def availableOffers = offerService.getOffers(userInfo.oAuthToken, "2015-07-13T12:00:01")
        result


//            def offers = svcRestCall("get", "/centralAccounts/$userInfo.me.json.masterAccountId/promotionPurses")

    }

    def "Register some users and get some available offers"() {
        when: "create a user"
            def userInfo = register()

            def offers = svcRestCall("get", "/centralAccounts/$userInfo.me.json.masterAccountId/promotionPurses")
//            def availableOffers = offerService.getOffers(userInfo.oAuthToken, "2015-03-07T12:00:01")

        then: ""
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

}
