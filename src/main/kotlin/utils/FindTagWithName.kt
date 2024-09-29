package utils

import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import java.sql.PreparedStatement


fun MutableCollection<Tag>.findTagWithName(
    possibleOsmTagsNames: List<String>,
): String? {
    return this.find { it.key in possibleOsmTagsNames }?.value
}


fun PreparedStatement.setStringOrNull(
    parameterIndex: Int,
    value: String?,
) {
    if (value != null) this.setString(parameterIndex, value)
    else this.setNull(parameterIndex, java.sql.Types.NULL)
}

fun PreparedStatement.setIntOrNull(
    parameterIndex: Int,
    value: Int?,
) {
    if (value != null) this.setInt(parameterIndex, value)
    else this.setNull(parameterIndex, java.sql.Types.NULL)
}

fun PreparedStatement.setDoubleOrNull(
    parameterIndex: Int,
    value: Double?,
) {
    if (value != null) this.setDouble(parameterIndex, value)
    else this.setNull(parameterIndex, java.sql.Types.NULL)
}


fun PreparedStatement.setLongOrNull(
    parameterIndex: Int,
    value: Long?,
) {
    if (value != null) this.setLong(parameterIndex, value)
    else this.setNull(parameterIndex, java.sql.Types.NULL)
}
