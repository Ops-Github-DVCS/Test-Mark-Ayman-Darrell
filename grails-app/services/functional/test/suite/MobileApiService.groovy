package functional.test.suite

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
        printServiceCall("Get unregistered oAuth Token")
        def data = [
                grantType: "client_credentials",
                client   : [
                        id: getOAuthId(),
                        secret: getOAuthSecret()
                ]
        ]
        def oAuthResult = executeMapiRestRequest("post", "oauth", getOAuthEndpoint(), data)
        if(!oAuthResult?.text?.isEmpty()){
            return oAuthResult.text
        }
        throw Exception("Could not provision non registered oAuth Token with id: ${getOAuthId()} and secret: ${getOAuthSecret()} for endpoint ${getOAuthEndpoint()}/oauth")
    }

    def getOAuthEndpoint(){
        def merchant = getTestMerchant()
        config.oauth.url."$merchant"
    }

    def getOAuthId(){
        def merchant = getTestMerchant()
        config.oauth.id."$merchant"
    }

    def getOAuthSecret(){
        def merchant = getTestMerchant()
        config.oauth.secret."$merchant"
    }

    def getTestMerchant(){
        System.getenv().merchant
    }

    String getPrintServiceCallSpacer(){
        return "   "
    }

    String getPrintRestCallSpacer(){
        return getPrintServiceCallSpacer() + "   "
    }

    def printServiceCall(def apiService){
        println(getPrintServiceCallSpacer() + apiService)
    }

    def printRestCall(def outputString){
        println(getPrintRestCallSpacer() + outputString)
    }

    def executeMapiRestRequest(String operation, path, root, def jsonObj=null, Map<String,String> headers=[:], def contentType="application/json"){
        def client = HttpClients.createDefault()

        def pathWithQuery = {
            if((jsonObj != null) && (operation=="get" || operation=="delete")){
                path + "?" + jsonObj.collect { k,v -> "$k=${URLEncoder.encode(v.toString())}" }.join('&')
            } else {
                path
            }
        }()

        def fullPath = root + pathWithQuery
        printRestCall(fullPath)
        if(jsonObj){
            printRestCall(jsonObj as JSON)
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
            printRestCall(status)
            printRestCall(responseString ? JSON.parse(responseString) : "")
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
            printRestCall("Could not finish HTTP request.  Error below")
            println e.message
            e.printStackTrace()
            return [:]
        } finally {
            client.close()
        }
    }
}
