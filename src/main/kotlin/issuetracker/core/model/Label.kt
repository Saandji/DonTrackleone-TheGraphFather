package issuetracker.core.model

data class Label(
    val id: String,
    val name: String,
    val description: String = ""
)