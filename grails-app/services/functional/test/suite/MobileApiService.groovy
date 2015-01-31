package functional.test.suite

import grails.converters.JSON
import grails.transaction.Transactional
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
        println(fullPath)

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

//        println "Making ${operation} request to '${fullPath}' with params '${jsonObj}'"
        try {
            def response = client.execute(method)
            def status = response.statusLine
            def entity = response?.entity
//            println "Received status ${status}"
            def inputStream = entity?.content
            def responseString = inputStream ? (IOUtils.toString(inputStream, "UTF-8") ?: "{}") : null
//            println "Received response ${responseString}"
            if(entity){
                EntityUtils.consume(entity)
            }
            def responseHeaders = [:]
            response.getAllHeaders().each { responseHeaders.put(it.name, it.value) }
            def responseContentType = responseHeaders ? responseHeaders."Content-Type" : ""
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
            println "Could not finish HTTP request.  Error below"
            println e.message
            e.printStackTrace()
            return [:]
        } finally {
            client.close()
        }
    }
}
