#!/bin/bash
algorithm="NNGA"
experiment="exp0_no_increvo"
problem="brokerage"
component_xml="../energyBrokers.xml"
result_file="result_${experiment}.csv"

activation="SIGMOID"
threads=50
popsize=50
generations=800

ann_types=("ANN_A" "ANN_B" "ANN_C" "ANN_D")
input=(6 5 34 33)
output=7

duration=86400
incr_evo="false"

echo "weather,beginning,gridtype,ann_tech_type,ann_interface,hidden_nodes,penalty_reimbursement,seed,fitness" >> ${result_file}	

for seed in {12345..12354} # put seed outside to have all ANN types simulated firstly (and have at least something to show on the way)
do
	for penalty_reimbursement in 10000.0 100000.0 1000.0
	do
		for weather in 0 1
		do
			for beginning in "2015 6 21 23:50:00" "2014 12 31 23:50:00"
			do
				for grid_type in 0 1 2 3 4
				do
					for hidden in 2 3 4
					do
						for entry in ${!input[@]}
						do
							controller=${ann_types[$entry]}
							for nn in "3LN" "FMN"
							do
								template="${problem}_${algorithm}_${nn}_template.zse"	# template file to be edited

								echo "Testing controller ${controller} as ${nn} using ${input[${entry}]} inputs and ${hidden} hidden nodes (Seed ${seed})"
				
								# define the name of the folder where to keep the files with information about the experiments
								dir_res="${experiment}\/w${weather}\/${beginning}\/g${grid_type}\/${controller}\/${nn}\/h${hidden}\/p${penalty_reimbursement}\/s${seed}"

								# modify the problem file
								sed -e "s/-INPUT-/${input[${entry}]}/g" -e "s/-OUTPUT-/${output}/g" < ${component_xml} > ../../Components/Problems/energyBrokers.xml
								# modify the session file
								sed -e "s/-INFO-/${dir_res}/g" -e "s/-SEED-/${seed}/g" \
									-e "s/-BEGINNING-/${beginning}/g" -e "s/-CONTROLLER-/${controller}/g" -e "s/-DURATION-/${duration}/g" \
									-e "s/-GRIDTYPE-/${grid_type}/g" -e "s/-WEATHERTYPE-/${weather}/g" \
									-e "s/-INCREVO-/${incr_evo}/g" -e "s/-PEN_REIMB-/${penalty_reimbursement}/g" \
									-e "s/-GENERATIONS-/${generations}/g" -e "s/-ACTIVATION-/${activation}/g" \
									-e "s/-HIDDEN-/${hidden}/g" -e "s/-POPSIZE-/${popsize}/g" \
									-e "s/-THREADS-/${threads}/g" < ${template} > session.zse

								if [ -f session.zse ]; then
									../../launch_Frevo.sh -s session.zse 
			
									fitness=$(tail -n 1  "../../Results/${experiment}/w${weather}/${beginning}/g${grid_type}/${controller}/${nn}/h${hidden}/p${penalty_reimbursement}/s${seed}/stats.csv" | awk -F ',' '{print $1}')
									echo "Seed ${seed} produced solution with fitness ${fitness}"
									echo "${weather},${beginning},${grid_type},${controller},${nn},${hidden},${penalty_reimbursement},${seed},${fitness}" >> ${result_file}     
		
									# remove the problem definition file
									rm ../../Components/Problems/energyBrokers.xml
								fi
							done
						done
					done
				done
			done
		done
	done
done

# put back in the FREVO folder the original xml problem description
cp ../energyBrokers.xml ../../Components/Problems/energyBrokers.xml
echo "Execution of the evalscript_brokerage.sh on $(hostname) is done!" | mail -s "Execution of the evalscript_brokerage.sh on $(hostname) is done!" andrea.monacchi@aau.at