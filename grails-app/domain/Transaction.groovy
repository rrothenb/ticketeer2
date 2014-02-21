
class Transaction {
    Date timeAndDate
    String type
    float amount
    String info
    Register register

    static belongsTo = [sale:Sale]

    def beforeInsert() {
        timeAndDate = new Date()
        println "Just set timeAndDate to $timeAndDate"
    }

    static constraints = {
        timeAndDate(editable:false,nullable:true)
        type(inList:['Cash','Check','Charge'])
        amount(format:"\\\$0.00")
        info()
        register(nullable:true)
        sale(nullable:true)
    }

    String toString() {
        return "$timeAndDate - $type"
    }
}
