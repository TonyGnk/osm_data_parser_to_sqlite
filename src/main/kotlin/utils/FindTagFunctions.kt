package utils

import data.locationCategories
import data.locationSubCategories
import org.openstreetmap.osmosis.core.domain.v0_6.Tag


fun MutableCollection<Tag>.findTagWithName(
    possibleOsmTagsNames: List<String>,
): String? {
    return this.find { it.key in possibleOsmTagsNames }?.value
}


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
    var category = this.findTagWithName(locationCategories)

    // 1. BLANK : If the category is blank
    if (category != null && category.isBlank()) {
        category = null
    }

    // 2. ToMAP : Convert the category to the corresponding value
    if (category != null) {
        categoryId = locationSubCategories[category]
    }

    if (
        categoryId == null && (addresses.first != null || addresses.second != null)
    ) {
        categoryId = 0
    }

    return categoryId
}
