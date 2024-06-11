Interface

| Type                | Class                  | Description |
|---------------------|------------------------|-------------|
| void, argumentless  | Runnable               
| (Argument) Receiver | Consumer               
| (Argument) Receiver | Function               
| (Argument) Receiver | BiFunction             
| (Argument) Receiver | Function<sub>*n*</sub> 
| (Result) Returner   | Supplier               
| (Result) Returner   | Supplier               
| (Result) Returner   | Function               
| (Result) Returner   | BiFunction             
| (Result) Returner   | Function<sub>*n*</sub> 

| Class Name                 | Class                                                                                                                 | Description |
|----------------------------|-----------------------------------------------------------------------------------------------------------------------|-------------|
| LoggingActionBuilder       | Entry point for building                                                                                              
| Logging*XXX*BaseBuilder    | Abstract class with almost all methods to build *XXX* which are hidden                                                
| Logging*XXX*Builder        | First step builder to build *XXX* after choosing call type using invoke-method of LoggingActionBuilder                
| Logging*XXX*ErrCorrBuilder | Error Correction step builder to build *XXX* (immediately used or after Logging*XXX*Builder)                          
| Logging*XXX*FinalBuilder   | Final step builder to build *XXX* (immediately used or after Logging*XXX*ErrCorrBuilder or after Logging*XXX*Builder) 


