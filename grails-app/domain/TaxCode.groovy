
class TaxCode implements Comparable {
    String name
    float percent

    static constraints = {
        name()
        percent(format:"0'%'")
    }

    String toString() {
        return "$name - $percent%"
    }
    int compareTo(taxCode) {
        return name.compareTo(taxCode.name)
    }
}
