package com.cardfree.sdk.client
/**
 * Created by gabe on 3/18/15.
 */
class ConfiguredApiClient extends ApiClient {

	ConfigObject config
	ConfiguredApiClient() {}

	void init(ConfigObject config) { // constructor would collide with the groovy autowired Map constructor, thus init method
		this.config = config
		this.prettyPrintJson = config?.testExecution?.prettyPrintJson ?: false
	}

	String getConfigurationPath(String path1, String path2) {
		def merchantValue = config.testConfigurations."${config.testExecution.endpoint}"."$path1"."$path2"."${config.testExecution.merchant}"
		if (!merchantValue.isEmpty())
			return merchantValue

		def defaultValue = config.testConfigurations."${config.testExecution.endpoint}"."$path1"."$path2"."defaultVal"
		return defaultValue
	}

	String getAccountManagementRequestEndpoint(){
		getConfigurationPath("api", "address") + getConfigurationPath("api", "account_management_application")
	}

	String getOrderManagementRequestEndpoint(){
		getConfigurationPath("api", "address") + getConfigurationPath("api", "order_management_application")
	}
}
