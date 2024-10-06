package convert

import BATCH_SIZE
import actions.findClosestRoadFromAll
import actions.findClosestRoadId
import actions.findRoadsWithSameName
import data.Location
import data.fullNodesMap
import data.globalLocationsList
import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import utils.getAddress
import utils.getAddressNumber
import utils.getCategoryId
import utils.getName
import utils.roundToFiveDecimals

fun convertNodesToLocations() {
    val locationBatch = mutableListOf<Location>()
    val previousSize = globalLocationsList.size
    print("\nNodes to locations...")

    for (node in fullNodesMap.values) {
        val tags = node.tags
        if (tags.isEmpty()) continue

        val location = findLocation(tags, node.latitude, node.longitude) ?: continue
        locationBatch.add(location)

        if (locationBatch.size >= BATCH_SIZE) {
            globalLocationsList.addAll(locationBatch)
            locationBatch.clear()

            val index = fullNodesMap.values.indexOf(node)
            val progress = (index + 1) * 100 / fullNodesMap.size
            print("\rNodes to locations...$progress%")
        }
    }

    if (locationBatch.isNotEmpty()) {
        globalLocationsList.addAll(locationBatch)
    }

    println("\rNodes to locations...OK : ${globalLocationsList.size - previousSize} locations")
}


fun findLocation(
    tags: MutableCollection<Tag>,
    latitude: Double,
    longitude: Double
): Location? {
    val addresses = tags.getAddress()
    val category = tags.getCategoryId(addresses) ?: return null

    val (elAddress, enAddress) = addresses
    val (elName, enName) = tags.getName()
    val addressNumber = tags.getAddressNumber()

    val roadId = findRoadsWithSameName(elAddress, enAddress).let { roads ->
        when {
            roads.isEmpty() -> findClosestRoadFromAll(latitude, longitude)
            roads.size == 1 -> roads.first().wayId
            else -> findClosestRoadId(latitude, longitude, roads)
                ?: findClosestRoadFromAll(latitude, longitude)
        }
    }

    return Location(
        elName = elName,
        enName = enName,
        elAddress = elAddress,
        enAddress = enAddress,
        addressNumber = addressNumber,
        latitude = roundToFiveDecimals(latitude),
        longitude = roundToFiveDecimals(longitude),
        wayId = roadId,
        category = category
    )
}
