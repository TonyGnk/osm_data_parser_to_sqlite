package utils

fun Int.withDots(): String {
    return this.toString().reversed().chunked(3).joinToString(".").reversed()
}
