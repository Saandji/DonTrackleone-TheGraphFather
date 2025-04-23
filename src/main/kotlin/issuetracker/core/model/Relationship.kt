package issuetracker.core.model

data class Relationship(
    val id: String,
    val type: Label,
    val from: Node,
    val to: Node,
    val description: String = "",
    val properties: MutableMap<String, Any> = mutableMapOf()
)
