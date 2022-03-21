# Business use cases

## Creating an order
An order should contain a ticker, e.g. SAVE for Nordnet, GME for GameStop, TSLA for Tesla, an order side indicating if the order is for buying or selling, a volume indicating how many stocks to purchase and price information indicating at which price to buy or sell and in which currency.
## Fetching an order
Should return an order given an id or a sensible response indicating an error. 
## Fetching summary
Calculates average/max/min price and the number of orders on that order side for a given ticker and date.

# Tech stack 
Java, Spring boot, h2, docker, junit, powermock, maven, spring data jpa, spring security, JWT, swagger, mvc, micro services 

# Design Enhancement for demo purpose [ outside scope of requirement ]
  1) Integrated well developed security implementation using spring security and JWT 
  2) Used spring profiling 
  3) Covered both unit test and spring integration tests 
  4) Documented Apis is using swagger library 
  
# Assumptions were taken into count while read through requirements 
  1) No more than one currency type order can be placed by user on a single day [ That means customer can only choose one currency type ]
  2) Tickers are static codes, mapped by TICKER_MAP data, and are usually provided/feed through Stock Exchanges API Integration.
  

# Development approach 

## Step1: Design entities for the application
   1) Business logic models -- Orders and TickerMap 
   2) Security models -- User, Role, RefreshToken 
        
## Step2: Design repository classes 
   1) Though i made use of spring data jpa for persistence but, still i have used JPQL native queries to handle Aggregation logic driven from db, rather than service layer 
     
## Step3: Create domain objects based on the requirements
   1) These are simple pojo classes nothing more or nothing less,  when we recieve request from client pojo's
     gather that information and map to entity object and vice versa 
     
   2) In order to provider some verification and validation on in  coming request and outgoing reponses i have to implement some util and valdiator classes example @TickerConstraint, @CurrencyConstraint, CurrencyValidator etc 

## Step4:  Implement Object Converters 
   1) to handle internal data conversion i have to extract that logic StringToEnumConverter, ToUpperCaseConverter etc   
   
##  Step5: Implement Security layer with JWT 
   1) This is application fully covered with Authentication and Authorization 
   2) For Demo purpose i have create User group with USER and ADMIN
   
   
##  Step6: Implement Service layer 
   1) The core logic of the business requirement is handled in this section, rest of the section just building block to support 
   that application. 
   
   2)In this service i have covered all the customer transaction use cases like createOrder, fetchOrder, fetchOrderSummary etc 
 
## Step7: Implement RestController 
   1) Auth controller will be taken care of Authentication and Authorization of user and admin group
   2) part of the this assignment i have implemented three end points which named createOrder, fetchOrder, fetchOrderSummary
        
        
## Step 8: Dockerise The application   
   1) Create Dockerfile with all required commands 
   

## Step 9: Orchestrate application using Docker compose 
   1) This feature mainly useful for multi container application, in our case we have only single container 
   
## Step 10: Create UNIT test coverage 

## Step 11: Create Spring integration tests
   1) run application on [test] profile -> spring.profiles.active=test
   2) And run mvn clean test to run all the tests as a group from terminal 



# How to run application 
 •	Execute/Run the application as Spring Boot application, or via maven using [mvn spring-boot:run]
 •	Application is also enriched with docker features and can be run as an independent containerized solution using [docker-compose up]
 •	App is currently configured to run on port 2022, however it can be changed in ‘application.properties’
 
 •	It uses internally H2 database, however, can be switched to any database by changing the drivers in properties file.
 Username -> sa
 Password -> password.1
 JDBC URL -> jdbc:h2:mem:devdb
 H2-Console URL -> [http://localhost:2022/order-book/h2-console]
 
 •	For integration testing, test profile is getting used, which executed on 3001, and connect to different instance of H2 database, as detailed below
 Username -> test
 Password -> test.1
 JDBC URL -> jdbc:h2:mem:testdb
 H2-Console URL -> [http://localhost:2022/order-book/h2-console]
 
 •	Application can be switched from ‘dev’ to ‘test’ profile by changing the value of ‘spring.profiles.active’ property in application.properties file
 
 •	Swagger Documentation is also enabled (only for dev profile) for firing the APIs on leisure, which can be access at [http://localhost:2022/bet-master/swagger-ui/#/]



 








