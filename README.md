# OSPFlooding

Latest version: v1.0.0

## Checks

**Main**: [![Production CI](https://github.com/TymianekPL/OSPFlooding/actions/workflows/ci.yml/badge.svg)](https://github.com/TymianekPL/OSPFlooding/actions/workflows/ci.yml)

**Development**: [![Development CI](https://github.com/TymianekPL/OSPFlooding/actions/workflows/ci.yml/badge.svg?branch=development)](https://github.com/TymianekPL/OSPFlooding/actions/workflows/ci.yml)


---

## Releases

### Latest Release
**Version:**  
[![Latest Release](https://img.shields.io/github/v/release/TymianekPL/OSPFlooding)](https://github.com/TymianekPL/OSPFlooding/releases/latest)

**Download:**
- **Backend ZIP:**  
  https://github.com/TymianekPL/OSPFlooding/releases/latest/download/backend.zip
- **Frontend Build (ZIP/Node 22):**  
  https://github.com/TymianekPL/OSPFlooding/releases/latest/download/frontend-node22.zip
- **Frontend Build (ZIP/Node 24):**  
  https://github.com/TymianekPL/OSPFlooding/releases/latest/download/frontend-node24.zip

---

## Requirements

### Backend
1. [optional, for the database] docker
2. Java (recommended: 21, tested: 21, minimum: 17)

### Frontend
1. Node.js (recommended: 24, tested: 22, 24, recommended minimum: 20.19+)

---

## Build (Production)

### Backend

> ```bash
> cd backend
> ./gradlew clean build
> ```

_Note: The build will probably fail if you are building it for the first time. The solution is just to re-run the build command (`gradlew clean build`) This is a Java problem_

Artifacts end up in `backend/build/libs/`.

### Frontend

> ```bash
> cd frontend
> npm install
> npm run build
> ```

Build output goes into `frontend/dist/`.

---

## Run (Production)

### Backend

After building or downloading the backend:

1. Create backend/.env using backend/.env.example as a template.
2. If you don’t have a running database, start one:
    > ```bash
    > docker compose up -d --build
    > ```
3. Start the backend:
    > ```bash
    > cd backend
    > java -jar build/libs/backend-x.y.z-SNAPSHOT.jar
    > ```

_Note: replace `x.y.z` with the artifact version, for instance `1.0.0` (which would produce `backend-0.0.1-SNAPSHOT.jar`)_
_Note: the directory contains two jar files. Run  the one that does **not** contain the `-plain` suffix_
---

### Frontend (production)

Serve the static files from `dist/` using a static server of your liking. Example:

> ```bash
> cd frontend/dist
> npx serve .
> ```

---

## Run (Development)

### Backend (dev)

Firstly, go to the backend directory and create a file called `.env` (use the template for `/.env.example`) and fill it with actual data

If you don't have a production database, start one with `docker compose`
> ```bash
> docker compose up -d --build
> ```

Then run the following commands (skip `cd backend` if you are already in  the `backend` directory)

> ```bash
> cd backend
> ./gradlew bootRun
> ```

Runs the Spring Boot app with hot‑reload support.

### Frontend (dev)

> ```bash
> cd frontend
> npm install
> npm run dev
> ```

Starts the Vite dev server.

---

## Project Structure

* **backend/** — Spring Boot API
* **frontend/** — React UI
* **.github/workflows/** — CI/CD setup

---

## Notes

* Full‑stack app is built and run separately.
* CI runs for both `main` and `development`.
