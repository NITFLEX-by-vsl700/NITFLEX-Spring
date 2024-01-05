var videoId = document.getElementById("videoId").value;
var canvasElement = document.getElementById('my-video');
var ctx = canvasElement.getContext("2d");
var minFramesCount = 50;
var frames = [];
var metadata = { duration: 0, frames: 0, frameRate: 0, width: 1920, height: 1080 };
var paused = false;
var init = false;
var ready = false;

const frame = function(bytes) {
    const BYTES = bytes;
    const PREV = NEXT = null;

    return {BYTES, PREV, NEXT};
}

async function initPlayer(){
    canvasElement.addEventListener("click", () => {
        paused = !paused;
        if(paused === false)
            playImagesAsVideo();
    });

    async function loadMeta(){
        await fetch(`/stream/info/${videoId}`)
            .then(response => response.text())
            .then(jsonResponse => {
            let obj = JSON.parse(jsonResponse);

            metadata.duration = obj.duration;
            metadata.frames = obj.frames;
            metadata.frameRate = obj.frameRate;
            metadata.width = obj.frameWidth;
            metadata.height = obj.frameHeight;
        })
    }
    await loadMeta();

    ctx.width = canvasElement.width = metadata.width;
    ctx.height = canvasElement.height = metadata.height;
    init = true;

    playImagesAsVideo();
}

async function loadImages(beginFrame, length){
    await fetch(`/stream/video/${videoId}?beginFrame=${beginFrame}&length=${length}`)
        .then(response => response.text())
        .then(jsonResponse => {
            let obj = JSON.parse(jsonResponse);
            let tempIndex = beginFrame;
            obj.forEach(f => {
                const newFrame = frame(f);

                let last = frames[tempIndex - 1];
                if(last != undefined){
                    newFrame.PREV = last;
                    last.NEXT = newFrame;
                }

                let next = frames[tempIndex + 1];
                if(next != undefined){
                    newFrame.NEXT = next;
                    next.PREV = newFrame;
                }

                frames[tempIndex] = newFrame;
                tempIndex++;
            });
        })

    ready = true;
}

var index = 10000;
function playImagesAsVideo() {
    function drawNextFrame(){
        if(paused)
            return;

        if(frames.length < minFramesCount || !init){
            setTimeout(drawNextFrame, 100);
            return;
        }

        if (frames[index] === undefined/*index === metadata.frames*/) {
            paused = true;
            index = 10000;
            return;
        }

        let imageObj = new Image();
        imageObj.src = "data:image/jpeg;base64,"+frames[index].BYTES;
        imageObj.onload = function(){
            ctx.drawImage(imageObj,0,0);
        }

        index++;
        setTimeout(drawNextFrame, 1000 / metadata.frameRate);
    }

    drawNextFrame();
}

initPlayer();
loadImages(10000, 50);
loadImages(10050, 50);
loadImages(10100, 50);