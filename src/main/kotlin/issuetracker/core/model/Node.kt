package issuetracker.core.model

data class Node(
    val id: String, //used to identify node
    val name: String,
    val labels: MutableSet<Label> = mutableSetOf(), //used to group nodes
    val properties: MutableMap<String, Any> = mutableMapOf(),

    // Direct links stored for O(1) traversals
    val outgoing: MutableMap<Label, MutableSet<Node>> = mutableMapOf(),
    val incoming: MutableMap<Label, MutableSet<Node>> = mutableMapOf()
)