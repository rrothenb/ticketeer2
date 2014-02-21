
class Theater implements SingletonDomainClass {
    Price defaultPrice
    Register defaultRegister
    static Theater getInstance() {
        Theater instance
        try {
            instance = get(1)
            if (!instance) {
                instance = new Theater()
                instance.save()
            }
        }
        catch (Exception e) {
        }
        return instance
    }
    static constraints = {
        defaultPrice(nullable:true)
        defaultRegister(nullable:true)
    }
}
