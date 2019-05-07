# Software Development 2 | Spring 2019
## Assignment 2 - 15%

<hr>

## Background

This assignment will test your knowledge of
* Objects
* Input and Output Streams
* Files
* Graphical user interface

## Specification

Design and implement a class called CreditUnion that manages user accounts using an appropriate graphical user interface in Swing or AWT. Each instance of the **CreditUnion class** will have a name and a maximum number of accounts that it can handle.

The **CreditUnion class** should have the following methods:

- *openAccount* (allow a user to open a new Credit Union account)
- *closeAccount* (allow an account holder to close an account on condition that the balance is currently zero)
- *makeLodgement* (allow an account holder to lodge money)
- *makeWithdrawal* (allow an account holder to withdraw money from their account)
- *requestOverdraft* (allow an account holder to request a new overdraft limit).

Each **CreditUnion Account** should have a number, a name, a current balance and an overdraft limit. The methods for each account are:

- *lodgement* (make a lodgement)
- *withdrawal* (remove a sum of money from the account on condition that there are sufficient funds available to meet the request)
- *balance* (check current balance)
- *overdraftLimit* (check current overdraft limit)
- *setNewOverdraft* (set the overdraft limit to some given value).

## Marking

1. All work attempted and submitted.
2. The quality of your solution design and you report describing this in detail.
3. The quality of your code and in-program documentation.
4. You will receive marks for a bug-free, fully tested, working application.
5. The quality and professionalism of the design of the graphical user interface. Ideally, this should be user friendly, realistic and have a well-designed Java look and feel while not permitting the entry of erroneous data.
6. Innovation and creativity in your design and solution 

|  | Marks in % |
| :--- | :---: |
| Quality of Code/Indentation           | 5
| Documentation of code                 | 5
| Open Account function                 | 10
| Close Account function                | 10
| Lodgement function                    | 10
| Withdrawal function                   | 10
| Overdraft function                    | 10
| GUI Design                            | 10
| Program Design Document prob/solution | 10
| Screen Shots                          | 5
| Overall professionalism of report     | 5
| Innovation and creativity             | 10
|  |  |
| **Total**                                 | **100%**

## Deliverables

1. A Java application including source code and .class file(s)
2. A description of your program design stating the design problems that you needed to address
and how you surmounted them. For each of these, state individually the design problem and
then your solution.
3. Screen shots illustrating each of the actions on each GUI component.
4. A professionally written and word-processed report that contains all of the above.
5. The word processed report and java source code can be uploaded to the link provided in Moodle