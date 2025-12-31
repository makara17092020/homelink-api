# Deploying homelink-api to Render

This document explains how to deploy the project to Render using a Docker build. The repository includes `render.yaml` so Render can auto-apply settings when you connect the repo. The `Dockerfile` is a multi-stage build that runs `mvn package` to produce the JAR.

Recommended workflow (automatic, using `render.yaml`)

1. In the Render dashboard, click "New" → "Web Service" and choose the GitHub repo (or connect your repo). If you connect the repo, Render will detect `render.yaml` and apply the service definition.
2. Ensure the branch is set to `develop` (the config in `render.yaml` uses `develop`).
3. Add the following Environment Secrets in the Render Dashboard (Environment → Secrets / Environment Variables):

   - `SPRING_DATASOURCE_URL` (JDBC URL for your Postgres, e.g. `jdbc:postgresql://host:5432/dbname`)
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET` (your JWT signing secret)

   Note: In `render.yaml` these are referenced with `fromSecret:` so set them as secrets on the Render service.

4. Trigger a deploy by pushing to the `develop` branch:

```bash
git checkout develop
git add render.yaml DEPLOY.md Dockerfile
git commit -m "Add Render config and deployment instructions"
git push origin develop
```

5. Open the Render service page and watch the build logs. If there are build errors, open the live logs in Render and copy the error output for debugging.

Notes and troubleshooting

- Build command: `./mvnw -DskipTests package` (configured in `render.yaml`). You can change this to run tests if you prefer.
- Dockerfile: The repository Dockerfile is multi-stage and builds the jar inside the builder stage, so Render does not need a pre-built `target/*.jar`.
- Database connections: If you're using a managed Postgres (Neon, AWS RDS, etc.), ensure Render's outbound IPs or network settings allow connecting to your DB. For some providers you may need to allow Render's IPs or use a private network / VPC peering.
- Application port: Render sets a `PORT` environment variable. By default Spring Boot listens on 8080; if Render requires a different port you can set `SERVER_PORT` or use `server.port=${PORT:8080}` in your `application.properties`.

Optional: Manual setup

If you prefer not to use `render.yaml` you can create the Web Service manually and set the Build Command to `./mvnw -DskipTests package` and the Dockerfile path to `Dockerfile`.

If you want, I can also:

- Add Render environment variable examples for local development (e.g., `.env.example`) or
- Configure health checks / scaling settings into `render.yaml`.

If you push to `develop` and the deploy still fails, paste the Render build logs here and I'll debug the error.
