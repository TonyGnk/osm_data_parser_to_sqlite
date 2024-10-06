package utils

import java.util.Locale


fun roundToFiveDecimals(value: Double): Double {
    return String.format(Locale.US, "%.5f", value).toDouble()
}
