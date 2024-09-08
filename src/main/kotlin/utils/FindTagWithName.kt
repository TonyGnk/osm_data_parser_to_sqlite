package utils

import org.openstreetmap.osmosis.core.domain.v0_6.Tag


fun MutableCollection<Tag>.findTagWithName(
    possibleOsmTagsNames: List<String>,
): String {
    return this.find { it.key in possibleOsmTagsNames }?.value ?: ""
}
