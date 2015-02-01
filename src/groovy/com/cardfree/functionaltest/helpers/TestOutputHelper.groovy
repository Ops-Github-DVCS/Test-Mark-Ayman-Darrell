package com.cardfree.functionaltest.helpers

class TestOutputHelper {

    static String getPrintServiceCallSpacer(){
        return "   "
    }

    static String getPrintRestCallSpacer(){
        return getPrintServiceCallSpacer() + "   "
    }

    static def printServiceCall(def apiService){
        println(getPrintServiceCallSpacer() + apiService)
    }

    static def printRestCall(def outputString){
        println("${getPrintRestCallSpacer()}${outputString}")
    }

    static def printRestCallIndent(def outputString){
        printRestCall("${getPrintRestCallSpacer()}${outputString}")
    }
}
