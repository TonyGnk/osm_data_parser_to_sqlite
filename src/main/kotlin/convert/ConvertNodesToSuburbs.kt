package convert

import BATCH_SIZE
import actions.findClosestRoadNameFromAll
import data.Glass
import data.Coordinate
import data.Suburb
import data.fullNodesMap
import data.globalGlassList
import data.globalCoordinateList
import data.globalSuburbsList
import data.specialSubCategoriesAndTranslations
import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import utils.getAddress
import utils.getAddressNumber
import utils.getCategory
import utils.getCategorySuburb
import utils.getName
import utils.roundToFiveDecimals

fun convertNodesToSuburbs() {
    val suburbsBatch = mutableListOf<Suburb>()
    val locationBatch = mutableListOf<Coordinate>()
    print("\nNodes to suburbs...")

    for (node in fullNodesMap.values) {
        val tags = node.tags
        if (tags.isEmpty()) continue

        val pair = findLocationSuburb(
            node.id, tags, node.latitude, node.longitude
        )
        if (pair == null) continue

        suburbsBatch.add(pair.first)
        locationBatch.add(pair.second)

        if (locationBatch.size >= BATCH_SIZE) {
            globalCoordinateList.addAll(locationBatch)
            globalSuburbsList.addAll(suburbsBatch)
            locationBatch.clear()
            suburbsBatch.clear()

            val index = fullNodesMap.values.indexOf(node)
            val progress = (index + 1) * 100 / fullNodesMap.size
            print("\rNodes to suburbs...$progress%")
        }
    }

    if (locationBatch.isNotEmpty()) {
        globalCoordinateList.addAll(locationBatch)
        globalSuburbsList.addAll(suburbsBatch)
    }

    println("\rNodes to suburbs...OK : ${globalSuburbsList.size} suburbs")
}


fun findLocationSuburb(
    id: Long,
    tags: MutableCollection<Tag>,
    latitude: Double,
    longitude: Double
): Pair<Suburb, Coordinate>? {

    val category = tags.getCategorySuburb() ?: return null
    val (elName, enName) = tags.getName()
    if (elName == null && enName == null) return null

    val suburb = Suburb(
        titleEl = elName,
        titleEn = enName,
        id = id.toString(),
        category = category
    )

    val location = Coordinate(
        id = id,
        latitude = roundToFiveDecimals(latitude),
        longitude = roundToFiveDecimals(longitude)
    )

    return Pair(suburb, location)
}
