# Best Route
Find the best route available from one location to another.
The data is scheduled times, stop locations, and route information from [one public transport data access interface of Australia](http://www.transperth.wa.gov.au/About/Spatial-Data-Access),  released as a collection of inter-related textfiles following the [Google Transit Feed Specification (GTFS)](https://developers.google.com/transit/gtfs/reference/?hl=en).

## Project Task

Develop a shellscript which checks for and accepts five command-line arguments:

- the name of a directory containing a set of GTFS files, and 


- a starting and an ending location (as latitude/longitude pairs, typically home and work),

and produces a simple HTML webpage describing the "best" public transport route that should be taken to travel between the locations. The starting time of the journey should be as close as possible to (and obviously, after) the time the shellscript is run, so that the traveller can load the HTML page onto their smartphone and then commence their journey (ideally, a version of your program would run *on* the smartphone).

The definition of the "best" route is up to you, but reasonable ones include minimal walking time, minimal waiting time, and minimal travelling time.

## Some simplifying assumptions

1. The pathway from starting location to destination will typically involve walking to the starting bus, train, or ferry stop, travelling on the bus....., and walking from the final stop to the destination.
2. Assume that each walking segment (if any) is shorter than 1000 metres.
3. There is no need to consider the day-of-the-week on which a particular service runs. Assume that all services run every day.
4. In order to reduce the number of potential journeys that need searching, the actual travel on a bus, train, or ferry, must commence within one hour of leaving the starting location (home).

The webpage will report an error if a single journey (one bus, one train, ...) cannot be found between source and destination.

