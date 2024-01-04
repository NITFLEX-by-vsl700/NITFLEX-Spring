var videoLink = document.getElementById("videoId").value;
var canvasElement = document.getElementById('my-video');
var ctx = canvasElement.getContext("2d");
var imageCount = 50; // Replace with the actual number of images
var frames = [];
var paused = false;
var ready = false;
var playerSized = false;

const frame = function(bytes) {
    const BYTES = bytes;
    const PREV = NEXT = null;

    return {BYTES, PREV, NEXT};
}

async function loadImages(){
    await fetch(`/stream/video/${videoLink}?beginFrame=${10000}&length=${imageCount}`)
        .then(response => response.text())
        .then(jsonResponse => {
            let obj = JSON.parse(jsonResponse);
            obj.forEach(f => {
                const newFrame = frame(f);
                if(frames.length > 1){
                    let last = frames[frames.length - 2];
                    newFrame.PREV = last;
                    last.NEXT = newFrame;
                }

                frames.push(newFrame);
            });
        })
    // await fetch(`/stream/video/${videoLink}?beginFrame=${10000}&length=${imageCount}`)
    //     .then(response => response.arrayBuffer())
    //     .then(buffer => {
    //         frames.push(frame(new Uint8Array(buffer)));
    //     })

    ready = true;
}

function playImagesAsVideo() {
    var index = 0;
    var interval = setInterval(function() {
        if(paused || !ready)
            return;

        if (index === imageCount) {
            //clearInterval(interval);
            index = 0;
        }

        let imageObj = new Image();
        imageObj.src = "data:image/jpeg;base64,"+frames[index].BYTES;
        imageObj.onload = function(){
            if(!playerSized){ // Lazy loading (of the render size)
                ctx.width = canvasElement.width = imageObj.width;
                ctx.height = canvasElement.height = imageObj.height;
                playerSized = true;
            }

            ctx.drawImage(imageObj,0,0);
        }
//        let imageData = ctx.getImageData(0, 0, width, height);
//        let data = imageData.data;
//
//        for (let y = 0; y < height; y++) {
//            for (let x = 0; x < width; x++) {
//                let i = (y * width + x) * 4;
//                let i1 = (y * width + x) * 3;
//                // Set the color of the pixel
//                data[i] = frames[index].BYTES[i1]; // Red value (0-255)
//                data[i + 1] = frames[index].BYTES[i1 + 1]; // Green value (0-255)
//                data[i + 2] = frames[index].BYTES[i1 + 2]; // Blue value (0-255)
//                data[i + 3] = 255; // Alpha value (0-255, 255 is fully opaque)
//            }
//        }
//
//        ctx.putImageData(imageData, 0, 0);

        index++;
    
    }, 1000/25); // TODO: Make sure it stops when video is paused
}

loadImages();
playImagesAsVideo();

canvasElement.addEventListener("click", () => {
    paused = !paused;
});