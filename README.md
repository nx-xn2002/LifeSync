# LifeSync

**Synchronise Your Life Rhythm, Safeguard Your Health**

LifeSync is a mobile health app designed to monitor health status and provide actionable recommendations. Leveraging advanced algorithms and user-friendly design, it empowers users to achieve better health outcomes.

## Project Overview

- **App Name:** LifeSync
- **Mission:** Synchronise your life rhythm, safeguard your health.
- **Core Features:**
    - Health monitoring (heart rate, blood pressure).
    - Personalised health recommendations.
    - Long-term health tracking.

## Technology Stack

- **Frontend:** React Native (to be implemented).
- **Backend:**
    - Spring Boot
    - MyBatis Plus
    - AIGC (Artificial Intelligence-Generated Content)
- **Database:** Relational Database (designed and implemented).

## Current Progress

### Backend Implementation

#### Database Schema

1. **User Information Table (`user_info`)**

| Field Name | Description                                            |
|-----------| ------------------------------------------------------ |
| `id`      | Unique identifier for each user.                       |
| `username` | User's chosen username.                                |
| `password` | Encrypted password for authentication purposes.        |
| `create_time` | Timestamp of when the user was created.                |
| `update_time` | Timestamp of the last update to the user's record.     |
| `is_deleted` | Logical deletion flag (e.g., 0 = active, 1 = deleted). |

2. **Basic Health Information Table (`basic_health`)**

| Field Name  | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| `user_id`   | Foreign key referencing the `user_id` in the `user_info` table. |
| `health_id` | Unique identifier for each health information record.        |
| `height`    | User's height in centimetres or meters.                      |
| `weight`    | User's weight in kilograms.                                  |
| `age`       | User's age in years.                                         |
| `gender`    | User's gender (e.g., 'Male', 'Female', 'Other').             |
| `create_time` | Timestamp of when the record was created.                    |
| `update_time` | Timestamp of the last update to the record.                  |
| `is_deleted` | Logical deletion flag (e.g., 0 = active, 1 = deleted).       |

3. **Detection Records Table (`detection_record`)**

| Field Name     | Description                                                  |
| -------------- | ------------------------------------------------------------ |
| `user_id`      | Foreign key referencing the `user_id` in the `user_info` table. |
| `record_id`    | Unique identifier for each detection record.                 |
| `systolic_bp`  | Systolic blood pressure value (high pressure).               |
| `diastolic_bp` | Diastolic blood pressure value (low pressure).               |
| `heart_rate`   | Heart rate value in beats per minute (BPM).                  |
| `create_time`  | Timestamp of when the detection was performed.               |
| `update_time`  | Timestamp of the last update to the record.                  |
| `is_deleted` | Logical deletion flag (e.g., 0 = active, 1 = deleted).       |

### Frontend Implementation

Frontend development is yet to begin. The plan is to use **React Native** to create a cross-platform mobile application.

## Future Work

1. **Frontend Development:**
    - Set up the React Native environment.
    - Implement the UI and integrate backend APIs.
2. **Backend Enhancements:**
    - Optimise database queries using MyBatis Plus.
    - Integrate AIGC for personalised health suggestions.
3. **Testing and Deployment:**
    - Perform comprehensive testing to ensure accuracy and reliability.
    - Deploy the app to mobile app stores.

## Getting Started

### Prerequisites

- **Frontend:** Node.js, npm/yarn (to be detailed after frontend setup).
- **Backend:**
    - Java 17+
    - Maven
    - MySQL or compatible relational database.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/nx-xn2002/LifeSync.git
   ```

2. Navigate to the backend directory and build the project:
   ```bash
   cd backend
   mvn clean install
   ```

3. Set up the database schema (scripts will be provided in the `db` folder).

4. Start the backend server:
   ```bash
   mvn spring-boot:run
   ```

### Usage

Once the backend server is running, frontend setup instructions will follow.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request. For major changes, kindly open an issue to discuss your ideas first.

## License

[MIT License](LICENSE)
