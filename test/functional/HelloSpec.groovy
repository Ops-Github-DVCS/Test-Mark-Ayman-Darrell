import spock.lang.Specification

class HelloSpec extends Specification {

    def "hello world"(){
        setup:
        def mathResult = 0

        when:
        mathResult = 1+1

        then:
        mathResult == 2
    }
}
