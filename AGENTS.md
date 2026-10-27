#Project
A Java spring-boot project handling HTTP requests for Task object with fields id (long), title (required, not blank) and done (boolean).
#Scope
Work only in the "C:\src\Gong\hello-agent\tasks" folder.
No need to scope or modify files outside this folder.
#Guidelines
Add a short method explanation above each new method including potential edge cases.
Flag any redundant or duplicate code.
Controllers only handle HTTP, logic goes in a service class.
Every new endpoint and edge case need a MockMvc test added into "C:\src\Gong\hello-agent\tasks\src\test\java\com\example\tasks\TasksApplicationTests.java"
Make sure tests check potential status codes related to the test such as 404,400,200 etc.
Tests should be independent and should not depend on execution order.
Run tests after every change that may affect the outcome of a potential test.
Before reporting completion, verify that the final test run passed. If tests fail or cannot run, report.
#build and commit
Build with maven
Run tests from project root with .\mvnw.cmd test
Do not commit automatically. Ask for explicit confirmation before creating a commit.