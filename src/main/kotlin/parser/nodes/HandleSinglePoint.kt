package parser.nodes

import nodesIn
import nodesOut
import utils.findTagWithName
import org.openstreetmap.osmosis.core.domain.v0_6.Node


fun handleSinglePoint(
    insertPlaceStmt: java.sql.PreparedStatement, node: Node,
) {
    if (node.tags.isEmpty()) return // Get rid of pure nodes

    val elName = node.tags.findTagWithName(listOf("name:el", "name"))
    val enName = node.tags.findTagWithName(listOf("name:en", "int_name"))

    val elAddress = node.tags.findTagWithName(listOf("addr:street:el", "addr:street"))
    val enAddress = node.tags.findTagWithName(listOf("addr:street:en"))

    val addressNumberStr = node.tags.findTagWithName(listOf("addr:housenumber", "addr:unit"))
    val addressNumberList = addressNumberStr.filter { it.isDigit() }
    val addressNumber: Int? = if (addressNumberStr.length <= 4 && addressNumberList.isNotEmpty()) {
        addressNumberList.toInt()
    } else {
        null
    }

    val category = node.tags.findTagWithName(possiblePlaceCategories)
    val subCategory = if (category in possiblePlaceSubCategories) category else ""


    val notEmpty = elName.isNotBlank()
            || enName.isNotBlank()
            || elAddress.isNotBlank()
            || enAddress.isNotBlank()
            || subCategory in subCategoriesThatCanBeWithoutFields

    if (notEmpty) insertPlace(
        insertPlaceStmt = insertPlaceStmt,
        id = node.id,
        elName = elName,
        enName = enName,
        elAddress = elAddress,
        enAddress = enAddress,
        addressNumber = addressNumber,
        category = subCategory,
        isSingePoint = true,
    ) else nodesOut++
}

fun insertPlace(
    insertPlaceStmt: java.sql.PreparedStatement,
    id: Long,
    elName: String,
    enName: String,
    elAddress: String,
    enAddress: String,
    addressNumber: Int?,
    category: String,
    isSingePoint: Boolean,
) {
    nodesIn++

    insertPlaceStmt.setLong(1, id)
    insertPlaceStmt.setString(2, elName)
    insertPlaceStmt.setString(3, enName)

    insertPlaceStmt.setString(4, elAddress)
    insertPlaceStmt.setString(5, enAddress)

    if (addressNumber != null) insertPlaceStmt.setInt(6, addressNumber)
    else insertPlaceStmt.setNull(6, java.sql.Types.INTEGER)

    insertPlaceStmt.setString(7, category)
    insertPlaceStmt.setInt(8, if (isSingePoint) 1 else 0)

    insertPlaceStmt.addBatch()
}
