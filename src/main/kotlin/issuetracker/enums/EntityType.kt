package com.samshend.issuetracker.enums

import issuetracker.core.model.Label

enum class EntityType(val displayName: String) {
    PROJECT("Project"),
    BOARD("Board"),
    ISSUE("Issue"),
    COMMENT("Comment"),
    ORGANIZATION("Organization"),
    USER("User"),
    TEAM("Team");

    fun toLabel() = Label(name, displayName)
}
