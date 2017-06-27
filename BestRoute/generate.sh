#!/bin/bash
function usage() {
    echo "Usage: generate <starting location latitude> <longitude> <ending location latitude> <longitude>"
}

if [ $# != $[4] ]
then
    usage
    exit 2
fi
U_START_LAT=$1
U_START_LON=$2
U_END_LAT=$3
U_END_LON=$4
# Web directory
WEB_DIR=./

eval $(awk '{ printf("trip_id=%d\n start_id=%d\n departure_time=%s\n lat0=%f\n lon0=%f\n dist0=%d\n end_id=%d\n arrival_time=%s\n lat1=%f\n lon1=%f\n dist1=%d\n route_id=%s\n",$1,$2,$4,$7,$8,$9,$10,$11,$14,$15,$16,$20); }' ./best_trip.txt)

# A little CSS and table layout to make the report look a little nicer
echo "<HTML>
<HEAD>
    <script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>
    <script type='text/javascript'>
      google.charts.load('current', {packages:['map']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Lat', 'Long', 'Name'], " > $WEB_DIR/report.html

          echo "[$U_START_LAT, $U_START_LON, 'Your location']," >> $WEB_DIR/report.html
          start_pos="start stop, depart at "${departure_time}" route_id: "${route_id}
          echo "[$lat0, $lon0, '$start_pos']," >> $WEB_DIR/report.html
          end_pos="end stop, arrive at "${arrival_time}" route_id: "${route_id}
          echo "[$lat1, $lon1, '$end_pos']," >> $WEB_DIR/report.html
          echo "[$U_END_LAT, $U_END_LON, 'Your destination']" >> $WEB_DIR/report.html

        echo "]);

        var map = new google.visualization.Map(document.getElementById('map_div'));
        map.draw(data, {
          showTooltip: true,
          showInfoWindow: true
        });
      }

    </script>
<style>
.titulo{font-size: 1em; color: white; background:#0863CE; padding: 0.1em 0.2em;}
table
{
border-collapse:collapse;
}
table, td, th
{
border:1px solid black;
}
html{text-align:center;}
</style>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
</HEAD>
<BODY align=center>" >> $WEB_DIR/report.html
# print title at the top of the html body
echo "the <strong>Best</strong> trip with shortest time and distance<br>
Last updated: <strong>$(date)</strong><br><br>" >> $WEB_DIR/report.html


eval $(wc -l best_trip.txt | awk '{
    printf("lineNum=%d\n", $1);
}')

# check if the result if exist
if [ $lineNum == 1 ]
then
    echo "<br><br>
<p>start location: $U_START_LAT, $U_START_LON</p>
<p>First, you should walk $dist0 m to the start stop (id: $start_id)</p>
<p>Then, wait for the vehicle start your trip (route id: $route_id), which will depart at $departure_time</p>
<p>The vehicle will arrive the stop (id: $end_id) at $arrival_time, you need get off.</p>
<p>Finally, you just need walk $dist1 m to your destination: $U_END_LAT, $U_END_LON</p>" >> $WEB_DIR/report.html
else
    echo "<br><br>
    <p>There is no route for this (start , end location) pair!</p>" >> $WEB_DIR/report.html
fi

echo "<center><div id='map_div' style='width: 800px; height: 600px'></div></center></BODY></HTML>" >> $WEB_DIR/report.html
