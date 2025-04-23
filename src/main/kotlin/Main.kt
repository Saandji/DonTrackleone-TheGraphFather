package com.samshend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import issuetracker.DonTrackleone
import issuetracker.core.TheGraphFather
import java.util.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val theGraphFather = TheGraphFather()
    val donTrackleone = DonTrackleone(theGraphFather)
    val mapper = jacksonObjectMapper()

    println("Welcome to The GraphFather Issue Tracker — where nothing gets unresolved... without consequences.")

    //  1 The Don adds his most trusted Capo
    val samBugfixelli = donTrackleone.createUser(
        UUID.randomUUID().toString(),
        "Sam Bugfixelli"
    )
    println("Capo registered: ${samBugfixelli.name}")

    // 2 Launch a top-secret operation
    val midnightPizzaRun = donTrackleone.createProject(
        id = UUID.randomUUID().toString(),
        name = "Operation Midnight Pizza Run",
        userId = samBugfixelli.id
    )
    println("Project created: ${midnightPizzaRun.name}")

    // 3 File a suspicious report (aka... an issue)
    val reportId = UUID.randomUUID().toString()
    donTrackleone.createIssue(
        id = reportId,
        name = "Someone left anchovies on the Margherita again 🍕😤",
        projectId = midnightPizzaRun.id,
        userId = samBugfixelli.id
    )
    println("New issue reported by ${samBugfixelli.name}")
    println("All records securely made in The GraphFather's book of problems.")
}