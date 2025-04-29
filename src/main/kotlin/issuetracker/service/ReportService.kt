package com.samshend.issuetracker.service

import com.samshend.issuetracker.enums.PropertyKey
import com.samshend.jobscheduler.Scheduler
import issuetracker.core.model.Node
import model.JobDefinitionBuilder

class ReportService(
    private val issueService: IssueService,
    private val scheduler: Scheduler
) {

    fun generateReportJob(userId: String): String {
        val jobId = "report-job-${System.currentTimeMillis()}"

        val job = JobDefinitionBuilder<String>()
            .id(jobId)
            .name("Generate Issue Report")
            .resultType(String::class.java)
            .action {
                val issues = issueService.listIssuesCreatedByUser(userId)
                val report = buildSimpleReport(issues)
                report
            }
            .build()

        scheduler.schedule(job)
        return jobId
    }

    fun awaitReportResult(jobId: String) =
        scheduler.awaitResultBlocking(jobId, String::class.java)

    private fun buildSimpleReport(issues: Set<Node>): String {
        return buildString {
            appendLine("=== Issue Report ===")
            appendLine("Total User issues: ${issues.size}")
            for (issue in issues) {
                val issueStatus = issue.properties[PropertyKey.STATE.key]
                //append line with structure "Issue: My AwesomeIssue (UUID) is in OPEN status.
                appendLine("Issue: - ${issue.name} (${issue.id}) is in $issueStatus status")
            }
        }
    }
}