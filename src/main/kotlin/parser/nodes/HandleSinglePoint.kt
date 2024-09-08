package parser.nodes

import utils.findTagWithName
import insert.insertPlace
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import arch.getTheIdOfTheRoad
import java.sql.Connection


fun handleSinglePoint(
    sql: Connection, node: Node,
) {
    if (node.tags.isEmpty()) return // Get rid of pure nodes

    val elName = node.tags.findTagWithName(listOf("name:el", "name"))
    val enName = node.tags.findTagWithName(listOf("name:en", "int_name"))

    val elAddress = node.tags.findTagWithName(listOf("addr:street:el", "addr:street"))
    val enAddress = node.tags.findTagWithName(listOf("addr:street:en"))

    val addressNumber = node.tags.findTagWithName(listOf("addr:housenumber", "addr:unit"))

    val category = node.tags.findTagWithName(possiblePlaceCategories)
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
            id = node.id,
            elName = elName,
            enName = enName,
            wayId = wayId,
            addressNumber = addressNumber,
            category = subCategory,
            isSingePoint = true,
        )
    }
}
