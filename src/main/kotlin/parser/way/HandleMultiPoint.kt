package parser.way

import utils.findTagWithName
import parser.nodes.possiblePlaceCategories
import parser.nodes.possiblePlaceSubCategories
import parser.nodes.subCategoriesThatCanBeWithoutFields
import insert.insertPlace
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import arch.getTheIdOfTheRoad
import java.sql.Connection

fun handleMultiPoint(
    sql: Connection, way: Way,
) {
    val elName = way.tags.findTagWithName(listOf("name:el", "name"))
    val enName = way.tags.findTagWithName(listOf("name:en", "int_name"))

    val elAddress = way.tags.findTagWithName(listOf("addr:street:el", "addr:street"))
    val enAddress = way.tags.findTagWithName(listOf("addr:street:en"))

    val addressNumber = way.tags.findTagWithName(listOf("addr:housenumber", "addr:unit"))

    val category = way.tags.findTagWithName(possiblePlaceCategories)
    val subCategory = if (category in possiblePlaceSubCategories) category else ""


    val notEmpty = elName.isNotBlank()
            || enName.isNotBlank()
            || elAddress.isNotBlank()
            || enAddress.isNotBlank()
            || subCategory in subCategoriesThatCanBeWithoutFields

    if (notEmpty) {
        val wayId = getTheIdOfTheRoad(
            sql = sql,
            elAddress = elAddress,
            enAddress = enAddress
        )

        insertPlace(
            sql = sql,
            id = way.id,
            elName = elName,
            enName = enName,
            wayId = wayId,
            addressNumber = addressNumber,
            category = subCategory,
            isSingePoint = false,
        )
    }

}
