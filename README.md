Webfootprinting Project
=======================

## Basic premise
- Take input from the user
- Grab related information from the public domain
- "Clean" the information
- Perform analysis and statistics

## Overview of parts
- Web interface
- Java background
	- data storage
	- scrapers
	- cleaners
	- image hasher
	- statistics
	- grouping
	- web interface information server

## Data Storage
All of the data storage classes are supposed to be json-serializable to allow
easy storage and retrieval into files.

`PersonalData`

- stores data as attribute/values from each record collected
- maintains a unique id that should persist between transformations

`PersonalDataStorage`

- stores all of the PersonalData collected about one query
- keeps each of the data from each site separate
- filters the useless (name only) results collected

`FieldBuilder`

- used when collecting data
- allows each field to have multiple values
- separates multiple values with '|'
- adds directly into PersonalData

## Scrapers
These scrapers use APIs and websites to collect information about certain users

All of them extend the base interface `Extractor`, which provides the method 
`getResults()` to get results about a certain query.

`AbstractLoader` provides a skeleton implementation for loading data, where
each class only needs to do one query and will spawn additional classes to
do additional queries

`PagingLoader` extends the functionality of `AbstractLoader` and will return
a new `PagingLoader` for each additional page that the original `Extractor` 
must load.

The badly named `OuterLoader` extends `PagingLoader` by creating a group of
`Extractor`s that need to be used for each page. Each result is loaded from 
one `Extractor` that is typically an instance of `IndividualExtractor`. 
`IndividualExtractor` only loads one result from it's request and returns it.

All of these Loaders are one-load-read-indefinitely type classes by using
`LazyField` to store the loaded data. The query is created with the 
constructor and the results only need to be found and parsed once.

## Cleaners
Cleaners should clean the `PersonalData` input and return as `PersonalData`
output with the same identifiers. 

`Cleaner` is the class that instantiates all of the cleaners properly and 
provides the method `clean(PersonalData)` to clean.

`DataCleaner` should be extended to provide cleaning capabilities. It's sole 
method `clean(PersonalData, PersonalData)` removes attributes from the source
(first) argument and stores their corresponding cleaned versions in the
destination (second) argument.

## Grouping
Provides method for grouping the gathered data into real-life individuals, 
where all of the information in a group is the same real-life individual.

Uses email, twitter handle, phone number, and profile picture as defining
characteristics of a real-life individual. If any of those characteristics
match, then the two profiles are grouped as the same person.

## Statistics
Calculates basic statistics about the data collected for a given input query.
Determines the most common and most distinguished values for each field. 

Generates the percentage accuracy of the most common attributes, i.e. the
fraction of individuals that have that attribute, and the percentage 
coverage of the values, i.e. the fraction of records that actually have a value
for the given attribute.

## Web interface
The web interface is mainly contained within the website folder. This website 
has a php script `statgrabber.php` that communicates with the java process
running in the background somewhere else on the hosting server via a local
port. 

The main page `index.php` displays the results from the java process
as a table and provides a very basic form for entering the query.

The class `PhpCommunicator` does the communication with the `statgrabber.php`
script over a Socket. 

## Other interesting things
`LazyField`

This class provides a basic way to create fields on the fly when they are 
needed instead of on class instantiation. It can be used for singletons, 
data needed to be grabbed with IO, etc. 

`NameComparison`

The class provides a way to compare names to see if they are essentially
equivalent. It uses the Name API provided by Pipl to do name queries and 
matches. It is most used in this project to provide an endpoint for the 
paging through results. 