

const BASE_URL = 'http://localhost:4725/api/points';

function authHeaders(token) {
    return {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };
}

export async function fetchPoints(token) {
    const res = await fetch(BASE_URL, {
        method: 'GET',
        headers: authHeaders(token),
    });


    if (res.status === 401 || res.status === 403) {
        const err = new Error('AUTH_EXPIRED');
        err.status = res.status;
        throw err;
    }

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Failed to fetch points');
    }

    return await res.json();
}

export async function addPoint(token, { x, y, r }) {
    const res = await fetch(BASE_URL, {
        method: 'POST',
        headers: authHeaders(token),
        body: JSON.stringify({ x, y, r }),
    });

    if (res.status === 401 || res.status === 403) {
        const err = new Error('AUTH_EXPIRED');
        err.status = res.status;
        throw err;
    }

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Failed to add point');
    }

    return await res.json();
}
