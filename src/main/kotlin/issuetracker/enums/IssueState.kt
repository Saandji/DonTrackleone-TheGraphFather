package com.samshend.issuetracker.enums

enum class IssueState(val displayName: String, val description: String) {
    OPEN("Open", "Open for business"),
    IN_PROGRESS("In Progress", "Currently being handled by the crew"),
    TO_BE_DISCUSSED("To be discussed", "Needs a sit-down with the family"),
    CANT_REPRODUCE("Can't Reproduce", "Can’t reproduce, like a ghost"),
    DUPLICATE("Duplicate", "We’ve seen this problem before"),
    INCOMPLETE("Incomplete", "Someone forgot to finish the cannoli"),
    RESOLVED("Resolved", "Issue is taken care of... permanently"),
    FINISHED("Finished", "It’s done, boss"),
    WONT_FIX("Won't fix", "Not worth the family’s time"),
    OBSOLETE("Obsolete", "Old news, forget about it");

    override fun toString(): String = displayName
}