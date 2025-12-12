

import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { fetchPoints, addPoint } from '../api/pointsApi';
import { logout } from './authSlice.js';

export const fetchPointsThunk = createAsyncThunk(
    'points/fetchAll',
    async (_, { getState, rejectWithValue, dispatch }) => {
        try {
            const token = getState().auth.token;
            if (!token) {
                dispatch(logout());
                return rejectWithValue('Not authenticated');
            }

            const data = await fetchPoints(token);
            return data; // массив точек
        } catch (err) {
            if (
                err.message === 'AUTH_EXPIRED' ||
                err.status === 401 ||
                err.status === 403
            ) {
                dispatch(logout('expired'));
                return rejectWithValue('AUTH_EXPIRED');
            }
            return rejectWithValue(err.message || 'Failed to fetch points');
        }
    }
);

export const addPointThunk = createAsyncThunk(
    'points/add',
    async ({ x, y, r }, { getState, rejectWithValue, dispatch }) => {
        try {
            const token = getState().auth.token;
            if (!token) {
                dispatch(logout());
                return rejectWithValue('Not authenticated');
            }

            const created = await addPoint(token, { x, y, r });
            return created;
        } catch (err) {
            if (
                err.message === 'AUTH_EXPIRED' ||
                err.status === 401 ||
                err.status === 403
            ) {
                dispatch(logout('expired'));
                return rejectWithValue('AUTH_EXPIRED');
            }
            return rejectWithValue(err.message || 'Failed to add point');
        }
    }
);

const pointsSlice = createSlice({
    name: 'points',
    initialState: {
        points: [],
        x: null,
        y: '',
        r: null,
        loading: false,
        error: null,
    },
    reducers: {
        setX(state, action) {
            state.x = action.payload;
        },
        setY(state, action) {
            state.y = action.payload;
        },
        setR(state, action) {
            state.r = action.payload;
        },
        clearPointsError(state) {
            state.error = null;
        },
        clearPoints(state) {
            state.points = [];
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchPointsThunk.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchPointsThunk.fulfilled, (state, action) => {
                state.loading = false;
                state.points = action.payload;
            })
            .addCase(fetchPointsThunk.rejected, (state, action) => {
                state.loading = false;
                if (action.payload === 'AUTH_EXPIRED') {
                    state.error = null;
                } else {
                    state.error = action.payload || 'Failed to fetch points';
                }
            })
            .addCase(addPointThunk.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(addPointThunk.fulfilled, (state, action) => {
                state.loading = false;
                state.points.push(action.payload);
            })
            .addCase(addPointThunk.rejected, (state, action) => {
                state.loading = false;
                if (action.payload === 'AUTH_EXPIRED') {
                    state.error = null;
                } else {
                    state.error = action.payload || 'Failed to add point';
                }
            });
    },
});

export const {
    setX,
    setY,
    setR,
    clearPointsError,
    clearPoints,
} = pointsSlice.actions;

export default pointsSlice.reducer;
