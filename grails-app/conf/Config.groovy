testExecution {
    merchant = "sonic"
    endpoint = "test_local"
    prettyPrintJSON = false
}




// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}

grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

userInformation{
    emailPrefix = "johnnyfutah"
    emailSuffix = "555@gmail.com"
    firstName = "Jim"
    lastName = "Test"
    password = "Password1"
    versionOfTerms = "1.1"
    deviceIdentifier = "my-made-up-ip"
    model = "iPhone"
    operatingSystem = "iOS"
    operatingSystemVersion = "6.0"
    currencyCode = "USD"
}

orderInformation{
    storeNumber = "E720329"
    //storeNumber = "027505"
    plu = "1003"
}

giftCardInformation{
    physicalCardNumberVisa = "4293960100423188"
    physicalCardPinVisa = "123"
    physicalCardNumberFD = "7777080793457313"
    physicalCardPinFD = "43170091"
}

testConfigurations{
    test_local {
        oauth {
            url {
                tacobell = "https://dev01-app.cardfree.net/"
                dunkin = "https://dev-dun-api01.cardfree.net/"
                checkers = "https://dev-mon-api01.cardfree.net/"
                sonic = "https://dev01-app.cardfree.net/"
            }
            id {
                tacobell = "e28833aa-64df-4541-bd16-67d80d55b558"
                dunkin = "B95D6A3EA972448BBB15C23DD0D20C25"
                checkers = "D93B5A92DA0D11E3BF4B005056B046D7"
                sonic = "e28833aa-64df-4541-bd16-67d80d55b558"
            }
            secret {
                tacobell = "03d91f77-973e-4d11-a3cb-ef860e12994e"
                dunkin = "670166E094F34196928A87E893B39FC1"
                checkers = "D9556C1DDA0D11E3BF4B005056B046D7"
                sonic = "03d91f77-973e-4d11-a3cb-ef860e12994e"
            }
            token {
                sonic = "4915C90C8F2343CE82E212A917C1768A"
            }
        }
        svc {
            url {
                defaultVal = "http://localhost:8080/CardfreeSVC/v1/"
            }
            username {
                defaultVal = "resttest"
            }
            password {
                defaultVal = "0RAP%y@73qa4*IHD"
            }
        }
        api {
            address {
                tacobell = "http://192.168.82.128/"
                dunkin = "http://192.168.82.128/"
                sonic = "http://192.168.82.128/"
            }
            account_management_application {
                tacobell = "vsvm/"
                dunkin = "vsvm/"
                sonic = "vsvm_sonic/"
            }
            order_management_application {
                tacobell = "vsvm_order/"
                dunkin = "vsvm_order/"
            }
        }
        verified_user {
            id {
                sonic = "c6ab4622-eda6-449d-835d-b331214a07cb"
            }
            userName {
                sonic = "johnnyfutah+sonic_2_20_15@gmail.com"
            }
            email {
                sonic = "johnnyfutah+sonic_2_20_15@gmail.com"
            }
            password {
                sonic = "Password1"
            }
        }
    }
    test_local_uat {
        oauth {
            url {
                tacobell = "https://uat01-app.cardfree.net/"
            }
            id {
                tacobell = "1699ecd1-88c5-4115-ad95-e5d26899d83b"
            }
            secret {
                tacobell = "5e7abeda-27bb-49eb-b58e-edd456b4c477"
            }
        }
        svc {
            url {
                defaultVal = "http://localhost:8080/CardfreeSVC/v1/"
            }
            username {
                defaultVal = "resttest"
            }
            password {
                defaultVal = "0RAP%y@73qa4*IHD"
            }
        }
        api {
            address {
                tacobell = "http://192.168.82.128/"
            }
            account_management_application {
                tacobell = "vsvm/"
            }
            order_management_application {
                tacobell = "vsvm_order/"
            }
        }
    }
    test_dev {
        oauth {
            url {
                tacobell = "https://dev01-app.cardfree.net/"
                dunkin = "https://dev-dun-api01.cardfree.net/"
                checkers = "https://dev-mon-api01.cardfree.net/"
            }
            id {
                tacobell = "e28833aa-64df-4541-bd16-67d80d55b558"
                dunkin = "B95D6A3EA972448BBB15C23DD0D20C25"
                checkers = "D93B5A92DA0D11E3BF4B005056B046D7"
            }
            secret {
                tacobell = "03d91f77-973e-4d11-a3cb-ef860e12994e"
                dunkin = "670166E094F34196928A87E893B39FC1"
                checkers = "D9556C1DDA0D11E3BF4B005056B046D7"
            }
        }
        api {
            address {
                tacobell = "https://dev01-app.cardfree.net/"
                dunkin = "https://dev-dun-api01.cardfree.net/"
            }
            account_management_application {
                tacobell = "account-management/v1/"
                dunkin = "account-management/v1/"
            }
            order_management_application {
                tacobell = "order-management/v1/"
                dunkin = "order-management/v1/"
            }
        }
        svc {
            url {
                defaultVal = "http://dev-cfr-app01.cardfree.net/CardfreeSVC/v1"
            }
            username {
                defaultVal = "resttest"
            }
            password {
                defaultVal = "0RAP%y@73qa4*IHD"
            }
        }
    }
    test_uat {
        oauth {
            url {
                tacobell = "https://uat01-app.cardfree.net/"
                checkers = "https://dev-mon-api01.cardfree.net/"
            }
            id {
                tacobell = "1699ecd1-88c5-4115-ad95-e5d26899d83b"
                checkers = "D93B5A92DA0D11E3BF4B005056B046D7"
            }
            secret {
                tacobell = "5e7abeda-27bb-49eb-b58e-edd456b4c477"
                checkers = "D9556C1DDA0D11E3BF4B005056B046D7"
            }
        }
        api {
            address {
                tacobell = "https://uat01-app.cardfree.net/"
            }
            account_management_application {
                tacobell = "account-management/v1/"
            }
            order_management_application {
                tacobell = "order-management/v1/"
            }
        }
        svc {
            url {
                defaultVal = "http://dev-cfr-app01.cardfree.net/CardfreeSVC/v1"
            }
            username {
                defaultVal = "resttest"
            }
            password {
                defaultVal = "0RAP%y@73qa4*IHD"
            }
        }
    }
}

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j.main = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
