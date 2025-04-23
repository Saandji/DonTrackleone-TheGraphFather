package issuetracker

import com.samshend.issuetracker.enums.EntityType
import com.samshend.issuetracker.enums.PropertyKey
import com.samshend.issuetracker.enums.RelationType
import issuetracker.core.TheGraphFather
import issuetracker.core.model.Node
import java.util.Date


/**
 * Serious Issue Tracker for serious people.
 * "In this family, we don’t leave bugs alive."
 *
 * TODO: extract Entities into their own Service classes e.g. IssueService, ProjectService and so forth
 */
class DonTrackleone(private val graphFather: TheGraphFather) {
    /**
     * Creates the project.
     *
     * Following the same idea as in YouTrack, the Project creates identifier for issues which would be created in the project.
     */
    fun createProject(id: String, name: String, userId: String): Node {
        val userNode = graphFather.getNode(userId)
            ?: throw IllegalStateException("User with userId=$userId does not exist. Cannot create the project without user")
        val projectNode = graphFather.createNode(
            id, name, mutableSetOf(EntityType.PROJECT.toLabel()), mutableMapOf(
                PropertyKey.NAME.key to name
            )
        )

        graphFather.createRelationship(
            id,
            userId,
            RelationType.CREATED_BY.toLabel(),
            projectNode,
            userNode,
            mutableMapOf(PropertyKey.CREATED_AT.key to Date())
        )

        return projectNode
    }

    /**
     * Issues can be created only inside the project.
     *
     * TODO: autogenerate id for the issues based on the project
     */
    fun createIssue(id: String, name: String, projectId: String, userId: String) {
        val projectNode = graphFather.getNode(projectId) ?: throw IllegalStateException("Project with projectId=$projectId does not exist. Cannot create the issue without project")
        val userNode = graphFather.getNode(userId) ?: throw IllegalStateException("User with userId=$userId does not exist. Cannot create the issue without user")

        val issueNode = graphFather.createNode(
            id,
            name,
            mutableSetOf(EntityType.ISSUE.toLabel()),
            mutableMapOf(
                PropertyKey.CREATED_AT.key to name,
            )
        )

        //create relationship between Issue and Project
        graphFather.createRelationship(
            id, projectId, RelationType.BELONGS_TO_PROJECT.toLabel(), issueNode, projectNode, mutableMapOf(
                PropertyKey.CREATED_AT.key to Date()
            )
        )
        //create relationship between Issue and User - :Issuer - CREATED_BY -> User
        graphFather.createRelationship(
            id, userId, RelationType.CREATED_BY.toLabel(), issueNode, userNode, mutableMapOf(
                PropertyKey.CREATED_AT.key to Date()
            )
        )
    }

    fun createUser(id: String, name: String) =
        graphFather.createNode(id, name, mutableSetOf(EntityType.USER.toLabel()), mutableMapOf("name" to name))


}
