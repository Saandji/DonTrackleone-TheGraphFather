package issuetracker.service

import com.samshend.issuetracker.enums.EntityType.ISSUE
import com.samshend.issuetracker.enums.EntityType.USER
import com.samshend.issuetracker.enums.IssueState
import com.samshend.issuetracker.enums.PropertyKey
import com.samshend.issuetracker.enums.RelationType
import com.samshend.issuetracker.service.IssueService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import issuetracker.core.TheGraphFather
import issuetracker.core.exception.ResourceNotFoundException
import issuetracker.core.model.Node
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class IssueServiceTest {

    @MockK
    private lateinit var graph: TheGraphFather

    private lateinit var issueService: IssueService

    private val userId: String = "user1"
    private val userName: String = "User Cool Name"

    @BeforeEach
    fun setup() {
        issueService = IssueService(graph)
    }

    @Nested
    inner class GetOpenIssuesForUser {

        @Test
        fun `should throw ResourceNotFound if user doesn't exist`() {
            every { graph.getNode(userId) } returns null

            assertThrows<ResourceNotFoundException> {
                issueService.getOpenIssuesForUser(userId)
            }
        }

        @Test
        fun `should return empty set if no issues were found`() {
            val userNodeNoIssues = Node(userId, userName, mutableSetOf(USER.toLabel()), mutableMapOf())
            every { graph.getNode(userId) } returns userNodeNoIssues

            val result = issueService.getOpenIssuesForUser(userId)

            assertTrue("Expected no open issues") { result.isEmpty() }
        }

        @Test
        fun `should filter issues and return only OPEN`() {
            val userNodeWithIssues = Node(userId, userName, mutableSetOf(USER.toLabel()), mutableMapOf())
            val openIssue = Node(
                "issuesId1",
                "cool issue name",
                mutableSetOf(ISSUE.toLabel()),
                mutableMapOf(PropertyKey.STATE.key to IssueState.OPEN)
            )
            userNodeWithIssues.incoming.put(
                RelationType.ASSIGNED_TO.toLabel(), mutableSetOf(
                    openIssue,
                    Node(
                        "issuesId12",
                        "cool issue name",
                        mutableSetOf(ISSUE.toLabel()),
                        mutableMapOf(PropertyKey.STATE.key to IssueState.CANT_REPRODUCE)
                    ),
                    Node(
                        "issuesId123",
                        "cool issue name",
                        mutableSetOf(ISSUE.toLabel()),
                        mutableMapOf(PropertyKey.STATE.key to IssueState.TO_BE_DISCUSSED)
                    ),
                )
            )
            every { graph.getNode(userId) } returns userNodeWithIssues


            val result = issueService.getOpenIssuesForUser(userId)
            assertTrue("Expected one open issue") { result.size == 1 }
            assertEquals(openIssue, result.first())
        }

        @Test
        fun `should return empty set if no OPEN issues were found`() {
            val userNodeWithIssues = Node(userId, userName, mutableSetOf(USER.toLabel()), mutableMapOf())
            userNodeWithIssues.incoming.put(
                RelationType.ASSIGNED_TO.toLabel(), mutableSetOf(
                    Node(
                        "issuesId12",
                        "cool issue name",
                        mutableSetOf(ISSUE.toLabel()),
                        mutableMapOf(PropertyKey.STATE.key to IssueState.CANT_REPRODUCE)
                    ),
                    Node(
                        "issuesId123",
                        "cool issue name",
                        mutableSetOf(ISSUE.toLabel()),
                        mutableMapOf(PropertyKey.STATE.key to IssueState.TO_BE_DISCUSSED)
                    ),
                )
            )
            every { graph.getNode(userId) } returns userNodeWithIssues


            val result = issueService.getOpenIssuesForUser(userId)
            assertTrue("Expected no open issues") { result.isEmpty() }
        }

    }
}