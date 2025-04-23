package com.samshend.issuetracker.enums

import issuetracker.core.model.Label

enum class RelationType(val id: String, val displayName: String) {
    BELONGS_TO_PROJECT("belongs_to_project",   "BELONGS_TO_PROJECT"),
    BELONGS_TO_BOARD  ("belongs_to_board",     "BELONGS_TO_BOARD"),
    CREATED_BY        ("created_by",           "CREATED_BY"),
    MODIFIED_BY       ("modified_by",          "MODIFIED_BY"),
    ASSIGNED_TO       ("assigned_to",          "ASSIGNED_TO")
    ;

    fun toLabel() = Label(id, displayName)
}
