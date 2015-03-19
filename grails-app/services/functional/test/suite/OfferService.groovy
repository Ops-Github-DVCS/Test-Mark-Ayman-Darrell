package functional.test.suite

import com.cardfree.sdk.client.Offers
import grails.util.Holders

class OfferService extends Offers{

	OfferService() {
		init(Holders.config)
	}

}
