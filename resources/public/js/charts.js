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

    if (data.hasOwnProperty(WRIST_DEVICE)) {
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


    if (data.hasOwnProperty(GROUND_TRUTH)) {
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

    if (data.hasOwnProperty(FINGERTIP_SENSOR)) {
        datifyTimestamps(data[FINGERTIP_SENSOR]);
        var lines = [{
            type: "line",
            dataPoints: data[FINGERTIP_SENSOR].red,
            legendText: "RED",
            showInLegend: true,
            color: "red"
        }, {
            type: "line",
            dataPoints: data[FINGERTIP_SENSOR].ir,
            legendText: "IR",
            showInLegend: true,
            color: "blue"
        }];
        charts.push(createChart(lines, "Fingertip LEDs", "fingertip_sensor"));
    } else {
        $("#fingertip_sensor").remove()
    }

    _CHARTS = charts;
}

function createChart(data, title, containerId) {
    var chart = new CanvasJS.Chart(containerId, {
        zoomEnabled: true,
        zoomType: "xy", // change it to "xy" to enable zooming on both axes
        title: {
            text: title
        },
        data: data,
        axisY: {
            includeZero: false
        },
        rangeChanged: syncHandler
    });
    chart.render();
    return chart;
}
