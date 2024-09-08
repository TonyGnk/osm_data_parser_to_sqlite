package insert

import java.sql.Connection

fun insertPlace(
    sql: Connection,
    id: Long,
    elName: String,
    enName: String,
    wayId: Long?,
    addressNumber: String,
    category: String,
    isSingePoint: Boolean,
) {
    val insertLocations = sql.prepareStatement(
        "INSERT INTO places " +
                "(entity_id, el_name, en_name, way_id, address_number, category, is_singe_point) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
    )

    insertLocations.setLong(1, id)
    insertLocations.setString(2, elName)
    insertLocations.setString(3, enName)
    if (wayId != null) {
        insertLocations.setLong(4, wayId)
    } else {
        insertLocations.setNull(4, java.sql.Types.NULL)
    }
    insertLocations.setString(5, addressNumber)
    insertLocations.setString(6, category)
    insertLocations.setBoolean(7, isSingePoint)

    insertLocations.addBatch()
    insertLocations.executeBatch()
    insertLocations.close()
}
