package utils

import java.sql.PreparedStatement

fun PreparedStatement.setIntOrNull(
    parameterIndex: Int,
    value: Int?,
) {
    if (value != null) this.setInt(parameterIndex, value)
    else this.setNull(parameterIndex, java.sql.Types.NULL)
}
