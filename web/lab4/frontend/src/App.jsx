import React from 'react';
import { useSelector } from 'react-redux';
import StartPage from './pages/StartPage.jsx';
import MainPage from './pages/MainPage.jsx';

const App = () => {
    const { isAuth } = useSelector((state) => state.auth);

    return (
        <>
            {isAuth ? <MainPage /> : <StartPage />}
        </>
    );
};

export default App;