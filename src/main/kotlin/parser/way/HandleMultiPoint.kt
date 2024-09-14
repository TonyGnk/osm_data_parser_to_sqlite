package parser.way

import utils.findTagWithName
import parser.nodes.possiblePlaceCategories
import parser.nodes.possiblePlaceSubCategories
import parser.nodes.subCategoriesThatCanBeWithoutFields
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import arch.getTheIdOfTheRoad
import nodesOut
import parser.nodes.insertPlace
import java.sql.Connection

fun handleMultiPoint(
    insertPlaceStmt: java.sql.PreparedStatement,
    insertWayNodeStmt: java.sql.PreparedStatement,
    way: Way,
) {
    val elName = way.tags.findTagWithName(listOf("name:el", "name"))
    val enName = way.tags.findTagWithName(listOf("name:en", "int_name"))

    val elAddress = way.tags.findTagWithName(listOf("addr:street:el", "addr:street"))
    val enAddress = way.tags.findTagWithName(listOf("addr:street:en"))

    val addressNumberStr = way.tags.findTagWithName(listOf("addr:housenumber", "addr:unit"))
    val addressNumberList = addressNumberStr.filter { it.isDigit() }
    val addressNumber: Int? = if (addressNumberStr.length <= 4 && addressNumberList.isNotEmpty()) {
        addressNumberList.toInt()
    } else {
        null
    }


    val category = way.tags.findTagWithName(possiblePlaceCategories)
    val subCategory = if (category in possiblePlaceSubCategories) category else ""


    val notEmpty = elName.isNotBlank()
            || enName.isNotBlank()
            || elAddress.isNotBlank()
            || enAddress.isNotBlank()
            || subCategory in subCategoriesThatCanBeWithoutFields

    if (notEmpty) {
        insertPlace(
            insertPlaceStmt = insertPlaceStmt,
            id = way.id,
            elName = elName,
            enName = enName,
            elAddress = elAddress,
            enAddress = enAddress,
            addressNumber = addressNumber,
            category = subCategory,
            isSingePoint = false,
        )
        insertWays(
            insertWayNodeStmt = insertWayNodeStmt,
            way = way
        )
    } else nodesOut++

}
