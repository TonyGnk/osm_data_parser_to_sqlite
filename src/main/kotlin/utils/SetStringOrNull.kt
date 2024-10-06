package utils

import java.sql.PreparedStatement

fun PreparedStatement.setStringOrNull(
    parameterIndex: Int,
    value: String?,
) {
    if (value != null) this.setString(parameterIndex, value)
    else this.setNull(parameterIndex, java.sql.Types.NULL)
}
