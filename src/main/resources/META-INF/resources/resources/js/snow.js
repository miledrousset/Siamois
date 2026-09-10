(function () {
    "use strict";

    var canvas = null;
    var ctx = null;
    var drawInterval = null;
    var flakes = [];

    function resizeCanvas() {
        if (!canvas) return;
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }

    function makeFlake() {
        return {
            x: Math.random() * window.innerWidth,
            y: Math.random() * -window.innerHeight,
            r: 1.5 + Math.random() * 3,
            speed: 0.6 + Math.random() * 1.6,
            drift: Math.random() * 1.2 - 0.6,
            angle: Math.random() * Math.PI * 2
        };
    }

    function startSnow() {
        if (canvas) return;

        canvas = document.createElement("canvas");
        canvas.id = "siaSnowCanvas";
        canvas.style.cssText =
            "position:fixed;inset:0;width:100%;height:100%;z-index:-1;pointer-events:none;";
        document.body.prepend(canvas);

        ctx = canvas.getContext("2d");
        resizeCanvas();
        window.addEventListener("resize", resizeCanvas);

        var flakeCount = 140;
        flakes = [];
        for (var i = 0; i < flakeCount; i++) {
            flakes.push(makeFlake());
        }

        drawInterval = setInterval(function () {
            if (!ctx) return;
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            ctx.fillStyle = "rgba(255, 255, 255, 0.9)";

            for (var i = 0; i < flakes.length; i++) {
                var f = flakes[i];
                f.angle += 0.01;
                f.y += f.speed;
                f.x += f.drift + Math.sin(f.angle) * 0.4;

                ctx.beginPath();
                ctx.arc(f.x, f.y, f.r, 0, Math.PI * 2);
                ctx.fill();

                if (f.y > canvas.height + 6) {
                    flakes[i] = makeFlake();
                    flakes[i].y = -6;
                }
                if (f.x > canvas.width + 6) f.x = -6;
                if (f.x < -6) f.x = canvas.width + 6;
            }
        }, 33);
    }

    function stopSnow() {
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
        flakes = [];
    }

    window.siaSnow = {
        start: startSnow,
        stop: stopSnow
    };
})();
