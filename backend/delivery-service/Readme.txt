POST http://localhost:8090/api/deliveries

{
  "trackingNumber": "TRK123",
  "userId": 1,
  "serviceType": "EXPRESS",
  "status": "CREATED",
  "totalAmount": 200.0,
  "parcel": {
    "weight": 2.5,
    "dimensions": "10x10x10",
    "fragile": true
  },
  "addresses": [
    {
      "type": "SENDER",
      "name": "Sameer",
      "phone": "9999999999",
      "address": "Delhi",
      "city": "Delhi"
    },
    {
      "type": "RECEIVER",
      "name": "Rahul",
      "phone": "8888888888",
      "address": "Mumbai",
      "city": "Mumbai"
    }
  ]
}

GET http://localhost:8090/api/deliveries

PUT http://localhost:8090/api/deliveries/{id}

{
  "status": "DELIVERED"
}