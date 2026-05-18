import requests
import json
import time

BASE_URL = "http://localhost:8090"
TOKEN = None
USER_ID = 1

def log_result(endpoint, res, expected_status=200):
    status_match = res.status_code == expected_status
    print(f"{'✅' if status_match else '❌'} [{res.status_code}] {endpoint}")
    if not status_match:
        try:
            print(f"   Response: {res.json()}")
        except:
            print(f"   Response: {res.text}")

print("--- Testing Auth Service ---")
# 1. Register
register_data = {
    "username": f"user_{int(time.time())}",
    "email": f"test{int(time.time())}@example.com",
    "password": "password123",
    "roles": [{"name": "USER"}]
}
res = requests.post(f"{BASE_URL}/api/auth/register", json=register_data)
log_result("POST /api/auth/register", res, 200)

# 2. Login
login_data = {"username": register_data["username"], "password": "password123"}
res = requests.post(f"{BASE_URL}/api/auth/login", json=login_data)
log_result("POST /api/auth/login", res, 200)
try:
    if res.text and len(res.text) > 20:
        TOKEN = res.text
except:
    pass

headers = {"Authorization": f"Bearer {TOKEN}"} if TOKEN else {}

print("\n--- Testing Delivery Service ---")
# 3. Create Delivery
delivery_data = {
    "userId": USER_ID,
    "serviceType": "standard",
    "status": "PENDING",
    "totalAmount": 10.99,
    "parcel": {"weight": 2.5, "dimensions": "10x10", "fragile": False},
    "addresses": [{"type": "SENDER", "name": "A", "city": "NY"}]
}
res = requests.post(f"{BASE_URL}/api/deliveries", json=delivery_data, headers=headers)
log_result("POST /api/deliveries", res, 200)

delivery_id = None
try:
    delivery_id = res.json().get("id")
except:
    pass

# 4. Get All Deliveries
res = requests.get(f"{BASE_URL}/api/deliveries", headers=headers)
log_result("GET /api/deliveries", res, 200)

# 5. Get My Deliveries
my_headers = headers.copy()
my_headers["X-User-Id"] = str(USER_ID)
res = requests.get(f"{BASE_URL}/api/deliveries/my", headers=my_headers)
log_result("GET /api/deliveries/my", res, 200)

print("\n--- Testing Tracking Service ---")
# 6. Create Tracking
tracking_data = {
    "trackingNumber": f"TRK{int(time.time())}",
    "status": "IN_TRANSIT",
    "location": "NY"
}
res = requests.post(f"{BASE_URL}/api/tracking", json=tracking_data, headers=headers)
log_result("POST /api/tracking", res, 200)

# 7. Get Tracking
res = requests.get(f"{BASE_URL}/api/tracking/{tracking_data['trackingNumber']}", headers=headers)
log_result("GET /api/tracking/{trackingNumber}", res, 200)

print("\n--- Testing Admin Service ---")
# 8. Admin Deliveries
res = requests.get(f"{BASE_URL}/api/admin/deliveries", headers=headers)
log_result("GET /api/admin/deliveries", res, 200)

# 9. Admin Dashboard
res = requests.get(f"{BASE_URL}/api/admin/dashboard", headers=headers)
log_result("GET /api/admin/dashboard", res, 200)

print("\n--- Testing Gateway ---")
res = requests.get(f"{BASE_URL}/gateway/services")
log_result("GET /gateway/services", res, 200)
