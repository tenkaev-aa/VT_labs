const points = Array.isArray(window.initialHits) ? window.initialHits.slice() : [];
// сообщения
function setMessage(el, text, ok) {
    if (!el) return;
    el.className = 'msg ' + (ok ? 'ok' : 'error');
    el.textContent = text || '';
    clearTimeout(window._msgTimer);
    if (text && ok) {
        window._msgTimer = setTimeout(() => {
            el.textContent = '';
            el.className = 'msg';
        }, 2000);
    }
}

//чтение r из спиннера/инпута
function getR(rInput) {
    if (!rInput) return null;
    const v = parseFloat((rInput.value || '').replace(',', '.'));
    return Number.isFinite(v) ? v : null;
}

// валидация
function validate(xSelect, yInput, rInput, msg, submitBtn, opts = { clearOnOk: true }) {
    const x = parseInt(xSelect.value, 10);
    const xOk = !Number.isNaN(x) && x >= -4 && x <= 4;

    const y = parseFloat((yInput.value || '').replace(',', '.'));
    const yOk = Number.isFinite(y) && y > -5 && y < 5;
    const r = getR(rInput);
    const rOk = r !== null && r > 0 && r <= 3;

    if (submitBtn) submitBtn.disabled = !(xOk && yOk && rOk);

    if (!xOk) {
        setMessage(msg, 'Выберите X из {-4..4}', false);
        return false;
    }
    if (!yOk) {
        setMessage(msg, 'Y должен быть числом в (-5; 5)', false);
        return false;
    }
    if (!rOk) {
        setMessage(msg, 'Задайте R в диапазоне (0; 3]', false);
        return false;
    }

    if (opts.clearOnOk) {
        setMessage(msg, '', true);
    }
    return true;
}



function getScale(SIZE) {
    return (SIZE * 0.42) / 5;
}


function isHitClient(x, y, r) {
    if (x >= 0 && y >= 0 && x <= r && y <= r) return true;


    if (x <= 0 && y >= 0 && x >= -(r/2) && y <= r) {
        if (y <= 2*x + r + 1e-12) return true;
    }


    if (x >= 0 && y <= 0 && (x * x + y * y) <= r * r + 1e-12) return true;

    return false;
}

function drawAxes(ctx, SIZE, r) {
    const s = getScale(SIZE);
    const AX = SIZE / 2, AY = SIZE / 2;
    const marks = [-r, -r / 2, r / 2, r];

    ctx.save();
    ctx.translate(AX, AY);
    ctx.scale(1, -1);

    // оси
    ctx.strokeStyle = '#555';
    ctx.lineWidth = 1.5;
    ctx.beginPath();
    ctx.moveTo(-SIZE / 2 + 8, 0); ctx.lineTo(SIZE / 2 - 8, 0); // X
    ctx.moveTo(0, -SIZE / 2 + 8); ctx.lineTo(0, SIZE / 2 - 8); // Y
    ctx.stroke();

    // стрелки
    ctx.fillStyle = '#555';
    ctx.beginPath();
    ctx.moveTo(SIZE / 2 - 8, 0); ctx.lineTo(SIZE / 2 - 18, 6); ctx.lineTo(SIZE / 2 - 18, -6);
    ctx.closePath(); ctx.fill();
    ctx.beginPath();
    ctx.moveTo(0, SIZE / 2 - 8); ctx.lineTo(6, SIZE / 2 - 18); ctx.lineTo(-6, SIZE / 2 - 18);
    ctx.closePath(); ctx.fill();

    // риски
    ctx.strokeStyle = '#555';
    ctx.lineWidth = 1;
    for (const m of marks) {
        ctx.beginPath(); ctx.moveTo(m * s, -4); ctx.lineTo(m * s, 4); ctx.stroke();   // X
        ctx.beginPath(); ctx.moveTo(-4, m * s); ctx.lineTo(4, m * s); ctx.stroke();   // Y
    }
    ctx.restore();

    // подписи
    ctx.save();
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.font = '12px Arial';
    ctx.fillStyle = '#333';

    const labelFor = (m) => {
        const eps = 1e-9;
        if (Math.abs(Math.abs(m) - r) < eps) return (m < 0 ? '-' : '') + 'R';
        if (Math.abs(Math.abs(m) * 2 - r) < eps) return (m < 0 ? '-' : '') + 'R/2';
        return '';
    };

    for (const m of marks) {
        const dx = AX + m * s;
        const dy = AY - m * s;
        const lx = labelFor(m);
        if (lx) {
            ctx.fillText(lx, dx - 10, AY + 16);  // по X
            ctx.fillText(lx, AX + 8, dy + 4);    // по Y
        }
    }

    ctx.font = 'bold 13px Arial';
    ctx.fillStyle = '#444';
    ctx.fillText('X', SIZE - 18, AY - 6);
    ctx.fillText('Y', AX + 6, 16);

    ctx.restore();
}

function drawArea(ctx, SIZE, r) {
    const s = getScale(SIZE);
    ctx.save();
    ctx.translate(SIZE / 2, SIZE / 2);
    ctx.scale(1, -1);

    ctx.fillStyle = 'rgba(78,161,255,0.35)';
    ctx.strokeStyle = 'rgba(78,161,255,0.95)';
    ctx.lineWidth = 1.5;


    ctx.beginPath();
    ctx.rect(0, 0, r * s, r * s);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();


    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.lineTo(-(r/2) * s, 0);
    ctx.lineTo(0, r * s);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    //  четверть круга
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.arc(0, 0, r * s, 0, -Math.PI / 2, true);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    ctx.restore();
}

function toCanvas(SIZE, x, y) {
    const s = getScale(SIZE);
    return [SIZE / 2 + x * s, SIZE / 2 - y * s];
}
function fromCanvas(SIZE, cx, cy) {
    const s = getScale(SIZE);
    return [(cx - SIZE / 2) / s, (SIZE / 2 - cy) / s];
}

function drawPoint(ctx, SIZE, x, y, color = '#111') {
    const [cx, cy] = toCanvas(SIZE, x, y);
    ctx.save();
    ctx.beginPath();
    ctx.arc(cx, cy, 4, 0, Math.PI * 2);
    ctx.fillStyle = color;
    ctx.fill();
    ctx.restore();
}

function redraw(ctx, SIZE, xSelect, yInput, rInput, clearOnly) {
    const r = getR(rInput) || 1;
    const x = parseInt(xSelect.value, 10);
    const y = parseFloat((yInput.value || '').replace(',', '.'));

    ctx.clearRect(0, 0, SIZE, SIZE);
    drawArea(ctx, SIZE, r);
    drawAxes(ctx, SIZE, r);

    // история
    for (const p of points) {
        const color = isHitClient(p.x, p.y, r) ? '#1a7f37' : '#b42318';
        drawPoint(ctx, SIZE, p.x, p.y, color);
    }

    // текущая точка
    if (!clearOnly && Number.isFinite(x) && Number.isFinite(y)) {
        drawPoint(ctx, SIZE, x, y, '#111');
    }
}

//  main
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('hit-form');
    if (!form) return;

    const xSelect = document.getElementById('x-input');
    const yInput  = document.getElementById('y-input');

    const rInput =
        document.querySelector("input[id$='r-input_input']") ||
        document.querySelector("#r-input_input") ||
        document.querySelector("#r-input input");
    const submitBtn = document.getElementById('submit-btn');
    const msg = document.getElementById('msg');

    const canvas = document.getElementById('plot');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const SIZE = canvas.width;

    let canvasSubmitTimer = null;

    setMessage(msg, 'Готов к работе', true);
    validate(xSelect, yInput, rInput, msg, submitBtn);
    redraw(ctx, SIZE, xSelect, yInput, rInput, true);


    xSelect.addEventListener('change', () => {
        validate(xSelect, yInput, rInput, msg, submitBtn);
        redraw(ctx, SIZE, xSelect, yInput, rInput, false);
    });

    yInput.addEventListener('input', () => {
        yInput.value = yInput.value.replace(/[^0-9\-.,]/g, '').replace(',', '.');
        validate(xSelect, yInput, rInput, msg, submitBtn);
        redraw(ctx, SIZE, xSelect, yInput, rInput, false);
    });

    if (rInput) {
        ['input', 'change'].forEach(evName => {
            rInput.addEventListener(evName, () => {
                validate(xSelect, yInput, rInput, msg, submitBtn);
                redraw(ctx, SIZE, xSelect, yInput, rInput, false);
            });
        });
    }

    //клик по графику
    canvas.addEventListener('click', (ev) => {
        const r = getR(rInput);
        if (r === null) {
            setMessage(msg, 'Сначала задайте R', false);
            return;
        }

        const rect = canvas.getBoundingClientRect();
        const cx = ev.clientX - rect.left;
        const cy = ev.clientY - rect.top;
        const [x, y] = fromCanvas(SIZE, cx, cy);

        const xSnap = Math.max(-4, Math.min(4, Math.round(x)));
        const yVal  = Math.max(-4.999, Math.min(4.999, Math.round(y * 1000) / 1000));


        xSelect.value = String(xSnap);
        yInput.value  = String(yVal);

        setMessage(msg, 'X округлён до ' + xSnap, true);

        validate(xSelect, yInput, rInput, msg, submitBtn, { clearOnOk: false });
        redraw(ctx, SIZE, xSelect, yInput, rInput, false);
        if (validate(xSelect, yInput, rInput, msg, submitBtn)) {
            submitBtn.click();
        }
    });

    window.redrawPlot = function () {
        validate(xSelect, yInput, rInput, msg, submitBtn);
        redraw(ctx, SIZE, xSelect, yInput, rInput, false);
    };
});
