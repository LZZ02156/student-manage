# Student-manage: rollcall initial implementation

This branch contains an initial implementation for the rollcall system: backend (Spring Boot + MyBatis), SQL init script, docker-compose and an updated Android Upload activity.

Quick start (development):
1. Start services with docker-compose:
   docker-compose up --build
   - This starts MySQL, backend, and nginx. MySQL init runs sql/init.sql.
2. Backend default config (application.yml) points to mysql:3306 with user root/root.
3. Upload folder mapped to backend/uploads and served at http://localhost:8081/uploads/

Notes:
- JWT authentication skeleton will be added in next iteration; currently endpoints are unauthenticated for quick testing. You requested JWT; I will add it in follow-ups.
- Adjust upload path in application.yml if needed.

Next steps performed after this commit:
- Add JWT filter and user management (login/register)
- Add more unit/integration tests
- Add CI and PR checks
