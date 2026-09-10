(function () {
    "use strict";

    var MATRIX_CHARS = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    var canvas = null;
    var ctx = null;
    var drawInterval = null;
    var scrollInterval = null;
    var refreshInterval = null;
    var scrollTargets = [];

    function resizeCanvas() {
        if (!canvas) return;
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }

    function startRain() {
        if (canvas) return;

        canvas = document.createElement("canvas");
        canvas.id = "siaMatrixRainCanvas";
        canvas.style.cssText =
            "position:fixed;inset:0;width:100%;height:100%;z-index:-1;pointer-events:none;";
        document.body.prepend(canvas);

        ctx = canvas.getContext("2d");
        resizeCanvas();
        window.addEventListener("resize", resizeCanvas);

        var fontSize = 16;
        var columns = Math.floor(canvas.width / fontSize);
        var drops = new Array(columns).fill(1);

        drawInterval = setInterval(function () {
            if (!ctx) return;
            ctx.fillStyle = "rgba(10, 14, 10, 0.08)";
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            ctx.fillStyle = "#2ee85e";
            ctx.font = fontSize + "px monospace";

            for (var i = 0; i < drops.length; i++) {
                var char = MATRIX_CHARS.charAt(Math.floor(Math.random() * MATRIX_CHARS.length));
                ctx.fillText(char, i * fontSize, drops[i] * fontSize);
                if (drops[i] * fontSize > canvas.height && Math.random() > 0.975) {
                    drops[i] = 0;
                }
                drops[i]++;
            }
        }, 50);
    }

    function stopRain() {
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
    }

    function findScrollTargets() {
        var candidates = document.querySelectorAll(".matrix-mode .content *");
        var targets = [];
        candidates.forEach(function (el) {
            var style = window.getComputedStyle(el);
            var scrollable = style.overflowY === "auto" || style.overflowY === "scroll";
            if (scrollable && el.scrollHeight > el.clientHeight + 4) {
                targets.push(el);
            }
        });
        scrollTargets = targets;
    }

    function startAutoScroll() {
        if (scrollInterval) return;

        findScrollTargets();
        refreshInterval = setInterval(findScrollTargets, 2000);

        scrollInterval = setInterval(function () {
            scrollTargets.forEach(function (el) {
                if (!el.isConnected) return;
                el.scrollTop += 1;
                if (el.scrollTop + el.clientHeight >= el.scrollHeight) {
                    el.scrollTop = 0;
                }
            });
        }, 35);
    }

    function stopAutoScroll() {
        if (scrollInterval) {
            clearInterval(scrollInterval);
            scrollInterval = null;
        }
        if (refreshInterval) {
            clearInterval(refreshInterval);
            refreshInterval = null;
        }
        scrollTargets = [];
    }

    window.siaMatrixRain = {
        start: function () {
            startRain();
            startAutoScroll();
        },
        stop: function () {
            stopRain();
            stopAutoScroll();
        }
    };
})();
