package issuetracker

import com.samshend.issuetracker.enums.EntityType.*
import com.samshend.issuetracker.enums.PropertyKey.*
import com.samshend.issuetracker.enums.RelationType.*
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import issuetracker.core.TheGraphFather
import issuetracker.core.model.Node
import issuetracker.core.model.Relationship
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Date
import kotlin.test.assertEquals

internal class DonTrackleoneTest {

    @MockK
    private lateinit var graphFather: TheGraphFather
    private lateinit var donTrackleone: DonTrackleone

    //test data
    companion object TestData {
        const val PROJECT_ID = "projectId"
        const val USER_ID = "userId"
        const val PROJECT_NAME = "Project Name"
        const val USER_NAME = "User Name"
        const val ISSUE_ID = "issueId"
        const val ISSUE_NAME = "Issue Title"
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        donTrackleone = DonTrackleone(graphFather,,)
    }

    @Nested
    @DisplayName("Project tests")
    inner class ProjectTests {
        @Test
        fun `creates project node with relationship to user`() {
            //arrange
            val projectNode = givenProjectNode(PROJECT_ID, PROJECT_NAME)
            val userNode = givenUserNode(USER_ID, USER_NAME)
            val relationship = givenProjectCreatedByRelationship(PROJECT_ID, USER_ID, projectNode, userNode)

            every { graphFather.getNode(eq(USER_ID)) } returns userNode
            every {
                graphFather.createNode(
                    eq(PROJECT_ID),
                    eq(PROJECT_NAME),
                    any(),
                    any()
                )
            } returns projectNode
            every {
                graphFather.createRelationship(
                    eq(PROJECT_ID),
                    eq(USER_ID),
                    eq(CREATED_BY.toLabel()),
                    eq(projectNode),
                    eq(userNode),
                    any()
                )
            } returns relationship

            //act
            val result = donTrackleone.createProject(PROJECT_ID, PROJECT_NAME, USER_ID)

            //assert
            assertEquals(projectNode, result)
            verifyProjectCreatedWithRelationship(projectNode, userNode)
        }

        @Test
        fun `create project node without user fails`() {
            //arrange
            val projectNode = givenProjectNode(PROJECT_ID, PROJECT_NAME)
            every { graphFather.getNode(eq(USER_ID)) } returns null

            //act
            assertThrows<IllegalStateException> { donTrackleone.createProject(PROJECT_ID, PROJECT_NAME, USER_ID) }
        }

        private fun verifyProjectCreatedWithRelationship(projectNode: Node, userNode: Node) {
            verifyOrder {
                graphFather.getNode(USER_ID)
                graphFather.createNode(
                    id = PROJECT_ID,
                    name = PROJECT_NAME,
                    labels = match { it.contains(PROJECT.toLabel()) },
                    props = match { it[NAME.key] == PROJECT_NAME },
                )
                graphFather.createRelationship(
                    PROJECT_ID,
                    USER_ID,
                    CREATED_BY.toLabel(),
                    projectNode,
                    userNode,
                    any()
                )
            }
        }

        private fun givenProjectCreatedByRelationship(
            projectId: String,
            userId: String,
            projectNode: Node,
            userNode: Node
        ): Relationship = Relationship(
            id = "$projectId-${CREATED_AT.key}-$userId",
            type = CREATED_BY.toLabel(),
            from = projectNode,
            to = userNode,
            description = "some long description of the relationship",
            properties = mutableMapOf(CREATED_AT.key to Date())
        )
    }

    @Nested
    @DisplayName("createUser")
    inner class CreateUserTests {
        @Test
        fun `creates user node`() {
            // Arrange
            val userNode = Node(USER_ID, USER_NAME, mutableSetOf(USER.toLabel()), mutableMapOf(NAME.key to USER_NAME))

            every { graphFather.createNode(USER_ID, USER_NAME, any(), any()) } returns userNode

            // Act
            val result = donTrackleone.createUser(USER_ID, USER_NAME)

            // Assert
            assertEquals(userNode, result)
            verify {
                graphFather.createNode(
                    USER_ID,
                    USER_NAME,
                    match { it.contains(USER.toLabel()) },
                    match { it[NAME.key] == USER_NAME })
            }
        }
    }

    @Nested
    @DisplayName("createIssue")
    inner class CreateIssueTests {
        @Test
        fun `creates issue and relationships when project and user exist`() {
            // Arrange
            val issueNode =
                Node(ISSUE_ID, ISSUE_NAME, mutableSetOf(ISSUE.toLabel()), mutableMapOf(NAME.key to ISSUE_NAME))
            val projectNode = Node(PROJECT_ID, PROJECT_NAME, mutableSetOf(PROJECT.toLabel()), mutableMapOf())
            val userNode = Node(USER_ID, USER_NAME, mutableSetOf(USER.toLabel()), mutableMapOf())

            every { graphFather.getNode(PROJECT_ID) } returns projectNode
            every { graphFather.getNode(USER_ID) } returns userNode
            every { graphFather.createNode(ISSUE_ID, ISSUE_NAME, any(), any()) } returns issueNode
            every {
                graphFather.createRelationship(
                    ISSUE_ID,
                    PROJECT_ID,
                    BELONGS_TO_PROJECT.toLabel(),
                    issueNode,
                    projectNode,
                    any()
                )
            } returns mockk()
            every {
                graphFather.createRelationship(
                    ISSUE_ID,
                    USER_ID,
                    CREATED_BY.toLabel(),
                    issueNode,
                    userNode,
                    any()
                )
            } returns mockk()

            // Act
            donTrackleone.createIssue(ISSUE_ID, ISSUE_NAME, PROJECT_ID, USER_ID)

            // Assert
            verify { graphFather.getNode(PROJECT_ID) }
            verify { graphFather.getNode(USER_ID) }
            verify {
                graphFather.createNode(
                    ISSUE_ID,
                    ISSUE_NAME,
                    match { ISSUE.toLabel() in it },
                    match {
                        it[CREATED_AT.key] == ISSUE_NAME
                    })
            }
            verify {
                graphFather.createRelationship(
                    ISSUE_ID,
                    PROJECT_ID,
                    BELONGS_TO_PROJECT.toLabel(),
                    issueNode,
                    projectNode,
                    any()
                )
            }
        }

        @Test
        fun `fails to create issue when project is missing`() {
            // Arrange
            val userNode = givenUserNode(USER_ID, USER_NAME)
            val issueNode =
                Node(ISSUE_ID, TestData.ISSUE_NAME, mutableSetOf(ISSUE.toLabel()), mutableMapOf("name" to ISSUE_NAME))

            every { graphFather.createNode(ISSUE_ID, ISSUE_NAME, any(), any()) } returns issueNode
            every { graphFather.getNode(PROJECT_ID) } returns null
            every { graphFather.getNode(USER_ID) } returns userNode

            // Act
            assertThrows<IllegalStateException> {
                donTrackleone.createIssue(ISSUE_ID, ISSUE_NAME, PROJECT_ID, USER_ID)
            }

            // Assert
            verify(exactly = 0) {
                graphFather.createRelationship(
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
            verify(exactly = 0) {
                graphFather.createRelationship(
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }

        @Test
        fun `fails to create issue when user is missing`() {
            // Arrange
            val projectNode = givenProjectNode(PROJECT_ID, PROJECT_NAME)
            val issueNode =
                Node(ISSUE_ID, TestData.ISSUE_NAME, mutableSetOf(ISSUE.toLabel()), mutableMapOf("name" to ISSUE_NAME))

            every { graphFather.createNode(ISSUE_ID, ISSUE_NAME, any(), any()) } returns issueNode
            every { graphFather.getNode(PROJECT_ID) } returns projectNode
            every { graphFather.getNode(USER_ID) } returns null

            // Act
            assertThrows<IllegalStateException> {
                donTrackleone.createIssue(ISSUE_ID, ISSUE_NAME, PROJECT_ID, USER_ID)
            }

            // Assert
            verify(exactly = 0) {
                graphFather.createRelationship(
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
            verify(exactly = 0) {
                graphFather.createRelationship(
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
        }
    }

    private fun givenUserNode(userId: String, userName: String): Node = Node(
        userId, userName, mutableSetOf(USER.toLabel()), mutableMapOf(
            NAME.key to userName
        )
    )

    private fun givenProjectNode(projectId: String, projectName: String): Node = Node(
        projectId, projectName, mutableSetOf(PROJECT.toLabel()), mutableMapOf(
            NAME.key to projectName
        )
    )
}