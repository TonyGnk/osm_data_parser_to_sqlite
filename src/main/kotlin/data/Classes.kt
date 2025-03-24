package data

/**
 * Represents a part of a road with its associated way ID, names, and way nodes.
 *
 * @property wayId The ID of the way.
 * @property elName The Greek name of the road part.
 * @property enName The English name of the road part.
 * @property wayNodes The list of way nodes associated with the road part.
 */
data class RoadPart(
    val wayId: Long,
    val elName: String?,
    val enName: String?,
    val wayNodes: List<WayNode>,
)

/**
 * Represents a node in a way with its associated way ID, node ID, sequence, latitude, and longitude.
 *
 * @property wayId The ID of the way.
 * @property nodeId The ID of the node.
 * @property sequence The sequence number of the node in the way.
 * @property latitude The latitude of the node.
 * @property longitude The longitude of the node.
 */
data class WayNode(
    val wayId: Long,
    val nodeId: Long,
    val sequence: Int,
    var latitude: Double,
    var longitude: Double,
)

/**
 * Represents a location with its associated names, address, way ID, latitude, longitude, and category.
 *
 * @property elName The Greek name of the location.
 * @property enName The English name of the location.
 * @property elAddress The Greek address of the location.
 * @property enAddress The English address of the location.
 * @property addressNumber The address number of the location.
 * @property wayId The ID of the way associated with the location.
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property category The category of the location.
 */
data class Location(
    val elName: String?,
    val enName: String?,
    val elAddress: String?,
    val enAddress: String?,
    val addressNumber: Int?,
    var wayId: Long?,
    val latitude: Double,
    val longitude: Double,
    val category: Int,
)

/**
 * Represents a set of connected road parts with their associated names, old way IDs, and new way nodes.
 *
 * @property elName The Greek name of the road set.
 * @property enName The English name of the road set.
 * @property wayOldIds The list of old way IDs associated with the road set.
 * @property wayNewIds The list of new way nodes associated with the road set.
 */
data class RoadSet(
    var elName: String? = null,
    var enName: String? = null,
    val wayOldIds: MutableList<Long> = mutableListOf(),
    val wayNewIds: MutableList<WayNode> = mutableListOf(),
)

/**
 * Represents a glass object with its associated titles, subtitles, ID, and category.
 *
 * @property titleEl The Greek title of the glass.
 * @property titleEn The English title of the glass.
 * @property subTitleEl The Greek subtitle of the glass.
 * @property subTitleEn The English subtitle of the glass.
 * @property id The ID of the glass.
 * @property category The category of the glass.
 */
data class Glass(
    val titleEl: String?,
    val titleEn: String?,
    val subTitleEl: String?,
    val subTitleEn: String?,
    val id: String,
    val category: String?,
)

/**
 * Represents a coordinate with its associated ID, latitude, and longitude.
 *
 * @property id The ID of the coordinate.
 * @property latitude The latitude of the coordinate.
 * @property longitude The longitude of the coordinate.
 */
data class Coordinate(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
)

/**
 * Represents a road glass object with its associated way ID, latitude, longitude, and sequence.
 *
 * @property wayId The ID of the way.
 * @property latitude The latitude of the road glass.
 * @property longitude The longitude of the road glass.
 * @property sequence The sequence number of the road glass.
 */
data class RoadGlass(
    val wayId: Long,
    val latitude: Double,
    val longitude: Double,
    val sequence: Int,
)

/**
 * Represents a suburb with its associated titles, ID, and category.
 *
 * @property titleEl The Greek title of the suburb.
 * @property titleEn The English title of the suburb.
 * @property id The ID of the suburb.
 * @property category The category of the suburb.
 */
data class Suburb(
    val titleEl: String?,
    val titleEn: String?,
    val id: String,
    val category: String?
)
