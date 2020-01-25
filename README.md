# Scala Nats Example

An example of how Scala can simplify interaction through NATS. 

This example uses Play JSON to serialize Case Classes as JSON strings, send them through NATS, and inflate them on the 
subscriber side.

BasicExample.scala assumes you have a NATS server running locally on port 4222 (but this can be changed in the code)