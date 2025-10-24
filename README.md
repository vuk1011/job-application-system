# Fullstack web app for managing job applications in a company
## Description
This project was developed while taking a course on Advanced Java Technologies at my university. We were tasked with creating a fullstack web app for any kind of system. Spring was mandatory for backend, while the frontend could be implemented in any technology. 

I decided to model a job application system, represented by [this](https://github.com/vuk1011/job-application-system/blob/main/UML-ClassDiagram.png) UML class diagram.
A user of the platform is either a candidate or an employee.

A candidate can:
- register
- log in
- edit personal information
- upload/download/delete their PDF resume
- view published job postings
- apply for a job posting
- view their previous applications
- withdraw their aplication

An employee can:
- log in
- view submitted job applications
- choose to manage a job application
- view job applications they manage
- change the status for job applications they manage
- view personal information and the resume for the candidate associated with an application
- create, edit and delete job postings

The server app is powered by Spring Boot. There are two separate user interfaces, for both candidates and employees. These are Vue apps.

## Prerequisites
Node and MySQL

## Running the project
After cloning the repo to your device, you need to run the server app.

On Windows, run:
```sh
.\mvnw spring-boot:run
```
On Linux, run:
```sh
chmod +x mvnw
./mvnw spring-boot:run
```

To run the client app, navigate to its directory and run:
```sh
npm i
npm run dev
```
