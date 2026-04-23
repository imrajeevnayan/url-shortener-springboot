# URL Shortener - Full Stack Application

A professional, feature-rich URL shortening service built with **Spring Boot** (backend) and **React + TypeScript** (frontend). This is an intermediate-level project demonstrating modern full-stack development practices.

## Features

### Core Features
- **URL Shortening** - Convert long URLs to short, shareable links
- **Custom Aliases** - Create memorable custom short URLs
- **QR Code Generation** - Auto-generated QR codes for every shortened URL
- **Click Analytics** - Detailed analytics with charts and visualizations
- **URL Management** - Full CRUD operations for your shortened URLs
- **Search & Filter** - Find URLs quickly with keyword search
- **Password Protection** - Secure links with password protection
- **Expiration Dates** - Set automatic expiration for temporary links
- **Responsive Design** - Works on desktop, tablet, and mobile

### Intermediate-Level Features
- **Spring Boot 3.2** with Java 17
- **Spring Data JPA** with H2 database
- **RESTful API** with proper HTTP status codes
- **Input Validation** with Bean Validation
- **Error Handling** with global exception handler
- **Scheduled Tasks** for cleaning up expired URLs
- **User Agent Parsing** for browser/OS/device detection
- **Base62 Encoding** for efficient short code generation
- **CORS Configuration** for cross-origin requests
- **TypeScript** frontend with strict typing
- **Recharts** for data visualization
- **Tailwind CSS** for modern styling

## Tech Stack

### Backend (Spring Boot)
| Technology | Purpose |
|-----------|---------|
| Spring Boot 3.2 | Application framework |
| Spring Data JPA | Database access |
| Spring Validation | Input validation |
| H2 Database | In-memory database |
| Lombok | Boilerplate reduction |
| ZXing | QR code generation |
| Maven | Build tool |

### Frontend (React)
| Technology | Purpose |
|-----------|---------|
| React 19 | UI framework |
| TypeScript | Type safety |
| Vite | Build tool |
| Tailwind CSS | Styling |
| shadcn/ui | UI components |
| Recharts | Charts & graphs |
| Lucide React | Icons |

## Project Structure

```
url-shortener/
├── backend/                          # Spring Boot Application
│   ├── mvnw                          # Maven wrapper
│   ├── pom.xml                       # Maven configuration
│   └── src/main/java/com/urlshortener/
│       ├── UrlShortenerApplication.java    # Main entry point
│       ├── entity/                   # JPA Entities
│       │   ├── Url.java              # URL entity
│       │   └── ClickEvent.java       # Analytics entity
│       ├── repository/               # Spring Data Repositories
│       │   ├── UrlRepository.java
│       │   └── ClickEventRepository.java
│       ├── service/                  # Business Logic
│       │   ├── UrlService.java       # Main service
│       │   ├── Base62Encoder.java    # Encoding utility
│       │   ├── QrCodeService.java    # QR generation
│       │   └── UserAgentParser.java  # UA parsing
│       ├── controller/               # REST Controllers
│       │   ├── UrlController.java    # URL CRUD API
│       │   ├── RedirectController.java  # Redirection
│       │   └── DashboardController.java # Stats API
│       ├── dto/                      # Data Transfer Objects
│       │   ├── UrlRequest.java
│       │   ├── UrlResponse.java
│       │   ├── AnalyticsDto.java
│       │   └── ApiResponse.java
│       └── config/                   # Configuration
│           ├── WebConfig.java
│           └── GlobalExceptionHandler.java
│
├── frontent/                         # React Application
│   ├── src/
│   │   ├── services/api.ts           # API client
│   │   ├── types/index.ts            # TypeScript types
│   │   ├── components/               # React components
│   │   │   ├── Navbar.tsx
│   │   │   ├── ShortenForm.tsx
│   │   │   ├── UrlCard.tsx
│   │   │   ├── UrlList.tsx
│   │   │   ├── DashboardStats.tsx
│   │   │   ├── AnalyticsPanel.tsx
│   │   │   └── BackendStatus.tsx
│   │   └── pages/
│   │       └── Home.tsx
│   ├── package.json
│   └── vite.config.ts
│
└── README.md
```

## API Endpoints

### URL Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/urls` | Create short URL |
| GET | `/api/urls` | List all URLs (paginated) |
| GET | `/api/urls/recent` | Get recently created URLs |
| GET | `/api/urls/{shortCode}` | Get URL by short code |
| GET | `/api/urls/search?keyword=` | Search URLs |
| PUT | `/api/urls/{id}` | Update URL |
| DELETE | `/api/urls/{id}` | Deactivate URL |
| DELETE | `/api/urls/{id}/permanent` | Delete permanently |
| GET | `/api/urls/{id}/analytics` | Get URL analytics |
| GET | `/api/urls/check/{shortCode}` | Check availability |

### Redirection
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/{shortCode}` | Redirect to original URL |

### Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | Get dashboard statistics |

## Quick Start

### Prerequisites
- **Java 21+** (Highly recommended for the latest features and security)
- **Node.js 20+**
- **Maven 3.9+** (Optional, Maven Wrapper is included)

### Running the Application

Follow these steps to get the full-stack application running on your local machine:

#### 1. Start the Backend (Spring Boot)
Open a terminal in the root directory and run:
```bash
cd backend
./mvnw spring-boot:run
```
*The backend will start on **http://localhost:8080***.

#### 2. Start the Frontend (React)
Open a **new** terminal in the root directory and run:
```bash
cd frontent
npm install
npm run dev
```
*The frontend will start on **http://localhost:3000***.

### Access Links
- **Frontend UI**: [http://localhost:3000](http://localhost:3000)
- **Backend API**: [http://localhost:8080/api](http://localhost:8080/api)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:urlshortenerdb`
  - User: `sa`
  - Password: (empty)

---

### Alternative: Building from Source
If you prefer to build a production JAR:

**Backend:**
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/url-shortener-1.0.0.jar
```

**Frontend:**
```bash
cd frontent
npm install
npm run build
npm run dev
```

## Configuration

### Backend
Edit `backend/src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Database (H2 in-memory)
spring.datasource.url=jdbc:h2:mem:urlshortenerdb

# URL Shortener settings
app.url-shortener.base-url=http://localhost:8080
app.url-shortener.default-expiry-days=30
```

### Frontend
Set environment variable for API URL:

```bash
# Development
VITE_API_URL=http://localhost:8080/api

# Or use localStorage in browser
# Click "Setup" in the navbar to configure
```

## H2 Database Console

Access the H2 console at `http://localhost:8080/h2-console` with:
- JDBC URL: `jdbc:h2:mem:urlshortenerdb`
- Username: `sa`
- Password: (empty)

## Features in Detail

### URL Shortening
- Paste any long URL and get a short 7-character code
- Optional: Set a custom alias like `my-link`
- Optional: Add title and description for organization
- Optional: Set expiration date
- Optional: Password protection

### QR Codes
- Every shortened URL gets an auto-generated QR code
- Click the QR icon on any URL card to view
- Download QR codes as PNG images
- Use for print materials, business cards, etc.

### Analytics
- Click the chart icon to view detailed analytics
- Charts: Daily clicks over time
- Statistics: Browsers, Operating Systems, Devices, Referrers, Countries
- Total clicks and unique visitor counts

### URL Management
- Copy short URLs with one click
- Open short URLs in new tab
- View analytics for any URL
- Delete URLs you no longer need
- Search through all your URLs

## Deployment

The frontend is built as a static site and can be deployed to any static hosting service (Netlify, Vercel, GitHub Pages, etc.).

The backend is packaged as an executable JAR and can be deployed to:
- Any server with Java 17+
- Heroku
- AWS Elastic Beanstalk
- Google Cloud Run
- Docker containers

## License

MIT License - feel free to use this project for learning or commercial purposes.
