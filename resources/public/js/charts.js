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
        console.log(obj);
        chartDevices(obj.devices);
    });
}

function datifyTimestamps(data){
    for (var key in data) {
        if (key == "_id") {
            continue;
        }
        if (data.hasOwnProperty(key)) {
            data[key].map(v => {
                v.x = new Date(v.x);
                return v;
            });
        }
    }
}

function chartDevices(devices) {
    var charts = [];

    for (var i = 0; i < devices.length; i++) {
        var device = devices[i];
        var data = device.data;
        datifyTimestamps(data);
        if (device.type == WRIST_DEVICE) {
            var lines = [{
                type: "line",
                dataPoints: data.red,
                legendText: "RED",
                showInLegend: true,
                color: "red"
            }, {
                type: "line",
                dataPoints: data.ir,
                lineColor: "blue",
                legendText: "IR",
                showInLegend: true,
                color: "blue"
            }];
            charts.push(createChart(lines, "Wrist LEDs", "wrist_device"));
        } else if (device.type == GROUND_TRUTH) {
            datifyTimestamps(data[GROUND_TRUTH]);
            var lines = [{
                type: "line",
                dataPoints: data.hr,
                legendText: "HR",
                showInLegend: true,
                color: "red"
            }, {
                type: "line",
                dataPoints: data.oxygen,
                legendText: "Oxygen",
                showInLegend: true,
                color: "blue"
            }];
            charts.push(createChart(lines, "GROUND TRUTH", "ground_truth"));
        } else if (device.type == FINGERTIP_SENSOR) {
            datifyTimestamps(data[FINGERTIP_SENSOR]);
            var lines = [{
                type: "line",
                dataPoints: data.red,
                legendText: "RED",
                showInLegend: true,
                color: "red"
            }, {
                type: "line",
                dataPoints: data.ir,
                legendText: "IR",
                showInLegend: true,
                color: "blue"
            }];
            charts.push(createChart(lines, "Fingertip LEDs", "fingertip_sensor"));
        } else {
            console.error("Unknown device type");
        }
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
