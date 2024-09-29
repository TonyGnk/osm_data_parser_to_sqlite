package parser.places

import org.openstreetmap.osmosis.core.domain.v0_6.Way
import parser.insertWays
import parser.places.policies.getAddress
import parser.places.policies.getAddressNumber
import parser.places.policies.getCategoryId
import parser.places.policies.getName
import utils.setIntOrNull
import utils.setStringOrNull


fun insertMultiPlace(
    insertPlaceMultiStmt: java.sql.PreparedStatement,
    insertWayNodeStmt: java.sql.PreparedStatement,
    way: Way,
) {
    val names = way.tags.getName()
    val elName = names.first
    val enName = names.second

    val addresses = way.tags.getAddress()
    val elAddress = addresses.first
    val enAddress = addresses.second
    val addressNumber = way.tags.getAddressNumber()

    val category = way.tags.getCategoryId(addresses) ?: return

    insertPlaceMultiStmt.setLong(1, way.id)
    insertPlaceMultiStmt.setStringOrNull(2, elName)
    insertPlaceMultiStmt.setStringOrNull(3, enName)

    insertPlaceMultiStmt.setStringOrNull(4, elAddress)
    insertPlaceMultiStmt.setStringOrNull(5, enAddress)
    insertPlaceMultiStmt.setIntOrNull(6, addressNumber)

    insertPlaceMultiStmt.setInt(7, category)

    insertPlaceMultiStmt.addBatch()

    insertWays(
        insertWayNodeStmt = insertWayNodeStmt,
        way = way
    )
}
