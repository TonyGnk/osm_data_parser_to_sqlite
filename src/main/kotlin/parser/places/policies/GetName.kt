package parser.places.policies

import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import parser.places.possiblePlaceCategoriesNames
import parser.places.possiblePlaceCategoriesValues
import utils.findTagWithName

fun MutableCollection<Tag>.getName(): Pair<String?, String?> {
    var elName = this.findTagWithName(listOf("name:el", "name"))
    var enName = this.findTagWithName(listOf("name:en", "int_name"))

    // 1. EQUAL : If the enName is equal to the elName
    if (elName != null && elName == enName) enName = null

    // 2. WRONG FORMAT : If the names has only digits or are smaller than 3 characters
    if (elName != null) {
        val wrongFormatEl = elName.length <= 3 || elName.all { it.isDigit() }
        if (wrongFormatEl) elName = null
    }

    if (enName != null) {
        val wrongFormatEn = enName.length <= 3 || enName.all { it.isDigit() }
        if (wrongFormatEn) enName = null
    }

    // 3. BLANK : If the names are blank
    if (elName != null && elName.isBlank()) elName = null
    if (enName != null && enName.isBlank()) enName = null

    return Pair(elName, enName)
}

fun MutableCollection<Tag>.getAddress(): Pair<String?, String?> {
    var elAddress = this.findTagWithName(listOf("addr:street:el", "addr:street"))
    var enAddress = this.findTagWithName(listOf("addr:street:en"))

    // 1. EQUAL : If the enAddress is equal to the elAddress
    if (elAddress != null && elAddress == enAddress) enAddress = null

    // 2. WRONG FORMAT : If the addresses has only digits or are smaller than 3 characters
    if (elAddress != null) {
        val wrongFormatEl = elAddress.length <= 3 || elAddress.all { it.isDigit() }
        if (wrongFormatEl) elAddress = null
    }

    if (enAddress != null) {
        val wrongFormatEn = enAddress.length <= 3 || enAddress.all { it.isDigit() }
        if (wrongFormatEn) enAddress = null
    }

    // 3. BLANK : If the addresses are blank
    if (elAddress != null && elAddress.isBlank()) elAddress = null
    if (enAddress != null && enAddress.isBlank()) enAddress = null

    return Pair(elAddress, enAddress)
}


fun MutableCollection<Tag>.getAddressNumber(): Int? {
    var addressNumberStr: String? = this.findTagWithName(listOf("addr:housenumber", "addr:unit"))

    // 1. BLANK : If the address number is blank
    if (addressNumberStr != null && addressNumberStr.isBlank()) addressNumberStr = null

    // 2. WRONG FORMAT : If the address number has only letters or is larger than 4 characters
    if (addressNumberStr != null) {
        val wrongFormat = addressNumberStr.length > 4 || addressNumberStr.all { !it.isDigit() }
        if (wrongFormat) addressNumberStr = null
    }

    return addressNumberStr?.toIntOrNull()
}


fun MutableCollection<Tag>.getCategoryId(addresses: Pair<String?, String?>): Int? {
    // For address return 0

    var categoryId: Int? = null
    var category = this.findTagWithName(possiblePlaceCategoriesNames)

    // 1. BLANK : If the category is blank
    if (category != null && category.isBlank()) {
        category = null
    }

    // 2. ToMAP : Convert the category to the corresponding value
    if (category != null) {
        categoryId = possiblePlaceCategoriesValuesMap[category]
    }

    if (
        categoryId == null && (addresses.first != null || addresses.second != null)
    ) {
        categoryId = 0
    }

    return categoryId
}


val possiblePlaceCategoriesValuesMap = mapOf(
    "pharmacy" to 1,
    "bank" to 2,
    "cafe" to 3,
    "supermarket" to 4,
    "ticket" to 5,
    "university" to 6,
    "school" to 7,
    "college" to 8,
    "hotel" to 9,
    "doctor" to 10,
    "studio" to 11,
    "bicycle_parking" to 12,
    "fast_food" to 13,
    "church" to 14,
    "atm" to 15,
    "hairdresser" to 16,
    "bicycle_rental" to 17,
    "fuel" to 18,
    "clothes" to 19,
    "butcher" to 20,
    "kiosk" to 21,
    "theatre" to 22,
    "museum" to 23,
    "restaurant" to 24,
    "memorial" to 25,
    "bakery" to 26,
    "pub" to 27,
    "optician" to 28,
    "laundry" to 29,
    "bar" to 30,
    "cinema" to 31,
    "suburb" to 32,
    //"neighbourhood" to 33,
    "civic" to 34,
    "park" to 35,
    "food_court" to 36,
    "ice_cream" to 37,
    "dancing_school" to 38,
    "driving_school" to 39,
    "parking_space" to 40,
    "hospital" to 41,
    "casino" to 42,
    "exhibition_centre" to 43,
    "events_venue" to 44,
    "courthouse" to 45,
    "fire_station" to 46,
    "police" to 47,
    "prison" to 48,
    "food" to 49,
    "post_office" to 50,
    "seafood" to 51,
    "townhall" to 52,
    "mall" to 53,
    "coffee" to 54,
    "nightclub" to 55,
    "social_centre" to 56,
    "retail" to 57,
    "nursing_home" to 58,
    "commercial" to 59,
)
