import java.text.SimpleDateFormat
import java.text.NumberFormat
class Performance implements Comparable {
    Date dateAndTime
    SortedSet reservations
    static belongsTo = [show:Show]
    static hasMany = [ reservations : Reservation, sales : SeatSale ]
    int getNumberSeatsSoldAndReserved() {
        return getNumberSeatsSold() + getNumberSeatsReserved()
    }
    int getNumberSeatsSold() {
        int result = 0
        sales.each { sale ->
                result += sale.numberSeats;
        }
        reservations.each { reservation->
            if (reservation.paid) {
                result += reservation.totalSeats
            }
        }
        return result
    }
    int getNumberSeatsReserved() {
        int result = 0
        reservations.each { reservation->
            if (!reservation.paid) {
                result += reservation.totalSeats;
            }
        }
        return result
    }
    float getTotalSales() {
        def totalsByPrice = Reservation.executeQuery("select ri.price.name, ri.price.cost, sum(ri.quantity), sum(ri.quantity * (ri.price.cost - ri.discount)) from Reservation r join r.seats ri where r.performance.id = " + id + " group by ri.price.name, ri.price.cost order by ri.price.cost desc, sum(ri.quantity) desc, ri.price.name");
        float totalPrice = 0.0
        for (priceSummary in totalsByPrice) {
            totalPrice += priceSummary[3]
        }
        return totalPrice
    }
    List getTotalsByPrice() {
        def results = []
        def totalsByPrice = Reservation.executeQuery("select ri.price.name, ri.price.cost, sum(ri.quantity), sum(ri.quantity * (ri.price.cost - ri.discount)) from Reservation r join r.seats ri where r.performance.id = " + id + " group by ri.price.name, ri.price.cost order by ri.price.cost desc, sum(ri.quantity) desc, ri.price.name");
        for (priceSummary in totalsByPrice) {
            priceSummary[3] = NumberFormat.getCurrencyInstance().format(priceSummary[3])
            results.add("${priceSummary[2]} ${priceSummary[0]} - ${priceSummary[3]}")
        }
        return results
    }
    String toString() {
        def format = new SimpleDateFormat("EEE, MM/dd/yy 'at' h:mm a")
        return show.toString() + " - " + format.format(dateAndTime) + " (" + getNumberSeatsSoldAndReserved() + ")"
    }
    static toStringFields = ['show']
    String toStringForShow() {
        def format = new SimpleDateFormat("EEE, MM/dd/yy 'at' h:mm a")
        return format.format(dateAndTime) + " (" + getNumberReservations() + ")"
    }
    static transients = ['numberSeatsSold', 'numberSeatsReserved', 'numberSeatsSoldAndReserved', 'timeAsString', 'totalSales', 'totalsByPrice']
    static dependencies = [['numberSeatsSold', 'numberSeatsReserved', 'numberSeatsSoldAndReserved', 'totalSales', 'totalsByPrice']:['reservations']]
    static constraints = {
        show()
        dateAndTime()
        numberSeatsSoldAndReserved(format:"#")
        numberSeatsSold(format:"#")
        numberSeatsReserved(format:"#",secondary:true)
        totalSales(format:"\\\$0.00",secondary:true)
        totalsByPrice()
        reservations()
        sales()
    }
    int compareTo(performance) {
        int result = dateAndTime.compareTo(performance.dateAndTime)
        if (result != 0) {
            return result
        }
        return id.compareTo(performance.id)
    }

    static mapping = {
        version false
        dateAndTime column:'TIME'
    }
}
