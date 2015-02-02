testExecution {
    merchant = "tacobell"
    endpoint = "test_dev"
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
    plu = "1003"
}

giftCardInformation{
    physicalCardNumberVisa = "4293962000470350"
    physicalCardPinVisa = "123"
    physicalCardNumberFD = "7777080793467994"
    physicalCardPinFD = "82688235"
}

testConfigurations{
    test_local {
        oauth {
            url {
                defaultVal = "https://dev01-app.cardfree.net/"
                tacobell = "https://dev01-app.cardfree.net/"
                checkers = "https://dev-mon-api01.cardfree.net/"
            }
            id {
                tacobell = "e28833aa-64df-4541-bd16-67d80d55b558"
                checkers = "D93B5A92DA0D11E3BF4B005056B046D7"
            }
            secret {
                tacobell = "03d91f77-973e-4d11-a3cb-ef860e12994e"
                checkers = "D9556C1DDA0D11E3BF4B005056B046D7"
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
                defaultVal = "http://192.168.82.128/"
                tacobell = "http://192.168.82.128/"
            }
            account_management_application {
                defaultVal = "vsvm/"
                tacobell = "vsvm/"
            }
            order_management_application {
                defaultVal = "vsvm_om/"
                tacobell = "vsvm_om/"
            }
        }
    }
    test_dev {
        oauth {
            url {
                defaultVal = "https://dev01-app.cardfree.net/"
                tacobell = "https://dev01-app.cardfree.net/"
                checkers = "https://dev-mon-api01.cardfree.net/"
            }
            id {
                tacobell = "e28833aa-64df-4541-bd16-67d80d55b558"
                checkers = "D93B5A92DA0D11E3BF4B005056B046D7"
            }
            secret {
                tacobell = "03d91f77-973e-4d11-a3cb-ef860e12994e"
                checkers = "D9556C1DDA0D11E3BF4B005056B046D7"
            }
        }
        api {
            address {
                defaultVal = "http://dev01-app.cardfree.net"
                tacobell = "http://dev01-app.cardfree.net/"
            }
            account_management_application {
                defaultVal = "account-management/v1/"
                tacobell = "account-management/v1/"
            }
            order_management_application {
                defaultVal = "order-management/v1/"
                tacobell = "order-management/v1/"
            }
        }
        api {
            address {
                defaultVal = "https://dev01-app.cardfree.net/"
                tacobell = "https://dev01-app.cardfree.net/"
            }
            account_management_application {
                defaultVal = "account-management/v1/"
                tacobell = "account-management/v1/"
            }
            order_management_application {
                defaultVal = "vsvm_om/"
                tacobell = "vsvm_om/"
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
                defaultVal = "https://uat-app.cardfree.net/"
                tacobell = "https://uat-app.cardfree.net/"
            }
            id {
                tacobell = "1699ecd1-88c5-4115-ad95-e5d26899d83b"
            }
            secret {
                tacobell = "03d91f77-973e-4d11-a3cb-ef860e12994e"
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
