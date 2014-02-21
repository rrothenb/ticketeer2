import java.text.NumberFormat;

class Reservation implements Comparable {
    Customer customer
    Performance performance
    String notes
    Boolean paid
    String toString() {
        Performance thePerformance = getPerformance()
        if (getTotalSeats() == 0) {
            if (thePerformance) {
                return "$customer for $thePerformance"
            }
            else {
                return "$customer for no performance"
            }
        }
        else {
            String response = "$customer - " + getCost() + " ("
            seats.each {
                if (it.price) {
                    response += it.quantity + " @ "
                    response += NumberFormat.getCurrencyInstance().format(it.price.cost - it.discount)
                    response += ", "
                }
            }
            response = response.substring(0,response.length()-2) + ")"

            if (thePerformance) {
                response = response + " for $thePerformance"
            }
            else {
                response = response + " for no performance"
            }

            return response
        }
    }

    Performance getPerformance() {
        // TODO - This is a total kludge to address the problem that performances and reservations can be easily deleted and cause a referential integrity problem
        try {
            log.debug("performance = ${performance}")
            return performance
        }
        catch (Exception e) {
            log.warn("Exception accessing performance - probably deleted - $e")
            return null
        }
    }
    
    static toStringFields = ['performance','customer']
    String toStringForCustomer() {
        if (getTotalSeats() == 0) {
            return "$performance"
        }
        else {
            String response = "$performance - " + getCost() + " ("
            seats.each {
                response += it.quantity + " @ "
                response += NumberFormat.getCurrencyInstance().format(it.price.cost - it.discount)
                response += ", "
            }
            response = response.substring(0,response.length()-2) + ")"

            return response
        }
    }
    String getCost() {
        if (paid == true) {
            return "PAID"
        }
        return NumberFormat.getCurrencyInstance().format(calcCost());
    }

    float calcCost() {
        float result = 0.0
        seats.each {
            if (it.price) {
                result += (it.price?.cost - it.discount)*it.quantity;
            }
        }
        return result
    }

    int getTotalSeats() {
        int result = 0
        seats.each {
                result += it.quantity;
        }
        return result
    }
    int compareTo(reservation) {
        int result = customer.lastName.compareTo(reservation.customer.lastName)
        if (result != 0) {
            return result;
        }
        result = customer.firstName.compareTo(reservation.customer.firstName)
        if (result != 0) {
            return result;
        }
        return id.compareTo(reservation.id)
    }
    static int countLimitedByTimeRange(String[] when) {
        if (when[0] == 'Future') {
            return Performance.findAllByTimeGreaterThan( new Date() ).size()
        }
        else if (when[0] == 'Past') {
            return Performance.findAllByTimeLessThan( new Date() ).size()
        }
        else {
            return Performance.count()
        }
    }
    static hasMany = [ seats : ReservationItem ]
    static transients = ['cost','totalSeats']
    static dependencies = [['cost','totalSeats']:['seats','paid']]
    static constraints = {
        customer()
        performance(futureOnly:true,nullable:true)
        notes(nullable:true)
        totalSeats()
        cost()
        paid(nullable:true,secondary:true)
        seats(minSize:1)
    }
    static mapping = {
        seats lazy:false, cascade:"all evict"
    }

    ReservationItem newReservationItem() {
        ReservationItem item = new ReservationItem()
        item.price = Price.findDefault(performance.show)
        item.reservation = this
        log.info "price - ${item.price}"
        return item
    }
}