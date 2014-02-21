
class Register implements Comparable {
    String name
    float cash

    static hasMany = [ transactions : Transaction, sales : Sale ]

    static constraints = {
        name()
        cash(format:"\\\$0.00")
        sales()
        transactions()
    }

    String toString() {
        return "$name"
    }
    static toStringFields = ['name']
    int compareTo(register) {
        return name.compareTo(register.name)
    }
}
