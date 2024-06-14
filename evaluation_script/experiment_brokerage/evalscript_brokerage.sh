#!/bin/bash

problem="brokerage"
component_xml="../energyBrokers.xml"
result_file="result_${problem}.csv"
activation="SIGMOID"
threads=40
popsize=80
generations=500

ann_types=("ANN_A" "ANN_B" "ANN_C" "ANN_D")
input=(6 5 34 33)
output=7

gridtype=0
weathertype=0

echo "seed,architecture,controller,hidden_nodes,fitness" >> ${result_file}	

#for algorithm in "CEA2D" "NNGA" "GASpecies"
for algorithm in "NNGA"
do
	for entry in ${!input[@]}
	do
		controller=${ann_types[$entry]}
		for nn in "3LN" "FMN"
		do
			template="${problem}_${algorithm}_${nn}_template.zse"	# template file to be edited
			for hidden in 0 2 4
			do
				echo "Testing controller ${controller} as ${nn} using ${input[${entry}]} inputs and ${hidden} hidden nodes"
				#echo "${ann_types[$entry]} ${input[${entry}]}"
				for seed in {12345..12355}
				do
					# define the name of the folder where to keep the files with information about the experiments
					dir_res="${problem}_${algorithm}_${nn}_s${seed}_${activation}_s${hidden}"
					# modify the problem file
					sed -e "s/-CONTROLLER-/${controller}/g" -e "s/-INPUT-/${input[${entry}]}/g" -e "s/-OUTPUT-/${output}/g" < ${component_xml} > ../../Components/Problems/energyBrokers.xml
					# modify the session file
					sed -e "s/-INFO-/${dir_res}/g" -e "s/-SEED-/${seed}/g" -e "s/-CONTROLLER-/${controller}/g" -e "s/-GENERATIONS-/${generations}/g" -e "s/-ACTIVATION-/${activation}/g" -e "s/-HIDDEN-/${hidden}/g" -e "s/-POPSIZE-/${popsize}/g" -e "s/-THREADS-/${threads}/g" < ${template} > session.zse

					# run frevo
					#read -r income fit_income expenses reimbursement <<<
					../../launch_Frevo.sh -s session.zse 

					fitness=$(tail -n 1  "../../Results/${dir_res}/stats.csv" | awk -F ',' '{print $1}')
					echo "\t Seed ${seed} produced solution with fitness ${fitness}"
					echo "${seed},${controller},${nn},${hidden},${fitness}" >> ${result_file}     

					# remove the problem definition file
					rm ../../Components/Problems/energyBrokers.xml 	        
				done
			done
		done
	done	
done

echo "Execution of the evalscript_brokerage.sh on $(hostname) is done!" | mail -s "Execution of the evalscript_brokerage.sh on $(hostname) is done!" andrea.monacchi@aau.at


