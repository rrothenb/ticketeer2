import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class Sale implements Comparable {
    Date timeAndDate
    Customer customer
    Register register
    String soldBy

    float getTotal() {
        float total = 0.0
        reservations?.each { item ->
            total = total + item.total
        }
        seats?.each { item ->
            total = total + item.total
        }
        merchandise?.each { item ->
            total = total + item.total
        }
        return total
    }

    float getChange() {
        if (transactions?.size() > 0) {
            for (transaction in transactions) {
                Float owed = getTotal()
                if (transaction.type == 'Cash' && transaction.amount > owed) {
                    return transaction.amount - owed
                }
            }
        }
        return 0
    }

    static hasMany = [ transactions : Transaction, reservations : ReservationSale, seats : SeatSale, merchandise : MerchandiseSale ]

    static transients = [ 'total', 'change' ]
    
    static dependencies = [['total','change']:['sales','reservations','merchandise','transactions']]

    /*
    def beforeInsert() {
        timeAndDate = new Date()
        println "Just set timeAndDate to $timeAndDate"
        try {
            GrailsWebRequest request = RequestContextHolder.currentRequestAttributes()
            println "user - ${request.currentRequest.remoteUser}"
            soldBy = request.currentRequest.remoteUser
        }
        catch (Exception e) {
            println "Exception trying to get remote user: $e"
        }
    }
    */

    static constraints = {
        timeAndDate(editable:false,nullable:true)
        customer(nullable:true)
        register(nullable:true)
        total(format:"\\\$0.00")
        change(format:"\\\$0.00",secondary:true)
        soldBy(editable:false,nullable:true)
        reservations()
        seats()
        merchandise()
        transactions()
    }

    String toString() {
        if (timeAndDate) {
            return timeAndDate.format("EEE, M/d/yyyy 'at' h:mm aa")
        }
        return ""
    }

    int compareTo(sale) {
        return timeAndDate.compareTo(sale.timeAndDate)
    }

    Transaction newTransaction() {
        Transaction transaction = new Transaction()
        transaction.register = register
        transaction.amount = total
        return transaction
    }

    SeatSale newSeatSale() {
        SeatSale seatSale = new SeatSale()
        seatSale.price = Theater.instance.defaultPrice
        return seatSale
    }

    Sale() {
        register = Theater.instance?.defaultRegister
    }

}
