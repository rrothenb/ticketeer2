
class MerchandiseSale implements Comparable {
    Merchandise merchandise
    int quantity
    float discount

    float getTotal() {
        float total = 0
        if (merchandise) {
            total = quantity * merchandise.cost
        }
        if (merchandise?.taxCode) {
            total = total + total*merchandise.taxCode.percent
        }
        if (discount) {
            total = total - discount
        }
        return total
    }

    static transients = ['total']

    static belongsTo = [sale:Sale]

    static constraints = {
        merchandise()
        quantity()
        sale()
        discount(format:"\\\$0.00")
        total(format:"\\\$0.00")
    }

    static dependencies = [['total']:['merchandise','quantity','discount']]

    String toString() {
        return "$merchandise"
    }
    int compareTo(merchandiseSale) {
        return merchandise.compareTo(merchandiseSale.merchandise)
    }
}
