Devoxx BE 2025 Loom Demo
========================

# Running the servers

1. You need to adjust the parameters in the [server.properties](server.properties) file to be able to run the servers.
2. Running the weather forecast servers: [WeatherServer.java](A_Weather-server/src/main/java/org/paumard/server/weather/WeatherServer.java)
3. Run the company servers: [CompanyServer.java](B_Companies-server/src/main/java/org/paumard/server/company/model/CompanyServer.java)

The demo files are located in the [C_Travel-agency-server](C_Travel-agency-server) module, under the packages [A_WeatherQuery](C_Travel-agency-server/src/main/java/org/paumard/server/travel/model/A_WeatherQuery), [B_CompanyQuery](C_Travel-agency-server/src/main/java/org/paumard/server/travel/model/B_CompanyQuery), and [C_TravelAgencyQuery](C_Travel-agency-server/src/main/java/org/paumard/server/travel/model/C_TravelAgencyQuery). There is no need to modify any other class than the ones in these packages.


# The Weather Forecast demo

The class is [A_WeatherQuery.java](C_Travel-agency-server/src/main/java/org/paumard/server/travel/model/A_WeatherQuery/A_WeatherQuery.java).

Getting the weather forecast from the client application consists in querying several servers at the same time. Since it is a weather forecast, all the servers are supposed to return the same result, so what is interesting here is to get a response as quickly as possible, interrupting the other requests once a response has been received.

## Step 01 [Step 01]

The objectives are the following.

1. Show the creation of a StructuredTaskScope.
2. Show the forking of several tasks (the corresponding Callables are already written).
3. Show the joining of the STS.
4. Show the SubTask object, and how it can be analyzed.


## Step 02 [Step 02]

The objective of this demo is to show how the default STS behaves with exceptions, by passing a callable that throws an exception.


## Step 03 [Step 03]

The objective of this demo is to pass a first Joiner to the creation of the STS: Joiner.anySuccessfulResultOrThrow().

1. Show that the first result closes the STS, and puts some subtasks in the UNAVAILABLE state.
2. Show that a Callable that throws an exception does not close this STS.
3. Show that the join() method returns the result, making the analysis of each subtastk useless.


# The Flight Company demo

This time the goal is different: we query several servers for the price of a flight, made of a departure city and a destination city, and we get responses from different companies. What we need is the best price.

The demo is in the [B_CompanyQuery.java](C_Travel-agency-server/src/main/java/org/paumard/server/travel/model/B_CompanyQuery/B_CompanyQuery.java) class.

The intial code prepares the Callables and put them in the list.

## Step 01 [Step 10]

Version 1 forks all the tasks to a StructuredTaskScope, and gets the subtasks in a list to analyze them.

Not only it is not ideal, but it looses the link between the air company and the flight, since what we get as a response is only the flight and its price.


## Step 02 [Step 11]

A tasks that throws an exception is added to the lot, this step shows how to manage this case with the basic StructuredTaskScope (no Joiner), to show again that this solution is not the one that fits this use case.


## Step 03 [Step 12]

This demo introduces the awaitAll() Joiner. It needs to be improved as only the price is shown, and we need to company and the flight.


## Step 04 [Step 13]

This demo adds records to keep track of which Company is providing what flight and price.



# The Travel Page Demo

This demo is about agregating the informations of Weather Forecast and Flight Price. The rule being: you need the Flight Price, and the Weather Forecast is optional.


## Step 01 [Step 20]

This is the initial situation, where all the code of the first two demos has been put in methods so that is can be called by Callables.


## Step 02 [Step 21]

Using the Joiner with Predicate and a Predicate that cancels the weather forecast if it is not here. The implementation uses an anonymous class as a non-denotable type. One can change the values in the [weather-agencies.txt](files/weather-agencies.txt) file to make weather servers slower.


# Adding ScopedValues


## Step 01 [Step 30]

Then we add a ScopedValue to be transmitted and read in the Pricing server. 