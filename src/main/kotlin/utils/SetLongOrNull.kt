package utils

import java.sql.PreparedStatement

fun PreparedStatement.setLongOrNull(
    parameterIndex: Int,
    value: Long?,
) {
    if (value != null) this.setLong(parameterIndex, value)
    else this.setNull(parameterIndex, java.sql.Types.NULL)
}
