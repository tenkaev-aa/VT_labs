import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { loginUser, registerUser } from '../api/authApi';

const storedToken = localStorage.getItem('token') || null;
const storedUsername = localStorage.getItem('username') || null;

export const registerThunk = createAsyncThunk(
    'auth/register',
    async ({ username, password }, { rejectWithValue }) => {
        try {
            const message = await registerUser(username, password);
            return message;
        } catch (err) {
            return rejectWithValue(err.message);
        }
    }
);

export const loginThunk = createAsyncThunk(
    'auth/login',
    async ({ username, password }, { rejectWithValue }) => {
        try {
            const token = await loginUser(username, password);
            return { token, username };
        } catch (err) {
            return rejectWithValue(err.message);
        }
    }
);

const authSlice = createSlice({
    name: 'auth',
    initialState: {
        token: storedToken,
        username: storedUsername,
        isAuth: !!storedToken,
        loading: false,
        error: null,
        registerMessage: null,
        sessionExpired: false,
    },
    reducers: {

        logout(state, action) {
            state.token = null;
            state.username = null;
            state.isAuth = false;
            state.error = null;
            state.registerMessage = null;

            state.sessionExpired = action?.payload === 'expired';

            localStorage.removeItem('token');
            localStorage.removeItem('username');
        },
        clearAuthError(state) {
            state.error = null;
        },
        clearRegisterMessage(state) {
            state.registerMessage = null;
        },

        clearSessionExpired(state) {
            state.sessionExpired = false;
        },
    },
    extraReducers: (builder) => {
        // REGISTER
        builder
            .addCase(registerThunk.pending, (state) => {
                state.loading = true;
                state.error = null;
                state.registerMessage = null;
            })
            .addCase(registerThunk.fulfilled, (state, action) => {
                state.loading = false;
                state.registerMessage = action.payload;
            })
            .addCase(registerThunk.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload || 'Registration failed';
            });

        // LOGIN
        builder
            .addCase(loginThunk.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(loginThunk.fulfilled, (state, action) => {
                state.loading = false;
                state.token = action.payload.token;
                state.username = action.payload.username;
                state.isAuth = true;
                state.sessionExpired = false;

                localStorage.setItem('token', action.payload.token);
                localStorage.setItem('username', action.payload.username);
            })
            .addCase(loginThunk.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload || 'Login failed';
            });
    },
});

export const {
    logout,
    clearAuthError,
    clearRegisterMessage,
    clearSessionExpired,
} = authSlice.actions;

export default authSlice.reducer;
