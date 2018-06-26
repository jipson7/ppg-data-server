Vue.filter('formatDate', function(value) {
    if (value) {
        return (new Date(value)).toLocaleString();
    }
});

(function getTrials() {
    $.get("/trials", function(data) {
        var obj = JSON.parse(data);
        console.log(obj);
        new Vue({
            el: '#trialList',
            data: {
                trials: obj
            },
            methods: {
                fetch: getTrial
            }
        });
    });
})();

var WRIST_DEVICE = "Wrist Worn Device";
var GROUND_TRUTH = "Ground Truth Sensor";

function getTrial(trial) {
    var trialId = trial._id;
    displayTrialInfo(trial);
    $.get("/trials/" + trialId, function(data) {
        var obj = JSON.parse(data);
        console.log(obj);
        chartDevices(obj);
    });
}

function displayTrialInfo(trial) {
    new Vue({
        el: '#selected_trial',
        data: {
            trial: trial
        }
    });
}

function chartDevices(data) {
    var redData = data[WRIST_DEVICE].red;
    var irData = data[WRIST_DEVICE].ir;
    _CHARTS = [
        createChart(redData, "RED LED", "red_led_chart"),
        createChart(irData, "IR LED", "ir_led_chart")
    ];
}

function createChart(dataPoints, title, containerId) {
    var timeSeries = dataPoints.map(v => {
        v.x = new Date(v.x);
        return v;
    });
    var chart = new CanvasJS.Chart(containerId, {
        zoomEnabled: true,
        zoomType: "x", // change it to "xy" to enable zooming on both axes
        title: {
            text: title
        },
        data: [{
            type: "line",
            dataPoints: dataPoints
        }],
        rangeChanged: syncHandler
    });
    chart.render();
    return chart;
}
