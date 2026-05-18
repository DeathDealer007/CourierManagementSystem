POST http://localhost:8090/api/tracking

{
  "trackingNumber": "TRK123",
  "status": "IN_TRANSIT",
  "location": "Delhi Hub",
  "message": "Package arrived at Delhi hub"
}


GET http://localhost:8090/api/TRK123
