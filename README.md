# DataViewer
Software Development in Java: Lab 3 / Lab 4

If you are having difficulties getting the data to load, make sure that the files are in the correct place. 
All .java files should be placed in "src" directory.
The .csv file MUST be placed in the project directory (The same folder as "src".)

---------------------------------------------------------------------------------------------------
Lab 4 -
Implemented Observer pattern as "DataController" class. Keeps everything separated into a single listener, in order to update all panels within TablePanel upon a change.
Makes the panel code cleaner. Provides a single place for the meat of the panel updating code. Routes all interactions to one place.
