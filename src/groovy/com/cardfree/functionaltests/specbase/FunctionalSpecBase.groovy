package com.cardfree.functionaltests.specbase

import functional.test.suite.AccountManagementService
import functional.test.suite.CreditCardService
import functional.test.suite.GiftCardService
import functional.test.suite.GiftingService
import functional.test.suite.MobileApiService
import functional.test.suite.OfferService
import functional.test.suite.OrderManagementService
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import grails.util.Holders
import spock.lang.Specification

@TestMixin(ControllerUnitTestMixin)
class FunctionalSpecBase extends Specification{
    static def config = Holders.config

    def mobileApiService = new MobileApiService()
    def accountManagementService = new AccountManagementService()
    def giftCardService = new GiftCardService()
    def orderManagementService = new OrderManagementService(config.orderInformation.storeNumber)
    def giftingService = new GiftingService()
    def creditCardService = new CreditCardService()
	def offerService = new OfferService()
}
