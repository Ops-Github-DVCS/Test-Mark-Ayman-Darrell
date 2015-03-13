package functional.test.suite

import com.cardfree.functionaltest.helpers.TestOutputHelper
import grails.converters.JSON
import grails.util.Holders
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

class MobileApiService {
    static def config = Holders.config

    private def setupFormData(def operation, def json, def method) {
        if(operation == "delete" || operation == "get") return

        def entity = new StringEntity((json as JSON).toString())
        method.setEntity(entity)
    }

    def getNonRegisteredOauthToken() {
        TestOutputHelper.printServiceCall("Get unregistered oAuth Token")
        def data = [
                grantType: "client_credentials",
                client   : [
                        id: getOAuthId(),
                        secret: getOAuthSecret()
                ]
        ]
        def oAuthResult = executeRestRequest("post", "oauth", getOAuthEndpoint(), data)
        if(!oAuthResult?.text?.isEmpty()){
            return oAuthResult.text
        }
        throw Exception("Could not provision non registered oAuth Token with id: ${getOAuthId()} and secret: ${getOAuthSecret()} for endpoint ${getOAuthEndpoint()}/oauth")
    }

    def getRegisteredUserToken(userName, password){
        TestOutputHelper.printServiceCall("Get registered user oAuth Token")
        def unregisteredToken = getNonRegisteredOauthToken()
        def data = [
                grantType: "password_grant",
                client   : [
                        accessToken: unregisteredToken
                ],
                user     : [
                        userName: userName,
                        password: password
                ]
        ]
        def oAuthResult = executeRestRequest("post", "oauth", getOAuthEndpoint(), data)
        if(!oAuthResult?.json?.accessToken?.isEmpty()){
            return oAuthResult.json.accessToken
        } else {
            throw new Exception("Could not generate oAuth token for a user with these credentials.")
        }
    }

    def getConfigurationPath(String path1, String path2) {
        def merchantValue = config.testConfigurations."${config.testExecution.endpoint}"."$path1"."$path2"."${config.testExecution.merchant}"
        if (!merchantValue.isEmpty())
            return merchantValue

        def defaultValue = config.testConfigurations."${config.testExecution.endpoint}"."$path1"."$path2"."defaultVal"
        return defaultValue
    }

    def getAccountManagementRequestEndpoint(){
        getConfigurationPath("api", "address") + getConfigurationPath("api", "account_management_application")
    }

    def getOrderManagementRequestEndpoint(){
        getConfigurationPath("api", "address") + getConfigurationPath("api", "order_management_application")
    }

    def getOAuthEndpoint(){
        getConfigurationPath("oauth", "url")
    }

    def getOAuthId(){
        getConfigurationPath("oauth", "id")
    }

    def getOAuthSecret(){
        getConfigurationPath("oauth", "secret")
    }

	Map basicAuthHeader(String username, password) {
		def base = "${username}:${password}"
		def base64 = base.bytes.encodeBase64()
		[Authorization: "Basic ${base64}"]
	}

	def executeMapiUserRequest(def operation, def path, def data, def token, def guest = false, def deviceIdentifier = null){
        if(!token){
            token = getNonRegisteredOauthToken()
        }
        def header = [Authorization: "bearer ${token}", Accept: "application/json"]
        if(guest && deviceIdentifier){
            header = [Authorization: "bearer ${token}", Accept: "application/json", "Device-Identifier": deviceIdentifier]
        }
        if(guest){
            executeRestRequest(operation, "" + path, getOrderManagementRequestEndpoint(), data, header, "application/json")
        } else {
            executeRestRequest(operation, "users/me/" + path, getAccountManagementRequestEndpoint(), data, header, "application/json")
        }
    }

    def executeMapiUserCreationRequest(def userData){
        def header = [Authorization: "bearer ${getNonRegisteredOauthToken()}", Accept: "application/json"]
        executeRestRequest("post", "users", getAccountManagementRequestEndpoint(), userData, header, "application/vnd.cardfree.users+json; account-creation-type=cardfree")
    }

    def executeSVCRequest(String operation, String path, def jsonObj = null) {
        String username = getConfigurationPath("svc", "username")
        String password = getConfigurationPath("svc", "password")
        executeRestRequest(operation, path, getConfigurationPath("svc", "url"), jsonObj,
                basicAuthHeader(username, password))
    }

    def executeRestRequest(String operation, String path, String root,
                           def jsonObj = null, Map<String, String> headers = [:], String contentType = "application/json") {
        def client = HttpClients.createDefault()

        def pathWithQuery = {
            if((jsonObj != null) && (operation=="get" || operation=="delete")){
                path + "?" + jsonObj.collect { k,v -> "$k=${URLEncoder.encode(v.toString())}" }.join('&')
            } else {
                path
            }
        }()

        def fullPath = root + pathWithQuery
        TestOutputHelper.printRestCall("Request:")
        TestOutputHelper.printRestCallIndent("[${operation.toUpperCase()}] -- ${fullPath}")

        if(jsonObj){
            TestOutputHelper.printRestCallIndent((jsonObj as JSON).toString(config.testExecution.prettyPrintJSON))
        }

        def method = {
            if(operation.toLowerCase()=="get") new HttpGet(fullPath)
            else if(operation.toLowerCase()=="delete") new HttpDelete(fullPath)
            else if(operation.toLowerCase()=="post") new HttpPost(fullPath)
            else if(operation.toLowerCase()=="put") new HttpPut(fullPath)
        }()

        setupFormData(operation, jsonObj, method)

        headers.each { k, v ->
            method.addHeader(k, v)
        }
        method.addHeader("Content-Type", contentType)

        try {
            def response = client.execute(method)
            def status = response.statusLine
            def entity = response?.entity
            def inputStream = entity?.content
            def responseString = inputStream ? (IOUtils.toString(inputStream, "UTF-8") ?: "{}") : null
            if(entity){
                EntityUtils.consume(entity)
            }
            def responseHeaders = [:]
            response.getAllHeaders().each { responseHeaders.put(it.name, it.value) }
            def responseContentType = responseHeaders ? responseHeaders."Content-Type" : ""
            TestOutputHelper.printRestCall("Response: ")
            TestOutputHelper.printRestCallIndent(status)
            if(!responseString?.isEmpty()){
                TestOutputHelper.printRestCallIndent(responseString && responseString?.contains("{") ? (JSON.parse(responseString) as JSON).toString(config.testExecution.prettyPrintJSON) : responseString)
            }
            return [
                    status: status,
                    text: responseString,
                    json: responseString ? JSON.parse(responseString) : "",
                    requestHeaders: headers,
                    requestPayload: jsonObj,
                    requestPath: fullPath,
                    requestOperation: operation,
                    contentType: responseContentType,
                    responseHeaders: responseHeaders
            ]
        } catch(Exception e) {
            TestOutputHelper.printRestCall("Could not finish HTTP request.  Error below")
            println e.message
            e.printStackTrace()
            return [:]
        } finally {
            client.close()
        }
    }
}
