#!/bin/bash
algorithm="NNGA"
experiment="exp0"
problem="brokerage"
component_xml="../energyBrokers.xml"
result_file="result_${experiment}.csv"

activation="SIGMOID"
hidden=2
threads=40
popsize=40
generations=800

ann_types=("ANN_A" "ANN_B" "ANN_C" "ANN_D")
input=(6 5 34 33)
output=7

#beginning=("2015 6 21 23:50:00" "2014 12 31 23:50:00")
duration=86400
seed=12345
incr_evo="true"

echo "seed,architecture,controller,hidden_nodes,fitness" >> ${result_file}	

for entry in ${!input[@]}
do
	controller=${ann_types[$entry]}
	for nn in "3LN" "FMN"
	do
		template="${problem}_${algorithm}_${nn}_template.zse"	# template file to be edited

		echo "Testing controller ${controller} as ${nn} using ${input[${entry}]} inputs and ${hidden} hidden nodes"
				
		# define the name of the folder where to keep the files with information about the experiments
		dir_res="${experiment}\/${controller}_${algorithm}_${nn}_s${seed}_${activation}_h${hidden}"

		# modify the problem file
		sed -e "s/-INPUT-/${input[${entry}]}/g" -e "s/-OUTPUT-/${output}/g" < ${component_xml} > ../../Components/Problems/energyBrokers.xml
		# modify the session file
		sed -e "s/-INFO-/${dir_res}/g" -e "s/-SEED-/${seed}/g" \
			-e "s/-CONTROLLER-/${controller}/g" -e "s/-DURATION-/${duration}/g" \
			-e "s/-INCREVO-/${incr_evo}/g" \
			-e "s/-GENERATIONS-/${generations}/g" -e "s/-ACTIVATION-/${activation}/g" \
			-e "s/-HIDDEN-/${hidden}/g" -e "s/-POPSIZE-/${popsize}/g" \
			-e "s/-THREADS-/${threads}/g" < ${template} > session.zse

		if [ -f session.zse ]; then
			../../launch_Frevo.sh -s session.zse 
	
			fitness=$(tail -n 1  "../../Results/${experiment}/${controller}_${algorithm}_${nn}_s${seed}_${activation}_h${hidden}/stats.csv" | awk -F ',' '{print $1}')
			echo "Seed ${seed} produced solution with fitness ${fitness}"
			echo "${seed},${controller},${nn},${hidden},${fitness}" >> ${result_file}     

			# remove the problem definition file
			rm ../../Components/Problems/energyBrokers.xml
		fi
	done
done

cp ../energyBrokers.xml ../../Components/Problems/energyBrokers.xml
echo "Execution of the evalscript_brokerage.sh on $(hostname) is done!" | mail -s "Execution of the evalscript_brokerage.sh on $(hostname) is done!" andrea.monacchi@aau.at