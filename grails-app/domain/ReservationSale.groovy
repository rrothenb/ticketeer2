
class ReservationSale implements Comparable {
    Reservation reservation
    float discount
    TaxCode taxCode

    float getTotal() {
        float total = 0
        if (reservation) {
            total = total + reservation.calcCost()
        }
        if (taxCode) {
            total = total + total*taxCode.percent
        }
        if (discount) {
            total = total - discount
        }
        return total
    }

    static transients = ['total']

    static belongsTo = [sale:Sale]

    static constraints = {
        reservation(futureOnly:true)
        sale()
        discount(format:"\\\$0.00")
        taxCode(nullable:true)
        total(format:"\\\$0.00")
    }

    static dependencies = [['total']:['reservation','taxCode','discount']]

    String toString() {
        return "$reservation"
    }
    int compareTo(reservationSale) {
        return reservation.compareTo(reservationSale.reservation)
    }
}
