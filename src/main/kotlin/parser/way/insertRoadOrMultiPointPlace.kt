package parser.way

import utils.findTagWithName
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import java.sql.Connection

fun insertRoadOrMultiPointPlace(
    sql: Connection,
    way: Way,
    placeSelected: Boolean,
) {

    val category = way.tags.findTagWithName(listOf("highway"))
    val isRoad = category.isNotBlank()

    when (isRoad) {
        true -> if (!placeSelected) handleRoads(sql, way)
        false -> if (placeSelected) handleMultiPoint(sql, way)
    }
}
