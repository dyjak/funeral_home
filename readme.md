# Zakład pogrzebowy
frontend app- https://funeralhomev2.netlify.app/
backend app-https://springboot-backend-hnmc.onrender.com
## Baza danych

Schemat bazy danych:  
![Schemat ERD](readme/erd.png)

```
INF_CZARNI
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
├─ database.sql
├─ frontend-next
│  ├─ .vite
│  │  └─ deps
│  │     ├─ package.json
│  │     └─ _metadata.json
│  ├─ app@0.0.0
│  ├─ eslint.config.js
│  ├─ index.html
│  ├─ package-lock.json
│  ├─ package.json
│  ├─ postcss.config.js
│  ├─ public
│  │  └─ vite.svg
│  ├─ README.md
│  ├─ reset-next.bat
│  ├─ src
│  │  ├─ assets
│  │  │  └─ react.svg
│  │  ├─ components
│  │  │  └─ NotFound.jsx
│  │  ├─ hooks
│  │  │  └─ useClientData.jsx
│  │  ├─ layouts
│  │  │  ├─ Navigation.jsx
│  │  │  └─ RootLayout.jsx
│  │  ├─ main.jsx
│  │  ├─ pages
│  │  │  ├─ AddUser.jsx
│  │  │  ├─ admin-panel
│  │  │  │  ├─ Admin.jsx
│  │  │  │  ├─ AdminLayout.jsx
│  │  │  │  └─ UserManagement.jsx
│  │  │  ├─ App.jsx
│  │  │  ├─ ClientForm.jsx
│  │  │  ├─ ClientInfo.jsx
│  │  │  ├─ Dashboard.jsx
│  │  │  ├─ Log.jsx
│  │  │  ├─ MainPanel.jsx
│  │  │  ├─ Profile.jsx
│  │  │  ├─ Raports.jsx
│  │  │  ├─ Receptionist.jsx
│  │  │  ├─ TaskPlans.jsx
│  │  │  └─ UpdateUser.jsx
│  │  └─ styles
│  │     └─ index.css
│  ├─ tailwind.config.js
│  └─ vite.config.js
├─ GUI_realizacja_nowego_zlecenia.simp.bak
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ readme
│  └─ erd.png
├─ readme.md
├─ sample_sql_tmp.sql
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ zaklad
   │  │     └─ pogrzebowy
   │  │        └─ api
   │  │           ├─ config
   │  │           │  └─ WebConfig.java
   │  │           ├─ controllers
   │  │           │  ├─ AuthController.java
   │  │           │  ├─ ClientController.java
   │  │           │  ├─ OrderController.java
   │  │           │  ├─ ReportController.java
   │  │           │  ├─ TaskAssignmentController.java
   │  │           │  ├─ TaskController.java
   │  │           │  └─ UserController.java
   │  │           ├─ db
   │  │           │  └─ SeedDB.java
   │  │           ├─ models
   │  │           │  ├─ Client.java
   │  │           │  ├─ Order.java
   │  │           │  ├─ Report.java
   │  │           │  ├─ Task.java
   │  │           │  ├─ TaskAssignment.java
   │  │           │  └─ User.java
   │  │           ├─ PogrzebowyApplication.java
   │  │           ├─ repositories
   │  │           │  ├─ ClientRepository.java
   │  │           │  ├─ OrderRepository.java
   │  │           │  ├─ ReportRepository.java
   │  │           │  ├─ TaskAssignmentRepository.java
   │  │           │  ├─ TaskRepository.java
   │  │           │  └─ UserRepository.java
   │  │           ├─ security
   │  │           │  ├─ JwtAuthFilter.java
   │  │           │  ├─ JwtUtil.java
   │  │           │  └─ SecurityConfig.java
   │  │           └─ services
   │  │              ├─ ClientService.java
   │  │              ├─ IClientService.java
   │  │              ├─ IOrderService.java
   │  │              ├─ IReportService.java
   │  │              ├─ ITaskAssignmentService.java
   │  │              ├─ ITaskService.java
   │  │              ├─ IUserService.java
   │  │              ├─ OrderService.java
   │  │              ├─ ReportService.java
   │  │              ├─ TaskAssignmentService.java
   │  │              ├─ TaskService.java
   │  │              └─ UserService.java
   │  └─ resources
   │     ├─ application.properties
   │     └─ DB.sql
   └─ test
      └─ java
         └─ zaklad
            └─ pogrzebowy
               ├─ api
               │  └─ db
               │     └─ SeedDBTest.java
               └─ PogrzebowyApplicationTests.java

```
