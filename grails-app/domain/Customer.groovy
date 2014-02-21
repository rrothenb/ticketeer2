import org.apache.commons.logging.LogFactory

class Customer implements Comparable {
    private static def log = LogFactory.getLog(this)
    String phone
    String email
    String lastName
    String firstName
    String address
    Date updated
    Boolean emailList
    static hasMany = [ reservations : Reservation, sales : Sale ]
    String toString() {
        if (!lastName) {
            return firstName
        }
        else if (!firstName) {
            return lastName
        }
        else {
            return "$lastName, $firstName"
        }
    }
    static toStringFields = ['lastName', 'firstName']
    
    static int countLimited(params) {
        if( params.max ) params.max = params.max.toInteger()
        if( params.offset) params.offset = params.offset.toInteger()
        if (params.filter) {
            return Customer.findAllByLastNameIlikeOrFirstNameIlike( params.filter + "%", params.filter + "%").size()
        }
        else {
            return Customer.count()
        }
    }
    static mapping = {
        updated column:'ADDRESS_UPDATED'
    }
    static constraints = {
        lastName(validator:{ value,obj ->
                if (!obj.lastName && !obj.firstName) {
                    return "need.first.and.or.last.name"
                }
        })
        firstName()
        phone(nullable:true)
        email(email:true,nullable:true)
        emailList(nullable:true,description:"Has this user been asked if they'd like to be on our mailing list?")
        address(nullable:true,secondary:true)
        updated(nullable:true, editable:false, secondary:true)
        reservations()
        sales()
    }
    int compareTo(customer) {
        int result = lastName.compareTo(customer.lastName)
        if (result != 0) {
            return result
        }
        return firstName.compareTo(customer.firstName)
    }
}

