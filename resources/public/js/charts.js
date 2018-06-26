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
            data: {trials: obj},
            methods: {
                fetch: getTrial
            }
        });
    });
})();

function getTrial(trialId) {
    console.log(trialId);
}


//--------------- Chart 1 ---------------//
var chart1 = new CanvasJS.Chart("chartContainer1", {
    zoomEnabled: true,
    zoomType: "x", // change it to "xy" to enable zooming on both axes
    title: {
        text: "Chart1 - Try Zooming / Panning"
    },
    data: [{
        type: "line",
        dataPoints: dataPointsGenerator(100)
    }],
    rangeChanged: syncHandler
});

chart1.render();

//--------------- Chart 2 ---------------//
var chart2 = new CanvasJS.Chart("chartContainer2", {
    zoomEnabled: true,
    zoomType: "x", // change it to "xy" to enable zooming on both axes
    title: {
        text: "Chart2 - Try Zooming / Panning"
    },
    data: [{
        type: "line",
        dataPoints: dataPointsGenerator(100)
    }],
    rangeChanged: syncHandler
});

chart2.render();

// List required for syncing zoom levels
var charts = [chart1, chart2]; // add all charts (with axes) to be synced
