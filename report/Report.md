# NoSQL Database

## Atypon Training: Final Project

### Khalid Nusserat



## Overview of the design

// DIAGRAM: stored documents structure

As the above diagram shows, the documents *- which represents the data that the user wants to store -* are stored in *collections*, and collections in turn are stored in a *database*, and the system contains a number of databases.

Each collection stores the following:

* The schema: The schema describes the structure that all of the collection's documents must follow.
* The documents.
* The indexes: The database allows the users to create an index on a property *- or more than one property, as multi-property indexing is also supported -*, which will make all future requests on that index much faster, since the database now no longer needs to search the entire collection to find the results.

In addition to storing users' databases, the system also stores a database of its own, called the *metadata database*, which as the name suggests stores information about the database. It contains two collections, namely:

* The users collection: Stores the credentials and the roles of the system users.
* The roles collection: Stores the authorities granted to each role, for example the `ROOT_ADMIN` role has all authorities. Users can of course create their own custom roles.

It is important to note that all objects, including objects like indexes, users data and roles description and whatsoever, are all stored as ***JSON documents***. There is a great benefit to having all objects in the application be stored in the exact same way, which is that the code used to store documents can now also be used to store other objects of interest, such as indexes and users data.

// DIAGRAM: separation between database and request handling

All of the logic concerning the database and the storage and retrieval of documents is completely decoupled and separated from the logic concerning handling requests, as the diagram above shows.

The requests are sent to the application as `REST` requests. First, Spring authenticate and authorizes requests using Spring Security. Then, using Spring's `RestController`, each endpoint is handled. Each endpoint wraps the entire request in a `DatabaseRequest`, which contains all the required information to process and handle the request, such as the `operation` type, the name of the `database`. The request is then forwarded to the default `DatabaseRequestHandler`. The handlers accept instances of `DatabaseRequest`, and then returns an instance of `DatabaseResponse`, which contains result of the request that is to be sent back to the user.

The default handler first passes the request through a number of filters. Each filter accepts a `DatabaseRequest` and returns `DatabaseRequest` after modifying it according to its own set of rules. The filtered request is then passed to the `DefaultOperationsHandler`, which then in turn forwards the request to a specific operation handler that is responsible for handling one and only one database operation, for example there can be a handler that handles the operation `ADD_DOCUMENT`. Upon receiving the response from the operation handler, the `DefaultOperationsHandler` returns the response back to the default handler, which then returns the response back to the controller and then to the user.



## In depth look at the design

### Storage

The most low level