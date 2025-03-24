package convert

import BATCH_SIZE
import actions.calculateDistance
import actions.findClosestRoadNameFromAll
import data.Glass
import data.Coordinate
import data.Suburb
import data.WayNode
import data.fullNodesMap
import data.globalGlassList
import data.globalCoordinateList
import data.globalSuburbsList
import data.specialSubCategoriesAndTranslations
import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import utils.getAddress
import utils.getAddressNumber
import utils.getCategory
import utils.getName
import utils.roundToFiveDecimals

/**
 * Converts nodes to locations and updates the global glass list and coordinate list.
 */
fun convertNodesToLocations() {
    val glassBatch = mutableListOf<Glass>()
    val locationBatch = mutableListOf<Coordinate>()
    val previousSize = globalGlassList.size
    print("\nNodes to locations...")


    for (node in fullNodesMap.values) {
        val tags = node.tags
        if (tags.isEmpty()) continue

        // val location = findLocation(tags, node.latitude, node.longitude) ?: continue
        //locationBatch.add(location)

        val pair = findLocationGlass(
            node.id, tags, node.latitude, node.longitude
        )
        if (pair != null) {
            glassBatch.add(pair.first)
            locationBatch.add(pair.second)
        }

        if (locationBatch.size >= BATCH_SIZE) {
            globalCoordinateList.addAll(locationBatch)
            globalGlassList.addAll(glassBatch)
            locationBatch.clear()
            glassBatch.clear()

            val index = fullNodesMap.values.indexOf(node)
            val progress = (index + 1) * 100 / fullNodesMap.size
            print("\rNodes to locations...$progress%")
        }
    }

    if (locationBatch.isNotEmpty()) {
        globalCoordinateList.addAll(locationBatch)
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

/**
 * Finds the location glass and coordinate for a given node.
 *
 * @param id The ID of the node.
 * @param tags The tags associated with the node.
 * @param latitude The latitude of the node.
 * @param longitude The longitude of the node.
 * @return A pair containing the glass and coordinate, or null if no valid location is found.
 */
fun findLocationGlass(
    id: Long,
    tags: MutableCollection<Tag>,
    latitude: Double,
    longitude: Double
): Pair<Glass, Coordinate>? {
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

    val glassId = "L$id"

    val glass = if (isLocality) {
        val suburb = findClosestSuburb(latitude, longitude)
        Glass(
            id = glassId,
            titleEl = elAddressG,
            titleEn = enAddressG,
            subTitleEl = suburb?.titleEl,
            subTitleEn = suburb?.titleEn,
            category = category,
        )
    } else Glass(
        id = glassId,
        titleEl = elName,
        titleEn = enName,
        subTitleEl = elAddressG,
        subTitleEn = enAddressG,
        category = category,
    )

    val location = Coordinate(
        id = id,
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

/**
 * Finds the closest suburb to a given latitude and longitude.
 *
 * @param latitude The latitude of the point to find the closest suburb to.
 * @param longitude The longitude of the point to find the closest suburb to.
 * @return The closest suburb, or null if no suburb is found within the specified range.
 */
private fun findClosestSuburb(latitude: Double, longitude: Double): Suburb? {
    // Map each suburb to its corresponding coordinate
    val suburbsMap: Map<Suburb, Coordinate?> = globalSuburbsList.associateWith { suburb ->
        globalCoordinateList.firstOrNull { coordinate ->
            coordinate.id == suburb.id.toLong()
        }
    }

    // Initialize variables to track the closest suburb and the shortest distance found
    var closestSuburb: Suburb? = null
    val maxDistance = 500.0 // 1.5 km in meters
    var shortestDistance = Double.MAX_VALUE

    // Iterate over each suburb and its coordinate
    for ((suburb, coordinate) in suburbsMap) {
        // Ensure the coordinate is not null
        if (coordinate != null) {
            // Calculate the distance between the way node and the suburb's coordinate
            val distance = calculateDistance(
                latitude, longitude,
                coordinate.latitude, coordinate.longitude
            )
            // Check if the distance is within 1.5 km and is shorter than the current shortest distance
            if (distance <= maxDistance && distance < shortestDistance) {
                shortestDistance = distance
                closestSuburb = suburb
            }
        }
    }
    // Return the closest suburb found within 1.5 km, or null if none was found
    return closestSuburb
}
