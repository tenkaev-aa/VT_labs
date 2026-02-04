import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
    loginThunk,
    registerThunk,
    clearAuthError,
    clearRegisterMessage,
    clearSessionExpired,
} from '../store/authSlice';

const StartPage = () => {
    const dispatch = useDispatch();
    const { loading, error, registerMessage, sessionExpired } = useSelector(
        (state) => state.auth
    );

    const [loginUsername, setLoginUsername] = useState('');
    const [loginPassword, setLoginPassword] = useState('');

    const [regUsername, setRegUsername] = useState('');
    const [regPassword, setRegPassword] = useState('');

    const handleLoginSubmit = (e) => {
        e.preventDefault();
        dispatch(clearAuthError());
        dispatch(clearRegisterMessage());
        dispatch(clearSessionExpired());
        dispatch(loginThunk({ username: loginUsername, password: loginPassword }));
    };

    const handleRegisterSubmit = (e) => {
        e.preventDefault();
        dispatch(clearAuthError());
        dispatch(clearRegisterMessage());
        dispatch(clearSessionExpired());
        dispatch(registerThunk({ username: regUsername, password: regPassword }));
    };

    return (
        <div className="start-page">
            <header className="start-header">
                <div className="start-header-left">
                    <div className="logo-badge">ITMO</div>
                    <div className="lab-title">
                        <h1>Web lab 4</h1>
                        <p>
                            Студент: Тенькаев Артём Антонович • Группа: P3231 • Вариант: 7485
                        </p>
                    </div>
                </div>
            </header>

            <main className="start-content start-centered">
                <div className="auth-card">
                    <h2 className="auth-title">Вход в систему</h2>

                    {sessionExpired && (
                        <div className="auth-banner">
                            Сессия истекла, войдите заново.
                            <button
                                type="button"
                                className="auth-banner-close"
                                onClick={() => dispatch(clearSessionExpired())}
                            >
                                ×
                            </button>
                        </div>
                    )}


                    <form className="auth-form" onSubmit={handleLoginSubmit}>
                        <input
                            type="text"
                            className="auth-input"
                            placeholder="Логин"
                            value={loginUsername}
                            onChange={(e) => setLoginUsername(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            className="auth-input"
                            placeholder="Пароль"
                            value={loginPassword}
                            onChange={(e) => setLoginPassword(e.target.value)}
                            required
                        />

                        <button
                            type="submit"
                            className="auth-btn auth-btn-primary"
                            disabled={loading}
                        >
                            {loading ? 'Входим...' : 'Вход'}
                        </button>
                    </form>

                    <div className="auth-divider">или</div>


                    <form className="auth-form" onSubmit={handleRegisterSubmit}>
                        <input
                            type="text"
                            className="auth-input"
                            placeholder="Логин для регистрации"
                            value={regUsername}
                            onChange={(e) => setRegUsername(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            className="auth-input"
                            placeholder="Пароль для регистрации"
                            value={regPassword}
                            onChange={(e) => setRegPassword(e.target.value)}
                            required
                        />

                        <button
                            type="submit"
                            className="auth-btn auth-btn-secondary"
                            disabled={loading}
                        >
                            {loading ? 'Регистрируем...' : 'Регистрация'}
                        </button>
                    </form>

                    {registerMessage && (
                        <p className="auth-message auth-message-success">
                            {registerMessage}
                        </p>
                    )}

                    {error && (
                        <p className="auth-message auth-message-error">
                            Ошибка: {error}
                        </p>
                    )}
                </div>
            </main>
        </div>
    );
};

export default StartPage;
