package com.cardfree.functionaltests.specbase

import functional.test.suite.SonicMobileApiService

class SonicFunctionalSpecBase extends FunctionalSpecBase{
    def mobileApiService = new SonicMobileApiService()
}
