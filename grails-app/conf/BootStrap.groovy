import java.text.SimpleDateFormat
import grails.util.GrailsUtil
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.crypto.hash.Md5Hash
class BootStrap {

     def init = { servletContext ->
         if (GrailsUtil.environment == "production") {
             return
         }
         def c1 = new Customer(lastName:"Wood", firstName:"Aidan").save();
         def c2 = new Customer(lastName:"Wood", firstName:"Ailie").save();
         def c3 = new Customer(lastName:"Rothenberg", firstName:"Jon").save();
         def c4 = new Customer(lastName:"Rothenberg", firstName:"Jessa").save();
         def c5 = new Customer(lastName:"Rothenberg", firstName:"Wendy").save();
         def c6 = new Customer(lastName:"Wood Rothenberg",firstName:"Shannon",phone:"303-699-7226").save()
         def c7 = new Customer(lastName:"Rothenberg",firstName:"Maureen").save()
         def c8 = new Customer(lastName:"Rothenberg",firstName:"Manny").save()
         def c9 = new Customer(lastName:"Rothenberg",firstName:"Izzy").save()
         def c10 = new Customer(lastName:"Rothenberg",firstName:"Alex").save()
         def c11 = new Customer(lastName:"Wood", firstName:"Dave").save();
         def c12 = new Customer(lastName:"Wood", firstName:"Pine").save();
         def c13 = new Customer(lastName:"Staley", firstName:"Stephanie").save();
         def c14 = new Customer(lastName:"Staley", firstName:"Steve").save();
         def c15 = new Customer(lastName:"Staley", firstName:"Connie").save();
         def c16 = new Customer(lastName:"Hoover",firstName:"Shannon").save()
         def c17 = new Customer(lastName:"Hoover",firstName:"Maureen").save()
         def c18 = new Customer(lastName:"Hoover",firstName:"Manny").save()
         def c19 = new Customer(lastName:"Hoover",firstName:"Izzy").save()
         def c20 = new Customer(lastName:"Hoover",firstName:"Alex").save()
         def c21 = new Customer(lastName:"Meeker", firstName:"Aidan").save();
         def c22 = new Customer(lastName:"Smith", firstName:"Ailie").save();
         def c23 = new Customer(lastName:"Jones", firstName:"Jon").save();
         def c24 = new Customer(lastName:"Cleese", firstName:"Jessa").save();
         def c25 = new Customer(lastName:"Allen", firstName:"Wendy").save();
         def c26 = new Customer(lastName:"Moofer",firstName:"Shannon").save()
         def c27 = new Customer(lastName:"Taylor",firstName:"Maureen").save()
         def c28 = new Customer(lastName:"Pizza",firstName:"Manny").save()
         def c29 = new Customer(lastName:"Rrrrar",firstName:"Izzy").save()
         def c30 = new Customer(lastName:"Tay",firstName:"Alex").save()
         def p1 = new Price(name:"Student/Senior",cost:18.00).save()
         def p2 = new Price(name:"Group",cost:18.00).save()
         def p3 = new Price(name:"Matinee",cost:15.00).save()
         def p4 = new Price(name:"Comp",cost:0.00).save()
         def p5 = new Price(name:"Gift Certificate",cost:0.00).save()
         def p6 = new Price(name:"BOGO",cost:0.00).save()
         def p7 = new Price(name:"Special",cost:10.00).save()
         def p8 = new Price(name:"Full",cost:22.00).save()
         //def p9 = new Price(name:"test1",cost:0.00).save()
         //def p11 = new Price(name:"Student/Senior2",cost:18.00).save()
         //def p12 = new Price(name:"Group2",cost:18.00).save()
         //def p13 = new Price(name:"Matinee2",cost:15.00).save()
         //def p14 = new Price(name:"Comp2",cost:0.00).save()
         //def p15 = new Price(name:"Gift Certificate2",cost:0.00).save()
         //def p16 = new Price(name:"BOGO2",cost:0.00).save()
         //def p17 = new Price(name:"Special2",cost:10.00).save()
         //def p18 = new Price(name:"Full2",cost:22.00).save()
         //def p19 = new Price(name:"test2",cost:0.00).save()
         //def p21 = new Price(name:"Student/Senior3",cost:18.00).save()
         //def p22 = new Price(name:"Group3",cost:18.00).save()
         //def p23 = new Price(name:"Matinee3",cost:15.00).save()
         //def p24 = new Price(name:"Comp3",cost:0.00).save()
         //def p25 = new Price(name:"Gift Certificate3",cost:0.00).save()
         //def p26 = new Price(name:"BOGO3",cost:0.00).save()
         //def p27 = new Price(name:"Special3",cost:10.00).save()
         //def p28 = new Price(name:"Full3",cost:22.00).save()
         //def p29 = new Price(name:"test3",cost:0.00).save()
         def s1 = new Show(name:"A Chicken Lips Christmas").save()
         def s2 = new Show(name:"What The Dickens!").save()
         def s3 = new Show(name:"That's My Pygmy!").save()
         def s4 = new Show(name:"The Annoyers").save()
         def s5 = new Show(name:"Take This Bob and Shove It").save()
         def s6 = new Show(name:"Ricky").save()
         def s7 = new Show(name:"Carbuncle").save()

         def p9 = new Price(name:"Carbuncle!",cost:10.00).save()
         def p10 = new Price(name:"Annoy",cost:11.00).save()
         def p11 = new Price(name:"ricky full",cost:12.00).save()
         def p12 = new Price(name:"chicken",cost:13.00).save()

         def sp1 = new ShowPrice(show:s1,price:p6).save()
         def sp2 = new ShowPrice(show:s1,price:p10).save()
         def sp3 = new ShowPrice(show:s1,price:p12).save()

         def pe1 = new Performance(show:s1,
                         dateAndTime:new SimpleDateFormat("MM/dd/yy hh:mm aa").parse("5/1/09 7:30 PM")).save()
         def pe2 = new Performance(show:s1,
                         dateAndTime:new SimpleDateFormat("MM/dd/yy hh:mm aa").parse("6/1/09 7:30 PM")).save()
         def pe3 = new Performance(show:s1,
                         dateAndTime:new SimpleDateFormat("MM/dd/yy hh:mm aa").parse("1/1/09 7:30 PM")).save()
         def pe4 = new Performance(show:s1,
                         dateAndTime:new SimpleDateFormat("MM/dd/yy hh:mm aa").parse("2/1/09 7:30 PM")).save()
         def pe5 = new Performance(show:s1,
                         dateAndTime:new Date() + 1).save()
         def pe6 = new Performance(show:s2,
                         dateAndTime:new Date() + 10).save()
         def pe7 = new Performance(show:s3,
                         dateAndTime:new Date() + 20).save()
         def pe8 = new Performance(show:s3,
                         dateAndTime:new Date()).save()
         def pe9 = new Performance(show:s4,
                         dateAndTime:new Date() + 30).save()
         def pe10 = new Performance(show:s4,
                         dateAndTime:new Date() + 40).save()
         def pe11 = new Performance(show:s4,
                         dateAndTime:new Date() + 50).save()
         def pe12 = new Performance(show:s4,
                         dateAndTime:new Date() + 60).save()
         def pe13 = new Performance(show:s5,
                         dateAndTime:new Date() + 70).save()
         def pe14 = new Performance(show:s5,
                         dateAndTime:new Date() - 1).save()
         def pe15 = new Performance(show:s6,
                         dateAndTime:new Date() - 10).save()
         def pe16 = new Performance(show:s2,
                         dateAndTime:new Date() + 11).save()
         def pe17 = new Performance(show:s3,
                         dateAndTime:new Date() + 21).save()
         def pe18 = new Performance(show:s3,
                         dateAndTime:new Date() + 5).save()
         def pe19 = new Performance(show:s4,
                         dateAndTime:new Date() + 31).save()
         def pe20 = new Performance(show:s4,
                         dateAndTime:new Date() + 45).save()
         def pe21 = new Performance(show:s4,
                         dateAndTime:new Date() + 55).save()
         def pe22 = new Performance(show:s4,
                         dateAndTime:new Date() + 65).save()
         def pe23 = new Performance(show:s5,
                         dateAndTime:new Date() + 75).save()
         def pe24 = new Performance(show:s5,
                         dateAndTime:new Date() - 15).save()
         def pe25 = new Performance(show:s6,
                         dateAndTime:new Date() - 17).save()
         def pe26 = new Performance(show:s7,
                         dateAndTime:new Date() + 17).save()
         def pe27 = new Performance(show:s7,
                         dateAndTime:new Date() + 27).save()
         def pe28 = new Performance(show:s7,
                         dateAndTime:new Date() + 7).save()
         def pe29 = new Performance(show:s7,
                         dateAndTime:new Date() + 37).save()

         def r1 = new Reservation(customer:c1, performance:pe1).save()
         new ReservationItem(quantity:2, price:p1, reservation:r1).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c2, performance:pe1).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c3, performance:pe1).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c4, performance:pe1).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c5, performance:pe11).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c6, performance:pe11).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c7, performance:pe11).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c8, performance:pe11).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c9, performance:pe11).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c10, performance:pe12).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c10, performance:pe12).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c11, performance:pe12).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c12, performance:pe12).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c13, performance:pe12).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c14, performance:pe13).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c15, performance:pe13).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c16, performance:pe14).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c1, performance:pe15).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c1, performance:pe15).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c11, performance:pe15).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c11, performance:pe15).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c11, performance:pe15).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c21, performance:pe15).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c21, performance:pe15).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c21, performance:pe16).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c21, performance:pe16).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c10, performance:pe16).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c10, performance:pe16).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c19, performance:pe17).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c19, performance:pe17).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c18, performance:pe17).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c18, performance:pe18).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c17, performance:pe18).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c17, performance:pe18).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c16, performance:pe19).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c16, performance:pe19).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c15, performance:pe21).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c15, performance:pe21).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c14, performance:pe21).save()).save()
         new ReservationItem(quantity:2, price:p1, reservation:new Reservation(customer:c13, performance:pe21).save()).save()

         new ReservationItem(quantity:4, price:p7, reservation:new Reservation(customer:c4, performance:null).save()).save()

        def tc1 = new TaxCode(name:'General Stuff', percent:1).save()

        def m1 = new Merchandise(name:'Hat', cost:10, taxCode:tc1, quantity:10).save()

        def reg1 = new Register(name:'Main Register', cash:100).save()

        def sale1 = new Sale(timeAndDate:new Date(), customer:c1, register:reg1).save()

        def seatSale1 = new SeatSale(performance:pe1, numberSeats:4, price:p1, sale:sale1).save()

        def rSale1 = new ReservationSale(reservation:r1, discount:1.5, sale:sale1).save()

        def mSale1 = new MerchandiseSale(merchandise:m1, quantity:5, sale:sale1).save()

        def tr1 = new Transaction(timeAndDate:new Date(), sale:sale1, register:reg1, type:'Cash', amount:5 ).save()
        new Theater(id:1,defaultPrice:p8,defaultRegister:reg1).save()
        def user = new ShiroUser(username: "rrothenb@yahoo.com", passwordHash: new Md5Hash("bovine").toHex())
        user.addToPermissions("*:*")
        user.save()
}
     def destroy = {
     }
} 