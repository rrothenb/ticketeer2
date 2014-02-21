import java.text.NumberFormat

class Merchandise implements Comparable {
    String name
    float cost
    TaxCode taxCode
    int quantity

    static constraints = {
        name()
        cost(format:"\\\$0.00")
        taxCode(nullable:true)
        quantity()
    }

    String toString() {
        return "$name (${NumberFormat.getCurrencyInstance().format(cost)})"
    }

    int compareTo(merchandise) {
        return name.compareTo(merchandise.name)
    }
}
