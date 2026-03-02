# Mintz Construction Estimator
### Product Requirements Document

| Author | Version | Stack | Status |
|---|---|---|---|
| John Mintz | 1.0 — March 2026 | Java + PostgreSQL + React | In Progress |

---

## Overview

The Mintz Construction Estimator is a Java-based application that calculates material requirements for residential wood-frame construction. It takes a set of room or structure dimensions and produces a detailed cut list and material estimate for floor systems, wall framing, and rough openings.

This is a personal portfolio project with real-world utility. It is built incrementally — each phase produces a working, testable application — so progress is always visible and the learning is always applied.

---

## The Problem This Solves

Residential framers and contractors currently estimate materials by hand or with generic spreadsheets. Errors are common, waste is expensive, and nothing is saved or reusable across projects. This app gives a carpenter-minded developer a tool they actually understand — and would actually use.

---

## Goals

- **Primary:** Build a complete, working Java application from scratch
- **Primary:** Apply Java fundamentals: classes, loops, file I/O, exceptions, and OOP
- **Primary:** Progress naturally from console app → file-based → database → web interface
- **Secondary:** Create a portfolio piece with real domain value

---

## Non-Goals

- This is not a CAD tool or architectural drawing application
- It does not handle electrical, plumbing, or HVAC estimation
- It is not intended for commercial release in Phase 1 or 2
- It does not need to be perfect before moving to the next phase — working beats perfect

---

## Who Uses This

- **John Mintz** — primary user, developer, domain expert
- Small residential contractors who frame by hand and estimate on paper
- Future: any builder who wants a simple, free framing estimator

---

## Development Roadmap

The application is built in four phases. Each phase builds directly on the last. No phase is skipped.

---

### Phase 1 — Console Application `Weeks 1–2`

**Goal:** Build a working console app that accepts dimensions and outputs framing material counts.

**Build:**
- Square footage calculator (already built — extend it)
- Wall stud calculator: linear feet of wall ÷ 16" OC spacing + doubles + corners
- Floor joist calculator: span ÷ 16" OC + rim joists
- Plywood sheet calculator: area ÷ 32 sq ft per sheet
- Rough opening calculator: door and window framing (king, jack, header studs)
- Simple console menu to choose calculation type

**Java Skills Practiced:**
- Classes and objects (`Room`, `Wall`, `Opening`)
- Methods and return types
- `Scanner` for user input
- Basic math and rounding (`Math.ceil` for material counts)
- `if/else` and `switch` statements

**✅ Done When:** Console app produces accurate stud/joist/plywood counts for any entered room dimensions.

---

### Phase 2 — File I/O `Weeks 3–4`

**Goal:** Read project dimensions from a CSV file. Write material estimates to an output file.

**Build:**
- Read room dimensions from a `.csv` file (room name, length, width, height)
- Loop through multiple rooms and calculate each
- Write output estimate to a formatted `.csv` or `.txt` report
- Handle file-not-found and malformed data gracefully
- Add a simple project name and date to the output

**Java Skills Practiced:**
- `FileReader` / `BufferedReader`
- String splitting and parsing
- For-each loops over collections
- Try-catch exception handling
- `ArrayList` to store multiple rooms

**✅ Done When:** App reads a multi-room CSV and writes a formatted estimate report without crashing on bad input.

---

### Phase 3 — Database Integration `Weeks 5–7`

**Goal:** Replace file I/O with a PostgreSQL database. Save and retrieve projects by name.

**Build:**
- Set up local PostgreSQL database
- Create tables: `projects`, `rooms`, `estimates`
- Connect Java to PostgreSQL using JDBC
- Save a new project to the database
- Load a saved project and recalculate
- List all saved projects
- Delete a project

**Java Skills Practiced:**
- JDBC connection and `PreparedStatement`
- SQL: `CREATE TABLE`, `INSERT`, `SELECT`, `DELETE`
- OOP: separating database logic into a DAO class
- Environment variables for DB credentials (not hardcoded)
- Basic data modeling

**✅ Done When:** Projects save to and load from a real database across separate application runs.

---

### Phase 4 — Web Interface (Spring Boot + React) `Weeks 8–12`

**Goal:** Expose the estimator as a REST API. Build a simple React frontend to drive it.

**Build:**
- Scaffold Spring Boot project
- Create REST endpoints: `POST /project`, `GET /project/{id}`, `GET /projects`
- Wire existing Java estimation logic into the API layer
- Build a React frontend with a form for entering dimensions
- Display the material estimate in a clean results table
- Deploy locally; optional: deploy to AWS (leverages existing Solutions Architect cert)

**Java Skills Practiced:**
- Spring Boot basics: `@RestController`, `@Service`, `@Repository`
- REST API design (GET, POST, PUT, DELETE)
- JSON serialization with Jackson
- React: `useState`, `useEffect`, `fetch()`
- Connecting a frontend to a backend API

**✅ Done When:** A browser form submits dimensions and displays a full material estimate from the Spring Boot API.

---

## Construction Reference — Framing Formulas

These are the standard framing calculations the application must implement correctly.

| Component | Formula | Notes |
|---|---|---|
| Wall Studs (16" OC) | `(linear ft × 12 / 16) + 1`, round up | Add 2 per corner, 1 per T-wall intersection |
| Top & Bottom Plates | `linear ft × 3` (double top, single bottom) | In linear feet of lumber |
| Floor Joists (16" OC) | `(span / 16 × 12) + 1`, round up | Add 2 rim joists (length of floor) |
| Plywood Subfloor | `area / 32`, round up | 4×8 sheet = 32 sq ft |
| Plywood Sheathing | `wall area / 32`, round up | Deduct rough opening areas |
| Door Rough Opening | 2 king + 2 jack + 1 header (doubled) | Header size varies by span |
| Window Rough Opening | Same as door + cripple studs above/below | Count cripples based on sill height |
| Cap Plate | Linear ft of exterior walls | Single plate, ties corners |

---

## A Note on This Project

Every line of code in this application is built on knowledge that matters — construction knowledge, military discipline, and the hard work of learning something real.

The goal is not a perfect application. The goal is a finished one.

> Build it like you'd frame a wall. Square the corners first. Then build up.