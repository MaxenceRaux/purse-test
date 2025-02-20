# Purse technical test

Here is my take on the realisation of Purse's technical test.

It consists in a simple API, made with Spring WebFlux, that handle several operations that can be made on purchases.
These purchases are stored in an embedded H2 database.
Here is a sample of the operations that can be performed:
 - Creating a new purchase
 - Making a purchase status progress, from IN_PROGRESS to AUTHORIZED to CAPTURED
 - Changing your payment method, as long as your payment has not been authorized yet
 - Fetching a single purchase
 - Fetching every purchase stored

## How to run it in intellij

Create a new run configuration and select PurseTestApplication.java as Main class
Be sure to use java 21 to be able to run it.

Once your configuration is done, you are good to go !

## Endpoints

Here is a list of the several endpoints at your disposal with a body example:

