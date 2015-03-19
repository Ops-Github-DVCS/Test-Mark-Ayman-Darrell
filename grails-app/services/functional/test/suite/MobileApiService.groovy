package functional.test.suite

import com.cardfree.sdk.client.OauthClient
import grails.util.Holders

class MobileApiService extends OauthClient{

	MobileApiService() {
		init(Holders.config)
	}

	Map basicAuthHeader(String username, password) {
		def base = "${username}:${password}"
		def base64 = base.bytes.encodeBase64()
		[Authorization: "Basic ${base64}"]
	}

    def executeSVCRequest(String operation, String path, def jsonObj = null) {
        String username = getConfigurationPath("svc", "username")
        String password = getConfigurationPath("svc", "password")
        executeRestRequest(operation, path, getConfigurationPath("svc", "url"), jsonObj,
                basicAuthHeader(username, password))
    }

}
