# Solving the complexity of a software (Work In Progress)
## A note
The current code does not match the following description.

## Abstract

It eventually happens, at some point in time, that the teams involved in a software development/maintenance are losing control of the software flows. This could be caused by turnover, short deadlines, design issues, general complexity of the code, ...

## What is a software
A software is basically made of data (or a collection of...) and processing units (processors).
> data -> processor -> data

**Data** may be coming as an external input or from a processor. 
Acquiring some the data may require send an explicit event (to trigger a remote call to a remote service or send a data request to a storage)
Data are sent as an external output (which may be a remote service, a storage device, ...) 
**Processors** are pure functions that are transforming some data to other ones.

The combination of data and processors can be considered as a data processing **flow**.
>                 external input -> processor -> processor output ->
processor input + external input -> processor -> processor output ->
...
processor input + external input -> processor -> external output

In a traditional software, procedures and functions are taking the responsibility of loading durable data, processing all data and opportunistically triggering the subsequent processing. This approach is creating a hierarchy, which may become quite complex.

In a reactive software, the loading of the durable data are still performed by the processor, and the processor is still opportunistically triggering the subsequent processing. A complexity is added because of the difficulty to transfer a growing set of data from a processing to another one
> processor1(processorInput): myData=f_1(processorInput)
    return r(processor2(myData))...

> processor2(processorInput): myData=f_2(processorInput)
    return r(processor2_part1(myData))
    .then(d -> combine(d, r(d -> externalInput(myData, d)))
    .then(d -> processor2_part2(myData, d))...

## The hierarchy issue
It is quite common to introduce bugs because no one really remembers which feature is calling some piece of code. We must understand that in some cases there is no sane way to identify the impact of some changes.

Why ? The reason is that reverse-engineering the call hierarchy is a complex task, and reverse-engineering the conditions in which calls are performed are even more complex.

## Solving the hierarchy issue
As a solution, we could consider taking an approach that is closer to the data processing flow, as previously defined, and a more data-centric conception.

A flow is defined by 
* A set of processors, which are 
   * taking a set of data as parameters
   * producing a set of data
* Binding Rules (defining how to bind two processors according to the data semantic and conditions)
* External Input Triggers (to trigger the event that would send external inputs)

For the shake of simplicity, Binding rules and External Input Triggers may contain simple function that are transforming the data set into processor or computing the external trigger parameters.

The flow implementations would be defined in a way that allows adequate visualization and testing.




