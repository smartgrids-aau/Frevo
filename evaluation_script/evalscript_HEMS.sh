#!/bin/bash

problem="HEMS"

# select the evolutionary algorithms for your experiments
#for algorithm in "CEA2D" "NNGA" "GASpecies"
for algorithm in "NNGA"
do
	# name of the file with initial settings
	template="${problem}_${algorithm}_testbench.zse"
	parallelthreads=20
		       
    	for seed in {12345..12543}
	do
		dir_res="${problem}_${algorithm}_s${seed}"
		# modify the session file
		sed -e "s/-SEED-/${seed}/g" -e "s/-PARALLELTHREADS-/${parallelthreads}/g" -e "s/-INFO-/${dir_res}/g" < ${template} > session.zse
		# run frevo
		../launch_Frevo.sh -s session.zse        	        
	done 
	
done

echo "Execution of the evalscript_HEMS.sh on $(hostname) is done!" | mail -s "Execution of the evalscript_HEMS.sh on $(hostname) is done!" sergii.zhevzhyk@aau.at


