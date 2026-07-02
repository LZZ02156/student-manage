# Pull Request: Initial rollcall implementation — backend + JWT auth + Android fixes

This PR adds an initial implementation for the rollcall system.

Summary
- Backend: Spring Boot 3 + MyBatis, JWT-based auth skeleton, users table and mapper, roll call endpoints (getStudentList, uploadRecord, getRollCallResult, randomCheck), file upload handling with validation, global exception handler.
- Docker: Dockerfile, docker-compose (mysql, backend, nginx), nginx config mapping /uploads.
- SQL: initialization script (sql/init.sql) with sample students/courses/users.
- Android: FileProvider config, UploadRecordActivity improvements (FileProvider, fused location, RequestBody multipart), Retrofit client & interface.

How to run
1. docker-compose up --build
2. POST /auth/registerDefaultTeacher to create a dev teacher (username: teacher, password: password)
3. POST /auth/login to get a token
4. Use Authorization: Bearer <token> to call protected endpoints
5. uploads are served at http://localhost:8081/uploads/

What’s next (planned)
- Improve role-based authorization mapping and tests
- Add user management APIs (register, change password, delete)
- Add validation, better error responses, and unit/integration tests
- Add CI (GitHub Actions)

Notes
- jwt.secret in application.yml is for development only. Use strong secrets in production.
- registerDefaultTeacher is a developer convenience and should be replaced with a proper user onboarding flow.
