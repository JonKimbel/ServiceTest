All tests should be conducted with the "Don't keep activities" developer setting enabled.

*   Threads started by the Activity are not killed with the Activity, but operations on views and
    stuff won’t work because the views will be toast.
*   Persisting results to the disk works while the app is still backgrounded, but if the app is
    foregrounded again it doesn’t work - the thread won’t know about the new activity, and the
    persisted file won’t be checked for by the file in onCreate.
*   Need to look into `Service`s.
