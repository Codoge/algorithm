#!/bin/bash
# get route from start point to end point

function usage() {
    echo "Usage: route <dir of GTFS files> <starting location latitude> <longitude> <ending location latitude> <longitude>"
}

if [ $# != $[5] ]
then
    usage
    exit 2
fi

DIR=$1
readonly DIR
START_LAT=$2
readonly START_LAT
START_LON=$3
readonly START_LON
END_LAT=$4
readonly END_LAT
END_LON=$5
readonly END_LON

echo $* | awk -v d=$init_distance 'BEGIN {
    space=" ";
}{
    system("./haversine """$2""space""$3""space"" $4""space"" $5""" > distance.tmp");
    getline distance < "distance.tmp";
    $d = (distance+0);
    printf("init_distance=%d\n",$d) > "command.txt";
    close("distance.tmp");
}'

eval $(cat command.txt)
if [ $init_distance -lt $[1000] ]
then
    echo "The distance between start location and end location less than 1000m, you can just walk to the destination."
    exit
fi

# find stops in a radius of 1 km of start location.
cat google_transit/stops.txt | sed '1d' | sed '$d' | awk -F ',' -v start_lat=$START_LAT -v start_lon=$START_LON 'BEGIN {
    space=" ";
    i=0;
    print "find stops in a radius of 1 km of start location."
}
{
    stop_id = $3;
    system("./haversine """start_lat""space"" start_lon ""space"" $7 ""space"" $8 """ > distance.tmp");
    getline distance < "distance.tmp";
    if(distance+0 < 1000){
        print stop_id, $7, $8, distance > "start.txt";
        i++;
    }
    close("distance.tmp");
}
END{
    print "num of stops at start location nearby: ", i;
}'

# find stops in a radius of 1 km of end location.
cat google_transit/stops.txt | sed '1d' | sed '$d' | sed '$d' | awk -F ',' -v end_lat=$END_LAT -v end_lon=$END_LON 'BEGIN {
    space=" ";
    i=0;
    print "find stops in a radius of 1 km of end location."
}
{
    stop_id = $3;
    system("./haversine """end_lat""space"" end_lon ""space"" $7 ""space"" $8 """ > distance.tmp");
    getline distance < "distance.tmp";
    if(distance+0 < 1000){
        print stop_id, $7, $8, distance > "end.txt";
        i++;
    }
    close("distance.tmp");
}
END{
    print "num of stops at end location nearby: ", i;
}'

# get trips departure in 1 hour from stops nearby start location.
awk '{print $1}' start.txt |xargs -I {} grep {} google_transit/stop_times.txt | awk -F ',' 'BEGIN{
    date = strftime("%Y %m %d ", systime());
    now = systime();
    print "get trips departure in 1 hour from stops nearby start location."
    print "trip_id arrival_time departure_time stop_id stop_sequence wait_time" > "start_avail.txt";
}{
    time = $2;
    gsub(/:/, " ", time);
    tstamp=mktime(date""time);
    elapse = tstamp - now;
    if (elapse > 0 && elapse < 3600){
        print $1, $2, $3, $4, $5, elapse > "start_avail.txt";
    }
}'

# get trips arrive stops nearby end location.
awk '{print $1}' end.txt |xargs -I {} grep {} google_transit/stop_times.txt | awk -F ',' 'BEGIN{
    date = strftime("%Y %m %d ", systime());
    now = systime();
    print "get trips arrive stops nearby end location."
    print "trip_id arrival_time departure_time stop_id stop_sequence" > "stop_avail.txt";
}{
    time = $2;
    gsub(/:/, " ", time);
    tstamp=mktime(date""time);
    elapse = tstamp - now;
    if (elapse > 0){
        print $1, $2, $3, $4, $5 > "stop_avail.txt";
    }
}'

# add latitude, longtitude and distance information to the availiable trips.
echo "add latitude, longtitude and distance information to the availiable trips."
sed -i '1d' start_avail.txt
sed -i '1d' stop_avail.txt
join -14 -21 start_avail.txt start.txt > start_trip_info.txt
join -14 -21 stop_avail.txt end.txt > end_trip_info.txt

# sort and join start trip with end trip on trip_id.
echo "sort and join start trip with end trip on trip_id. "
sort -nk 2 start_trip_info.txt > start_avail.sorted
sort -nk 2 end_trip_info.txt > end_avail.sorted
join -12 -22 start_avail.sorted end_avail.sorted | awk -F ' ' 'BEGIN{
    date = strftime("%Y %m %d ", systime());
    print "trip_id stop_id arrival_time departure_time stop_sequence wait_time latitude longtitude distance stop_id arrival_time departure_time stop_sequence latitude longtitude distance duration offstamp walk_distance" > "avail_trip.txt";
}{
    on = $4;
    off = $11;
    gsub(/:/, " ", on);
    gsub(/:/, " ", off);
    onstamp=mktime(date""on);
    offstamp=mktime(date""off);
    walk_distance = ($9+0) + ($16+0);
    duration = offstamp - onstamp;
    if (duration >= 0 && offstamp > 0){
        print $0, duration, offstamp, walk_distance > "avail_trip.txt";
    }
}'

echo "find the best trip."
# sort on departure_time (finish time)
cat avail_trip.txt | awk '{printf $18"\n"}' | sort -n | sed -n '2p' | xargs -I {} grep {} avail_trip.txt > shortest_trips.txt

# if there are many trip finished in one time, then find the walk distance shortest one.
sort -n -k 19 shortest_trips.txt | sed -n '1p' > best_trip.txt

cat best_trip.txt | awk '{print $1}' | xargs -I {} grep {} google_transit/trips.txt | sed -n '1p' |awk -F ',' '{print $1}' | xargs -I {} sed 's/$/& {}/g' best_trip.txt | tr "\n" " " | awk '{print $0 > "best_trip.txt"}'

# remove temporary files
rm avail_trip.txt distance.tmp end_avail.sorted end_trip_info.txt end.txt shortest_trips.txt
rm start_avail.sorted start_avail.txt start_trip_info.txt start.txt stop_avail.txt

# generate html file
. ./generate.sh $START_LAT $START_LON $END_LAT $END_LON
