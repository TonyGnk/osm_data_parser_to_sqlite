package convert

import BATCH_SIZE
import actions.findClosestRoadFromAll
import actions.findClosestRoadId
import actions.findClosestRoadNameFromAll
import actions.findRoadsWithSameName
import data.Glass
import data.Location
import data.LocationGlass
import data.fullNodesMap
import data.globalGlassList
import data.globalLocationGlassList
import data.specialSubCategoriesAndTranslations
import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import utils.getAddress
import utils.getAddressNumber
import utils.getCategory
import utils.getCategoryId
import utils.getName
import utils.roundToFiveDecimals

fun convertNodesToLocations() {
    val glassBatch = mutableListOf<Glass>()
    val locationBatch = mutableListOf<LocationGlass>()
    val previousSize = globalGlassList.size
    print("\nNodes to locations...")


    for (node in fullNodesMap.values) {
        val tags = node.tags
        if (tags.isEmpty()) continue

        // val location = findLocation(tags, node.latitude, node.longitude) ?: continue
        //locationBatch.add(location)

        val pair = findLocationGlass(
            idTypeIsNode = true, node.id, tags, node.latitude, node.longitude
        )
        if (pair != null) {
            glassBatch.add(pair.first)
            locationBatch.add(pair.second)
        }

        if (locationBatch.size >= BATCH_SIZE) {
            globalLocationGlassList.addAll(locationBatch)
            globalGlassList.addAll(glassBatch)
            locationBatch.clear()
            glassBatch.clear()

            val index = fullNodesMap.values.indexOf(node)
            val progress = (index + 1) * 100 / fullNodesMap.size
            print("\rNodes to locations...$progress%")
        }
    }

    if (locationBatch.isNotEmpty()) {
        globalLocationGlassList.addAll(locationBatch)
        globalGlassList.addAll(glassBatch)
    }

    println("\rNodes to locations...OK : ${globalGlassList.size - previousSize} locations")
}


//fun findLocation(
//    tags: MutableCollection<Tag>,
//    latitude: Double,
//    longitude: Double
//): Location? {
//    val addresses = tags.getAddress()
//    val category = tags.getCategoryId(addresses) ?: return null
//
//    val (elAddress, enAddress) = addresses
//    val (elName, enName) = tags.getName()
//    val addressNumber = tags.getAddressNumber()
//
//    val roadId = findRoadsWithSameName(elAddress, enAddress).let { roads ->
//        when {
//            roads.isEmpty() -> findClosestRoadFromAll(latitude, longitude)
//            roads.size == 1 -> roads.first().wayId
//            else -> findClosestRoadId(latitude, longitude, roads)
//                ?: findClosestRoadFromAll(latitude, longitude)
//        }
//    }
//
//    return Location(
//        elName = elName,
//        enName = enName,
//        elAddress = elAddress,
//        enAddress = enAddress,
//        addressNumber = addressNumber,
//        latitude = roundToFiveDecimals(latitude),
//        longitude = roundToFiveDecimals(longitude),
//        wayId = roadId,
//        category = category
//    )
//}

fun findLocationGlass(
    idTypeIsNode: Boolean,
    id: Long,
    tags: MutableCollection<Tag>,
    latitude: Double,
    longitude: Double
): Pair<Glass, LocationGlass>? {
    val addresses = tags.getAddress()
    val category = tags.getCategory()

    val notAllowCategory = category == null && addresses.first == null && addresses.second == null
    if (notAllowCategory) return null

    var (elAddress, enAddress) = addresses


    var (elName, enName) = tags.getName()
    if (
        category in specialSubCategoriesAndTranslations && elName == null && enName == null
    ) {
        elName = specialSubCategoriesAndTranslations[category]?.first
        enName = specialSubCategoriesAndTranslations[category]?.second
    }

    val addressNumber = tags.getAddressNumber()
    val onlyCategory =
        elName == null && enName == null && elAddress == null && enAddress == null && category != null
    if (onlyCategory) return null
    if (elAddress == null && enAddress == null) {
        val pair = findClosestRoadNameFromAll(latitude, longitude)
        elAddress = pair.first
        enAddress = pair.second
    }

//    val roadId = findRoadsWithSameName(elAddress, enAddress).let { roads ->
//        when {
//            roads.isEmpty() -> findClosestRoadFromAll(latitude, longitude)
//            roads.size == 1 -> roads.first().wayId
//            else -> findClosestRoadId(latitude, longitude, roads)
//                ?: findClosestRoadFromAll(latitude, longitude)
//        }
//    }
    //findClosestRoadFromAll(latitude, longitude)


    val number = addressNumber ?: ""

    val ad1 = elAddress ?: ""
    val elAddressG = if (ad1.isNotBlank()) "$ad1 $number" else null

    val ad2 = enAddress ?: ""
    val enAddressG = if (ad2.isNotBlank()) "$ad2 $number" else null


    //2 categories: Businesses and Localities. Is Locality if the names are blank
    val isLocality = elName == null && enName == null

    val locationIdFix = if (idTypeIsNode) 1 else 2
    val locationId = "$locationIdFix$id".toLong()
    val glassId = "L$locationId"

    val glass = if (isLocality) Glass(
        id = glassId,
        titleEl = elAddressG,
        titleEn = enAddressG,
        subTitleEl = null, //TODO Add close regions
        subTitleEn = null,
        category = category
    ) else Glass(
        id = glassId,
        titleEl = elName,
        titleEn = enName,
        subTitleEl = elAddressG,
        subTitleEn = enAddressG,
        category = category
    )

    val location = LocationGlass(
        id = locationId,
        latitude = roundToFiveDecimals(latitude),
        longitude = roundToFiveDecimals(longitude)
    )


//    return Location(
//        elName = elName,
//        enName = enName,
//        elAddress = elAddress,
//        enAddress = enAddress,
//        addressNumber = addressNumber,
//        latitude = roundToFiveDecimals(latitude),
//        longitude = roundToFiveDecimals(longitude),
//        wayId = roadId,
//        category = category
//    )
    return Pair(glass, location)
}
