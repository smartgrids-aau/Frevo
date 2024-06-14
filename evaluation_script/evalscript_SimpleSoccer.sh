#!/bin/bash

problem="SimpleSoccer"

#for algorithm in "CEA2D" "NNGA" "GASpecies"
for algorithm in "NNGA"
do
	template="${problem}_${algorithm}_template.zse"
	parallelthreads=20
	
	#s score_weight 4000000
	#b ball_goal_weight 100000
	#k kick_weight 20000
	#d ball_distance_weight 1000
	#m max_kicks 10
	#p position_weight 1
		
	for score_weight in 4000000 1000000 500000 10000 1000
	do  
	
		for ball_goal_weight in 100000 50000 10000 1000
		do
		
			#for kick_weight in 100000 20000 5000 1000
			for kick_weight in 20000 5000 1000
			do
    
				for ball_distance_weight in 100000 1000
				do
    
					for max_kicks in 10 30 50
					do
						
						for position_weight in 1 500 1000 10000  
						do
    
							for seed in 1234
							do
								dir_res="${problem}_${algorithm}_s${score_weight}_b${ball_goal_weight}_k${kick_weight}_d${ball_distance_weight}_m${max_kicks}_p${position_weight}_s${seed}"
								# create the session file
								sed -e "s/-SEED-/${seed}/g" -e "s/-POSITION-/${position_weight}/g" -e "s/-MAX_KICKS-/${max_kicks}/g" -e "s/-BALL_DISTANCE-/${ball_distance_weight}/g" -e "s/-KICK-/${kick_weight}/g" -e "s/-BALL_GOAL-/${ball_goal_weight}/g" -e "s/-SCORE-/${score_weight}/g" -e "s/-PARALLELTHREADS-/${parallelthreads}/g" -e "s/-INFO-/${dir_res}/g" < ${template} > session.zse
								# run frevo
								../launch_Frevo.sh -s session.zse        	        
							done 
						done 
					done 
				done 
			done 
		done 
	done
done

echo "Execution of the evalscript_SimpleSoccer.sh on $(hostname) is done!" | mail -s "Execution of the evalscript_SimpleSoccer.sh on $(hostname) is done!" sergii.zhevzhyk@aau.at


   	