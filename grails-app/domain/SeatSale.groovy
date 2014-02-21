
class SeatSale implements Comparable {
    Performance performance
    int numberSeats
    Price price
    float discount
    TaxCode taxCode

    float getTotal() {
        float total = 0;
        if (price) {
            total = numberSeats * price.cost
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
        performance(futureOnly:true)
        numberSeats()
        sale()
        discount(format:"\\\$0.00")
        taxCode(nullable:true)
        total(format:"\\\$0.00")
    }

    static dependencies = [['total']:['price','taxCode','discount']]

    String toString() {
        return "$performance"
    }
    int compareTo(seatSale) {
        return performance.compareTo(seatSale.performance)
    }
}
