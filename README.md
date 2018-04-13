# Switchfully

www.switchfully.com

## Örder technical implementation

-  Large exercise to conclude the following modules:
    - Dependency Management
    - Spring (boot)
    - REST
- Contains the basic skeleton of the application (not the full implementation):
    - Multi-module Maven setup
    - Integration test configuration
    - Spring boot executable
    - Logging
        - To enable logging, we need to depend on a logging library, framework. If we want to use Logback as our logging framework,
         we need to include it (and jcl-over-slf4j (which implements the Commons Logging API)) 
        on the classpath as a dependency. The simplest way to do that is through the Spring boot Starters which all depend on 
        spring-boot-starter-logging (which contains the required dependencies such as Logback). For a web application you only need   spring-boot-starter-web since it depends transitively on the logging Starter.
    - Jenkins `Jenkinsfile`
        - Jenkins requires the Pipeline plugin
        - Jenkins requires the Build Monitor View
        - Jenkins runs as a service (task manager) on Windows
    - Event Publishing with Spring (!)
        - (to update the stock of an item when an order is created in a very decoupled way)

## Usage

### Running Locally
- Run `mvn clean install`
- Multiple ways of running:
    - Inside the target folder of jar, run the `war-1.0-SNAPSHOT.jar` using the `java -jar` command
    - Run Application.java inside the IDE
    - Execute command `mvn spring-boot:run` from inside module (folder) war
- Surf to `http://localhost:9000/example`

## REST Endpoints

### Customers
- **Get Customer for ID**
    - Path: `/customers/{id}`
    - Methods: [GET]
    - Produces: [application/json]
- **Create a new Customer**
    - Path: `/customers`
    - Methods: [POST]
    - consumes: [application/json]
    - Produces: [application/json]
    - Example
        - Call: `http://localhost:9000/customers/`
        - Request Body:
            ```json
            {
                "firstname": "Jucky",
                "lastname": "Chun",
                "email": {
                    "localPart": "jucky.chun",
                    "domain": "kangfa.com",
                    "complete": "jucky.chun@kangfa.com"
                },
                "address": {
                    "streetName": "Livablestreet",
                    "houseNumber": "85D",
                    "postalCode": "3052TK",
                    "country": "China"
                },
                "phoneNumber": {
                    "number": "488522210",
                    "countryCallingCode": "+32"
                }
            }
            ```
- **Get all Customers**
    - Path: `/customers`
    - Methods: [GET]
    - Produces: [application/json]

### Items
- **Create a new Item**
    - Path: `/items`
    - Methods: [POST]
    - consumes: [application/json]
    - Produces: [application/json]
    - Example 
        - Call: `http://localhost:9000/items/`
        - Request Body:
            ```json
            {
                "name": "Headphone",
                "description": "This is a cool headphone",
                "price": 49.95,
                "amountOfStock": 10
            }
            ```
- **Get all Items**
    - Path: `/items`
    - Methods: [GET]
    - Produces: [application/json]
- **Update the specified Item**
    - Path: `/items/{id}`
    - Methods: [PUT]
    - consumes: [application/json]
    - Produces: [application/json]
    - Example
        - Call: `http://localhost:9000/items/7287faca-y0ur-0wn1d-891f-1f102b14ce3f`
        - Request Body:
            ```json
            {
                "name": "Headphone",
                "description": "Coolest headphone on the market!",
                "price": 35.45,
                "amountOfStock": 10
            }
            ```

### Orders
- **Create a new Order**
    - Path: `/orders`
    - Methods: [POST]
    - consumes: [application/json]
    - Produces: [application/json]
        - Call: `http://localhost:9000/orders`
        - Request Body:
            ```json
            {
                "customerId": "e34d90ca-ecc3-49c3-ab6b-c9b353f5fef3",
                "itemGroups": [
                    {
                        "itemId": "de2f9283-1f82-44ad-a869-3b2f80f73724",
                        "orderedAmount": 1
                    },
                    {
                        "itemId": "7287faca-25c9-43f8-891f-1f102b14ce3f",
                        "orderedAmount": 2
                    }
                ]
            }
            ```
- **get all Orders**
    - Path: `/orders`
    - Methods: [GET]
    - Produces: [application/json]
- **Reorder the specified order**
    - Path: `/orders/{id}/reorder`
    - Methods: [POST] (no request body should be provided)
    - Produces: [application/json]
- **Get all orders for the specified customer**
    - Path: `/orders/customers/{customerId}`
    - Methods: [GET]
    - Produces: [application/json]

## General remarks

### Regarding inspecting this code: 
the best way to take a look at it is by `cloning` (not forking) the repository 
on your development laptop and go through it in IntelliJ (use `F4` or `left-click` + `ctrl` to go into a method or 
object). Pick one story (e.g. creating an order) and start from the `controller`, then work your way down to the 
`service` and then to the `domain`. That way, you're really following a context *flow*. Randomly inspecting code 
without any context won't give much clarity.

### Regarding events: 
Spring's Event functionality was used (search for the `ItemEventHandler` and `OrderItemCreatedEvent`) 
to update the stock of an Item when an Order contains that Item. 
It decouples the creation of an Order with updating the stock of an Item.
A small tutorial on Event Publishing with Spring (it's really not difficult) can be found here: http://www.baeldung.com/spring-events

### Regarding Ubiquitous language: 
The term (and object)`OrderItem` exists in the domain instead of `ItemGroup`. 
In the `api` module, `ItemGroupDto` was used (as the Dto of `OrderITem`). This can be seen as a temporary state. 
In the end, we should use one or the other (ubiquitous language). We do believe `OrderItem` is a better name 
(item in the order) than `ItemGroup(Dto)`. If the business is convinced of this, we could refactor the code to only 
contain `OrderItem` and `OrderItemDto`. If not, we should refactor `OrderIem` to `ItemGroup`.

### Regarding logging: 
Some essential, but limited logging is performed: All the REST requests are logged (logger level TRACE) 
in `LoggingInterceptor` class. All exceptions (logger level ERROR) in `ControllerExceptionHandler`, which has 
the `@ControllerAdvice` annotation, are logged.

### Regarding reuse, design: 
Generics and abstraction are used to create reusable repositories, builders, mappers, 
entities,... They offer code-reuse, but also help in making it clear to other developers how those kind 
of objects should behave.

### Regarding Entities 
Every class in the domain that is an entity (evolves over times, is unique (has uniqueness, 
almost always resulting in an ID) extends the `Entity` class which has the ID as its state and behavior. 
All classes extending `Entity` inherit this state and behavior.

### Regarding repositories 
All repositories (extending `Repository`) will set the ID for the entity upon 
creation of that entity.

### Regarding TDD
All flows and edge cases are covered. Around 90% of the code was written *test-first*. Do inspect those tests. You'll see (for example) 
that for the services, I have both written *integration* and *unit* tests, it might be interesting to see which test covers what cases.
