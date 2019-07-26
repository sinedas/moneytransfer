# Money transfer

Simple money transfer rest application.

# Endpoints

## Create account

Generate account order and create account with provided balance:

POST localhost:8080/account/initial
```
{
 "balance": "12"
}
``` 

## Get account 

Get account with balance:

GET localhost:8080/acount/:id

## Transfer money

Transfer money from one account to another

POST localhost:8080/transfer
```
{
	"from": "2",
	"to": "1",
	"amount" : "2"
}
```



