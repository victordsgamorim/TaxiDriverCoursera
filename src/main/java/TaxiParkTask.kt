package taxipark

fun TaxiPark.findFakeDrivers(): Set<Driver> = this.allDrivers.filter { driver ->
    val listOfDrivers = this.trips.map { trip -> trip.driver }
    driver !in listOfDrivers
}.toSet()


fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        this.allPassengers.filter { passenger ->
            this.trips.count { trip -> passenger in trip.passengers } >= minTrips
        }.toSet()

fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        this.allPassengers.filter passengerFilter@{ passenger ->
            val driverTrips = this.trips.filter { it.driver == driver }
            val driverPassengers = driverTrips.flatMap { it.passengers }
            val count = driverPassengers.count { passengerFromList -> passenger == passengerFromList }
            count > 1
        }.toSet()

fun TaxiPark.findSmartPassengers(): Set<Passenger> =
        this.allPassengers.filter passengerFilter@{ passenger ->
            val passengersWithNoDiscount = this.trips.filter { passenger in it.passengers && it.discount == null }
            val passengersWithDiscount = this.trips.filter { passenger in it.passengers && it.discount != null }
            passengersWithDiscount.size > passengersWithNoDiscount.size
        }.toSet()

fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    val key = this.trips
            .map { trip -> trip.duration }
            .groupBy { duration -> duration / 10 }
            .maxBy { groupedDuration -> groupedDuration.value.size }
            ?.key ?: -1

    if (key >= 0) {
        val min = key * 10
        val max = min + 9
        return min..max
    }
    return null

}

fun TaxiPark.checkParetoPrinciple(): Boolean {

    if (this.trips.isNotEmpty()) {

        val map = this.trips
                .groupBy { it.driver }
                .mapValues { it.value.map { trip -> trip.cost }.sum() }

        val twentyPercentOfDrivers = map
                .entries
                .sortedByDescending { it.value }
                .take((this.allDrivers.size * 0.2).toInt())

        val driversCost = twentyPercentOfDrivers.sumByDouble { it.value }
        val eightyPercent = map.values.sum() * 0.8

        return driversCost >= eightyPercent
    }
    return false


}


