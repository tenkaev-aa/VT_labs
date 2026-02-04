

const BASE_URL = 'http://localhost:4725/api/auth';

export async function registerUser(username, password) {
    const res = await fetch(`${BASE_URL}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    });

    const text = await res.text();

    if (!res.ok) {
        throw new Error(text || 'Registration failed');
    }


    return text;
}

export async function loginUser(username, password) {
    const res = await fetch(`${BASE_URL}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Login failed');
    }

    const data = await res.json();
    return data.token;
}
