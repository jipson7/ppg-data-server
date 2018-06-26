function fetchTrialData


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
