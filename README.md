# functional-test-suite

. You need a JDK installed, http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html

. http://gvmtool.net to install the Groovy Environment Manager
.. `curl -s get.gvmtool.net | bash`

. Install grails 2.4.4  (or whatever version is listed here https://github.com/Cardfree/functional-test-suite/blob/master/application.properties)
.. `gvm install grails 2.4.4`

. `git clone git@github.com:Cardfree/functional-test-suite.git`

. `cd functional-test-suite`
 
. Run some or all the tests
.. `grails test-app functional: offers.TBOffersIntegrationSpec`
.. `grails test-app`
.. `grails test-app functional: offers.TBOffersIntegrationSpec -echoOut -DtestExecution.merchant=tacobell -DtestExecution.endpoint=test_dev`
