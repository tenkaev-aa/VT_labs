
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
function getR(rChecks) {
  const c = rChecks.find(x => x.checked);
  return c ? parseFloat(c.value) : null;
}
function validate(xHidden, yInput, rChecks, msg, submitBtn, opts = { clearOnOk: true }) {
  const xOk = xHidden.value !== '' && Number.isInteger(Number(xHidden.value));
  const y = parseFloat((yInput.value || '').replace(',', '.'));
  const yOk = Number.isFinite(y) && y > -3 && y < 3;
  const rOk = getR(rChecks) !== null;

  if (submitBtn) submitBtn.disabled = !(xOk && yOk && rOk);

  if (!xOk) {
    setMessage(msg, 'Выберите X', false);
    return false;
  }
  if (!yOk) {
    setMessage(msg, 'Y должен быть числом в (-3; 3)', false);
    return false;
  }
  if (!rOk) {
    setMessage(msg, 'Выберите R', false);
    return false;
  }


  if (opts.clearOnOk) {
    setMessage(msg, '', true);
  }
  return true;
}


function getScale(SIZE, r) { return (SIZE * 0.42) / Math.max(3, r); }

function drawAxes(ctx, SIZE, r) {
  const s = getScale(SIZE, r);
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
    ctx.beginPath(); ctx.moveTo(m * s, -4); ctx.lineTo(m * s, 4); ctx.stroke();   // по оси X
    ctx.beginPath(); ctx.moveTo(-4, m * s); ctx.lineTo(4, m * s); ctx.stroke();   // по оси Y
  }
  ctx.restore();

  // Подписи
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
      ctx.fillText(lx, dx - 10, AY + 16);  // подписи на оси X
      ctx.fillText(lx, AX + 8, dy + 4);    // подписи на оси Y
    }
  }

  // подписи осей
  ctx.font = 'bold 13px Arial';
  ctx.fillStyle = '#444';
  ctx.fillText('X', SIZE - 18, AY - 6);
  ctx.fillText('Y', AX + 6, 16);

  ctx.restore();
}


function drawArea(ctx, SIZE, r) {
  const s = getScale(SIZE, r);
  ctx.save();
  ctx.translate(SIZE/2, SIZE/2);
  ctx.scale(1, -1);

  ctx.fillStyle = 'rgba(78,161,255,0.35)';
  ctx.strokeStyle = 'rgba(78,161,255,0.95)';
  ctx.lineWidth = 1.5;

 // квадрат
  ctx.beginPath(); ctx.rect(0, 0, r*s, r*s); ctx.closePath(); ctx.fill(); ctx.stroke();

  //  четверть круга
  ctx.beginPath(); ctx.moveTo(0,0); ctx.arc(0,0,(r*s)/2, Math.PI/2, Math.PI, false);
  ctx.closePath(); ctx.fill(); ctx.stroke();

  // треугольник
  ctx.beginPath(); ctx.moveTo(0,0); ctx.lineTo(r*s,0); ctx.lineTo(0,-r*s);
  ctx.closePath(); ctx.fill(); ctx.stroke();

  ctx.restore();
}

function toCanvas(SIZE, x, y, r) { const s=getScale(SIZE,r); return [SIZE/2 + x*s, SIZE/2 - y*s]; }
function fromCanvas(SIZE, cx, cy, r) { const s=getScale(SIZE,r); return [(cx - SIZE/2)/s, (SIZE/2 - cy)/s]; }

function drawPoint(ctx,SIZE,x,y,r){
  const [cx,cy]=toCanvas(SIZE,x,y,r);
  ctx.save(); ctx.beginPath(); ctx.arc(cx,cy,4,0,Math.PI*2); ctx.fillStyle='#111'; ctx.fill(); ctx.restore();
}

function redraw(ctx, SIZE, xHidden, yInput, rChecks, clearOnly){
  const r = getR(rChecks) || 1;
  const x = xHidden.value === '' ? null : Number(xHidden.value);
  const y = yInput.value === '' ? null : Number((yInput.value||'').replace(',', '.'));
  ctx.clearRect(0,0,SIZE,SIZE);
  drawArea(ctx, SIZE, r);
  drawAxes(ctx, SIZE, r);
  if (!clearOnly && Number.isInteger(x) && Number.isFinite(y)) drawPoint(ctx, SIZE, x, y, r);
}

// --- main ---
document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('hit-form');
  const btnsX = Array.from(document.querySelectorAll('.btn-x'));
  const xHidden = document.getElementById('x-input');
  const yInput  = document.getElementById('y-input');
  const rChecks = Array.from(document.querySelectorAll('.r-check'));
  const submitBtn = document.getElementById('submit-btn');
  const resetBtn  = document.getElementById('reset-btn');
  const msg = document.getElementById('msg');
  let canvasSubmitTimer= null;
  const canvas = document.getElementById('plot');
  const ctx = canvas.getContext('2d');
  const SIZE = canvas.width;

  setMessage(msg, 'Готов к работе', true);
  validate(xHidden, yInput, rChecks, msg, submitBtn);
  redraw(ctx, SIZE, xHidden, yInput, rChecks, true);

  // выбор X
  btnsX.forEach(b => b.addEventListener('click', () => {
    btnsX.forEach(bb => bb.classList.remove('active'));
    b.classList.add('active');
    xHidden.value = b.dataset.x;
    validate(xHidden, yInput, rChecks, msg, submitBtn);
    redraw(ctx, SIZE, xHidden, yInput, rChecks, false);
  }));

  // только один R
  rChecks.forEach(ch => ch.addEventListener('change', () => {
    if (ch.checked) rChecks.forEach(o => { if (o !== ch) o.checked = false; });
    validate(xHidden, yInput, rChecks, msg, submitBtn);
    redraw(ctx, SIZE, xHidden, yInput, rChecks, false);
  }));

  // фильтр Y
  yInput.addEventListener('input', () => {
    yInput.value = yInput.value.replace(/[^0-9\-.,]/g, '').replace(',', '.');
    validate(xHidden, yInput, rChecks, msg, submitBtn);
    redraw(ctx, SIZE, xHidden, yInput, rChecks, false);
  });

  // сброс
  if (resetBtn) {
    resetBtn.addEventListener('click', (e) => {
      e.preventDefault();
      btnsX.forEach(bb => bb.classList.remove('active'));
      xHidden.value = '';
      yInput.value = '';
      rChecks.forEach(c => c.checked = false);
      setMessage(msg, '', true);
      validate(xHidden, yInput, rChecks, msg, submitBtn);
      redraw(ctx, SIZE, xHidden, yInput, rChecks, true);
    });
  }

  // клик по графику
  canvas.addEventListener('click', (ev) => {
    const r = getR(rChecks);
    if (r === null) {
      setMessage(msg, 'Сначала выберите R', false);
      return;
    }

    const rect = canvas.getBoundingClientRect();
    const cx = ev.clientX - rect.left;
    const cy = ev.clientY - rect.top;
    const [x, y] = fromCanvas(SIZE, cx, cy, r);


    const xSnap = Math.max(-4, Math.min(4, Math.round(x)));
    const yVal = Math.max(-2.999, Math.min(2.999, Math.round(y * 1000) / 1000));


    btnsX.forEach(bb => {
      if (bb.dataset.x === String(xSnap)) bb.classList.add('active');
      else bb.classList.remove('active');
    });


    xHidden.value = String(xSnap);
    yInput.value = String(yVal);


    setMessage(msg, 'X округлён до ' + xSnap, true);


    validate(xHidden, yInput, rChecks, msg, submitBtn,{ clearOnOk: false })
    redraw(ctx, SIZE, xHidden, yInput, rChecks, false);


    if (canvasSubmitTimer) clearTimeout(canvasSubmitTimer);


    canvasSubmitTimer = setTimeout(() => {
      if (validate(xHidden, yInput, rChecks, msg, submitBtn,{ clearOnOk: false })) {
        form.submit();
      }
    }, 700);
  });


  form.addEventListener('submit', (e) => {
    if (!validate(xHidden, yInput, rChecks, msg, submitBtn)) {
      e.preventDefault();
    }
  });
});
