package data


data class RoadPart(
    val wayId: Long,
    val elName: String?,
    val enName: String?,
    val wayNodes: List<WayNode>,
)

data class WayNode(
    val wayId: Long,
    val nodeId: Long,
    val sequence: Int,
    var latitude: Double,
    var longitude: Double,
)

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

data class RoadSet(
    var elName: String? = null,
    var enName: String? = null,
    val wayOldIds: MutableList<Long> = mutableListOf(),
    val wayNewIds: MutableList<WayNode> = mutableListOf(),
)
