package com.samshend.issuetracker.service

import com.samshend.issuetracker.enums.IssueState
import com.samshend.issuetracker.enums.PropertyKey
import com.samshend.issuetracker.enums.RelationType
import issuetracker.core.TheGraphFather
import issuetracker.core.exception.ResourceNotFoundException
import issuetracker.core.model.Node

/**
 * The service to work with Issue Entity.
 *
 * TODO: we need the mapper from Node to Issue
 * TODO: currectly we just have quick written functions, we need a proper API and service
 */
class IssueService(private val graph: TheGraphFather) {

    fun getOpenIssuesForUser(userId: String): Set<Node> {
        val user = graph.getNode(userId) ?: throw ResourceNotFoundException("User with userId=$userId does not exist.")

        return user.incoming[RelationType.ASSIGNED_TO.toLabel()]
            .orEmpty()
            .filter { it.properties[PropertyKey.STATE.key] == IssueState.OPEN }
            .toSet()
    }

    fun listIssuesCreatedByUser(userId: String): Set<Node> {
        val user = graph.getNode(userId) ?: throw ResourceNotFoundException("User with userId=$userId does not exist.")
        return user.incoming[RelationType.CREATED_BY.toLabel()].orEmpty()
    }

}