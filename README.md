# TUDub-SoftwareDev2-Assignemnt2

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
