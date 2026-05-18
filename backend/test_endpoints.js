const BASE_URL = "http://localhost:8090";
let TOKEN = null;
const USER_ID = 1;

async function request(endpoint, method, body, headers = {}) {
    const options = {
        method,
        headers: {
            "Content-Type": "application/json",
            ...headers
        }
    };
    if (TOKEN) options.headers["Authorization"] = `Bearer ${TOKEN}`;
    if (body) options.body = JSON.stringify(body);

    try {
        const res = await fetch(`${BASE_URL}${endpoint}`, options);
        const text = await res.text();
        const success = res.status >= 200 && res.status < 300;
        console.log(`${success ? '✅' : '❌'} [${res.status}] ${method} ${endpoint}`);
        if (options.headers["Authorization"]) console.log(`   Auth: ${options.headers["Authorization"].substring(0, 30)}...`);
        if (!success) console.log(`   Response: ${text.substring(0, 200)}`);
        return { status: res.status, text };
    } catch (e) {
        console.log(`❌ [ERROR] ${method} ${endpoint}`);
        console.log(`   ${e.message}`);
        return { status: 500, text: e.message };
    }
}

async function run() {
    console.log("--- Testing Auth Service ---");
    const t = Date.now();
    const user = { username: `user_${t}`, email: `test${t}@example.com`, password: "Password@123", roles: [{name: "USER"}] };
    
    await request("/api/auth/register", "POST", user);
    const loginRes = await request("/api/auth/login", "POST", { username: user.username, email: user.email, password: "Password@123" });
    console.log(`   Login Response: ${loginRes.text.substring(0, 50)}...`);
    if (loginRes.status === 200 && loginRes.text.length > 20 && !loginRes.text.includes("{")) {
        TOKEN = loginRes.text;
    }

    console.log("\n--- Testing Delivery Service ---");
    const delivery = {
        userId: USER_ID, serviceType: "standard", status: "PENDING", totalAmount: 10.99,
        parcel: { weight: 2.5, dimensions: "10x10", fragile: false },
        addresses: [{ type: "SENDER", name: "A", city: "NY" }]
    };
    const delRes = await request("/api/deliveries", "POST", delivery);
    await request("/api/deliveries", "GET", null);
    await request("/api/deliveries/my", "GET", null, { "X-User-Id": USER_ID });

    console.log("\n--- Testing Tracking Service ---");
    const tracking = { trackingNumber: `TRK${t}`, status: "IN_TRANSIT", location: "NY" };
    await request("/api/tracking", "POST", tracking);
    await request(`/api/tracking/${tracking.trackingNumber}`, "GET", null);

    console.log("\n--- Testing Admin Service ---");
    await request("/api/admin/deliveries", "GET", null);
    await request("/api/admin/dashboard", "GET", null);

    console.log("\n--- Testing API Gateway ---");
    await request("/gateway/services", "GET", null);
}

run();
