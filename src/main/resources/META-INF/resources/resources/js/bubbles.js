(function () {
    "use strict";

    var canvas = null;
    var ctx = null;
    var drawInterval = null;
    var bubbles = [];

    function resizeCanvas() {
        if (!canvas) return;
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }

    function makeBubble() {
        return {
            x: Math.random() * window.innerWidth,
            y: window.innerHeight + Math.random() * 100,
            r: 4 + Math.random() * 14,
            speed: 0.5 + Math.random() * 1.5,
            drift: Math.random() * 1 - 0.5,
            wobble: Math.random() * Math.PI * 2
        };
    }

    function startBubbles() {
        if (canvas) return;

        canvas = document.createElement("canvas");
        canvas.id = "siaBubblesCanvas";
        canvas.style.cssText =
            "position:fixed;inset:0;width:100%;height:100%;z-index:-1;pointer-events:none;";
        document.body.prepend(canvas);

        ctx = canvas.getContext("2d");
        resizeCanvas();
        window.addEventListener("resize", resizeCanvas);

        var bubbleCount = 55;
        bubbles = [];
        for (var i = 0; i < bubbleCount; i++) {
            var b = makeBubble();
            b.y = Math.random() * window.innerHeight;
            bubbles.push(b);
        }

        drawInterval = setInterval(function () {
            if (!ctx) return;
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            for (var i = 0; i < bubbles.length; i++) {
                var b = bubbles[i];
                b.wobble += 0.02;
                b.y -= b.speed;
                b.x += b.drift + Math.sin(b.wobble) * 0.6;

                ctx.beginPath();
                ctx.arc(b.x, b.y, b.r, 0, Math.PI * 2);
                ctx.strokeStyle = "rgba(255, 255, 255, 0.55)";
                ctx.lineWidth = 1.5;
                ctx.stroke();
                ctx.fillStyle = "rgba(174, 226, 255, 0.18)";
                ctx.fill();

                if (b.y < -20) {
                    bubbles[i] = makeBubble();
                }
            }
        }, 33);
    }

    function stopBubbles() {
        if (drawInterval) {
            clearInterval(drawInterval);
            drawInterval = null;
        }
        window.removeEventListener("resize", resizeCanvas);
        if (canvas) {
            canvas.remove();
            canvas = null;
            ctx = null;
        }
        bubbles = [];
    }

    window.siaBubbles = {
        start: startBubbles,
        stop: stopBubbles
    };
})();
