. $(dirname "$0")/config.sh

# make dir if not existing
hadoop fs -mkdir -p /user/$username/
hadoop fs -mkdir -p /user/$username/$jobname
hadoop fs -mkdir -p /user/$username/$jobname/input
hadoop fs -mkdir -p /user/$username/$jobname/output

# remove previous input documents if already existing
hadoop fs -rm -r /user/$username/$jobname/input/documents

# upload input documents
hadoop fs -put documents /user/$username/$jobname/input
hadoop jar mapreduce-$jobname.jar $jobname /user/$username/$jobname/input/documents /user/$username/$jobname/output/$(date +%s)