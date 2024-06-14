#!/bin/bash

problem="Light"

# run for different evolutionary algorithms
for algorithm in "RandomSearch" "CEA2D" "NNGA" 
#for algorithm in "GASpecies" 
do

	# run for different neural networks
	for nn in "3LN" "FMN" #
	do	
		# the name of the file with defined settings to start experiment
		template="Light_${algorithm}_${nn}_template.zse"
		# evaluations is popsize times generations
		evaluations=100000 
		# define the name of the file with results
		result_file="result_${algorithm}_${nn}_${evaluations}.csv"
		# remove the file with results from previous experiments
		rm ${result_file}
		# print names of the columns into the file with results	
		echo "seed,evaluations,popsize,generations,fitness,success" >> ${result_file}		
		# define the number of parallel threads	
		parallelthreads=8
		# different sizes of population. the population size is important quantity for metaheuristic algorithm
		for popsize in 25 36 49 64 81 100 121 144 169 196 225 256 #for popsize in 289 324 361  or 400 441 484 529 576 625  
		do
			# calculate the number of generations, which is important value for metaheuristic algorithm
			generations=`expr ${evaluations} / ${popsize}`
			# range of seeds for the experiments
			for seed in {1234..1333}
			do
				# define the name of the folder where to keep the files with information about the experiments
				dir_res="${problem}_${algorithm}_${nn}_p${popsize}_s${seed}"
				# remove log file for light problem
				rm light-log.txt
				# create the session file
				sed -e "s/-SEED-/${seed}/g" -e "s/-GENERATIONS-/${generations}/g" -e "s/-POPSIZE-/${popsize}/g" -e "s/-PARALLELTHREADS-/${parallelthreads}/g" -e "s/-INFO-/${dir_res}/g" < ${template} > session.zse
				# run frevo
				../launch_Frevo.sh -s session.zse        
				# post process results
				# get the best fitness
				fitness=$(tail -n 1  "../Results/${dir_res}/stats.csv" | awk -F ',' '{print $1}')	
				# receive success
				success=$(cat light-log.txt | egrep ${fitness} | tail -n 1 | awk '{print $2}')
				# print information about the best evolved controller from the last generations into the file with results
				echo "${seed},${evaluations},${popsize},${generations},${fitness},${success}" >> ${result_file}
			done
		done
	done
done

echo "Execution of the evalscript.sh  on $(hostname) is done!" | mail -s "Execution of the evalscript.sh on $(hostname) is done!" sergii.zhevzhyk@aau.at
