import React, { useEffect, useState } from 'react';
import GraphCanvas from '../components/GraphCanvas.jsx';

import { useDispatch, useSelector } from 'react-redux';
import {
    fetchPointsThunk,
    addPointThunk,
    setX,
    setY,
    setR,
    clearPointsError,
} from '../store/pointsSlice';
import { logout } from '../store/authSlice';

const X_VALUES = [-4, -3, -2, -1, 0, 1, 2, 3, 4];

const R_VALUES = [1, 2, 3, 4];

const MainPage = () => {
    const dispatch = useDispatch();
    const { username } = useSelector((state) => state.auth);
    const { points, x, y, r, loading, error } = useSelector((state) => state.points);

    const [formError, setFormError] = useState('');

    useEffect(() => {
        dispatch(fetchPointsThunk());
    }, [dispatch]);


    const parsedY = parseFloat(y);
    const isYEmpty = y === '';
    const isYNumber = !Number.isNaN(parsedY);
    const isYInRange = isYNumber && parsedY > -5 && parsedY < 3;
    const isYValid = !isYEmpty && isYNumber && isYInRange;

    const canSubmit =
        !loading &&
        x !== null &&
        r !== null &&
        isYValid;


    useEffect(() => {
        setFormError('');
    }, [x, y, r]);

    const handleSubmit = (e) => {
        e.preventDefault();
        dispatch(clearPointsError());
        setFormError('');

        if (x === null || r === null || y === '') {
            setFormError('Заполните X, Y и R.');
            return;
        }

        const parsed = parseFloat(y);
        if (Number.isNaN(parsed)) {
            setFormError('Y должен быть числом.');
            return;
        }
        if (!(parsed > -5 && parsed < 3)) {
            setFormError('Y должен быть в диапазоне (-5; 3).');
            return;
        }

        dispatch(addPointThunk({ x, y: parsed, r }));
    };

    const handleLogout = () => {
        dispatch(logout());
    };


    const handleCanvasClick = (worldX, worldY) => {
        if (r === null) {
            setFormError('Сначала выберите R.');
            return;
        }


        let nearestX = X_VALUES[0];
        let minDiff = Infinity;
        X_VALUES.forEach((val) => {
            const d = Math.abs(val - worldX);
            if (d < minDiff) {
                minDiff = d;
                nearestX = val;
            }
        });

        const yVal = Number(worldY.toFixed(3));

        if (Number.isNaN(yVal)) {
            setFormError('Ошибка преобразования координаты Y.');
            return;
        }

        if (!(yVal > -5 && yVal < 3)) {
            setFormError('Y, полученный с графика, вне диапазона (-5; 3).');
            return;
        }

        dispatch(setX(nearestX));
        dispatch(setY(String(yVal)));
        dispatch(clearPointsError());
        setFormError('');

        dispatch(addPointThunk({ x: nearestX, y: yVal, r }));
    };

    return (
        <div className="main-page">
            <header className="main-header">
                <div className="main-header-left">
                    <div className="logo-badge">ITMO</div>
                    <div className="lab-title">
                        <h1>Web lab 4</h1>
                        <p>
                            Студент: Тенькаев Артём Антонович • Группа: P3231 • Вариант: 7485
                        </p>
                    </div>
                </div>

                <div className="main-header-right">
                    <span className="user-pill">
                        {username || 'Неизвестный пользователь'}
                    </span>
                    <button onClick={handleLogout}>Выйти</button>
                </div>
            </header>

            <main className="main-content">
                <section className="controls">
                    <h2>Ввод точки</h2>
                    <form onSubmit={handleSubmit}>
                        <div className="field">
                            <p className="field-label">X:</p>
                            <div className="field-options">
                                {X_VALUES.map((value) => (
                                    <label key={value} className="option">
                                        <input
                                            type="radio"
                                            name="x"
                                            value={value}
                                            checked={x === value}
                                            onChange={() => dispatch(setX(value))}
                                        />
                                        <span>{value}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        <div className="field" style={{ marginTop: '8px' }}>
                            <label className="field-label">
                                Y (-5; 3):
                                <input
                                    type="number"
                                    className="field-input"
                                    value={y}
                                    min={-5}
                                    max={3}
                                    step="any"
                                    onChange={(e) => dispatch(setY(e.target.value))}
                                    placeholder="например, 1.5"
                                />
                            </label>
                        </div>

                        <div className="field" style={{ marginTop: '8px' }}>
                            <p className="field-label">R:</p>
                            <div className="field-options">
                                {R_VALUES.map((value) => (
                                    <label key={value} className="option">
                                        <input
                                            type="radio"
                                            name="r"
                                            value={value}
                                            checked={r === value}
                                            onChange={() => dispatch(setR(value))}
                                        />
                                        <span>{value}</span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={!canSubmit}
                            style={{ marginTop: '12px' }}
                        >
                            {loading ? 'Отправка...' : 'Проверить точку'}
                        </button>
                    </form>


                    {formError && (
                        <p className="form-error">⚠ {formError}</p>
                    )}
                    {error && (
                        <p className="form-error">
                            Ошибка при работе с сервером: {error}
                        </p>
                    )}
                </section>

                <section className="graph">
                    <h2>График</h2>
                    <GraphCanvas
                        r={r}
                        points={points}
                        currentX={x}
                        currentY={y}
                        onCanvasClick={handleCanvasClick}
                    />

                    <p style={{ fontSize: '12px', marginTop: '4px' }}>
                        Клик по графику добавляет точку. X привязывается к ближайшему значению
                        из набора.
                    </p>
                </section>

                <section className="results">
                    <h2>Результаты</h2>
                    {loading && points.length === 0 && <p>Загрузка...</p>}
                    {points.length === 0 && !loading && <p>Точек пока нет</p>}

                    {points.length > 0 && (
                        <div className="results-table-wrapper">
                        <table
                            border="1"
                            cellPadding="4"
                            style={{ marginTop: '8px', borderCollapse: 'collapse' }}
                        >
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>X</th>
                                <th>Y</th>
                                <th>R</th>
                                <th>Попадание</th>
                                <th>Время запроса</th>
                                <th>Время работы (мс)</th>
                            </tr>
                            </thead>
                            <tbody>
                            {points.map((p) => (
                                <tr key={p.id}>
                                    <td>{p.id}</td>
                                    <td>{p.x}</td>
                                    <td>{p.y}</td>
                                    <td>{p.r}</td>
                                    <td>{p.hit ? 'Да' : p.isHit ? 'Да' : 'Нет'}</td>
                                    <td>{p.nowTime}</td>
                                    <td>
                                        {p.workTime?.toFixed ? p.workTime.toFixed(3) : p.workTime}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                        </div>
                    )}
                </section>
            </main>
        </div>
    );
};

export default MainPage;
