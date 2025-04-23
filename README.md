# DonTrackleone Graph Issue Tracker

A toy Kotlin graph-database plus a minimal issue-tracker API built on top—perfect for learning how graph engines work,
modeling nodes/relationships, and exploring traversals (BFS/DFS) in a real-world use case.

## 🚀 Features

- **Core Graph Engine** (`TheGraphFather`)
    - Create/read nodes & relationships with typed `Label`s
    - O(1) link-traversal via index-free adjacency
- **Domain Model** (`DonTrackleone`)
    - Entities: `Project`, `Board`, `Issue`, `User`, `Team`, etc.
    - Relationships: `CREATED_BY`, `BELONGS_TO_PROJECT`, `ASSIGNED_TO`, …
    - Simple CRUD-style API for projects, boards, users, issues
- **Type-Safe Labels**  
  Enums for entity types, relation types, and property keys

## Mini roadmap:

  - Graph enhancements: 
    - Breadth-First Search (BFS) & Depth-First Search (DFS) for relation searches
    - Path finding (`findPathBfs`)
  - Proper unit tests coverage
  - Extract Entities into their own Service classes
  - Deploy on AWS
  - quick React UI (?)
  - 