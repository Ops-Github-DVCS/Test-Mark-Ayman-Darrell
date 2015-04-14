package com.cardfree.sdk.client

import grails.converters.JSON
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

class ApiClient {

	boolean prettyPrintJson = true

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
		log.debug("Request:")
		log.debug("\t"+"[${operation.toUpperCase()}] -- ${fullPath}")

		if(jsonObj){
			log.debug("\t"+(jsonObj as JSON).toString(prettyPrintJson))
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
			log.debug("Response: ")
			log.debug("\t"+status)
			if(!responseString?.isEmpty()){
				log.debug("\t"+responseString && responseString?.contains("{") ? (JSON.parse(responseString) as JSON).toString(prettyPrintJson) : responseString)
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
			log.debug("Could not finish HTTP request.  Error below")
			println e.message
			e.printStackTrace()
			return [:]
		} finally {
			client.close()
		}
	}

	private void setupFormData(String operation, def json, HttpRequestBase method) {
		if(operation == "delete" || operation == "get") return

		def entity = new StringEntity((json as JSON).toString())
		method.setEntity(entity)
	}	
}
