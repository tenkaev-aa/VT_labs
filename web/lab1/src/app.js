
function setMessage(el, text, ok) {
  el.className = 'msg ' + (ok ? 'ok' : 'error');
  el.textContent = text || '';
}
function getR(rChecks) {
  const c = rChecks.find(x => x.checked);
  return c ? parseInt(c.value, 10) : null;
}
function validate(xHidden, yInput, rChecks, msg, submitBtn) {
  const xOk = xHidden.value !== '';
  const y = parseFloat(yInput.value);
  const yOk = Number.isFinite(y) && y >= -5 && y <= 3;
  const rOk = getR(rChecks) !== null;

  submitBtn.disabled = !(xOk && yOk && rOk);
  if (!xOk) { setMessage(msg, 'Выберите X', false); return false; }
  if (!yOk) { setMessage(msg, 'Y должен быть числом от -5 до 3', false); return false; }
  if (!rOk) { setMessage(msg, 'Выберите R', false); return false; }
  setMessage(msg, '', true);
  return true;
}
function lsKeyForSid(sid) {
  const s = (sid && String(sid).trim()) || 'anonymous';
  return `weblab1:history:${s}`;
}
function loadLocalHistory(sid) {
  try {
    const raw = localStorage.getItem(lsKeyForSid(sid));
    if (!raw) return [];
    const arr = JSON.parse(raw);
    return Array.isArray(arr) ? arr : [];
  } catch { return []; }
}
function saveLocalHistory(sid, items) {
  try {
    localStorage.setItem(lsKeyForSid(sid), JSON.stringify(items));
  } catch {}
}
function mergeServerHistoryIntoLocal(sid, serverItems) {
  const local = loadLocalHistory(sid);
  const key = h => `${h.x}|${h.y}|${h.r}|${h.ts || ''}|${h.sid || ''}`;
  const seen = new Set(local.map(key));
  const merged = local.slice();
  for (const it of (serverItems || [])) {
    const k = key(it);
    if (!seen.has(k)) { merged.push(it); seen.add(k); }
  }
  saveLocalHistory(sid, merged);
  return merged;
}

function getScale(SIZE, r) {
  return (SIZE * 0.42) / Math.max(5, r);
}
function drawAxes(ctx, SIZE, r) {
  const s =getScale(SIZE, r)
  const AX = SIZE / 2, AY = SIZE / 2;

  //оси и стрелки
  ctx.save();
  ctx.translate(AX, AY);
  ctx.scale(1, -1);

  // оси
  ctx.strokeStyle = '#555';
  ctx.lineWidth = 1.5;
  ctx.beginPath();
  ctx.moveTo(-SIZE/2 + 8, 0); ctx.lineTo(SIZE/2 - 8, 0); // X
  ctx.moveTo(0, -SIZE/2 + 8); ctx.lineTo(0, SIZE/2 - 8); // Y
  ctx.stroke();

  // стрелки
  ctx.fillStyle = '#555';
  // стрелка x
  ctx.beginPath();
  ctx.moveTo(SIZE/2 - 8, 0); ctx.lineTo(SIZE/2 - 18, 6); ctx.lineTo(SIZE/2 - 18, -6);
  ctx.closePath(); ctx.fill();
  // стрелка y
  ctx.beginPath();
  ctx.moveTo(0, SIZE/2 - 8); ctx.lineTo(6, SIZE/2 - 18); ctx.lineTo(-6, SIZE/2 - 18);
  ctx.closePath(); ctx.fill();

  // деления на R
  const marks = [-r, -r/2, r/2, r];
  ctx.strokeStyle = '#555';
  ctx.lineWidth = 1;

  for (const m of marks) {

    ctx.beginPath();
    ctx.moveTo(m * s, -4);
    ctx.lineTo(m * s,  4);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(-4, m * s);
    ctx.lineTo( 4, m * s);
    ctx.stroke();
  }

  ctx.restore();


  ctx.save();
  ctx.setTransform(1, 0, 0, 1, 0, 0);

  ctx.font = '12px Arial';
  ctx.fillStyle = '#333';

  for (const m of marks) {
    const dx = AX + m * s;
    const dy = AY - m * s;

    const lx = labelFor(m, r);
    if (lx) ctx.fillText(lx, dx - 10, AY + 16);


    if (lx) ctx.fillText(lx, AX + 8, dy + 4);
  }

  // подписи осей
  ctx.font = 'bold 13px Arial';
  ctx.fillStyle = '#444';
  ctx.fillText('X', SIZE - 18, AY - 6);
  ctx.fillText('Y', AX + 6, 16);

  ctx.restore();

  function labelFor(m, r) {
    const eps = 1e-9;
    if (Math.abs(Math.abs(m) - r) < eps)   return (m < 0 ? '-' : '') + 'R';
    if (Math.abs(Math.abs(m) * 2 - r) < eps) return (m < 0 ? '-' : '') + 'R/2';
    return '';
  }
}


function drawArea(ctx, SIZE, r) {
  const s = getScale(SIZE, r);

  ctx.save();
  ctx.translate(SIZE/2, SIZE/2);
  ctx.scale(1, -1);


  ctx.fillStyle = 'rgba(78, 161, 255, 0.35)';
  ctx.strokeStyle = 'rgba(78, 161, 255, 0.95)';
  ctx.lineWidth = 1.5;
  ctx.lineJoin = 'round';
  ctx.lineCap = 'round';

  // 2 четверть четверть круга
  ctx.beginPath();
  ctx.moveTo(0, 0);
  ctx.arc(0, 0, r * s, Math.PI / 2, Math.PI, false);
  ctx.closePath();
  ctx.fill();
  ctx.stroke();

  // 4 четверть прямоугольник
  ctx.beginPath();
  ctx.rect(0, -r * s / 2, r * s, r * s / 2);
  ctx.closePath();
  ctx.fill();
  ctx.stroke();

  // 3 четверть треугольник
  ctx.beginPath();
  ctx.moveTo(0, 0);
  ctx.lineTo(-r * s / 2, 0);
  ctx.lineTo(0, -r * s / 2);
  ctx.closePath();
  ctx.fill();
  ctx.stroke();

  ctx.restore();
}

function toCanvas(SIZE, x, y, r){ const s=getScale(SIZE, r); return [SIZE/2 + x*s, SIZE/2 - y*s]; }
function drawPoint(ctx, SIZE, x, y, r){
  const [cx,cy]=toCanvas(SIZE,x,y,r);
  ctx.save(); ctx.beginPath(); ctx.arc(cx,cy,4,0,Math.PI*2); ctx.fillStyle='#111'; ctx.fill(); ctx.restore();
}
function redraw(ctx, SIZE, xHidden, yInput, rChecks, clearOnly){
  const r = getR(rChecks) || 1;
  const x = xHidden.value === '' ? null : Number(xHidden.value);
  const y = yInput.value === '' ? null : Number(yInput.value);
  ctx.clearRect(0,0,SIZE,SIZE);
  drawArea(ctx, SIZE, r);
  drawAxes(ctx, SIZE, r);
  if (!clearOnly && x !== null && Number.isFinite(y)) drawPoint(ctx, SIZE, x, y, r);
}

//  main
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('hit-form');
  const btnsX = Array.from(document.querySelectorAll('.btn-x'));
  const xHidden = document.getElementById('x-input');
  const yInput = document.getElementById('y-input');
  const rChecks = Array.from(document.querySelectorAll('.r-check'));
  const sidInput = document.getElementById('sid-input');
  const submitBtn = document.getElementById('submit-btn');
  const resetBtn = document.getElementById('reset-btn');
  const msg = document.getElementById('msg');

  const serverTimeEl = document.getElementById('server-time');
  const execTimeEl = document.getElementById('exec-time');
  const historyTable = document.getElementById('history-table');

  const canvas = document.getElementById('plot');
  const ctx = canvas.getContext('2d');
  const SIZE = canvas.width;
  const initialSid = (sidInput.value || 'anonymous').trim() || 'anonymous';
  const offline = loadLocalHistory(initialSid);
  if (offline.length) {
    fillHistory(historyTable, offline);
  }
  //первичный рендер
  setMessage(msg, 'Готов к работе', true);
  validate(xHidden, yInput, rChecks, msg, submitBtn);
  redraw(ctx, SIZE, xHidden, yInput, rChecks, true);

  // выбор x с подсветкой при выборе
  btnsX.forEach(b => b.addEventListener('click', () => {
    btnsX.forEach(bb => bb.classList.remove('active'));
    b.classList.add('active');
    xHidden.value = b.dataset.x;
    validate(xHidden, yInput, rChecks, msg, submitBtn);
    redraw(ctx, SIZE, xHidden, yInput, rChecks, false);
  }));

  // только один r
  rChecks.forEach(ch => ch.addEventListener('change', () => {
    if (ch.checked) rChecks.forEach(o => { if (o !== ch) o.checked = false; });
    validate(xHidden, yInput, rChecks, msg, submitBtn);
    redraw(ctx, SIZE, xHidden, yInput, rChecks, false);
  }));

  // фильтр ввода y
  yInput.addEventListener('input', () => {
    yInput.value = yInput.value.replace(/[^0-9\-\.\,]/g, '').replace(',', '.');
    validate(xHidden, yInput, rChecks, msg, submitBtn);
    redraw(ctx, SIZE, xHidden, yInput, rChecks, false);
  });
   sidInput.addEventListener('input', () => {
      const sidNow = (sidInput.value || 'anonymous').trim() || 'anonymous';
      const off = loadLocalHistory(sidNow);
      fillHistory(historyTable, off);
    });

  // сброс
  resetBtn.addEventListener('click', () => {
    btnsX.forEach(bb => bb.classList.remove('active'));
    xHidden.value = '';
    yInput.value = '';
    rChecks.forEach(c => c.checked = false);
    sidInput.value = '';
    setMessage(msg, '', true);
    validate(xHidden, yInput, rChecks, msg, submitBtn);
    redraw(ctx, SIZE, xHidden, yInput, rChecks, true);
  });


  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!validate(xHidden, yInput, rChecks, msg, submitBtn)) return;

     const sidRaw = (sidInput.value || '').trim();
        const params = new URLSearchParams({
          x: xHidden.value,
          y: yInput.value,
          r: String(getR(rChecks))
        });
        if (sidRaw) params.set('sid', sidRaw);

    try {
      setMessage(msg, 'Отправка запроса…', true);
      const resp = await fetch(form.action + '?' + params.toString(), { method: 'GET' });
      const data = await resp.json();
      if (!data.ok) { setMessage(msg, data.error || 'Ошибка', false); return; }

      serverTimeEl.textContent = data.data.serverTime ?? '—';
      execTimeEl.textContent = typeof data.data.execTimeMs === 'number'
        ? data.data.execTimeMs.toFixed(3) : '—';
      fillHistory(historyTable, data.history || []);
      const sidStore = (sidInput.value || 'anonymous').trim() || 'anonymous';
            saveLocalHistory(sidStore, data.history || []);
      setMessage(msg, 'Готово', true);
      redraw(ctx, SIZE, xHidden, yInput, rChecks, false);
    } catch (err) {
      console.error(err);
      setMessage(msg, 'Сетевая ошибка', false);
    }
  });

  function fillHistory(table, items) {
    while (table.rows.length > 1) table.deleteRow(1);
    items.forEach((h, i) => {
      const tr = table.insertRow(-1);
      tr.insertCell(-1).textContent = String(i+1);
      tr.insertCell(-1).textContent = String(h.x);
      tr.insertCell(-1).textContent = String(h.y);
      tr.insertCell(-1).textContent = String(h.r);
      const c = tr.insertCell(-1);
      c.textContent = h.hit ? 'да' : 'нет';
      c.className = h.hit ? 'hit-yes' : 'hit-no';
      tr.insertCell(-1).textContent = h.ts || '';
      tr.insertCell(-1).textContent = h.sid || '';
    });
  }
  const clearLocalBtn = document.getElementById('clear-local');
    if (clearLocalBtn) {
      clearLocalBtn.addEventListener('click', () => {
        const sidNow = (sidInput.value || 'anonymous').trim() || 'anonymous';
        saveLocalHistory(sidNow, []);
        while (historyTable.rows.length > 1) historyTable.deleteRow(1);
        setMessage(msg, 'Локальная история очищена', true);
      });
    }
});
