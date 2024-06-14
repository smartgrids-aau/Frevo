#!/bin/bash

experiment="exp2"
problem="brokerage"
component_xml="../energyBrokers.xml"
result_file="result_${experiment}.csv"
activation="SIGMOID"
threads=40
popsize=30
generations=500

ann_types=("ANN_C" "ANN_D")
input=(34 33)
output=7

duration=86400 #604800

echo "seed,architecture,controller,hidden_nodes,fitness" >> ${result_file}	

for algorithm in "NNGA"
do
	for entry in ${!input[@]}
	do
		controller=${ann_types[$entry]}
		for nn in "3LN" "FMN"
		do
			template="${problem}_${algorithm}_${nn}_template.zse"	# template file to be edited
			for hidden in 2 4 6
			do
				echo "Testing controller ${controller} as ${nn} using ${input[${entry}]} inputs and ${hidden} hidden nodes"
				#echo "${ann_types[$entry]} ${input[${entry}]}"
				for seed in {12345..12355}
				do
					# define the name of the folder where to keep the files with information about the experiments
					dir_res="${experiment}\/${controller}_${algorithm}_${nn}_s${seed}_${activation}_h${hidden}"

					# modify the problem file
					sed -e "s/-INPUT-/${input[${entry}]}/g" -e "s/-OUTPUT-/${output}/g" < ${component_xml} > ../../Components/Problems/energyBrokers.xml
					# modify the session file
					sed -e "s/-INFO-/${dir_res}/g" -e "s/-SEED-/${seed}/g" \
						-e "s/-CONTROLLER-/${controller}/g" -e "s/-DURATION-/${duration}/g" \
						-e "s/-GENERATIONS-/${generations}/g" -e "s/-ACTIVATION-/${activation}/g" \
						-e "s/-HIDDEN-/${hidden}/g" -e "s/-POPSIZE-/${popsize}/g" \
						-e "s/-THREADS-/${threads}/g" < ${template} > session.zse

					if [ -f session.zse ]; then
						#read -r income fit_income expenses reimbursement <<<
						../../launch_Frevo.sh -s session.zse 

						#fitness=$(tail -n 1  "../../Results/${dir_res}/stats.csv" | awk -F ',' '{print $1}')
						fitness=$(tail -n 1  "../../Results/${experiment}/${controller}_${algorithm}_${nn}_s${seed}_${activation}_h${hidden}/stats.csv" | awk -F ',' '{print $1}')
						echo "Seed ${seed} produced solution with fitness ${fitness}"
						echo "${seed},${controller},${nn},${hidden},${fitness}" >> ${result_file}     

						# remove the problem definition file
						rm ../../Components/Problems/energyBrokers.xml
					fi
				done
			done
		done
	done	
done

cp ../energyBrokers.xml ../../Components/Problems/energyBrokers.xml
echo "Execution of the evalscript_brokerage.sh on $(hostname) is done!" | mail -s "Execution of the evalscript_brokerage.sh on $(hostname) is done!" andrea.monacchi@aau.at