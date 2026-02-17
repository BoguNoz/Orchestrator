# Simple AI Orchestrator (Spring Boot)

## Overview

This project is a simple orchestration service built with **Spring Boot** that integrates multiple external APIs and aggregates their responses into a single unified output. The application demonstrates how different services — AI capabilities, external data providers, and search engines — can be composed into one cohesive system.

The orchestrator communicates with:

* **Microsoft Foundry** – used for AI-powered response generation and data summarization.
* **Google Custom Search API** – used to retrieve relevant web search results.
* **OpenWeather API** – used to fetch real-time weather data for a specified location.

The service acts as a central coordination layer that handles communication with external systems, collects their responses, and returns a combined result to the client. This pattern is commonly used in distributed systems and microservice architectures to reduce client complexity and centralize business logic.

The main goal of this project is to demonstrate:

* API orchestration and aggregation patterns
* Integration with multiple third-party services
* Basic AI service integration in backend systems
* Separation of concerns using service-based architecture
* External API composition in a Spring Boot application

This project is intended as a **proof-of-concept (PoC)** and educational example rather than a production-ready system. It provides a minimal implementation of an orchestration layer that can be extended with production-grade features such as error handling, resilience patterns, asynchronous processing, and observability.
