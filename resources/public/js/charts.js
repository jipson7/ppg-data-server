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
    $.get("/trials/" + trialId, function(data) {
        var obj = JSON.parse(data);
        console.log(obj);
        chartDevices(obj);
    });
}

function chartDevices(data) {
    var charts = [];

    if (data.hasOwnProperty(WRIST_DEVICE)) {
        var redData = [{
            type: "line",
            dataPoints: data[WRIST_DEVICE].red
        }];
        var irData = [{
            type: "line",
            dataPoints: data[WRIST_DEVICE].ir
        }];
        charts.push(createChart(redData, "RED LED", "wrist_red_led_chart"));
        charts.push(createChart(irData, "IR LED", "wrist_ir_led_chart"));
    }

    if (data.hasOwnProperty(GROUND_TRUTH)) {
        var data = [{
            type: "line",
            dataPoints : data[GROUND_TRUTH].hr,
            legendText: "HR"
        }, {
            type: "line",
            dataPoints : data[GROUND_TRUTH].oxygen,
            legendText: "Oxygen"
        }];
        charts.push(createChart(data, "GROUND TRUTH", "ground_truth"));
    }
    _CHARTS = charts;
}

function createChart(data, title, containerId) {
    for (var i = 0; i < data.length; i++) {
        data[i].dataPoints.map(v => {
            v.x = new Date(v.x);
            return v;
        });
    }
    var chart = new CanvasJS.Chart(containerId, {
        zoomEnabled: true,
        zoomType: "x", // change it to "xy" to enable zooming on both axes
        title: {
            text: title
        },
        data: data,
        rangeChanged: syncHandler
    });
    chart.render();
    return chart;
}
