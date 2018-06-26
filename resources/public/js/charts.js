Vue.filter('formatDate', function(value) {
    if (value) {
        return (new Date(value)).toLocaleString();
    }
});

(function getTrials() {
    $.get("/trials", function(data) {
        var obj = JSON.parse(data);
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
var FINGERTIP_SENSOR = "Fingertip Sensor";

function getTrial(trial) {
    var trialId = trial._id;
    $.get("/trials/" + trialId, function(data) {
        var obj = JSON.parse(data);
        chartDevices(obj);
    });
}

function datifyTimestamps(device) {
    for (var key in device) {
        if (device.hasOwnProperty(key)) {
            device[key].map(v => {
                v.x = new Date(v.x);
                return v;
            });
        }
    }
}

function chartDevices(data) {
    var charts = [];

    console.log(data);

    if (data.hasOwnProperty(WRIST_DEVICE)) {
        console.log("creating wrist device");
        datifyTimestamps(data[WRIST_DEVICE]);
        var lines = [{
            type: "line",
            dataPoints: data[WRIST_DEVICE].red,
            legendText: "RED",
            showInLegend: true,
            color: "red"
        }, {
            type: "line",
            dataPoints: data[WRIST_DEVICE].ir,
            lineColor: "blue",
            legendText: "IR",
            showInLegend: true,
            color: "blue"
        }];
        charts.push(createChart(lines, "Wrist LEDs", "wrist_device"));
    }

    console.log(data);

    if (data.hasOwnProperty(GROUND_TRUTH)) {
        console.log("creating GT");
        datifyTimestamps(data[GROUND_TRUTH]);
        var lines = [{
            type: "line",
            dataPoints: data[GROUND_TRUTH].hr,
            legendText: "HR",
            showInLegend: true,
            color: "red"
        }, {
            type: "line",
            dataPoints: data[GROUND_TRUTH].oxygen,
            legendText: "Oxygen",
            showInLegend: true,
            color: "blue"
        }];
        charts.push(createChart(lines, "GROUND TRUTH", "ground_truth"));
    }

    _CHARTS = charts;
}

function createChart(data, title, containerId) {
    console.log("Creating chart for " + containerId);
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
    console.log("rendered");
    return chart;
}
