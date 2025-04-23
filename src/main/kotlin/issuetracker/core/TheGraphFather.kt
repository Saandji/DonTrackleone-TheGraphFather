package issuetracker.core

import com.samshend.issuetracker.core.exception.ResourceAlreadyExistsException
import issuetracker.core.model.Label
import issuetracker.core.model.Node
import issuetracker.core.model.Relationship

/**
 * "I’m gonna make you an offer you can’t refuse... a fast O(1) traversal."
 */

class TheGraphFather {

    /**
     * TODO: this is a temporary solution to store in memory for testing.
     * TODO: make it persistent: Instead of storing these in memory, create the Store and store them in File system (or DDB maybe for AWS)
     */
    private val nodes = mutableMapOf<String, Node>()
    private val labels = mutableMapOf<String, Label>()
    private val relationships = mutableMapOf<String, Relationship>()

    fun getNode(id: String): Node? {
        return nodes[id]
    }

    /**
     * Creates the Node.
     *
     * For example, create Node with:
     *  name = "My first Node",
     *  id = "DP-1" (provided by the user),
     *  labels: []
     */
    fun createNode(
        id: String,
        name: String,
        labels: MutableSet<Label>,
        props: MutableMap<String, Any>,
    ): Node {
        if (nodes.containsKey(id)) {
            throw ResourceAlreadyExistsException("Node with id=$id already exists")
        }

        labels.forEach { createOrGetLabel(it.id, it.name) }

        val node = Node(id, name, labels, props)
        nodes[id] = node
        return node
    }

    fun createOrGetLabel(
        id: String,
        name: String,
    ): Label {
        labels.putIfAbsent(id, Label(id, name))
        return Label(id, name)
    }

    fun createRelationship(
        idFrom: String,
        idTo: String,
        type: Label,
        from: Node,
        to: Node,
        props: MutableMap<String, Any> = mutableMapOf()
    ): Relationship {
        val relationshipId = "$idFrom-${type.id}-$idTo"
        val rel = Relationship(relationshipId, type, from, to, "", props)
        relationships[relationshipId] = rel

        // O(1) adjacency updates
        from.outgoing.computeIfAbsent(type) { mutableSetOf(to) }.add(to)
        to.incoming.computeIfAbsent(type) { mutableSetOf() }.add(from)

        return rel
    }


    /**
     * Breadth-First Search traversal
     */
    fun bfs(start: Node, visit: (Node) -> Unit) {
        val visited = mutableSetOf<Node>()
        val queue = ArrayDeque<Node>()
        visited.add(start)
        queue.add(start)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            visit(current)
            for (neighbors in current.outgoing.values) {
                for (nbr in neighbors) {
                    if (nbr !in visited) {
                        visited.add(nbr)
                        queue.add(nbr)
                    }
                }
            }
        }
    }

    /**
     * Depth-First Search traversal (recursive)
     */
    fun dfs(start: Node, visit: (Node) -> Unit) {
        val visited = mutableSetOf<Node>()
        fun recurse(node: Node) {
            visited.add(node)
            visit(node)
            for (neighbors in node.outgoing.values) {
                for (nbr in neighbors) {
                    if (nbr !in visited) {
                        recurse(nbr)
                    }
                }
            }
        }
        recurse(start)
    }

    /**
     * Find path between two nodes using BFS
     */
    fun findPathBfs(start: Node, target: Node): List<Node>? {
        val visited = mutableSetOf<Node>()
        val queue = ArrayDeque<List<Node>>()
        visited.add(start)
        queue.add(listOf(start))
        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val current = path.last()
            if (current == target) return path
            for (neighbors in current.outgoing.values) {
                for (nbr in neighbors) {
                    if (nbr !in visited) {
                        visited.add(nbr)
                        queue.add(path + nbr)
                    }
                }
            }
        }
        return null
    }
}