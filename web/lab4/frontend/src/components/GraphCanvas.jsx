
import React, { useEffect, useRef } from 'react';

const WORLD_MAX = 4;

function drawScene(ctx, width, height, r, points, currentPoint) {
    ctx.clearRect(0, 0, width, height);

    const margin = 40;
    const cx = width / 2;
    const cy = height / 2;
    const s = Math.min(
        (width / 2 - margin) / WORLD_MAX,
        (height / 2 - margin) / WORLD_MAX
    );

    const X = (x) => cx + x * s;
    const Y = (y) => cy - y * s;


    ctx.stroke();
    ctx.restore();


    ctx.save();
    ctx.strokeStyle = '#111827';
    ctx.lineWidth = 1.4;


    ctx.beginPath();
    ctx.moveTo(margin / 2, cy);
    ctx.lineTo(width - margin / 2, cy);
    ctx.stroke();


    ctx.beginPath();
    ctx.moveTo(width - margin / 2, cy);
    ctx.lineTo(width - margin / 2 - 7, cy - 4);
    ctx.lineTo(width - margin / 2 - 7, cy + 4);
    ctx.closePath();
    ctx.fillStyle = '#111827';
    ctx.fill();


    ctx.beginPath();
    ctx.moveTo(cx, height - margin / 2);
    ctx.lineTo(cx, margin / 2);
    ctx.stroke();


    ctx.beginPath();
    ctx.moveTo(cx, margin / 2);
    ctx.lineTo(cx - 4, margin / 2 + 7);
    ctx.lineTo(cx + 4, margin / 2 + 7);
    ctx.closePath();
    ctx.fill();


    ctx.font = '12px system-ui';
    ctx.fillStyle = '#111827';
    ctx.fillText('X', width - margin / 2 + 10, cy + 4);
    ctx.fillText('Y', cx + 6, margin / 2 - 6);


    const ticks = [-4, -3, -2, -1, 0, 1, 2, 3, 4];
    ctx.font = '10px system-ui';
    ctx.fillStyle = '#374151';

    ticks.forEach((t) => {
        const x = X(t);
        ctx.beginPath();
        ctx.moveTo(x, cy - 3);
        ctx.lineTo(x, cy + 3);
        ctx.stroke();
        if (t !== 0) {
            ctx.fillText(String(t), x - 4, cy + 13);
        }

        const y = Y(t);
        ctx.beginPath();
        ctx.moveTo(cx - 3, y);
        ctx.lineTo(cx + 3, y);
        ctx.stroke();
        if (t !== 0) {
            ctx.fillText(String(t), cx + 7, y + 3);
        }
    });

    ctx.restore();

    if (r != null && r !== 0) {
        const R = r;

        ctx.save();
        ctx.globalAlpha = 0.25;
        ctx.fillStyle = '#2563eb';
        ctx.strokeStyle = '#1d4ed8';
        ctx.lineWidth = 1.4;


        ctx.beginPath();
        ctx.moveTo(X(0), Y(0));
        ctx.lineTo(X(R), Y(0));
        ctx.lineTo(X(0), Y(R / 2));
        ctx.closePath();
        ctx.fill();
        ctx.stroke();


        ctx.beginPath();
        ctx.moveTo(X(0), Y(0));
        ctx.lineTo(X(-R), Y(0));
        ctx.lineTo(X(-R), Y(R));
        ctx.lineTo(X(0), Y(R));
        ctx.closePath();
        ctx.fill();
        ctx.stroke();


        ctx.beginPath();
        ctx.moveTo(X(0), Y(0));
        ctx.arc(X(0), Y(0), R * s, Math.PI / 2, Math.PI, false);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();

        ctx.restore();
    }


    points.forEach((p) => {
        const hit = p.isHit ?? p.hit;


        const pointR = p.r ?? r ?? 1;


        const scale =
            r && pointR && pointR !== 0
                ? r / pointR
                : 1;


        const scaledX = p.x * scale;
        const scaledY = p.y * scale;

        const px = X(scaledX);
        const py = Y(scaledY);

        ctx.beginPath();
        ctx.arc(px, py, 4, 0, Math.PI * 2);
        ctx.fillStyle = hit ? '#16a34a' : '#ef4444';
        ctx.fill();
        ctx.strokeStyle = 'rgba(31,41,55,0.4)';
        ctx.lineWidth = 0.8;
        ctx.stroke();
    });



    if (
        currentPoint &&
        currentPoint.x != null &&
        typeof currentPoint.y === 'number' &&
        !Number.isNaN(currentPoint.y)
    ) {
        const { x: cxw, y: cyw } = currentPoint;

        const px = X(cxw);
        const py = Y(cyw);


        if (
            px >= margin / 4 &&
            px <= width - margin / 4 &&
            py >= margin / 4 &&
            py <= height - margin / 4
        ) {
            ctx.save();


            ctx.setLineDash([4, 4]);
            ctx.strokeStyle = 'rgba(37,99,235,0.5)';
            ctx.lineWidth = 0.8;

            ctx.beginPath();
            ctx.moveTo(px, cy);
            ctx.lineTo(px, py);
            ctx.moveTo(cx, py);
            ctx.lineTo(px, py);
            ctx.stroke();

            ctx.setLineDash([]);


            ctx.beginPath();
            ctx.arc(px, py, 5, 0, Math.PI * 2);
            ctx.fillStyle = '#ffffff';
            ctx.fill();
            ctx.lineWidth = 2;
            ctx.strokeStyle = '#2563eb';
            ctx.stroke();

            ctx.restore();
        }
    }
}

function GraphCanvas({ r, points, currentX, currentY, onCanvasClick }) {
    const canvasRef = useRef(null);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        const dpr = window.devicePixelRatio || 1;
        const rect = canvas.getBoundingClientRect();
        canvas.width = rect.width * dpr;
        canvas.height = rect.height * dpr;
        ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

        const numericY =
            typeof currentY === 'string' ? parseFloat(currentY) : currentY;
        const currentPoint =
            currentX != null && !Number.isNaN(numericY)
                ? { x: currentX, y: numericY }
                : null;

        drawScene(ctx, rect.width, rect.height, r, points, currentPoint);
    }, [r, points, currentX, currentY]);

    const handleClick = (e) => {
        if (!onCanvasClick) return;

        const canvas = canvasRef.current;
        if (!canvas) return;
        const rect = canvas.getBoundingClientRect();

        const margin = 40;
        const width = rect.width;
        const height = rect.height;
        const cx = width / 2;
        const cy = height / 2;
        const s = Math.min(
            (width / 2 - margin) / WORLD_MAX,
            (height / 2 - margin) / WORLD_MAX
        );

        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        const worldX = (x - cx) / s;
        const worldY = (cy - y) / s;

        onCanvasClick(worldX, worldY);
    };

    return (
        <div className="graph-container">
            <canvas
                ref={canvasRef}
                style={{ width: '100%', height: '100%' }}
                onClick={handleClick}
            />
        </div>
    );
}

export default GraphCanvas;
