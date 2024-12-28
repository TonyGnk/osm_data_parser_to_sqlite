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


//___

data class Glass(
    val titleEl: String?,
    val titleEn: String?,
    val subTitleEl: String?,
    val subTitleEn: String?,
    val id: String,
    val category: String?
)


data class LocationGlass(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
)

data class RoadGlass(
    val wayId: Long,
    val latitude: Double,
    val longitude: Double,
    val sequence: Int,
)
