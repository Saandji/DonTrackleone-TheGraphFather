package issuetracker.core.model

import java.util.*

data class Node(
    val id: String, //used to identify node
    val name: String,
    val labels: MutableSet<Label> = mutableSetOf(), //used to group nodes
    val properties: MutableMap<String, Any> = mutableMapOf(),

    // Direct links stored for O(1) traversals
    // TODO: store reference only? not Node itself
    val outgoing: MutableMap<Label, MutableSet<Node>> = mutableMapOf(),
    val incoming: MutableMap<Label, MutableSet<Node>> = mutableMapOf()
) {
    override fun toString(): String {
        return "Node(id='$id', name='$name', labels=$labels, properties=$properties)"
    }

    override fun hashCode(): Int {
        // Only use the id and properties for hashCode calculation
        // Exclude connections to prevent infinite recursion
        return Objects.hash(id, properties)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false
        return id == other.id && properties == other.properties
    }

}