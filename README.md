# transaction-statistics

To run, from the root folder just type 

`gradle bootRun`

Available endpoints: 

```
curl --request POST \
  --url http://localhost:8080/transactions \
  --header 'content-type: application/json' \
  --data '{
	"amount": 10,
	"timestamp": 1529244381164
}'
```

```
curl --request GET \
  --url http://localhost:8080/statistics
```