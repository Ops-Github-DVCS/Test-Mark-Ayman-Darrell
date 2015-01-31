package com.cardfree.functionaltest.helpers

class TestOutputHelper {

    static String getPrintServiceCallSpacer(){
        return "   "
    }

    static String getPrintRestCallSpacer(){
        return getPrintServiceCallSpacer() + "   "
    }

    static def printServiceCall(String apiService){
        println(getPrintServiceCallSpacer() + apiService)
    }

    static def printRestCall(String outputString){
        println("${getPrintRestCallSpacer()}${outputString}")
    }

    static def printRestCallIndent(String outputString){
        printRestCall("${getPrintRestCallSpacer()}${outputString}")
    }
}
