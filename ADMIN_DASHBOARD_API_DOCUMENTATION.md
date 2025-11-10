# Admin Dashboard API Documentation

This document provides detailed information about all the admin dashboard APIs available for the LogiNeko learning application.

## Table of Contents
1. [Base URL](#base-url)
2. [Authentication](#authentication)
3. [API Endpoints](#api-endpoints)
   - [Get Admin Statistics](#1-get-admin-statistics)
   - [Get Subscription Status Breakdown](#2-get-subscription-status-breakdown)
   - [Get Churn Rate](#3-get-churn-rate)
   - [Get Course Performance](#4-get-course-performance)
   - [Get Revenue By Type](#5-get-revenue-by-type)
   - [Get Active Users Metrics](#6-get-active-users-metrics)
4. [Common Response Structure](#common-response-structure)
5. [Error Handling](#error-handling)

---

## Base URL
```
/statistics
```

## Authentication
All admin dashboard endpoints require admin authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## API Endpoints

### 1. Get Admin Statistics

**Endpoint:** `GET /statistics/admin`

**Description:** Retrieves comprehensive admin statistics for a given year, including total users, revenue, monthly data, and growth metrics. **Note: This endpoint now filters only ACTIVE subscriptions.**

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| year | Long | No | 2025 | The year for statistics |

**Example Request:**
```bash
GET /statistics/admin?year=2025
```

**Response Body:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "totalUsers": 1500,
    "totalPremiumUsers": 450,
    "totalRevenue": 50000000,
    "totalQuestions": 5000,
    "year": 2025,
    "totalRevenueInYear": 20000000,
    "averageRevenueInMonth": 1666666.67,
    "monthWithHighestRevenue": 6,
    "yearOverYearGrowth": 0.25,
    "monthData": [
      {
        "month": 1,
        "revenue": 1500000,
        "newUsers": 120,
        "newPremiumUsers": 35,
        "monthOverMonthGrowth": 0.0
      },
      {
        "month": 2,
        "revenue": 1800000,
        "newUsers": 150,
        "newPremiumUsers": 42,
        "monthOverMonthGrowth": 20.0
      }
      // ... more months
    ]
  }
}
```

**Response Fields:**
- `totalUsers`: Total number of registered users (all time)
- `totalPremiumUsers`: Total number of premium users (all time)
- `totalRevenue`: Total revenue from all active subscriptions (all time)
- `totalQuestions`: Total number of questions/videos available
- `year`: The requested year
- `totalRevenueInYear`: Total revenue generated in the specified year from active subscriptions
- `averageRevenueInMonth`: Average monthly revenue for the year
- `monthWithHighestRevenue`: Month number with highest revenue (1-12)
- `yearOverYearGrowth`: Year-over-year growth rate as a decimal (0.25 = 25%)
- `monthData`: Array of monthly statistics
  - `month`: Month number (1-12)
  - `revenue`: Revenue for the month
  - `newUsers`: New user registrations
  - `newPremiumUsers`: New premium user subscriptions
  - `monthOverMonthGrowth`: Month-over-month growth percentage

---

### 2. Get Subscription Status Breakdown

**Endpoint:** `GET /statistics/admin/subscriptions/status`

**Description:** Provides a breakdown of all subscriptions by status (ACTIVE, INACTIVE, EXPIRED) with percentages.

**Query Parameters:** None

**Example Request:**
```bash
GET /statistics/admin/subscriptions/status
```

**Response Body:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "totalSubscriptions": 1000,
    "activeSubscriptions": 650,
    "inactiveSubscriptions": 200,
    "expiredSubscriptions": 150,
    "activePercentage": 65.0,
    "inactivePercentage": 20.0,
    "expiredPercentage": 15.0
  }
}
```

**Response Fields:**
- `totalSubscriptions`: Total number of all subscriptions
- `activeSubscriptions`: Number of active subscriptions
- `inactiveSubscriptions`: Number of inactive subscriptions
- `expiredSubscriptions`: Number of expired subscriptions
- `activePercentage`: Percentage of active subscriptions
- `inactivePercentage`: Percentage of inactive subscriptions
- `expiredPercentage`: Percentage of expired subscriptions

**Use Cases:**
- Display subscription health dashboard
- Show pie chart of subscription status distribution
- Monitor subscription lifecycle

---

### 3. Get Churn Rate

**Endpoint:** `GET /statistics/admin/churn`

**Description:** Calculates churn rate, retention rate, and growth rate for a specific month.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| year | int | Yes | - | Year for churn analysis |
| month | int | Yes | - | Month (1-12) for churn analysis |

**Example Request:**
```bash
GET /statistics/admin/churn?year=2025&month=3
```

**Response Body:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "year": 2025,
    "month": 3,
    "subscriptionsAtStart": 500,
    "newSubscriptions": 80,
    "canceledSubscriptions": 30,
    "expiredSubscriptions": 20,
    "subscriptionsAtEnd": 530,
    "churnRate": 10.0,
    "retentionRate": 90.0,
    "growthRate": 6.0
  }
}
```

**Response Fields:**
- `year`: Requested year
- `month`: Requested month (1-12)
- `subscriptionsAtStart`: Active subscriptions at the start of the month
- `newSubscriptions`: New subscriptions added during the month
- `canceledSubscriptions`: Subscriptions that were canceled (became inactive)
- `expiredSubscriptions`: Subscriptions that expired during the month
- `subscriptionsAtEnd`: Active subscriptions at the end of the month
- `churnRate`: Churn rate percentage (% of lost customers)
- `retentionRate`: Retention rate percentage (100 - churn rate)
- `growthRate`: Net growth rate percentage

**Calculation Formulas:**
```
churnRate = ((canceledSubscriptions + expiredSubscriptions) / subscriptionsAtStart) × 100
retentionRate = 100 - churnRate
growthRate = ((subscriptionsAtEnd - subscriptionsAtStart) / subscriptionsAtStart) × 100
```

**Use Cases:**
- Monitor subscription health over time
- Track customer retention trends
- Identify periods of high churn for investigation

---

### 4. Get Course Performance

**Endpoint:** `GET /statistics/admin/courses/popular`

**Description:** Returns the most popular courses with detailed performance metrics including student engagement and completion rates.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| limit | int | No | 10 | Number of top courses to return |

**Example Request:**
```bash
GET /statistics/admin/courses/popular?limit=5
```

**Response Body:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "popularCourses": [
      {
        "courseId": 1,
        "courseName": "Japanese N5 Grammar",
        "description": "Learn basic Japanese grammar for beginners",
        "thumbnailUrl": "https://example.com/thumbnail.jpg",
        "isPremium": true,
        "totalLessons": 20,
        "totalVideos": 150,
        "totalQuestions": 1500,
        "uniqueStudents": 450,
        "totalAttempts": 67500,
        "averageScore": 85.5,
        "completionRate": 75.0,
        "price": 500000
      }
      // ... more courses
    ],
    "totalCourses": 25,
    "totalPremiumCourses": 15,
    "totalFreeCourses": 10,
    "activeCourses": 23,
    "averageStudentsPerCourse": 180.5
  }
}
```

**Response Fields:**
- `popularCourses`: Array of course statistics (sorted by unique students)
  - `courseId`: Unique course identifier
  - `courseName`: Name of the course
  - `description`: Course description
  - `thumbnailUrl`: URL to course thumbnail image
  - `isPremium`: Whether course requires premium subscription
  - `totalLessons`: Number of lessons in the course
  - `totalVideos`: Number of videos in the course
  - `totalQuestions`: Total question attempts for this course
  - `uniqueStudents`: Number of unique students who attempted questions
  - `totalAttempts`: Total number of question attempts
  - `averageScore`: Average score as percentage of correct answers (0-100)
  - `completionRate`: Estimated completion rate (0-100)
  - `price`: Course price
- `totalCourses`: Total number of courses in the system
- `totalPremiumCourses`: Number of premium courses
- `totalFreeCourses`: Number of free courses
- `activeCourses`: Number of active courses
- `averageStudentsPerCourse`: Average students per course

**Score Calculation:**
```
averageScore = (number of correct answers / total attempts) × 100
```

**Use Cases:**
- Display top performing courses
- Identify courses that need improvement (low completion rates)
- Analyze premium vs free course engagement
- Course recommendation system

---

### 5. Get Revenue By Type

**Endpoint:** `GET /statistics/admin/revenue/by-type`

**Description:** Breaks down revenue by subscription type for a given year. Only includes ACTIVE subscriptions.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| year | int | No | 2025 | Year for revenue analysis |

**Example Request:**
```bash
GET /statistics/admin/revenue/by-type?year=2025
```

**Response Body:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "year": 2025,
    "totalRevenue": 15000000,
    "revenueByType": [
      {
        "subscriptionType": "MONTHLY",
        "count": 350,
        "revenue": 8750000,
        "percentage": 58.33,
        "averagePrice": 25000
      },
      {
        "subscriptionType": "YEARLY",
        "count": 150,
        "revenue": 6250000,
        "percentage": 41.67,
        "averagePrice": 41666.67
      }
    ],
    "mostProfitableType": "MONTHLY",
    "mostPopularType": "MONTHLY"
  }
}
```

**Response Fields:**
- `year`: Requested year
- `totalRevenue`: Total revenue from all subscription types
- `revenueByType`: Array of revenue breakdown by type (sorted by revenue descending)
  - `subscriptionType`: Name of the subscription type
  - `count`: Number of subscriptions of this type
  - `revenue`: Total revenue from this type
  - `percentage`: Percentage of total revenue
  - `averagePrice`: Average price per subscription
- `mostProfitableType`: Subscription type with highest total revenue
- `mostPopularType`: Subscription type with most subscriptions

**Use Cases:**
- Pricing strategy analysis
- Identify most profitable subscription tiers
- Revenue composition visualization
- Subscription type comparison

---

### 6. Get Active Users Metrics

**Endpoint:** `GET /statistics/admin/users/activity`

**Description:** Provides detailed user activity metrics including DAU, WAU, MAU, and daily activity breakdown.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| from | LocalDate | Yes | - | Start date (format: YYYY-MM-DD) |
| to | LocalDate | Yes | - | End date (format: YYYY-MM-DD) |

**Example Request:**
```bash
GET /statistics/admin/users/activity?from=2025-03-01&to=2025-03-31
```

**Response Body:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "from": "2025-03-01",
    "to": "2025-03-31",
    "totalActiveUsers": 850,
    "totalPremiumActiveUsers": 320,
    "totalFreeActiveUsers": 530,
    "dailyActiveUsers": 245,
    "weeklyActiveUsers": 580,
    "monthlyActiveUsers": 850,
    "averageAttemptsPerUser": 45.5,
    "dailyActivities": [
      {
        "date": "2025-03-01",
        "activeUsers": 220,
        "totalAttempts": 3500,
        "totalQuestions": 450
      },
      {
        "date": "2025-03-02",
        "activeUsers": 235,
        "totalAttempts": 3800,
        "totalQuestions": 480
      }
      // ... more days
    ]
  }
}
```

**Response Fields:**
- `from`: Start date of the analysis period
- `to`: End date of the analysis period
- `totalActiveUsers`: Total unique users who attempted questions in the period
- `totalPremiumActiveUsers`: Number of premium users who were active
- `totalFreeActiveUsers`: Number of free users who were active
- `dailyActiveUsers`: Number of unique users active yesterday (DAU)
- `weeklyActiveUsers`: Number of unique users active in the last 7 days (WAU)
- `monthlyActiveUsers`: Number of unique users active in the last 30 days (MAU)
- `averageAttemptsPerUser`: Average question attempts per active user
- `dailyActivities`: Array of daily activity data
  - `date`: The date
  - `activeUsers`: Number of unique active users on that date
  - `totalAttempts`: Total question attempts on that date
  - `totalQuestions`: Number of unique questions attempted on that date

**Use Cases:**
- User engagement tracking
- Activity trend analysis
- User retention monitoring
- Peak usage time identification
- DAU/WAU/MAU charts and graphs

---

## Common Response Structure

All API responses follow this structure:

```json
{
  "code": 200,
  "message": "Success",
  "data": { /* response data */ }
}
```

### Success Response
- `code`: HTTP status code (200 for success)
- `message`: Response message
- `data`: The actual response data (varies by endpoint)

### Error Response
```json
{
  "code": 400,
  "message": "Error message describing what went wrong",
  "data": null
}
```

---

## Error Handling

### Common Error Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | Success | Request completed successfully |
| 400 | Bad Request | Invalid parameters or malformed request |
| 401 | Unauthorized | Missing or invalid authentication token |
| 403 | Forbidden | User does not have admin privileges |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Server encountered an error |

### Error Response Example
```json
{
  "code": 400,
  "message": "Invalid date format. Expected YYYY-MM-DD",
  "data": null
}
```

---

## Implementation Notes for Frontend

### 1. Date Handling
- All dates should be in `YYYY-MM-DD` format (ISO 8601)
- Use date picker components that format dates correctly
- Example: `2025-03-15`

### 2. Number Formatting
- Revenue values are in the smallest currency unit (e.g., VND)
- Format large numbers with thousand separators for display
- Example: `1500000` → `1,500,000 VND`

### 3. Percentage Display
- Percentages are returned as decimal numbers (e.g., `25.5` means 25.5%)
- Add the `%` symbol in the UI
- Consider using progress bars or pie charts for visualization

### 4. Loading States
- Display loading indicators while fetching data
- Implement retry logic for failed requests
- Cache data appropriately to reduce API calls

### 5. Data Visualization Recommendations
- **Admin Stats**: Line chart for monthly revenue/users over time
- **Subscription Status**: Pie chart or donut chart
- **Churn Rate**: Line chart showing trend over multiple months
- **Course Performance**: Bar chart or table with sorting
- **Revenue By Type**: Pie chart or stacked bar chart
- **Active Users**: Line chart for daily activities, gauge for DAU/WAU/MAU

### 6. Refresh Intervals
- Real-time metrics: Refresh every 5 minutes
- Daily metrics: Refresh once per hour
- Historical data: Cache for 24 hours

### 7. Error Handling Best Practices
```javascript
try {
  const response = await fetch('/statistics/admin?year=2025', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (!response.ok) {
    throw new Error('Failed to fetch statistics');
  }

  const data = await response.json();

  if (data.code !== 200) {
    console.error('API Error:', data.message);
    // Display user-friendly error message
  }

  // Process successful response
  return data.data;
} catch (error) {
  console.error('Network Error:', error);
  // Display network error message
}
```

---

## Change Log

### Version 1.1 (Current)
- **BREAKING CHANGE**: Admin statistics endpoint now filters only ACTIVE subscriptions
- Added 5 new admin dashboard endpoints
- Created detailed DTOs for all endpoints
- Enhanced documentation with examples and use cases

### Version 1.0
- Initial admin statistics endpoint
- Basic user statistics endpoint

---

## Support

For questions or issues related to these APIs, please contact:
- Backend Team: backend@logineko.com
- API Documentation: docs@logineko.com

---

## Testing

### Sample cURL Commands

#### 1. Get Admin Statistics
```bash
curl -X GET "http://localhost:8080/statistics/admin?year=2025" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 2. Get Subscription Status
```bash
curl -X GET "http://localhost:8080/statistics/admin/subscriptions/status" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 3. Get Churn Rate
```bash
curl -X GET "http://localhost:8080/statistics/admin/churn?year=2025&month=3" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 4. Get Course Performance
```bash
curl -X GET "http://localhost:8080/statistics/admin/courses/popular?limit=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 5. Get Revenue By Type
```bash
curl -X GET "http://localhost:8080/statistics/admin/revenue/by-type?year=2025" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 6. Get Active Users Metrics
```bash
curl -X GET "http://localhost:8080/statistics/admin/users/activity?from=2025-03-01&to=2025-03-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

*Last Updated: 2025-03-15*
*API Version: 1.1*
